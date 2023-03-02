FROM node:latest as dep-download

WORKDIR /usr/src/app
COPY ./ /usr/src/app

RUN npm i

FROM dep-download AS build
ARG REACT_APP_BACKEND_URL
ENV REACT_APP_BACKEND_URL $REACT_APP_BACKEND_URL

RUN npm run build

## https://lipanski.com/posts/smallest-docker-image-static-website
FROM lipanski/docker-static-website:latest as runtime

COPY --from=build /usr/src/app/build/. .

#CMD ["/busybox", "httpd", "-f", "-v", "-p", "3000", "-c", "httpd.conf"]

