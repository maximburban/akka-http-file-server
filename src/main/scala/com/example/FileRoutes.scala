package com.example

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol

class FileRoutes(val fileService: FileService)(implicit val system: ActorSystem[_]) extends SprayJsonSupport with DefaultJsonProtocol {

  private val listRoute: Route =
    path("list") {
      get {
        complete(fileService.getFilesList)
      }
    }

  private val uploadRoute: Route =
    path("upload") {
      post {
        fileService.uploadMultipartFile
      }
    }

  private val downloadRoute: Route =
    path(Segment) { fileName =>
      fileService.downloadFile(fileName)
    }

  private val deleteRoute: Route =
    path(Segment) { fileName =>
      delete {
        fileService.deleteFile(fileName)
      }
    }

  val fileRoutes: Route = {
    pathPrefix("file") {
      concat(listRoute, uploadRoute, downloadRoute, deleteRoute)
    }
  }
}
