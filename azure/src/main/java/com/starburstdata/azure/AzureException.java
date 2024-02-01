package com.starburstdata.azure;

public class AzureException extends Exception
{
    public AzureException( String errorMessage )
    {
        super(errorMessage);
    }

    public AzureException( String errorMessage, Throwable err )
    {
        super(errorMessage, err, false, false);
    }
}
