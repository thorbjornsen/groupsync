package com.starburstdata;

import com.starburstdata.ranger.RangerClient;
import com.starburstdata.ranger.RangerException;

public class Main
{
    public static void main( String[] args )
    {
        RangerClient client = new RangerClient.Builder( "http://localhost:6080" ).credentials("admin","Starburst4Me").build();

        //RangerClient client = new RangerClient.Builder( "https://postman-echo.com/basic-auth" ).user("postman").password("password").build();

        try
        {
            client.createPortalUser( "test@groupsync.com" );

            //VXGroupUserInfo info = new VXGroupUserInfo();



            //client.createGroupInfo( info );
        }
        catch( RangerException ex )
        {
            System.out.println( ex.toString() );
        }



        /*
        try
        {
            client.createUserGroupInfo( "test@groupsync.com", "Created from ranger test" );
        }
        catch( RangerException ex )
        {
            System.out.println( ex.toString() );
        }

        try
        {
            var groups = client.getGroups();
        }
        catch( RangerException ex )
        {
            System.out.println( ex.toString() );
        }

        try
        {
            var info = client.getGroupUsers( "" );
        }
        catch( RangerException ex )
        {
            System.out.println( ex.toString() );
        }
        */
    }
}
