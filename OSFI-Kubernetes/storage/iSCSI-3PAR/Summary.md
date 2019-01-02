## Quick Summary
* The generic driver supports only static volumes. i.e. tenant needs to request the volume upfront
* CHAP [Challenge Handshake Authentication Protocol] can be configured to provide authentication
* Tenant will not get access to create Persistence Volume
* During static volume provsioning, PV(persistence volume) configuration must contain PVC(persistence volume claim) reference pointing to tenant namespaces
* A volume can be mounted to only on container in Read only or ReadWrite mode
* Same volume can't be mounted in read only mode in different containers
