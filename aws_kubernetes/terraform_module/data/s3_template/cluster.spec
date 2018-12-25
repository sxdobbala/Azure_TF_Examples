metadata:
  creationTimestamp: 2017-12-17T15:21:37Z 
  name: CLUSTER_NAME_PLACE_HOLDER
spec:
  api:
    loadBalancer:
      type: Public
  authorization:
    rbac: {}
  channel: stable
  cloudProvider: aws
  clusterDNSDomain: cluster.local
  configBase: s3://S3_STORE_BUCKET_NAME_PLACE_HOLDER/CLUSTER_NAME_PLACE_HOLDER
  configStore: s3://S3_STORE_BUCKET_NAME_PLACE_HOLDER/CLUSTER_NAME_PLACE_HOLDER
  dnsZone: DNS_ZONE_PLACE_HOLDER
  spec:
  egressProxy:
    httpProxy:
      host: EGRESS_PROXY_HOST_PLACE_HOLDER 
      port: EGRESS_PROXY_PORT_PLACE_HOLDER 
    excludes: 169.254.169.254,localhost,127.0.0.1,api.CLUSTER_NAME_PLACE_HOLDER,CLUSTER_NAME_PLACE_HOLDER,100.64.0.1,100.64.0.0/10,.svc,VPC_CIDR_PLACE_HOLDER
  docker:
    ipMasq: false
    ipTables: false
    logDriver: json-file
    logLevel: warn
    logOpt:
    - max-size=10m
    - max-file=5
    storage: overlay,aufs
    version: 17.03.2
  etcdClusters: [{"etcdMembers":INSTANCE_GROUP_PLACE_HOLDER,"name":"main","version":"3.1.17","image":"k8s.gcr.io/etcd:3.1.17"}, {"etcdMembers":INSTANCE_GROUP_PLACE_HOLDER,"name":"events","version":"3.1.17","image":"k8s.gcr.io/etcd:3.1.17"}]
  iam:
    allowContainerRegistry: true
    legacy: false
  keyStore: s3://S3_STORE_BUCKET_NAME_PLACE_HOLDER/CLUSTER_NAME_PLACE_HOLDER/pki
  kubeAPIServer:
    allowPrivileged: true
    anonymousAuth: false
    apiServerCount: 3
    authorizationMode: RBAC
    bindAddress: 0.0.0.0
    cloudProvider: aws
    enableAdmissionPlugins:
    - Initializers
    - NamespaceLifecycle
    - LimitRanger
    - ServiceAccount
    - PersistentVolumeLabel
    - DefaultStorageClass
    - DefaultTolerationSeconds
    - MutatingAdmissionWebhook
    - ValidatingAdmissionWebhook
    - NodeRestriction
    - ResourceQuota
    etcdQuorumRead: false
    auditLogPath: /var/log/kube-apiserver-audit.log
    auditLogMaxAge: 10
    auditLogMaxBackups: 1
    auditLogMaxSize: 100
    auditPolicyFile: /srv/kubernetes/audit.conf
    etcdServers:
    - http://127.0.0.1:4001
    etcdServersOverrides:
    - /events#http://127.0.0.1:4002
    image: k8s.gcr.io/kube-apiserver:v1.10.11
    insecureBindAddress: 127.0.0.1
    insecurePort: 8080
    kubeletPreferredAddressTypes:
    - InternalIP
    - Hostname
    - ExternalIP
    logLevel: 2
    requestheaderAllowedNames:
    - aggregator
    requestheaderExtraHeaderPrefixes:
    - X-Remote-Extra-
    requestheaderGroupHeaders:
    - X-Remote-Group
    requestheaderUsernameHeaders:
    - X-Remote-User
    securePort: 443
    serviceClusterIPRange: 100.64.0.0/13
    storageBackend: etcd2
  kubeControllerManager:
    allocateNodeCIDRs: true
    attachDetachReconcileSyncPeriod: 1m0s
    cloudProvider: aws
    clusterCIDR: 100.96.0.0/11
    clusterName: CLUSTER_NAME_PLACE_HOLDER
    configureCloudRoutes: false
    image: k8s.gcr.io/kube-controller-manager:v1.10.11
    leaderElection:
      leaderElect: true
    logLevel: 2
    useServiceAccountCredentials: true
  kubeDNS:
    cacheMaxConcurrent: 150
    cacheMaxSize: 1000
    domain: cluster.local
    replicas: 2
    serverIP: 100.64.0.10
  kubeProxy:
    clusterCIDR: 100.96.0.0/11
    cpuRequest: 100m
    hostnameOverride: '@aws'
    image: k8s.gcr.io/kube-proxy:v1.10.11
    logLevel: 2
  kubeScheduler:
    image: k8s.gcr.io/kube-scheduler:v1.10.11
    leaderElection:
      leaderElect: true
    logLevel: 2
  kubelet:
    allowPrivileged: true
    cgroupRoot: /
    cloudProvider: aws
    clusterDNS: 100.64.0.10
    clusterDomain: cluster.local
    enableDebuggingHandlers: true
    evictionHard: memory.available<100Mi,nodefs.available<10%,nodefs.inodesFree<5%,imagefs.available<10%,imagefs.inodesFree<5%
    featureGates:
      ExperimentalCriticalPodAnnotation: "true"
    hostnameOverride: '@aws'
    kubeconfigPath: /var/lib/kubelet/kubeconfig
    logLevel: 2
    networkPluginName: cni
    nonMasqueradeCIDR: 100.64.0.0/10
    podInfraContainerImage: k8s.gcr.io/pause-amd64:3.0
    podManifestPath: /etc/kubernetes/manifests
    kubeReserved:
        cpu: "300m"
        memory: "1000Mi"
    systemReserved:
        cpu: "200m"
        memory: "1000Mi"
    enforceNodeAllocatable: "pods"
  kubernetesApiAccess:
  - 0.0.0.0/0
  kubernetesVersion: 1.10.11
  masterInternalName: api.internal.CLUSTER_NAME_PLACE_HOLDER
  masterKubelet:
    allowPrivileged: true
    cgroupRoot: /
    cloudProvider: aws
    clusterDNS: 100.64.0.10
    clusterDomain: cluster.local
    enableDebuggingHandlers: true
    evictionHard: memory.available<100Mi,nodefs.available<10%,nodefs.inodesFree<5%,imagefs.available<10%,imagefs.inodesFree<5%
    featureGates:
      ExperimentalCriticalPodAnnotation: "true"
    hostnameOverride: '@aws'
    kubeconfigPath: /var/lib/kubelet/kubeconfig
    logLevel: 2
    networkPluginName: cni
    nonMasqueradeCIDR: 100.64.0.0/10
    podInfraContainerImage: k8s.gcr.io/pause-amd64:3.0
    podManifestPath: /etc/kubernetes/manifests
    registerSchedulable: false
    kubeReserved:
        cpu: "300m"
        memory: "1000Mi"
    systemReserved:
        cpu: "200m"
        memory: "1000Mi"
    enforceNodeAllocatable: "pods"
  masterPublicName: api.CLUSTER_NAME_PLACE_HOLDER
  networkCIDR: VPC_CIDR_PLACE_HOLDER 
  networkID: VPC_ID_PLACE_HOLDER
  networking:
    calico: {}
  nonMasqueradeCIDR: 100.64.0.0/10
  secretStore: s3://S3_STORE_BUCKET_NAME_PLACE_HOLDER/CLUSTER_NAME_PLACE_HOLDER/secrets
  serviceClusterIPRange: 100.64.0.0/13
  subnets: [ SUBNET_LIST_PLACE_HOLDER ]
  topology:
    dns:
      type: Public
    masters: private
    nodes: private
  fileAssets:
  - name: audit.conf
    path: /srv/kubernetes/audit.conf
    roles: [Master,Node]
    content: |
      apiVersion: audit.k8s.io/v1beta1
      kind: Policy
      rules:
      - level: Metadata
