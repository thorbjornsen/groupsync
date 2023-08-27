package com.starburstdata.ranger;

public class RangerException extends Exception
{
    public RangerException( String errorMessage )
    {
        super(errorMessage);
    }

    public RangerException( String errorMessage, Throwable err )
    {
        super(errorMessage, err, false, false);
    }
}
