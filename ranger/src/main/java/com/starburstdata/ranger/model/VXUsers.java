package com.starburstdata.ranger.model;

import java.util.Arrays;

public class VXUsers
{
    int startIndex;
    int pageSize;
    int totalCount;
    int resultSize;
    VXUser[] vXUsers = {};

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

    public VXUser[] users()
    {
        return this.vXUsers;
    }

    public VXUsers append( VXUsers users )
    {
        // Increase the size of the member array and copy in the objects
        VXUser[] result = Arrays.copyOf(this.vXUsers, this.vXUsers.length + users.vXUsers.length);

        // Copy the obejcts from the parameter to the end of the member array
        System.arraycopy(users.vXUsers, 0, result, this.vXUsers.length, users.vXUsers.length);

        // Save the resulting array to the object
        this.vXUsers = result;

        return this;
    }
}
