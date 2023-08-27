package com.starburstdata;

import com.starburstdata.ldap.LDAPClient;
import com.starburstdata.ldap.LDAPConfig;
import com.starburstdata.ldap.LDAPException;
import com.starburstdata.ranger.RangerClient;
import com.starburstdata.ranger.RangerConfig;
import com.starburstdata.ranger.RangerException;
import com.starburstdata.ranger.VXGroupUserInfo;
import com.starburstdata.ranger.VXUserGroupInfo;

import java.net.URISyntaxException;
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
                System.setProperty( "java.util.logging.config.file", Main.class.getClassLoader().getResource( "logging.properties" ).getFile() );
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

        if( config.exists( Configuration.SOURCE_LDAP_IGNORE_EMPTY_GROUPS ) )
        {
            builder.ignoreEmptyGroups( config.get( Configuration.SOURCE_LDAP_IGNORE_EMPTY_GROUPS ) );
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
            var users = new java.util.HashSet<String>();

            //
            // Get and process LDAP groups
            //

            var ldapGroups = ldapClient.getGroups( ldapConfig );

            for( var entry : ldapGroups.entrySet() )
            {
                logger.log( Level.INFO, "Processing LDAP group '" + entry.getKey() + "'"  );

                //
                // If no users (group is empty), check ignore flag
                //
                if( entry.getValue().isEmpty() )
                {
                    if( ldapConfig.ignoreEmptyGroups() )
                    {
                        logger.log( Level.INFO, "LDAP group '" + entry.getKey() + "' contains no users, skipping"  );
                        continue;
                    }
                    else
                    {
                        logger.log( Level.INFO, "LDAP group '" + entry.getKey() + "' contains no users"  );
                    }
                }

                // Create the group info
                var builder = new VXGroupUserInfo.Builder( entry.getKey() ).groupDescription( description );

                // Add the users to the group
                for( var user : entry.getValue() )
                {
                    logger.log( Level.INFO, "LDAP group '" + entry.getKey() + "' contains user '" + user + "'"  );

                    //
                    // TODO there may be a limit on the number of users per submission
                    //

                    builder.addUser( user );

                    if( ! users.contains( user ) )
                    {
                        users.add( user );
                    }
                    else
                    {
                        logger.log( Level.FINE, "Found duplicate user '" + user + "' in another LDAP group" );
                    }
                }

                rangerClient.createGroupInfo( builder.build() );

                if( rangerConfig.deleteUsers() )
                {
                    //
                    // Delete all Ranger users that aren't in the LDAP list
                    //
                    for( var user : rangerClient.getGroupUsers( entry.getKey() ).getUsers() )
                    {
                        if( ! entry.getValue().contains( user ) )
                        {
                            rangerClient.deleteGroupUser( entry.getKey(), user );
                        }
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
                        continue;
                    }

                    //
                    // Delete any Ranger groups that are not in the map of LDAP groups
                    //
                    if( ! ldapGroups.containsKey( vxGroup.getName() ) )
                    {
                        rangerClient.deleteGroup( vxGroup.getId(), vxGroup.getName() );
                    }
                }
            }

            for( var user : users )
            {
                // Create a portal user for each user seen in groups
                rangerClient.createPortalUser( user );

                // Create usergroupinfo for each user seen in groups
                rangerClient.createUserGroupInfo( new VXUserGroupInfo.Builder( user ).description( description ).build() );
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
                    ldapsync( getLDAPConfiguration( configuration ), rangerConfig );
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
            case "scim" ->
            {
                logger.log( Level.SEVERE, "Configuration: '" + Configuration.SOURCE_TYPE + "=scim' is not supported yet" );
            }
            case "aad" ->
            {
                logger.log( Level.SEVERE, "Configuration: '" + Configuration.SOURCE_TYPE + "=aad' is not supported yet" );
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
