package com.starburstdata.ranger;

import com.google.gson.Gson;
import com.starburstdata.ranger.model.VXGroupUserInfo;
import com.starburstdata.ranger.model.VXGroups;
import com.starburstdata.ranger.model.VXPortalUser;
import com.starburstdata.ranger.model.VXUsers;
import com.starburstdata.ranger.model.VXUserGroupInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

public class RangerClient
{
    // Date parsing - https://kylewbanks.com/blog/String-Date-Parsing-with-GSON-UTC-Time-Zone

    private final static Logger logger = Logger.getLogger( RangerClient.class.getName() );

    private HttpClient client;
    private final String base;
    private String auth;
    private final boolean dryrun;

    private final static HttpClient.Version version = HttpClient.Version.HTTP_1_1;

    private RangerClient( RangerClient.Builder builder )
    {
        this.base = builder.base;

        if( builder.user == null || builder.password == null )
        {
            createHttpClient();
        }
        else
        {
            createHttpClient( builder.user, builder.password );
        }

        this.dryrun = builder.dryrun;
    }

    public void createHttpClient()
    {
        client = HttpClient.newBuilder().version(version).build();
    }

    public void createHttpClient( String user, String password )
    {
        //
        // Have to work around bug in HttpClient. Ranger does not return WWW-Authenticate
        // https://stackoverflow.com/questions/54208945/java-11-httpclient-not-sending-basic-authentication
        //

        // Have to just cretae a non-auth client for now
        createHttpClient();

        //
        // Check the params, if either is null, no auth
        //
        if( user == null || user.isEmpty() )
        {
            return;
        }
        if( password == null || password.isEmpty() )
        {
            return;
        }

        // Compute the basic auth string once
        auth = "Basic " + Base64.getEncoder().encodeToString( (user+":"+password).getBytes() );

        /*
        //
        // This is the right way to create the client, but the HttpClient bug prevents this from working with Ranger
        //
        client = HttpClient.newBuilder().authenticator(new Authenticator()
                 {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(user, password.toCharArray());
                    }
                 }).version(HttpClient.Version.HTTP_1_1).build();
        */
    }

    public void createPortalUser( String loginId ) throws RangerException
    {
        createPortalUser( new VXPortalUser.Builder( loginId ).build() );
    }

    public void createPortalUser( VXPortalUser user ) throws RangerException
    {
        URI uri;

        try
        {
            uri = new URI( base + "/service/users/default" );
        }
        catch( URISyntaxException ex )
        {
            throw new RangerException( "Error formatting createPortalUser URI", ex );
        }

        var builder = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user))).header("Content-type", "application/json").header("Accept", "application/json");

        if( auth != null )
        {
            builder.header("Authorization", auth );
        }

        var request = builder.build();

        if( dryrun )
        {
            return;
        }

        var response = client.sendAsync( request, HttpResponse.BodyHandlers.ofString() ).join();

        // Expected return status OK
        if( response.statusCode() != 200 )
        {
            throw new RangerException( "[Ranger] Create Portal User: Status " + response.statusCode() + " returned, expected 200" );
        }

        //var ret = new Gson().fromJson( response.body(), VXPortalUser.class );
    }

    public void deleteUser( int id, String name ) throws RangerException
    {
        URI uri;

        try
        {
            uri = new URI( base + "/service/xusers/users/" + id + "?forceDelete=true" );
        }
        catch( URISyntaxException ex )
        {
            throw new RangerException( "Error formatting createPortalUser URI", ex );
        }

        var builder = HttpRequest.newBuilder(uri).DELETE().header("Accept", "application/json");

        if( auth != null )
        {
            builder.header("Authorization", auth );
        }

        var request = builder.build();

        if( dryrun )
        {
            return;
        }

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.discarding()).join();

        // Expected return status NoContent
        if( response.statusCode() != 204 )
        {
            throw new RangerException( "[Ranger] Delete User: Status " + response.statusCode() + " returned, expected 204" );
        }
    }

    public VXUsers getUsers() throws RangerException
    {
        VXUsers users = new VXUsers();

        URI uri;

        do
        {
            try
            {
                uri = new URI( base + "/service/xusers/users?startIndex=" + users.users().length );
            }
            catch( URISyntaxException ex )
            {
                throw new RangerException( "Error formatting getPortalUsers URI", ex );
            }

            var builder = HttpRequest.newBuilder(uri).GET().header("Accept", "application/json");

            if( auth != null )
            {
                builder.header("Authorization", auth );
            }

            var request = builder.build();

            if( dryrun )
            {
                return new VXUsers();
            }

            var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

            // Expected return status OK
            if( response.statusCode() != 200 )
            {
                throw new RangerException( "[Ranger] Get Users: Status " + response.statusCode() + " returned, expected 200" );
            }

            // Add the next set of users
            users = new Gson().fromJson( response.body(), VXUsers.class ).append( users );
        }
        while( users.users().length < users.totalCount() );

        return users;
    }

    public void createUserGroupInfo( String name, String description ) throws RangerException
    {
        createUserGroupInfo( new VXUserGroupInfo.Builder(name).description(description).build() );
    }

    public void createUserGroupInfo( VXUserGroupInfo info ) throws RangerException
    {
        URI uri;

        try
        {
            uri = new URI( base + "/service/xusers/users/userinfo" );
        }
        catch( URISyntaxException ex )
        {
            throw new RangerException( "Error formatting createUserGroupInfo URI", ex );
        }

        var builder = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(info))).header("Content-type", "application/json").header("Accept", "application/json");

        if( auth != null )
        {
            builder.header("Authorization", auth );
        }

        var request = builder.build();

        if( dryrun )
        {
            return;
        }

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

        // Expected return status OK
        if( response.statusCode() != 200 )
        {
            throw new RangerException( "[Ranger] Create User Group Info: Status " + response.statusCode() + " returned, expected 200" );
        }

        //var ret = new Gson().fromJson( response.body(), VXPortalUser.class );
    }

    public VXGroups getGroups() throws RangerException
    {
        URI uri;

        try
        {
            uri = new URI( base + "/service/xusers/groups" );
        }
        catch( URISyntaxException ex )
        {
            throw new RangerException( "Error formatting getGroups URI", ex );
        }

        var builder = HttpRequest.newBuilder(uri).GET().header("Accept", "application/json");

        if( auth != null )
        {
            builder.header("Authorization", auth );
        }

        var request = builder.build();

        if( dryrun )
        {
            return new VXGroups();
        }

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

        // Expected return status OK
        if( response.statusCode() != 200 )
        {
            throw new RangerException( "[Ranger] Get Groups: Status " + response.statusCode() + " returned, expected 200" );
        }

        return new Gson().fromJson( response.body(), VXGroups.class );
    }

    public void deleteGroup( int id, String name ) throws RangerException
    {
        URI uri;

        try
        {
            uri = new URI( base + "/service/xusers/secure/groups/id/" + id + "?forceDelete=true" );
        }
        catch( URISyntaxException ex )
        {
            throw new RangerException( "Error formatting deleteGroup URI", ex );
        }

        var builder = HttpRequest.newBuilder(uri).DELETE().header("Accept", "application/json");

        if( auth != null )
        {
            builder.header("Authorization", auth );
        }

        var request = builder.build();

        if( dryrun )
        {
            return;
        }

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.discarding()).join();

        // Expected return status NoContent
        if( response.statusCode() != 204 )
        {
            throw new RangerException( "[Ranger] Delete Group: Status " + response.statusCode() + " returned, expected 204" );
        }
    }

    public VXGroupUserInfo getGroupUsers( String name ) throws RangerException
    {
        URI uri;

        try
        {
            uri = new URI( base + "/service/xusers/groupusers/groupName/" + URLEncoder.encode( name, StandardCharsets.UTF_8 ) );
        }
        catch( URISyntaxException ex )
        {
            throw new RangerException( "Error formatting getGroupUsers URI", ex );
        }

        var builder = HttpRequest.newBuilder(uri).GET().header("Accept", "application/json");

        if( auth != null )
        {
            builder.header("Authorization", auth );
        }

        var request = builder.build();

        if( dryrun )
        {
            return new VXGroupUserInfo.Builder("dryrun").build();
        }

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

        // Expected return status Ok
        if( response.statusCode() != 200 )
        {
            throw new RangerException( "[Ranger] Get Group Users: Status " + response.statusCode() + " returned, expected 200" );
        }

        return new Gson().fromJson( response.body(), VXGroupUserInfo.class );
    }

    public void deleteGroupUser( String group, String user ) throws RangerException
    {
        URI uri;

        try
        {
            uri = new URI( base + "/service/xusers/group/" + URLEncoder.encode( group, StandardCharsets.UTF_8 ) + "/id/" + URLEncoder.encode( user, StandardCharsets.UTF_8 ) );
        }
        catch( URISyntaxException ex )
        {
            throw new RangerException( "Error formatting deleteGroupUser URI", ex );
        }

        var builder = HttpRequest.newBuilder(uri).DELETE().header("Accept", "application/json");

        if( auth != null )
        {
            builder.header("Authorization", auth );
        }

        var request = builder.build();

        if( dryrun )
        {
            return;
        }

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

        // Expected return status NoContent
        if( response.statusCode() != 204 )
        {
            throw new RangerException( "[Ranger] Delete Group User: Status " + response.statusCode() + " returned, expected 204" );
        }
    }

    public void createGroupInfo( VXGroupUserInfo info ) throws RangerException
    {
        URI uri;

        try
        {
            uri = new URI( base + "/service/xusers/groups/groupinfo" );
        }
        catch( URISyntaxException ex )
        {
            throw new RangerException( "Error formatting createGroupInfo URI", ex );
        }

        var builder = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(info))).header("Content-type", "application/json").header("Accept", "application/json");

        if( auth != null )
        {
            builder.header("Authorization", auth );
        }

        var request = builder.build();

        if( dryrun )
        {
            return;
        }

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

        // Expected return status OK
        if( response.statusCode() != 200 )
        {
            throw new RangerException( "[Ranger] Create Group Info: Status " + response.statusCode() + " returned, expected 200" );
        }

        //var ret = new Gson().fromJson( response.body(), VXGroupUserInfo.class );
    }

    public static class Builder
    {
        String base;
        String user;
        String password;

        boolean dryrun = false;

        public Builder( String base )
        {
            this.base = base;
        }

        public Builder credentials( String user, String password )
        {
            //
            // Ensure the value is null regardless if it's empty or null
            //

            if( user != null && user.isEmpty() )
            {
                this.user = null;
            }
            else
            {
                this.user = user;
            }

            if( password != null && password.isEmpty() )
            {
                this.password = null;
            }
            else
            {
                this.password = password;
            }

            return this;
        }

        public Builder dryrun( boolean dryrun )
        {
            this.dryrun = dryrun; return this;
        }

        public RangerClient build()
        {
            return new RangerClient( this );
        }
    }
}
