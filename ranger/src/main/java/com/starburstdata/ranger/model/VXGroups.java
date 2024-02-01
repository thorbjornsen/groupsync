package com.starburstdata.ranger.model;

public class VXGroups
{
    int startIndex;
    int pageSize;
    int totalCount;
    int resultSize;
    VXGroup[] vXGroups = {};

    public int pageSize()
    {
        return this.pageSize;
    }

    public int totalCount()
    {
        return this.totalCount;
    }

    public int resultSize()
    {
        return this.resultSize;
    }

    public VXGroup[] getVXGroups()
    {
        return vXGroups;
    }
}
