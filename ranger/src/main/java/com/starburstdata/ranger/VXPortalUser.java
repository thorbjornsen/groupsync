package com.starburstdata.ranger;

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
