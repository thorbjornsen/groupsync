package com.starburstdata.ranger;

public class VXGroups
{
    VXGroup[] vXGroups = {};
    int listSize;
    // TODO Array of structs - list
    int startIndex;
    int pageSize;
    int totalCount;
    int resultSize;
    String sortType;
    String sortBy;

    public VXGroup[] getVXGroups()
    {
        return vXGroups;
    }
}
