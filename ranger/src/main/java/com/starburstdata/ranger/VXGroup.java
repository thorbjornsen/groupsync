package com.starburstdata.ranger;

import java.util.Date;

public class VXGroup
{
    String name;
    String description;
    int groupType;
    int isVisible;
    int myClassType;
    int groupSource;
    int id;
    Date createDate;
    Date updateDate;
    String owner;
    String updatedBy;

    public String getName()
    {
        return name;
    }

    public int getGroupSource()
    {
        return groupSource;
    }

    public int getId()
    {
        return id;
    }
}
