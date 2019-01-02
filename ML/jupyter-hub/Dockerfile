#
# Centos based jupyter notebook Machine Learning image
# 

ARG CONDA_BASE=docker.optum.com/bd_docid/ml-anaconda3
ARG CONDA_VERSION=1.3


## Build args are not supported by Docker content trust, when using content trust, use absolute values
FROM ${CONDA_BASE}:${CONDA_VERSION}


# Label meta data
ARG BUILD_DATE 
ARG MAINTAINER_DL=DAaaS.Supportuhg.com_DL@ds.uhc.com 
ARG GITHUB_URL="TODO - URL here "

LABEL org.label-schema.maintainer=${MAINTAINER_DL} \
      org.label-schema.description=" Centos based jupyter notebook 3 image for Deep Learning " \
      org.label-schema.name="condapy3dl" \
      org.label-schema.build-date=$BUILD_DATE \
      org.label-schema.url=$GITHUB_URL \
      org.label-schema.vcs-url=$GITHUB_URL \
      org.label-schema.vcs-ref=$GIT_HASH \
      org.label-schema.docker.cmd=$DOCKER_CMD


ENV ANACONDA_DIR=/opt/anaconda3 \
     SHELL=/bin/bash 


ENV ANACONDA_VERSION=3-5.3.0 \
    PATH=$ANACONDA_DIR/bin:$PATH \
    LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8 LANGUAGE=en_US.UTF-8 

# Setup jupyter config/data directories

ENV JUPYTER_CONFIG_DIR=/jupyter JUPYTER_PATH=/jupyter/data JUPYTER_RUNTIME_DIR=/jupyter/data/runtime NB_USER=gpuuser

USER root

RUN mkdir -p $JUPYTER_RUNTIME_DIR \
    && chown -R $NB_USER $JUPYTER_CONFIG_DIR \
    && chmod -R ug+s $JUPYTER_CONFIG_DIR


CMD ["jupyter", "notebook", "--ip=0.0.0.0", "--port=8888", "--no-browser"]

#CMD ["/bin/bash"]
