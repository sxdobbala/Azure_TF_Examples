Choose the appropriate Base image

EX:
```
FROM docker.optum.com/bd_docid/bdpaas_jupyter_pathfinder:elrsingle

```

Install new components

Ex:

Dokerfile_Dash
```
RUN conda install -c conda-forge dash-renderer dash dash-html-components dash-core-components  python=3.5

```


Build and tag your image

```
docker build -t docker.optum.com/bd_docid/bdpaas_jupyter_pathfinder:dash -f Dokerfile_Dash .

```
Push the image to DTR

```
docker login docker.optum.com 
docker push docker.optum.com/bd_docid/bdpaas_jupyter_pathfinder:dash

```
