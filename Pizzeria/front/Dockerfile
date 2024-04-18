FROM node:18
WORKDIR /app
COPY front/package.json front/package-lock.json ./
RUN npm install
COPY front .
EXPOSE 3000
CMD ["npm", "start"]

