FROM nginx:stable
LABEL authors="charlie"

COPY nginx-reverse-proxy-config.conf /etc/nginx/conf.d

ENTRYPOINT ["/docker-entrypoint.sh", "nginx", "-g", "daemon off;"]