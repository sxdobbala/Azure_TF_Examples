#
# Centos based Python3 Machine Learning image
# 

ARG centos_version=7


## Build args are not supported by Docker content trust, when using content trust, use absolute values
FROM centos:${centos_version}


# Label meta data
ARG BUILD_DATE
ARG MAINTAINER_DL=DAaaS.Supportuhg.com_DL@ds.uhc.com
ARG GITHUB_URL="TODO - URL here "

LABEL org.label-schema.maintainer=${MAINTAINER_DL}
LABEL org.label-schema.description=" Centos based Python 3 image for Deep Learning "
LABEL org.label-schema.name="condapy3dl"
LABEL org.label-schema.build-date=$BUILD_DATE 
LABEL org.label-schema.url=$GITHUB_URL
LABEL org.label-schema.vcs-url=$GITHUB_URL
LABEL org.label-schema.vcs-ref=$GIT_HASH
LABEL org.label-schema.docker.cmd=$DOCKER_CMD

USER root

# Install Anaconda dependencies for Centos
RUN yum update -y \
    && yum -y install bzip2 \
    && yum clean all \ 
    && rm -rf /var/cache/yum


ENV NB_USER=gpuuser \
     NB_UID=1000 \
     NB_GID=100 \
     ANACONDA_DIR=/opt/anaconda3 \
     SHELL=/bin/bash 

# Add gpguser with UID=1000 and GID=100

RUN useradd -m -s $SHELL -N -u $NB_UID $NB_USER \
    && mkdir $ANACONDA_DIR \
    && chown $NB_USER $ANACONDA_DIR 

ENV ANACONDA_VERSION=3-5.3.0 \
    PATH=$ANACONDA_DIR/bin:$PATH \
    ANACONDA_SHA=cfbf5fe70dd1b797ec677e63c61f8efc92dad930fd1c94d60390bb07fdc09959 \
    LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8 LANGUAGE=en_US.UTF-8 

USER $NB_USER

RUN cd $HOME \
    && curl -O https://repo.continuum.io/archive/Anaconda$ANACONDA_VERSION-Linux-x86_64.sh \
    && sha256sum Anaconda$ANACONDA_VERSION-Linux-x86_64.sh | grep $ANACONDA_SHA \
    && chmod u+x Anaconda$ANACONDA_VERSION-Linux-x86_64.sh \
    && bash Anaconda$ANACONDA_VERSION-Linux-x86_64.sh -bup $ANACONDA_DIR \
    && rm Anaconda$ANACONDA_VERSION-Linux-x86_64.sh \
    && conda list 

CMD ["/bin/bash"]
