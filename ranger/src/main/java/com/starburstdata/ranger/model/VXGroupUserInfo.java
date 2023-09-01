package com.starburstdata.ranger.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VXGroupUserInfo
{
    Date createDate;
    Date updateDate;
    VXGroup xgroupInfo;
    VXUser[] xuserInfo = {};

    private VXGroupUserInfo( Builder builder )
    {
        this.xgroupInfo = builder.xgroupInfo;

        if( ! builder.xuserInfo.isEmpty() )
        {
            this.xuserInfo = builder.xuserInfo.toArray( new VXUser[0] );
        }
    }

    public List<String> getUsers()
    {
        List<String> users = new ArrayList<>();

        if( xuserInfo != null )
        {
            for( var user : xuserInfo )
            {
                users.add( user.name );
            }
        }

        return users;
    }

    public List<String> containsUsers( VXGroupUserInfo comp )
    {
        List<String> users = new ArrayList<>();

        for( var user : xuserInfo )
        {
            users.add( user.name );
        }

        List<String> remove = new ArrayList<>();

        if( comp.xuserInfo != null )
        {
            for( var user : comp.xuserInfo )
            {
                remove.add( user.name );
            }

            users.removeAll( remove );
        }

        return users;
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        VXGroupUserInfo comp = (VXGroupUserInfo)o;

        if( comp.xgroupInfo == null || comp.xuserInfo == null )
        {
            return false;
        }

        if( xuserInfo.length != comp.xuserInfo.length )
        {
            return false;
        }


        return  xgroupInfo.name.equalsIgnoreCase( comp.xgroupInfo.name ) &&
                xgroupInfo.isVisible == comp.xgroupInfo.isVisible &&
                xgroupInfo.groupType == comp.xgroupInfo.groupType &&
                xgroupInfo.groupSource == comp.xgroupInfo.groupSource;
    }

    public static class Builder
    {
        VXGroup xgroupInfo;
        List<VXUser> xuserInfo = new ArrayList<>();

        public Builder( String groupName )
        {
            xgroupInfo = new VXGroup();

            xgroupInfo.name = groupName;

            xgroupInfo.groupSource = 1;
            xgroupInfo.groupType = 1;
            xgroupInfo.isVisible = 1;
        }

        public VXGroupUserInfo.Builder groupName( String name )
        {
            xgroupInfo.name = name; return this;
        }

        public VXGroupUserInfo.Builder groupDescription( String description )
        {
            xgroupInfo.description = description; return this;
        }

        public VXGroupUserInfo.Builder addUser( String name )
        {
            xuserInfo.add( new VXUser.Builder( name ).build() ); return this;
        }

        public VXGroupUserInfo build()
        {
            return new VXGroupUserInfo( this );
        }
    }
}
