package com.starburstdata.ranger;

import java.net.URI;
import java.net.URISyntaxException;

public class RangerConfig
{
    private final URI uri;
    private final String user;
    private final String password;

    private final boolean deleteUsers;
    private final boolean deleteGroups;
    private final boolean dryrun;

    private RangerConfig( RangerConfig.Builder builder )
    {
        this.uri = builder.uri;
        this.user = builder.user;
        this.password = builder.password;

        this.deleteUsers = builder.deleteUsers;
        this.deleteGroups = builder.deleteGroups;
        this.dryrun = builder.dryrun;
    }

    public URI uri()
    {
        return this.uri;
    }

    public String user()
    {
        return this.user;
    }

    public String password()
    {
        return this.password;
    }

    public boolean deleteUsers()
    {
        return this.deleteUsers;
    }

    public boolean deleteGroups()
    {
        return this.deleteGroups;
    }

    public boolean dryrun()
    {
        return this.dryrun;
    }

    public static class Builder
    {
        URI uri;
        String user;
        String password;

        boolean deleteUsers = true;
        boolean deleteGroups = true;
        boolean dryrun = false;

        public Builder( String uri ) throws URISyntaxException
        {
            this.uri = new URI( uri );
        }

        public RangerConfig.Builder credentials( String user, String password )
        {
            //
            // Ensure the value is empty regardless if it's empty or null
            //

            if( user != null && user.isEmpty() )
            {
                this.user = "";
            }
            else
            {
                this.user = user;
            }

            if( password != null && password.isEmpty() )
            {
                this.password = "";
            }
            else
            {
                this.password = password;
            }

            return this;
        }

        public Builder deleteUsers( String flag )
        {
            this.deleteUsers = Boolean.parseBoolean( flag ); return this;
        }

        public Builder deleteGroups( String flag )
        {
            this.deleteGroups = Boolean.parseBoolean( flag ); return this;
        }

        public Builder dryrun( String flag )
        {
            this.dryrun = Boolean.parseBoolean( flag ); return this;
        }

        public RangerConfig build()
        {
            return new RangerConfig( this );
        }
    }
}
