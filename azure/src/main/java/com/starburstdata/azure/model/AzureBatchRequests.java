package com.starburstdata.azure.model;

import java.util.Arrays;

public class AzureBatchRequests
{
    AzureBatchRequest[] requests;

    public AzureBatchRequest[] requests()
    {
        return this.requests;
    }

    public AzureBatchRequests requests( AzureBatchRequest[] requests )
    {
        this.requests = requests; return this;
    }

    public AzureBatchRequests append( AzureBatchRequest[] requests )
    {
        // Increase the size of the member array and copy in the objects
        AzureBatchRequest[] result = Arrays.copyOf(this.requests, this.requests.length + requests.length);

        // Copy the obejcts from the parameter to the end of the member array
        System.arraycopy(requests, 0, result, this.requests.length, requests.length);

        // Save the resulting array to the object
        this.requests = result;

        return this;
    }
}
