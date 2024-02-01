package com.starburstdata.azure.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class AzureGroups
{
    @SerializedName("@odata.context")
    String odataContext;
    @SerializedName("@odata.nextLink")
    String odataNextLink;
    AzureGroup[] value;

    public String odataNextLink()
    {
        return this.odataNextLink;
    }

    public AzureGroup[] value()
    {
        return this.value;
    }

    public AzureGroups append( AzureGroup[] value )
    {
        // Increase the size of the member array and copy in the objects
        AzureGroup[] result = Arrays.copyOf(this.value, this.value.length + value.length);

        // Copy the obejcts from the parameter to the end of the member array
        System.arraycopy(value, 0, result, this.value.length, value.length);

        // Save the resulting array to the object
        this.value = result;

        return this;
    }
}
