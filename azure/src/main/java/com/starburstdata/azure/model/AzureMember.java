package com.starburstdata.azure.model;

import com.google.gson.annotations.SerializedName;

public class AzureMember
{
    @SerializedName("@odata.type")
    String odataType;
    @SerializedName("@odata.id")
    String odataId;
    String userPrincipalName;

    public String odataType()
    {
        return this.odataType;
    }

    public String userPrincipalName()
    {
        return this.userPrincipalName;
    }
}
