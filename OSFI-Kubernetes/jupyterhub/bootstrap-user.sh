#!/bin/sh
set -e
if getent passwd $USER_ID > /dev/null ; then
  echo "$JUPYTERHUB_USER ($USER_ID) exists"
else
  echo "Creating user $JUPYTERHUB_USER with $USER_ID"
  useradd -u $USER_ID $JUPYTERHUB_USER
fi

if getent passwd $GROUP_ID > /dev/null ; then
  echo "$JUPYTERHUB_USER ($GROUP_ID) exists"
else
  echo "Creating group $JUPYTERHUB_USER ($GROUP_ID)"
  groupadd -g $GROUP_ID $JUPYTERHUB_USER
  usermod -a -G $JUPYTERHUB_USER $JUPYTERHUB_USER
fi