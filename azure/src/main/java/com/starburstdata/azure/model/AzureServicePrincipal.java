package com.starburstdata.azure.model;

import com.google.gson.annotations.SerializedName;

public class AzureServicePrincipal
{
    @SerializedName("@odata.type")
    String odataType;
    @SerializedName("@odata.id")
    String odataId;
    String id;
    String displayName;

    public String odataType()
    {
        return this.odataType;
    }

    public String id()
    {
        return this.id;
    }

    public String displayName()
    {
        return this.displayName;
    }
}
