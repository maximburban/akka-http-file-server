The File Server
================
Build a docker image:
```shell
sbt docker:publishLocal
```

Running docker image locally:

```shell
docker run -p 8080:8080 akka-http-file-server:0.1.0-SNAPSHOT
```
###API of file server:

- List of files:
```shell
GET /file/list 
```

- upload file as form-data with key _fileUpload_:
```shell
POST /file/upload 
```

- delete by file name:
```shell
DELETE /file/$fileName
```

- download by file name:
```shell
GET /file/$fileName
```