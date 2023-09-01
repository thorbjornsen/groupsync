package com.starburstdata.ranger.model;

public class VXPortalUser
{
    String loginId;
    String emailAddress;
    String firstName;
    String lastName;
    int userSource;
    int id;

    private VXPortalUser( Builder builder )
    {
        this.loginId = builder.loginId;
    }

    public String loginId()
    {
        return this.loginId;
    }

    public boolean isExternal()
    {
        return this.userSource == 1;
    }

    public int id()
    {
        return this.id;
    }

    public static class Builder
    {
        String loginId;

        public Builder( String loginId )
        {
            this.loginId = loginId;
        }

        public VXPortalUser build()
        {
            return new VXPortalUser( this );
        }
    }
}
