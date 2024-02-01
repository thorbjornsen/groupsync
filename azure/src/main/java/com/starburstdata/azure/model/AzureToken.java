package com.starburstdata.azure.model;

import com.google.gson.annotations.SerializedName;

public class AzureToken
{
    @SerializedName("access_token")
    String accessToken;
    @SerializedName("token_type")
    String tokenType;
    @SerializedName("expires_in")
    int expiresIn;

    public String getAccessToken()
    {
        return this.accessToken;
    }
}
