FROM node:latest AS dep-download

WORKDIR /usr/src/app
COPY ./calculator-ui /usr/src/app

RUN npm i

FROM dep-download AS build

ARG REACT_APP_BACKEND_URL
ENV REACT_APP_BACKEND_URL $REACT_APP_BACKEND_URL

RUN npm run build

FROM nginx:alpine AS runtime

WORKDIR /app

COPY --from=build /usr/src/app/build/. .

COPY ./nginx.conf /etc/nginx/nginx.conf
EXPOSE 3000

