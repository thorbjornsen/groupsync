package com.starburstdata.ranger;

import java.util.Date;

public class VXUser
{
    String name;
    int status;
    int isVisible;
    int userSource;
    Date createDate;
    Date updateDate;
    String[] groupNameList = {};
    String[] userRoleList = {};

    private VXUser( Builder builder )
    {
        this.name = builder.name;

        this.isVisible = builder.isVisible;
        this.userSource = builder.userSource;
    }

    public static class Builder
    {
        String name;
        int isVisible = 1;
        int userSource = 0;

        public Builder( String name )
        {
            this.name = name;
        }

        public VXUser build()
        {
            return new VXUser( this );
        }
    }
}
