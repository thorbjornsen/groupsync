package com.starburstdata.azure.model;

public class AzureGroup
{
    String id;
    String displayName;

    public String id()
    {
        return this.id;
    }

    public AzureGroup id( String id )
    {
        this.id = id; return this;
    }

    public String displayName()
    {
        return this.displayName;
    }

    public AzureGroup displayName( String displayName )
    {
        this.displayName = displayName; return this;
    }
}
