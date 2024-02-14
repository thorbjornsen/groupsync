package com.starburstdata.azure;

import com.google.gson.Gson;
import com.starburstdata.azure.model.AzureBatchRequest;
import com.starburstdata.azure.model.AzureBatchRequests;
import com.starburstdata.azure.model.AzureBatchResponse;
import com.starburstdata.azure.model.AzureBatchResponses;
import com.starburstdata.azure.model.AzureGroup;
import com.starburstdata.azure.model.AzureGroups;
import com.starburstdata.azure.model.AzureMembers;
import com.starburstdata.azure.model.AzureServicePrincipals;
import com.starburstdata.azure.model.AzureToken;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AzureClient
{
    private final static Logger logger = Logger.getLogger( AzureClient.class.getName() );

    private HttpClient client;
    private String accessToken = null;

    private final static HttpClient.Version version = HttpClient.Version.HTTP_1_1;
    private final static int MaxBatchCount = 20;
    private final static int MaxGroupFilterCount = 15;

    public AzureClient()
    {
        createHttpClient();
    }

    public void createHttpClient()
    {
        client = HttpClient.newBuilder().version( version ).build();
    }

    public void login( AzureConfig config ) throws AzureException
    {
        URI uri;

        try
        {
            uri = new URI( "https://login.microsoftonline.com/" + config.tenantId() + "/oauth2/v2.0/token" );
        }
        catch( URISyntaxException ex )
        {
            throw new AzureException( "Error formatting login URI", ex );
        }

        var body = "client_id=" + config.clientId() + "&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default&client_secret=" + config.clientSecret() + "&grant_type=client_credentials";

        var request = HttpRequest.newBuilder( uri ).POST( HttpRequest.BodyPublishers.ofString( body ) ).header( "Content-type", "application/x-www-form-urlencoded" ).build();

        var response = client.sendAsync( request, HttpResponse.BodyHandlers.ofString() ).join();

        // Expected return status OK
        if( response.statusCode() != 200 )
        {
            throw new AzureException( "[Azure] Login: Status " + response.statusCode() + " returned, expected 200" );
        }

        accessToken = new Gson().fromJson( response.body(), AzureToken.class ).getAccessToken();
    }

    public Map<String, List<String>> getGroups( AzureConfig config ) throws AzureException
    {
        if( accessToken == null )
        {
            throw new AzureException( "Authentication (login) has not been run" );
        }

        // Keep track of the groups that have been processed already
        var processed = new HashMap<String, List<String>>();

        for( var group : getGroups( config.groupFilter() ).value() )
        {
            if( processed.containsKey( group.displayName() ) )
            {
                logger.log( Level.WARNING, "Duplicate group name '" + group.displayName() + "' found, skipping" );
                continue;
            }

            var users = new ArrayList<String>();

            for( var member : getMembers( group.id() ).value() )
            {
                if( "#microsoft.graph.user".equals( member.odataType() ) )
                {
                    users.add( member.userPrincipalName() );
                }
                else if( "#microsoft.graph.group".equals( member.odataType() ) )
                {
                    logger.log( Level.FINE, "Found nested group '" + member.displayName() + "' in group " + group.displayName() );
                }
                else
                {
                    logger.log( Level.WARNING, "Found unsupported OdataType '" + member.odataType() + "' in group " + group.displayName() );
                }
            }

            if( config.syncServicePrincipals() )
            {
                for( var servicePrincipal : getServicePrincipals( group.id() ).value() )
                {
                    users.add( servicePrincipal.id() );
                }
            }

            processed.put( group.displayName(), users );
        }

        return processed;
    }

    protected AzureGroups getGroups( String[] filter ) throws AzureException
    {
        //
        // Workaround for Azure API limitation - max 15 filter entries
        // NOTE: could just use batch for everything, where's the fun in that?
        //
        if( filter.length > MaxGroupFilterCount )
        {
            return batchGroups( filter );
        }

        StringBuilder sb = new StringBuilder( "https://graph.microsoft.com/v1.0/groups" );

        if( filter.length > 0 )
        {
            // Add the initial group filter
            sb.append( "?$filter=startsWith(displayName,%27" ).append( URLEncoder.encode( filter[0], StandardCharsets.UTF_8 ) ).append( "%27)" );

            // Add any additional group filters
            for( int i = 1; i < filter.length; i++ )
            {
                sb.append( "+or+startsWith(displayName,%27" ).append( URLEncoder.encode( filter[i], StandardCharsets.UTF_8 ) ).append( "%27)" );
            }
        }

        URI uri;

        try
        {
            uri = new URI( sb.toString() );
        }
        catch( URISyntaxException ex )
        {
            throw new AzureException( "Error formatting URI", ex );
        }

        var request = HttpRequest.newBuilder( uri ).GET().headers( "Accept", "application/json", "ConsistencyLevel", "eventual", "Authorization", "Bearer " + accessToken ).build();

        var response = client.sendAsync( request, HttpResponse.BodyHandlers.ofString() ).join();

        // Expected return status OK
        if( response.statusCode() != 200 )
        {
            throw new AzureException( "[Azure] Get Groups: Status " + response.statusCode() + " returned, expected 200" );
        }

        var groups = new Gson().fromJson( response.body(), AzureGroups.class );

        //
        // Loop through any paged results
        //

        while( groups.odataNextLink() != null )
        {
            try
            {
                uri = new URI( groups.odataNextLink() );
            }
            catch( URISyntaxException ex )
            {
                throw new AzureException( "Error formatting URI", ex );
            }

            request = HttpRequest.newBuilder( uri ).GET().headers( "Accept", "application/json", "ConsistencyLevel", "eventual", "Authorization", "Bearer " + accessToken ).build();

            response = client.sendAsync( request, HttpResponse.BodyHandlers.ofString() ).join();

            // Expected return status OK
            if( response.statusCode() != 200 )
            {
                throw new AzureException( "[Azure] Get Groups: Status " + response.statusCode() + " returned, expected 200" );
            }

            groups = new Gson().fromJson( response.body(), AzureGroups.class ).append( groups.value() );
        }

        return groups;
    }

    protected AzureGroups batchGroups( String[] filter ) throws AzureException
    {
        if( filter.length == 0 )
        {
            throw new AzureException( "[Azure] No group filters supplied to batch operation");
        }
        else if( filter.length > (MaxGroupFilterCount * MaxBatchCount) )
        {
            throw new AzureException( "[Azure] Number of group filters exceeds the batch maximum: " + filter.length + " > " + MaxGroupFilterCount * MaxBatchCount );
        }

        StringBuilder url = new StringBuilder( "https://graph.microsoft.com/v1.0/$batch" );

        URI uri;

        try
        {
            uri = new URI( url.toString() );
        }
        catch( URISyntaxException ex )
        {
            throw new AzureException( "Error formatting URI", ex );
        }

        List<AzureBatchRequest> requests = new ArrayList<>();

        //
        // Spread the requests over the batched requests array
        // NOTE: Don't have to spread em, I think it's more performant if there are many filters
        //
        for( int i = 1; i <= filter.length && i <= MaxBatchCount; i++ )
        {
            StringBuilder sb = new StringBuilder( "/groups" ).append( "?$filter=startsWith(displayName,%27" ).append( URLEncoder.encode( filter[i-1], StandardCharsets.UTF_8 ) ).append( "%27)" );

            for( int d = MaxBatchCount+i; d <= filter.length; d += MaxBatchCount )
            {
                sb.append( "+or+startsWith(displayName,%27" ).append( URLEncoder.encode( filter[d-1], StandardCharsets.UTF_8 ) ).append( "%27)" );
            }

            requests.add( new AzureBatchRequest().id( Integer.toString( i ) ).url( sb.toString() ) );
        }

        AzureBatchRequests batch = new AzureBatchRequests().requests( requests.toArray( new AzureBatchRequest[0] ) );

        String json = new Gson().toJson( batch );

        var request = HttpRequest.newBuilder( uri ).POST( HttpRequest.BodyPublishers.ofString( new Gson().toJson( batch ) ) ).headers( "Content-type", "application/json", "Accept", "application/json", "Authorization", "Bearer " + accessToken ).build();

        var response = client.sendAsync( request, HttpResponse.BodyHandlers.ofString() ).join();

        // Expected return status OK
        if( response.statusCode() != 200 )
        {
            throw new AzureException( "[Azure] Batch Groups: Status " + response.statusCode() + " returned, expected 200" );
        }

        List<AzureGroup> groups = new ArrayList<>();

        for( var resp : new Gson().fromJson( response.body(), AzureBatchResponses.class ).responses() )
        {
            if( resp.status() != 200 )
            {
                throw new AzureException( "[Azure] Batch Groups Response: Status " + response.statusCode() + " returned, expected 200 for id " + resp.id() );
            }

            for( var value : resp.body().value() )
            {
                groups.add( new AzureGroup().id( value.id() ).displayName( value.displayName() ) );
            }
        }

        return new AzureGroups().value( groups.toArray( new AzureGroup[0]) );
    }

    protected AzureMembers getMembers( String id ) throws AzureException
    {
        if( accessToken == null )
        {
            throw new AzureException( "Authentication (login) has not been run" );
        }

        URI uri;

        try
        {
            uri = new URI( "https://graph.microsoft.com/v1.0/groups/" + id + "/members" );
        }
        catch( URISyntaxException ex )
        {
            throw new AzureException( "Error formatting URI", ex );
        }

        var request = HttpRequest.newBuilder(uri).GET().headers( "Accept", "application/json", "Authorization", "Bearer " + accessToken ).build();

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

        // Expected return status OK
        if( response.statusCode() != 200 )
        {
            throw new AzureException( "[Azure] Get Members: Status " + response.statusCode() + " returned, expected 200" );
        }

        var members = new Gson().fromJson( response.body(), AzureMembers.class );

        //
        // Loop through any paged results
        //

        while( members.odataNextLink() != null )
        {
            try
            {
                uri = new URI( members.odataNextLink() );
            }
            catch( URISyntaxException ex )
            {
                throw new AzureException( "Error formatting URI", ex );
            }

            request = HttpRequest.newBuilder(uri).GET().headers( "Accept", "application/json", "Authorization", "Bearer " + accessToken ).build();

            response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

            // Expected return status OK
            if( response.statusCode() != 200 )
            {
                throw new AzureException( "[Azure] Get Members: Status " + response.statusCode() + " returned, expected 200" );
            }

            members = new Gson().fromJson( response.body(), AzureMembers.class ).append( members.value() );
        }

        return members;
    }

    protected AzureServicePrincipals getServicePrincipals( String id ) throws AzureException
    {
        if( accessToken == null )
        {
            throw new AzureException( "Authentication (login) has not been run" );
        }

        URI uri;

        try
        {
            uri = new URI( "https://graph.microsoft.com/v1.0/groups/" + id + "/members/microsoft.graph.servicePrincipal" );
        }
        catch( URISyntaxException ex )
        {
            throw new AzureException( "Error formatting URI", ex );
        }

        var request = HttpRequest.newBuilder(uri).GET().headers( "Accept", "application/json", "Authorization", "Bearer " + accessToken ).build();

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

        // Expected return status OK
        if( response.statusCode() != 200 )
        {
            throw new AzureException( "[Azure] Get Service Principals: Status " + response.statusCode() + " returned, expected 200" );
        }

        var servicePrincipals = new Gson().fromJson( response.body(), AzureServicePrincipals.class );

        //
        // Loop through any paged results
        //

        while( servicePrincipals.odataNextLink() != null )
        {
            try
            {
                uri = new URI( servicePrincipals.odataNextLink() );
            }
            catch( URISyntaxException ex )
            {
                throw new AzureException( "Error formatting URI", ex );
            }

            request = HttpRequest.newBuilder(uri).GET().headers( "Accept", "application/json", "Authorization", "Bearer " + accessToken ).build();

            response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

            // Expected return status OK
            if( response.statusCode() != 200 )
            {
                throw new AzureException( "[Azure] Get Members: Status " + response.statusCode() + " returned, expected 200" );
            }

            servicePrincipals = new Gson().fromJson( response.body(), AzureServicePrincipals.class ).append( servicePrincipals.value() );
        }

        return servicePrincipals;
    }
}