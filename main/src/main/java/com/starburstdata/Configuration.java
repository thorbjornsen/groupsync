package com.starburstdata;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Matcher.quoteReplacement;

public class Configuration
{
    private final static Logger logger = Logger.getLogger( Configuration.class.getName() );

    private static final Pattern ENV_PATTERN = Pattern.compile("\\$\\{ENV:([a-zA-Z][a-zA-Z0-9_-]*)}");

    public final static String SOURCE_TYPE = "source.type";
    public final static String DEST_TYPE = "destination.type";

    //
    // Common
    //
    public final static String IGNORE_EMPTY_GROUPS = "source.ignore-empty-groups";
    public final static String SYNC_INTERVAL = "sync-interval";

    //
    // LDAP
    //
    public final static String SOURCE_LDAP_HOST = "source.ldap.host";
    public final static String SOURCE_LDAP_PORT = "source.ldap.port";
    public final static String SOURCE_LDAP_BIND = "source.ldap.bind";
    public final static String SOURCE_LDAP_BIND_PASS = "source.ldap.bind.password";
    public final static String SOURCE_LDAP_GROUP_SEARCHBASE = "source.ldap.group.searchbase";
    public final static String SOURCE_LDAP_GROUP_SEARCHSCOPE = "source.ldap.group.searchscope";
    public final static String SOURCE_LDAP_GROUP_OBJECTCLASS = "source.ldap.group.objectclass";
    public final static String SOURCE_LDAP_GROUP_MEMBER_ATTRIBUTE = "source.ldap.group.memberattribute";
    public final static String SOURCE_LDAP_GROUP_NAME_ATTRIBUTE = "source.ldap.group.nameattribute";
    public final static String SOURCE_LDAP_GROUP_SEARCHFILTER = "source.ldap.group.searchfilter";
    public final static String SOURCE_LDAP_GROUP_ATTRIBUTES = "source.ldap.group.attributes";
    public final static String SOURCE_LDAP_USER_NAME_ATTRIBUTE = "source.ldap.user.nameattribute";

    //
    // Active Directory
    //
    public final static String SOURCE_AZURE_TENANT_ID = "source.azure.tenant-id";
    public final static String SOURCE_AZURE_CLIENT_ID = "source.azure.client-id";
    public final static String SOURCE_AZURE_CLIENT_SECRET = "source.azure.client-secret";
    public final static String SOURCE_AZURE_GROUP_FILTER = "source.azure.group-filter";

    //
    // Ranger
    //
    public final static String DEST_RANGER_URI = "destination.ranger.uri";
    public final static String DEST_RANGER_USER = "destination.ranger.user";
    public final static String DEST_RANGER_PASS = "destination.ranger.password";
    public final static String DEST_RANGER_DELETE_USERS = "destination.ranger.delete.users";
    public final static String DEST_RANGER_DELETE_GROUPS = "destination.ranger.delete.groups";
    public final static String DEST_RANGER_UPLOAD_LIMIT = "destination.ranger.upload.limit";
    public final static String DEST_RANGER_DRY_RUN = "destination.ranger.dryrun";

    private final Properties configuration;

    public Configuration()
    {
        var filename = System.getProperty( "config" );

        if( filename == null )
        {
            logger.log( Level.SEVERE, "Configuration: file was not provided" );
            throw new RuntimeException();
        }

        configuration = new Properties();

        try( InputStream is = new FileInputStream( filename ) )
        {
            configuration.load( is );

            //
            // Based on https://github.com/airlift/airlift/blob/master/configuration/src/main/java/io/airlift/configuration/ConfigurationUtils.java#L32
            //

            configuration.forEach(( key, value ) ->
            {
                Matcher matcher = ENV_PATTERN.matcher( value.toString() );
                StringBuilder replacement = new StringBuilder();

                while( matcher.find() )
                {
                    String envName = matcher.group( 1 );
                    String envValue = System.getenv().get( envName );

                    if( envValue == null )
                    {
                        logger.log( Level.SEVERE, "Configuration: Property '"+ key +"' references unset environment variable '"+ envName +"'" );
                        throw new RuntimeException();
                    }

                    matcher.appendReplacement( replacement, quoteReplacement( envValue ) );
                }

                matcher.appendTail( replacement );

                // Replace the configuration value, stays the same if no matches were found
                configuration.replace( key, replacement.toString() );
            });
        }
        catch( IOException ex )
        {
            logger.log( Level.SEVERE, "Configuration: file '" + filename + "' is invalid" );
            logger.log( Level.SEVERE, ex.toString() );
            throw new RuntimeException();
        }

        if( ! configuration.containsKey( SOURCE_TYPE ) )
        {
            logger.log( Level.SEVERE, "Configuration: '" + SOURCE_TYPE + "' is missing" );
            throw new RuntimeException();
        }
    }

    public boolean exists( String key )
    {
        return configuration.containsKey( key );
    }

    public String get( String key )
    {
        return configuration.getProperty( key, "" );
    }
}
