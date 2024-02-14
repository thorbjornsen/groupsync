package com.starburstdata.azure.model;

public class AzureBatchResponse
{
    String id;
    int status;
    Body body;

    public String id()
    {
        return this.id;
    }

    public int status()
    {
        return this.status;
    }

    public Body body()
    {
        return this.body;
    }

    public static class Body
    {
        Value[] value;

        public Value[] value()
        {
            return this.value;
        }
    }

    public static class Value
    {
        String id;
        String displayName;

        public String id()
        {
            return this.id;
        }

        public Value id( String id )
        {
            this.id = id; return this;
        }

        public String displayName()
        {
            return this.displayName;
        }

        public Value displayName( String displayName )
        {
            this.displayName = displayName; return this;
        }
    }

    /*
           {
            "id": "12",
            "status": 200,
            "headers": {
                "Cache-Control": "no-cache",
                "x-ms-resource-unit": "1",
                "OData-Version": "4.0",
                "Content-Type": "application/json;odata.metadata=minimal;odata.streaming=true;IEEE754Compatible=false;charset=utf-8"
            },
            "body": {
                "@odata.context": "https://graph.microsoft.com/v1.0/$metadata#groups",
                "value": [
                    {
                        "id": "b9d9564a-1fde-4eb2-a63e-cf5cadf57ef0",
                        "deletedDateTime": null,
                        "classification": null,
                        "createdDateTime": "2024-01-30T21:00:11Z",
                        "creationOptions": [],
                        "description": "Group including an SP",
                        "displayName": "FG SP Group",
                        "expirationDateTime": null,
                        "groupTypes": [],
                        "isAssignableToRole": null,
                        "mail": null,
                        "mailEnabled": false,
                        "mailNickname": "e92300ff-9",
                        "membershipRule": null,
                        "membershipRuleProcessingState": null,
                        "onPremisesDomainName": null,
                        "onPremisesLastSyncDateTime": null,
                        "onPremisesNetBiosName": null,
                        "onPremisesSamAccountName": null,
                        "onPremisesSecurityIdentifier": null,
                        "onPremisesSyncEnabled": null,
                        "preferredDataLocation": null,
                        "preferredLanguage": null,
                        "proxyAddresses": [],
                        "renewedDateTime": "2024-01-30T21:00:11Z",
                        "resourceBehaviorOptions": [],
                        "resourceProvisioningOptions": [],
                        "securityEnabled": true,
                        "securityIdentifier": "S-1-12-1-3118028362-1320296414-1557085862-4034852269",
                        "theme": null,
                        "visibility": null,
                        "onPremisesProvisioningErrors": [],
                        "serviceProvisioningErrors": []
                    }
                ]
            }
        },
     */
}
