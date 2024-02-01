package com.starburstdata.ldap;

import com.unboundid.ldap.sdk.SearchScope;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LDAPConfig
{
    //
    // Connection
    //
    private final String host;
    private final int port;
    private final String bind;
    private final String password;
    private int timeout = 0;

    //
    // Search
    //
    private final String groupSearchBase;
    private final String groupObjectClass;
    private final String groupMemberAttribute;
    private final String groupNameAttribute;
    private final String userNameAttribute;

    private final SearchScope groupSearchScope;
    private final String groupSearchFilter;
    private final String[] groupAttributes;

    //
    // Misc
    //
    private final boolean ignoreEmptyGroups;
    private final int syncInterval;

    private String userObjectClass;


    private LDAPConfig( LDAPConfig.Builder builder )
    {
        this.host = builder.address;
        this.port = builder.port;
        this.bind = builder.bind;
        this.password = builder.password;

        this.groupSearchBase = builder.groupSearchBase;
        this.groupObjectClass = builder.groupObjectClass;
        this.groupMemberAttribute = builder.groupMemberAttribute;
        this.groupNameAttribute = builder.groupNameAttribute;
        this.userNameAttribute = builder.userNameAttribute;

        this.groupSearchScope = builder.groupSearchScope;
        this.groupSearchFilter = builder.groupSearchFilter;
        this.groupAttributes = builder.groupAttributes;

        this.ignoreEmptyGroups = builder.ignoreEmptyGroups;
        this.syncInterval = builder.syncInterval;
    }

    public String host()
    {
        return this.host;
    }

    public int port()
    {
        return this.port;
    }

    public String bind()
    {
        return this.bind;
    }

    public String password()
    {
        return this.password;
    }

    public String groupSearchBase()
    {
        return this.groupSearchBase;
    }

    public String groupObjectClass()
    {
        return this.groupObjectClass;
    }

    public String groupMemberAttribute()
    {
        return this.groupMemberAttribute;
    }

    public String groupNameAttribute()
    {
        return this.groupNameAttribute;
    }

    public String userNameAttribute()
    {
        return this.userNameAttribute;
    }

    public SearchScope groupSearchScope()
    {
        return this.groupSearchScope;
    }

    public String groupSearchFilter()
    {
        return this.groupSearchFilter;
    }

    public String[] groupAttributes()
    {
        return this.groupAttributes;
    }

    public boolean ignoreEmptyGroups()
    {
        return this.ignoreEmptyGroups;
    }

    public int syncInterval()
    {
        return this.syncInterval;
    }

    public static class Builder
    {
        String address;
        int port;
        String bind;
        String password;
        String groupSearchBase;
        String groupObjectClass;
        String groupMemberAttribute;
        String groupNameAttribute;
        String userNameAttribute;

        SearchScope groupSearchScope = SearchScope.SUB;
        String groupSearchFilter = null;
        String[] groupAttributes = null;

        boolean ignoreEmptyGroups = true;
        int syncInterval = 0;

        public Builder( String address, String port ) throws LDAPException
        {
            address( address );

            try
            {
                this.port = Integer.parseInt( port );
            }
            catch( NumberFormatException ex )
            {
                throw new LDAPException( ex.getMessage() );
            }
        }

        private void address( String address ) throws LDAPException
        {
            // Assume it's a hostname and go from there
            getByHost( address );
        }

        private void getByHost( String host ) throws LDAPException
        {
            try
            {
                this.address = InetAddress.getByName( host ).getHostName();
            }
            catch( UnknownHostException ex )
            {
                getByAddress( host );
            }
        }

        private void getByAddress( String address ) throws LDAPException
        {
            try
            {
                this.address = InetAddress.getByAddress( address.getBytes() ).getHostAddress();
            }
            catch( UnknownHostException ex )
            {
                throw new LDAPException( "Invalid inet address", ex );
            }
        }

        public Builder bind( String bind )
        {
            this.bind = bind; return this;
        }

        public Builder password( String password )
        {
            this.password = password; return this;
        }

        public Builder groupSearchBase( String base )
        {
            this.groupSearchBase = base; return this;
        }

        public Builder groupObjectClass( String clazz )
        {
            this.groupObjectClass = clazz; return this;
        }

        public Builder groupMemberAttribute( String attribute )
        {
            this.groupMemberAttribute = attribute; return this;
        }

        public Builder groupNameAttribute( String attribute )
        {
            this.groupNameAttribute = attribute; return this;
        }

        public Builder userNameAttribute( String attribute )
        {
            this.userNameAttribute = attribute; return this;
        }

        public Builder groupSearchScope( String scope ) throws LDAPException
        {
            switch( scope.toLowerCase() )
            {
                case "base" ->
                {
                    groupSearchScope = SearchScope.BASE;
                }
                case "one" ->
                {
                    groupSearchScope = SearchScope.ONE;
                }
                case "sub" ->
                {
                    groupSearchScope = SearchScope.SUB;
                }
                case "subtree" ->
                {
                    groupSearchScope = SearchScope.SUBORDINATE_SUBTREE;
                }
                default ->
                {
                    throw new LDAPException( scope + " is not supported" );
                }
            }

            return this;
        }

        public Builder groupSearchFilter( String filter )
        {
            this.groupSearchFilter = filter; return this;
        }

        public Builder groupAttributes( String attributes )
        {
            this.groupAttributes = attributes.split( "," ); return this;
        }

        public Builder ignoreEmptyGroups( String flag )
        {
            this.ignoreEmptyGroups = Boolean.parseBoolean( flag ); return this;
        }

        public Builder syncInterval( String value )
        {
            this.syncInterval = Integer.parseInt( value ); return this;
        }

        public LDAPConfig build()
        {
            return new LDAPConfig( this );
        }
    }
}
