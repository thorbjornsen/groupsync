package com.starburstdata.ldap;

public class LDAPException extends Exception
{
    public LDAPException( String errorMessage )
    {
        super(errorMessage);
    }

    public LDAPException( String errorMessage, Throwable err )
    {
        super(errorMessage, err, false, false);
    }
}
