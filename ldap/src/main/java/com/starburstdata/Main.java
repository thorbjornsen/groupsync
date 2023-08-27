package com.starburstdata;

import com.starburstdata.ldap.LDAPClient;
import com.starburstdata.ldap.LDAPException;

public class Main
{
    public static void main( String[] args )
    {
        try
        {
            new LDAPClient().connect( "badhost", 1234 );
        }
        catch( LDAPException ex )
        {
            System.out.println( ex );
        }

        try
        {
            new LDAPClient().connect( "1234.12.12", 1234 );
        }
        catch( LDAPException ex )
        {
            System.out.println( ex );
        }

        try
        {
            var client = new LDAPClient();
            client.connect( "ldap.forumsys.com", 389 );
            client.bind( "cn=read-only-admin,dc=example,dc=com","bad" );
        }
        catch( LDAPException ex )
        {
            System.out.println( ex );
        }

        try
        {
            //
            // sa.fieldeng.starburstdata.net
            // 3268
            //

            var client = new LDAPClient();

            client.connect( "ldap.forumsys.com", 389 );
            // BindDN, Bind password
            client.bind( "cn=read-only-admin,dc=example,dc=com","password" );

            //var groups = client.getGroups();

            client.disconnect();
        }
        catch( LDAPException ex )
        {
            System.out.println( ex );
        }

    }
}
