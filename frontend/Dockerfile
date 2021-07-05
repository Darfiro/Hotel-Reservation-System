FROM node:14.15.0
WORKDIR /app

ENV PATH /app/node_modules/.bin:$PATH
COPY package.json /app/package.json

RUN npm install
RUN npm install -g @angular/cli@7.3.9 --unsafe
COPY . /app

CMD npm run start