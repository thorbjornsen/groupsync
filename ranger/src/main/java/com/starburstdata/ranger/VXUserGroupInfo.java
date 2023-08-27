package com.starburstdata.ranger;

public class VXUserGroupInfo
{
    XUserInfo xuserInfo;
    XGroupInfo[] xgroupInfo = {};

    private VXUserGroupInfo( Builder builder )
    {
        xuserInfo = new XUserInfo();

        xuserInfo.name = builder.name;

        if( builder.description != null )
        {
            xuserInfo.description = builder.description;
        }
    }

    public static class Builder
    {
        String name;
        String description;

        public Builder( String name )
        {
            this.name = name;
        }

        public VXUserGroupInfo.Builder description( String description )
        {
            this.description = description; return this;
        }

        public VXUserGroupInfo build()
        {
            return new VXUserGroupInfo( this );
        }
    }
}
