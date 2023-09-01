package com.starburstdata.ranger.model;

import java.util.Date;

public class VXUser
{
    int id;
    String name;
    int status;
    int isVisible;
    int userSource;
    Date createDate;
    Date updateDate;
    String[] groupNameList = {};
    String[] userRoleList = {};

    public int id()
    {
        return this.id;
    }

    public String name()
    {
        return this.name;
    }

    public boolean isExternal()
    {
        return this.userSource == 1;
    }

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
