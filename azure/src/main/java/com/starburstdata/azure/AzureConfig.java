package com.starburstdata.azure;

public class AzureConfig
{
    private final String tenantId;
    private final String clientId;
    private final String clientSecret;
    private final String[] groupFilter;

    private final boolean createEmptyGroups;
    private final boolean syncServicePrincipals;
    private final int syncInterval;

    private AzureConfig( Builder builder )
    {
        this.tenantId = builder.tenantId;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.groupFilter = builder.groupFilter;

        this.createEmptyGroups = builder.createEmptyGroups;
        this.syncServicePrincipals = builder.syncServicePrincipals;
        this.syncInterval = builder.syncInterval;
    }

    public String tenantId()
    {
        return this.tenantId;
    }

    public String clientId()
    {
        return this.clientId;
    }

    public String clientSecret()
    {
        return this.clientSecret;
    }

    public String[] groupFilter()
    {
        return this.groupFilter;
    }

    public boolean createEmptyGroups()
    {
        return this.createEmptyGroups;
    }

    public boolean syncServicePrincipals()
    {
        return this.syncServicePrincipals;
    }

    public int syncInterval()
    {
        return this.syncInterval;
    }

    public static class Builder
    {
        String tenantId;
        String clientId;
        String clientSecret;
        String[] groupFilter = {};

        boolean createEmptyGroups = false;
        boolean syncServicePrincipals = false;
        int syncInterval = 0;

        public AzureConfig.Builder tenantId( String tenantId )
        {
            this.tenantId = tenantId; return this;
        }

        public AzureConfig.Builder clientId( String clientId )
        {
            this.clientId = clientId; return this;
        }

        public AzureConfig.Builder clientSecret( String clientSecret )
        {
            this.clientSecret = clientSecret; return this;
        }

        public AzureConfig.Builder groupFilter( String filter )
        {
            this.groupFilter = filter.split( ";" ); return this;
        }

        public AzureConfig.Builder createEmptyGroups( String flag )
        {
            this.createEmptyGroups = Boolean.parseBoolean( flag ); return this;
        }
        public AzureConfig.Builder syncServicePrincipals( String flag )
        {
            this.syncServicePrincipals = Boolean.parseBoolean( flag ); return this;
        }

        public AzureConfig.Builder syncInterval( String value )
        {
            this.syncInterval = Integer.parseInt( value ); return this;
        }

        public AzureConfig build()
        {
            return new AzureConfig( this );
        }
    }
}
