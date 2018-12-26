To regenerate a new SAS Token:

1) Via Portal, navigate to 'commercial-cloud-storage' Resource Group, then to 'optumcc' Storage Account.
2) In the left-side menu under 'Settings', click 'Shared access signature'
3) Select the following options:

     Allowed services: Blob
     Allowed resource types: Object
     Allowed permissions: Read
     Start: (today's date)
     End: (+1 year from today)
     Allowed protocols: HTTPS only
     Signing key: key1

4) Then click 'Generate SAS and connection string'
5) Copy the SAS Token - don't include the leading '?' if there is one
