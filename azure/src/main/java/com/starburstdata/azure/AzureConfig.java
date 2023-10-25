package com.starburstdata.azure;

public class AzureConfig
{
    private final String tenantId;
    private final String clientId;
    private final String clientSecret;
    private final String[] groupFilter;

    private final boolean ignoreEmptyGroups;
    private final int syncInterval;

    private AzureConfig( Builder builder )
    {
        this.tenantId = builder.tenantId;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.groupFilter = builder.groupFilter;

        this.ignoreEmptyGroups = builder.ignoreEmptyGroups;
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
        String tenantId;
        String clientId;
        String clientSecret;
        String[] groupFilter = {};

        boolean ignoreEmptyGroups = true;
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

        public AzureConfig.Builder ignoreEmptyGroups( String flag )
        {
            this.ignoreEmptyGroups = Boolean.parseBoolean( flag ); return this;
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
