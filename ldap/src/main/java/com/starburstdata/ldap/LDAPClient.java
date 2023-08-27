package com.starburstdata.ldap;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.SearchRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LDAPClient
{
    private final static Logger logger = Logger.getLogger( LDAPClient.class.getName() );

    LDAPConnection connection;

    public LDAPClient()
    {
        this.connection = new LDAPConnection();
    }

    public void connect( String address, int port ) throws LDAPException
    {
        connect( address, port, 0 );
    }

    public void connect( String address, int port, int timeout ) throws LDAPException
    {
        logger.log( Level.INFO, "Connecting to '" + address + ":" + port + "'" );

        if( connection.isConnected() )
        {
            // Check if already connected to the address/port provided
            if( connection.getConnectedAddress().equalsIgnoreCase( address ) && connection.getConnectedPort() == port )
            {
                logger.log( Level.INFO, "Already connected to '" + address + ":" + port + "'" );
                return;
            }

            // Disconnect and then reconnect to the new address/port
            disconnect();
        }

        try
        {
            connection.connect( address, port, timeout );
        }
        catch( com.unboundid.ldap.sdk.LDAPException ex )
        {
            throw new LDAPException( "Cannot connect to LDAP server '" + address + ":" + port + "'", ex );
        }

        logger.log( Level.INFO, "Connected to '" + address + ":" + port + "'" );
    }

    public void bind( String bind, String password ) throws LDAPException
    {
        if( ! connection.isConnected() )
        {
            throw new LDAPException( "Bind failed, not connected to LDAP server" );
        }

        logger.log( Level.INFO, "Binding to LDAP server '" + connection.getConnectedAddress() + ":" + connection.getConnectedPort() + "'" );

        try
        {
            connection.bind( bind, password );
        }
        catch( com.unboundid.ldap.sdk.LDAPException ex )
        {
            throw new LDAPException( "Cannot bind to LDAP server '" + connection.getConnectedAddress() + ":" + connection.getConnectedPort() + "'", ex );
        }

        logger.log( Level.INFO, "Bound to LDAP server '" + connection.getConnectedAddress() + ":" + connection.getConnectedPort() + "'" );
    }

    public void disconnect()
    {
        if( ! connection.isConnected() )
        {
            return;
        }

        connection.close();

        logger.log( Level.INFO, "Disconnected from LDAP server '" + connection.getConnectedAddress() + ":" + connection.getConnectedPort() + "'" );
    }

    public Map<String,List<String>> getGroups( LDAPConfig config ) throws LDAPException
    {
        //
        // https://ldap.com/the-ldap-search-operation/
        // https://ldap.com/ldap-filters/
        // https://docs.oracle.com/javase/jndi/tutorial/ldap/misc/aliases.html
        //

        try
        {
            // Default search filter
            var filter = "(objectClass="+config.groupObjectClass()+")";

            // Override the default search filter if user provides one
            if( config.groupSearchFilter() != null && ! config.groupSearchFilter().isEmpty() )
            {
                filter = config.groupSearchFilter();
            }

            var request = new SearchRequest( config.groupSearchBase(), config.groupSearchScope(), filter, config.groupAttributes() );

            var response = connection.search( request );

            var groups = new HashMap<String,List<String>>();

            for( var result : response.getSearchEntries() )
            {
                if( ! result.hasAttribute( config.groupNameAttribute() ) )
                {
                    logger.log( Level.WARNING, "Attribute " + config.groupNameAttribute() + " is not available in search result" );
                    continue;
                }

                if( ! result.hasAttribute( config.groupMemberAttribute() ) )
                {
                    logger.log( Level.WARNING, "Attribute " + config.groupMemberAttribute() + " is not available in search result" );
                    continue;
                }

                var users = new ArrayList<String>();

                for( var member : result.getAttribute( config.groupMemberAttribute() ).getValues() )
                {
                    for( var sub : member.split( "," ) )
                    {
                        if( sub.startsWith( config.userNameAttribute() ) )
                        {
                            var name = sub.split( "=" );

                            if( name.length == 2 )
                            {
                                //
                                // TODO check if name already exists
                                //

                                users.add( name[1] );
                            }
                            else
                            {
                                logger.log( Level.SEVERE, "Attribute " + config.groupMemberAttribute() + " is not available in search result" );
                            }

                            break;
                        }
                    }
                }

                groups.put( result.getAttributeValue( config.groupNameAttribute() ), users );
            }

            return groups;
        }
        catch( com.unboundid.ldap.sdk.LDAPException ex )
        {
            throw new LDAPException( "Cannot connect to LDAP server", ex );
        }
    }

    public static class Builder
    {
        InetAddress address;
        int port;

        public Builder( String address, int port ) throws LDAPException
        {
            host( address );

            this.port = port;
        }

        private void host( String host ) throws LDAPException
        {
            try
            {
                this.address = InetAddress.getByName( host );
            }
            catch( UnknownHostException ex )
            {
                address( host );
            }
        }

        private void address( String address ) throws LDAPException
        {
            try
            {
                this.address = InetAddress.getByAddress( address.getBytes() );
            }
            catch( UnknownHostException ex )
            {
                throw new LDAPException( "Invalid inet address", ex );
            }
        }
    }
}
