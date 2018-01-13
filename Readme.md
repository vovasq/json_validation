# About

Implementation of simple server for JSON files validation.

Run container with server: 

`docker run -d -p 80:80 github.com/vovasq/validator`

Send file to validation:

`curl -s --data-binary @filename.json http://localhost`

