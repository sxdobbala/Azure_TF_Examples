# Image source
FROM nginx:alpine
# Copy static assets into var/www
COPY /_site /usr/share/nginx/html
EXPOSE 80/tcp