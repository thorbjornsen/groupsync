package com.starburstdata.azure.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;

public class AzureMembers
{
    @SerializedName("@odata.context")
    String odataContext;
    @SerializedName("@odata.nextLink")
    String odataNextLink;
    AzureMember[] value;

    public String odataNextLink()
    {
        return this.odataNextLink;
    }

    public AzureMember[] value()
    {
        return this.value;
    }

    public AzureMember[] users()
    {
        var list = new ArrayList<AzureMember>();

        for( var member : value )
        {
            if( "#microsoft.graph.user".equals( member.odataType ) )
            {
                list.add( member );
            }
        }

        return list.toArray( new AzureMember[0] );
    }

    public AzureMembers append( AzureMember[] value )
    {
        // Increase the size of the member array and copy in the objects
        AzureMember[] result = Arrays.copyOf(this.value, this.value.length + value.length);

        // Copy the obejcts from the parameter to the end of the member array
        System.arraycopy(value, 0, result, this.value.length, value.length);

        // Save the resulting array to the object
        this.value = result;

        return this;
    }
}
