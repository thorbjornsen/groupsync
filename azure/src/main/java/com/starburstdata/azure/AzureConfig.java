package com.starburstdata.azure;

public class AzureConfig
{
    private final String tenantId;
    private final String clientId;
    private final String clientSecret;

    private final boolean ignoreEmptyGroups;

    private AzureConfig( Builder builder )
    {
        this.tenantId = builder.tenantId;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;

        this.ignoreEmptyGroups = builder.ignoreEmptyGroups;
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

    public boolean ignoreEmptyGroups()
    {
        return this.ignoreEmptyGroups;
    }

    public static class Builder
    {
        String tenantId;
        String clientId;
        String clientSecret;

        boolean ignoreEmptyGroups = true;

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

        public Builder ignoreEmptyGroups( String flag )
        {
            this.ignoreEmptyGroups = Boolean.parseBoolean( flag ); return this;
        }

        public AzureConfig build()
        {
            return new AzureConfig( this );
        }
    }
}
