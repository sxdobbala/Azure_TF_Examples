**Creating /data**
- Run ```lsblk``` to figure out free disk. We will assume /dev/sdd    
- Create the VG      
``` 
#  vgcreate datavg /dev/sdd
  Physical volume "/dev/sdd" successfully created.
  Volume group "datavg" successfully created
```    
- Create the LV (We are creating 500G volume)
```
# lvcreate -L 500G -n lv_data datavg
  Logical volume "lv_data" created.
```    
- Create the Filesystem
```
# mkfs.xfs /dev/mapper/datavg-lv_data
meta-data=/dev/mapper/datavg-lv_data isize=512    agcount=16, agsize=8191936 blks
         =                       sectsz=4096  attr=2, projid32bit=1
         =                       crc=1        finobt=0, sparse=0
data     =                       bsize=4096   blocks=131070976, imaxpct=25
         =                       sunit=64     swidth=64 blks
naming   =version 2              bsize=4096   ascii-ci=0 ftype=1
log      =internal log           bsize=4096   blocks=63999, version=2
         =                       sectsz=4096  sunit=1 blks, lazy-count=1
realtime =none                   extsz=4096   blocks=0, rtextents=0
root@dbslp0850:/root
```    
-  Create the directory, add to /etc/fstab and mount it   
```root@dbslp0850:/root
# mkdir /data
root@dbslp0850:/root
# vi /etc/fstab
root@dbslp0850:/root
# grep datavg /etc/fstab
/dev/mapper/datavg-lv_data /data                    xfs     defaults,x-systemd.device-timeout=0 0 0
root@dbslp0850:/root
# mount -a
root@dbslp0850:/root
# df -h /data
Filesystem                  Size  Used Avail Use% Mounted on
/dev/mapper/datavg-lv_data  500G   33M  500G   1% /data
root@dbslp0850:/root
#
```    

