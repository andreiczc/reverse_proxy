FROM node:16-alpine

LABEL title="express-server" \
  description="Express Server that implements GET method"

RUN mkdir -p /usr/app
WORKDIR /usr/app

ADD app.js .
ADD package.json .

RUN npm install

ENTRYPOINT ["npm", "start"]