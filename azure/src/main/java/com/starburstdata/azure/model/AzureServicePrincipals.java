package com.starburstdata.azure.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;

public class AzureServicePrincipals
{
    @SerializedName("@odata.context")
    String odataContext;
    @SerializedName("@odata.nextLink")
    String odataNextLink;
    AzureServicePrincipal[] value;

    public String odataNextLink()
    {
        return this.odataNextLink;
    }

    public AzureServicePrincipal[] value()
    {
        return this.value;
    }

    public AzureServicePrincipal[] users()
    {
        var list = new ArrayList<AzureServicePrincipal>();

        for( var servicePrincipal : value )
        {
            if( "#microsoft.graph.servicePrincipal".equals( servicePrincipal.odataType ) )
            {
                list.add( servicePrincipal );
            }
        }

        return list.toArray( new AzureServicePrincipal[0] );
    }

    public AzureServicePrincipals append(AzureServicePrincipal[] value )
    {
        // Increase the size of the servicePrincipal array and copy in the objects
        AzureServicePrincipal[] result = Arrays.copyOf(this.value, this.value.length + value.length);

        // Copy the objects from the parameter to the end of the servicePrincipal array
        System.arraycopy(value, 0, result, this.value.length, value.length);

        // Save the resulting array to the object
        this.value = result;

        return this;
    }
}
