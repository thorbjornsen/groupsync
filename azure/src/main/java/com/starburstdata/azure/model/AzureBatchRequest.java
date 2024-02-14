package com.starburstdata.azure.model;

public class AzureBatchRequest
{
    String id;
    String url;

    final String method = "GET";

    public String id()
    {
        return this.id;
    }

    public AzureBatchRequest id( String id )
    {
        this.id = id; return this;
    }

    public String url()
    {
        return this.url;
    }

    public AzureBatchRequest url( String url )
    {
        this.url = url; return this;
    }

    public String method()
    {
        return this.method;
    }
}
