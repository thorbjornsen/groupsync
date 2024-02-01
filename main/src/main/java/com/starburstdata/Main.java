package com.starburstdata;

import com.starburstdata.azure.AzureClient;
import com.starburstdata.azure.AzureConfig;
import com.starburstdata.azure.AzureException;
import com.starburstdata.ldap.LDAPClient;
import com.starburstdata.ldap.LDAPConfig;
import com.starburstdata.ldap.LDAPException;
import com.starburstdata.ranger.RangerClient;
import com.starburstdata.ranger.RangerConfig;
import com.starburstdata.ranger.RangerException;
import com.starburstdata.ranger.model.VXGroupUserInfo;
import com.starburstdata.ranger.model.VXUserGroupInfo;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main
{
    static
    {
        // If logging property file is set, then don't override it
        if( System.getProperty( "java.util.logging.config.file" ) == null )
        {
            try
            {
                // Otherwise, try to load the logging property file from the resources
                System.setProperty( "java.util.logging.config.file", Main.class.getClassLoader().getResource( "log.properties" ).getFile() );
            }
            catch( NullPointerException ex )
            {
                // Basically, just use the default logging configuration
                assert true;
            }
        }
    }

    private final static Logger logger = Logger.getLogger( Main.class.getName() );
    private final static String missing = "Configuration: '%s' is missing";

    public static RangerConfig getRangerConfiguration( Configuration config ) throws RangerException
    {
        //
        // Mandatory
        //

        boolean error = false;

        if( ! config.exists( Configuration.DEST_RANGER_URI ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.DEST_RANGER_URI ) );
            error = true;
        }

        if( ! config.exists( Configuration.DEST_RANGER_USER ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.DEST_RANGER_USER ) );
            error = true;
        }

        if( ! config.exists( Configuration.DEST_RANGER_PASS ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.DEST_RANGER_PASS ) );
            error = true;
        }

        if( error )
        {
            throw new RangerException( "Failed to load Ranger configuration" );
        }

        RangerConfig.Builder builder;

        try
        {
            builder = new RangerConfig.Builder( config.get( Configuration.DEST_RANGER_URI ) ).credentials( config.get( Configuration.DEST_RANGER_USER ), config.get( Configuration.DEST_RANGER_PASS ) );
        }
        catch( URISyntaxException ex )
        {
            throw new RangerException( ex.getMessage() );
        }

        //
        // Optional
        //

        if( config.exists( Configuration.DEST_RANGER_DELETE_USERS ) )
        {
            builder.deleteUsers( config.get( Configuration.DEST_RANGER_DELETE_USERS ) );
        }

        if( config.exists( Configuration.DEST_RANGER_DELETE_GROUPS ) )
        {
            builder.deleteGroups( config.get( Configuration.DEST_RANGER_DELETE_GROUPS ) );
        }

        if( config.exists( Configuration.DEST_RANGER_UPLOAD_LIMIT ) )
        {
            builder.uploadLimit( config.get( Configuration.DEST_RANGER_UPLOAD_LIMIT ) );
        }

        if( config.exists( Configuration.DEST_RANGER_DRY_RUN ) )
        {
            builder.dryrun( config.get( Configuration.DEST_RANGER_DRY_RUN ) );
        }

        return builder.build();
    }

    public static LDAPConfig getLDAPConfiguration( Configuration config ) throws LDAPException
    {
        //
        // Mandatory
        //

        boolean error = false;

        if( ! config.exists( Configuration.SOURCE_LDAP_HOST ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_LDAP_HOST ) );
            error = true;
        }

        if( ! config.exists( Configuration.SOURCE_LDAP_PORT ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_LDAP_PORT ) );
            error = true;
        }

        if( ! config.exists( Configuration.SOURCE_LDAP_BIND ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_LDAP_BIND ) );
            error = true;
        }

        if( ! config.exists( Configuration.SOURCE_LDAP_BIND_PASS ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_LDAP_BIND_PASS ) );
            error = true;
        }

        if( ! config.exists( Configuration.SOURCE_LDAP_GROUP_SEARCHBASE ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_LDAP_GROUP_SEARCHBASE ) );
            error = true;
        }

        if( ! config.exists( Configuration.SOURCE_LDAP_GROUP_OBJECTCLASS ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_LDAP_GROUP_OBJECTCLASS ) );
            error = true;
        }

        if( ! config.exists( Configuration.SOURCE_LDAP_GROUP_MEMBER_ATTRIBUTE ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_LDAP_GROUP_MEMBER_ATTRIBUTE ) );
            error = true;
        }

        if( ! config.exists( Configuration.SOURCE_LDAP_GROUP_NAME_ATTRIBUTE ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_LDAP_GROUP_NAME_ATTRIBUTE ) );
            error = true;
        }

        if( ! config.exists( Configuration.SOURCE_LDAP_USER_NAME_ATTRIBUTE ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_LDAP_USER_NAME_ATTRIBUTE ) );
            error = true;
        }

        if( error )
        {
            throw new LDAPException( "Failed to load LDAP configuration" );
        }

        var builder = new LDAPConfig.Builder( config.get( Configuration.SOURCE_LDAP_HOST ), config.get( Configuration.SOURCE_LDAP_PORT ) )
                                            .bind( config.get( Configuration.SOURCE_LDAP_BIND ) )
                                            .password( config.get( Configuration.SOURCE_LDAP_BIND_PASS ) )
                                            .groupSearchBase( config.get( Configuration.SOURCE_LDAP_GROUP_SEARCHBASE ) )
                                            .groupObjectClass( config.get( Configuration.SOURCE_LDAP_GROUP_OBJECTCLASS ) )
                                            .groupMemberAttribute( config.get( Configuration.SOURCE_LDAP_GROUP_MEMBER_ATTRIBUTE ) )
                                            .groupNameAttribute( config.get( Configuration.SOURCE_LDAP_GROUP_NAME_ATTRIBUTE ) )
                                            .userNameAttribute( config.get( Configuration.SOURCE_LDAP_USER_NAME_ATTRIBUTE ) );

        //
        // Optional
        //

        if( config.exists( Configuration.SOURCE_LDAP_GROUP_SEARCHSCOPE ) )
        {
            builder.groupSearchScope( config.get( Configuration.SOURCE_LDAP_GROUP_SEARCHSCOPE ) );
        }

        if( config.exists( Configuration.SOURCE_LDAP_GROUP_SEARCHFILTER ) )
        {
            builder.groupSearchFilter( config.get( Configuration.SOURCE_LDAP_GROUP_SEARCHFILTER ) );
        }

        if( config.exists( Configuration.SOURCE_LDAP_GROUP_ATTRIBUTES ) )
        {
            builder.groupAttributes( config.get( Configuration.SOURCE_LDAP_GROUP_ATTRIBUTES ) );
        }

        if( config.exists( Configuration.CREATE_EMPTY_GROUPS ) )
        {
            builder.createEmptyGroups( config.get( Configuration.CREATE_EMPTY_GROUPS ) );
        }

        if( config.exists( Configuration.SYNC_INTERVAL ) )
        {
            builder.syncInterval( config.get( Configuration.SYNC_INTERVAL ) );
        }

        return builder.build();
    }

    public static AzureConfig getAzureConfiguration( Configuration config ) throws AzureException
    {
        //
        // Mandatory
        //

        boolean error = false;

        if( !config.exists( Configuration.SOURCE_AZURE_TENANT_ID ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_AZURE_TENANT_ID ) );
            error = true;
        }

        if( !config.exists( Configuration.SOURCE_AZURE_CLIENT_ID ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_AZURE_CLIENT_ID ) );
            error = true;
        }

        if( !config.exists( Configuration.SOURCE_AZURE_CLIENT_SECRET ) )
        {
            logger.log( Level.SEVERE, String.format( missing, Configuration.SOURCE_AZURE_CLIENT_SECRET ) );
            error = true;
        }

        if( error )
        {
            throw new AzureException( "Failed to load Azure configuration" );
        }

        var builder = new AzureConfig.Builder().tenantId( config.get( Configuration.SOURCE_AZURE_TENANT_ID ) ).clientId( config.get( Configuration.SOURCE_AZURE_CLIENT_ID ) ).clientSecret( config.get( Configuration.SOURCE_AZURE_CLIENT_SECRET ) );

        //
        // Optional
        //

        if( config.exists( Configuration.SOURCE_AZURE_GROUP_FILTER ) )
        {
            builder.groupFilter( config.get( Configuration.SOURCE_AZURE_GROUP_FILTER ) );
        }

        if( config.exists( Configuration.CREATE_EMPTY_GROUPS ) )
        {
            builder.createEmptyGroups( config.get( Configuration.CREATE_EMPTY_GROUPS ) );
        }

        if( config.exists( Configuration.SYNC_INTERVAL ) )
        {
            builder.syncInterval( config.get( Configuration.SYNC_INTERVAL ) );
        }

        return builder.build();
    }

    public static void ldapsync( LDAPConfig ldapConfig, RangerConfig rangerConfig )
    {
        final String description = "Imported by LDAP sync";

        try
        {
            //
            // LDAP connect
            //

            var ldapClient = new LDAPClient();

            ldapClient.connect( ldapConfig.host(), ldapConfig.port() );

            ldapClient.bind( ldapConfig.bind(), ldapConfig.password() );

            //
            // Ranger connect
            //

            RangerClient rangerClient = new RangerClient.Builder( rangerConfig.uri().toString() ).credentials( rangerConfig.user(), rangerConfig.password() ).dryrun( rangerConfig.dryrun() ).build();

            // Need to keep a list of unique users from all LDAP groups
            var users = new java.util.HashMap<String,List<String>>();

            //
            // Get and process LDAP groups
            //

            var ldapGroups = ldapClient.getGroups( ldapConfig );

            if( ldapGroups.isEmpty() )
            {
                logger.log( Level.INFO, "No LDAP groups returned"  );
                return;
            }

            for( var entry : ldapGroups.entrySet() )
            {
                logger.log( Level.INFO, "Processing LDAP group '" + entry.getKey() + "'"  );

                //
                // If no users (group is empty), check ignore flag
                //
                if( entry.getValue().isEmpty() )
                {
                    if( ! ldapConfig.createEmptyGroups() )
                    {
                        logger.log( Level.INFO, "LDAP group '" + entry.getKey() + "' contains no users, skipping"  );
                        continue;
                    }
                    else
                    {
                        logger.log( Level.INFO, "LDAP group '" + entry.getKey() + "' contains no users"  );
                    }
                }
                else
                {
                    logger.log( Level.INFO, "LDAP group '" + entry.getKey() + "' contains " + entry.getValue().size() + " users"  );
                }

                // Create the group info
                var builder = new VXGroupUserInfo.Builder( entry.getKey() ).groupDescription( description );

                int count = 0;

                //
                // Add the users to the group
                //
                for( var user : entry.getValue() )
                {
                    logger.log( Level.FINE, "LDAP group '" + entry.getKey() + "' contains user '" + user + "'"  );

                    rangerClient.createPortalUser( user );

                    rangerClient.createUserGroupInfo( new VXUserGroupInfo.Builder( user ).description( description ).build() );

                    builder.addUser( user ); count++;

                    // Check if an upload limit is configured
                    if( rangerConfig.hasUploadLimit() && count >= rangerConfig.uploadLimit() )
                    {
                        logger.log( Level.INFO, "Uploading " + count + " users to Ranger group '" +  entry.getKey() + "'" );

                        // Upload the current user set
                        rangerClient.createGroupInfo( builder.build() );

                        // Reset the builder
                        builder = new VXGroupUserInfo.Builder( entry.getKey() ).groupDescription( description ); count = 0;
                    }

                    //
                    // Track all the users added, and the groups they are members of
                    //

                    if( ! users.containsKey( user ) )
                    {
                        users.put( user, new ArrayList<>() );
                    }

                    users.get( user ).add( entry.getKey() );
                }

                if( count > 0 )
                {
                    logger.log( Level.INFO, "Uploading " + count + " users to Ranger group '" +  entry.getKey() + "'" );
                    rangerClient.createGroupInfo( builder.build() );
                }

                //
                // Delete all Ranger users from the group that aren't in the group
                //
                for( var user : rangerClient.getGroupUsers( entry.getKey() ).getUsers() )
                {
                    if( ! entry.getValue().contains( user ) )
                    {
                        logger.log( Level.INFO, "Removing user " + count + " user from Ranger group '" +  entry.getKey() + "'" );
                        rangerClient.deleteGroupUser( entry.getKey(), user );
                    }
                }
            }

            if( rangerConfig.deleteGroups() )
            {
                for( var vxGroup : rangerClient.getGroups().getVXGroups() )
                {
                    // Skip any non-external group
                    if( vxGroup.getGroupSource() != 1 )
                    {
                        logger.log( Level.FINEST, "Group '" +  vxGroup.getName() + "' is not external" );
                        continue;
                    }

                    //
                    // Delete any existing Ranger groups that were not returned from LDAP
                    //
                    if( ! ldapGroups.containsKey( vxGroup.getName() ) )
                    {
                        logger.log( Level.INFO, "Removing Ranger group '" +  vxGroup.getName() + "'" );
                        rangerClient.deleteGroup( vxGroup.getId(), vxGroup.getName() );
                    }
                    else
                    {
                        logger.log( Level.FINEST, "Group '" +  vxGroup.getName() + "' exists in both source/destination, skipping" );
                    }
                }
            }

            if( rangerConfig.deleteUsers() )
            {
                for( var user : rangerClient.getUsers().users() )
                {
                    // Skip any users that were not created externally
                    if( ! user.isExternal() )
                    {
                        continue;
                    }

                    // Skip if the user exists as a member of an LDAP group
                    if( users.containsKey( user.name() ) )
                    {
                        continue;
                    }

                    logger.log( Level.INFO, "Removing Ranger user '" +  user.name() + "'" );

                    // Delete user from Ranger that is not a member of any group returned from LDAP
                    rangerClient.deleteUser( user.id(), user.name() );
                }
            }
        }
        catch( LDAPException | RangerException ex )
        {
            logger.log( Level.SEVERE, ex.getMessage() );

            if( ex.getCause() != null )
            {
                logger.log( Level.SEVERE, ex.getCause().toString() );
            }
        }
    }

    public static void azuresync( AzureConfig azureConfig, RangerConfig rangerConfig )
    {
        final String description = "Imported by Active Directory sync";

        try
        {
            //
            // Azure connect
            //

            var azureClient = new AzureClient();

            azureClient.login( azureConfig );

            //
            // Ranger connect
            //

            RangerClient rangerClient = new RangerClient.Builder( rangerConfig.uri().toString() ).credentials( rangerConfig.user(), rangerConfig.password() ).dryrun( rangerConfig.dryrun() ).build();

            // Need to keep a list of unique users from all Azure groups
            var users = new java.util.HashMap<String,List<String>>();

            //
            // Get and process Azure groups
            //

            var azureGroups = azureClient.getGroups( azureConfig );

            if( azureGroups.isEmpty() )
            {
                logger.log( Level.INFO, "No Azure groups returned"  );
                return;
            }

            for( var entry : azureGroups.entrySet() )
            {
                logger.log( Level.INFO, "Processing Azure group '" + entry.getKey() + "'"  );

                //
                // If no users (group is empty), check ignore flag
                //
                if( entry.getValue().isEmpty() )
                {
                    if( azureConfig.createEmptyGroups() )
                    {
                        logger.log( Level.INFO, "Azure group '" + entry.getKey() + "' contains no users, skipping"  );
                        continue;
                    }
                    else
                    {
                        logger.log( Level.INFO, "Azure group '" + entry.getKey() + "' contains no users"  );
                    }
                }
                else
                {
                    logger.log( Level.INFO, "Azure group '" + entry.getKey() + "' contains " + entry.getValue().size() + " users"  );
                }

                // Create the group info
                var builder = new VXGroupUserInfo.Builder( entry.getKey() ).groupDescription( description );

                int count = 0;

                //
                // Add the users to the group
                //
                for( var user : entry.getValue() )
                {
                    logger.log( Level.FINE, "Azure group '" + entry.getKey() + "' contains user '" + user + "'"  );

                    rangerClient.createPortalUser( user );

                    rangerClient.createUserGroupInfo( new VXUserGroupInfo.Builder( user ).description( description ).build() );

                    builder.addUser( user ); count++;

                    // Check if an upload limit is configured
                    if( rangerConfig.hasUploadLimit() && count >= rangerConfig.uploadLimit() )
                    {
                        logger.log( Level.INFO, "Uploading " + count + " users to Ranger group '" +  entry.getKey() + "'" );

                        // Upload the current user set
                        rangerClient.createGroupInfo( builder.build() );

                        // Reset the builder
                        builder = new VXGroupUserInfo.Builder( entry.getKey() ).groupDescription( description ); count = 0;
                    }

                    //
                    // Track all the users added, and the groups they are members of
                    //

                    if( ! users.containsKey( user ) )
                    {
                        users.put( user, new ArrayList<>() );
                    }

                    users.get( user ).add( entry.getKey() );
                }

                if( count > 0 )
                {
                    logger.log( Level.INFO, "Uploading " + count + " users to Ranger group '" +  entry.getKey() + "'" );
                    rangerClient.createGroupInfo( builder.build() );
                }

                //
                // Delete all Ranger users from the group that aren't in the group
                //
                for( var user : rangerClient.getGroupUsers( entry.getKey() ).getUsers() )
                {
                    if( ! entry.getValue().contains( user ) )
                    {
                        rangerClient.deleteGroupUser( entry.getKey(), user );
                    }
                }
            }

            if( rangerConfig.deleteGroups() )
            {
                for( var vxGroup : rangerClient.getGroups().getVXGroups() )
                {
                    // Skip any non-external group
                    if( vxGroup.getGroupSource() != 1 )
                    {
                        logger.log( Level.FINEST, "Group '" +  vxGroup.getName() + "' is not external" );
                        continue;
                    }

                    //
                    // Delete any existing Ranger groups that were not returned from Azure
                    //
                    if( ! azureGroups.containsKey( vxGroup.getName() ) )
                    {
                        logger.log( Level.INFO, "Removing Ranger group '" +  vxGroup.getName() + "'" );
                        rangerClient.deleteGroup( vxGroup.getId(), vxGroup.getName() );
                    }
                    else
                    {
                        logger.log( Level.FINEST, "Group '" +  vxGroup.getName() + "' exists in both source/destination, skipping" );
                    }
                }
            }

            if( rangerConfig.deleteUsers() )
            {
                for( var user : rangerClient.getUsers().users() )
                {
                    // Skip any users that were not created externally
                    if( ! user.isExternal() )
                    {
                        continue;
                    }

                    // Skip if the user exists as a member of an LDAP group
                    if( users.containsKey( user.name() ) )
                    {
                        continue;
                    }

                    logger.log( Level.INFO, "Removing Ranger user '" +  user.name() + "'" );

                    // Delete user from Ranger that is not a member of any group returned from Azure
                    rangerClient.deleteUser( user.id(), user.name() );
                }
            }
        }
        catch( AzureException | RangerException ex )
        {
            logger.log( Level.SEVERE, ex.getMessage() );

            if( ex.getCause() != null )
            {
                logger.log( Level.SEVERE, ex.getCause().toString() );
            }
        }
    }

    public static void main( String[] args )
    {
        //
        // Process configuration
        //

        var configuration = new Configuration();

        //
        // This is here in case a different destination is desired (at some point in the future)
        //

        RangerConfig rangerConfig = null;

        switch( configuration.get( Configuration.DEST_TYPE ).toLowerCase() )
        {
            case "ranger" ->
            {
                try
                {
                    rangerConfig = getRangerConfiguration( configuration );
                }
                catch( RangerException ex )
                {
                    logger.log( Level.SEVERE, ex.getMessage() );

                    if( ex.getCause() != null )
                    {
                        logger.log( Level.SEVERE, ex.getCause().toString() );
                    }

                    System.exit( -1 );
                }
            }
            default ->
            {
                if( configuration.exists( Configuration.DEST_TYPE ) )
                {
                    logger.log( Level.SEVERE, "Configuration: '" + Configuration.DEST_TYPE + "=" + configuration.get( Configuration.DEST_TYPE ) + "' value is invalid" );
                }
                else
                {
                    logger.log( Level.SEVERE, "Configuration: '" + Configuration.DEST_TYPE + "=" + configuration.get( Configuration.DEST_TYPE ) + "' is missing" );
                }

                System.exit( -1 );
            }
        }

        //
        // Call into the configured source for processing
        //

        switch( configuration.get( Configuration.SOURCE_TYPE ).toLowerCase() )
        {
            case "ldap" ->
            {
                try
                {
                    var ldapConfig = getLDAPConfiguration( configuration );

                    for( ; ; )
                    {
                        logger.log( Level.INFO, "Syncing group information from LDAP" );

                        ldapsync( ldapConfig, rangerConfig );

                        logger.log( Level.INFO, "Synced group information from LDAP" );

                        if( ldapConfig.syncInterval() > 0 )
                        {
                            try
                            {
                                logger.log( Level.INFO, "Waiting for " + ldapConfig.syncInterval() + " seconds for next sync" );

                                TimeUnit.SECONDS.sleep( ldapConfig.syncInterval() );
                            }
                            catch( InterruptedException ex )
                            {
                                logger.log( Level.WARNING, "LDAP sync was interrupted: ", ex );

                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                catch( LDAPException ex )
                {
                    logger.log( Level.SEVERE, ex.getMessage() );

                    if( ex.getCause() != null )
                    {
                        logger.log( Level.SEVERE, ex.getCause().toString() );
                    }

                    System.exit( -1 );
                }
            }
            case "azure" ->
            {
                try
                {
                    var azureConfig = getAzureConfiguration( configuration );

                    for( ; ; )
                    {
                        logger.log( Level.INFO, "Syncing group information from Azure" );

                        azuresync( azureConfig, rangerConfig );

                        logger.log( Level.INFO, "Synced group information from Azure" );

                        if( azureConfig.syncInterval() > 0 )
                        {
                            try
                            {
                                logger.log( Level.INFO, "Waiting for " + azureConfig.syncInterval() + " seconds for next sync" );

                                TimeUnit.SECONDS.sleep( azureConfig.syncInterval() );
                            }
                            catch( InterruptedException ex )
                            {
                                logger.log( Level.FINE, "Azure sync was interrupted: ", ex );

                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                catch( AzureException ex )
                {
                    logger.log( Level.SEVERE, ex.getMessage() );

                    if( ex.getCause() != null )
                    {
                        logger.log( Level.SEVERE, ex.getCause().toString() );
                    }

                    System.exit( -1 );
                }
            }
            case "scim" ->
            {
                logger.log( Level.SEVERE, "Configuration: '" + Configuration.SOURCE_TYPE + "=" + configuration.get( Configuration.SOURCE_TYPE ) + "' value is not supported yet" );
            }
            default ->
            {
                if( configuration.exists( Configuration.SOURCE_TYPE ) )
                {
                    logger.log( Level.SEVERE, "Configuration: '" + Configuration.SOURCE_TYPE + "=" + configuration.get( Configuration.SOURCE_TYPE ) + "' value is invalid" );
                }
                else
                {
                    logger.log( Level.SEVERE, "Configuration: '" + Configuration.SOURCE_TYPE + "=" + configuration.get( Configuration.SOURCE_TYPE ) + "' is missing" );
                }

                System.exit( -1 );
            }
        }
    }
}
