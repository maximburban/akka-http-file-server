package com.example

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, fileUpload, getFromFile, onComplete, onSuccess}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.FileIO

import java.nio.file.{Files, NoSuchFileException, Path, Paths}
import scala.concurrent.Future
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.util.{Failure, Success}

class FileService(implicit val system: ActorSystem[_]) {

  import system.executionContext

  val rootDirectory: Path = Paths.get("/opt/docker/[files]")
//  val rootDirectory: Path = Paths.get("files")

  def uploadMultipartFile: Route = {
    fileUpload("fileUpload") {
      case (fileInfo, fileStream) =>
        val sink = FileIO.toPath(rootDirectory resolve fileInfo.fileName)
        val writeResult = fileStream.runWith(sink)
        onSuccess(writeResult) { result =>
          result.status match {
            case Success(_) => complete(s"Successfully written ${result.count} bytes")
            case Failure(e) => throw e
          }
        }
    }
  }

  def getFilesList: Future[List[String]] = {
    val fileLists = Files.list(rootDirectory)
      .iterator()
      .asScala
      .map(path => path.getFileName)
      .map(path => path.toString)
      .toList
    Future.successful(fileLists)
  }

  def deleteFile(fileName: String): Route = {
    val deletedFilePath = Paths.get(s"$rootDirectory/$fileName")
    val deleteResult = Future(Files.delete(deletedFilePath))
    onComplete(deleteResult) {
      case Success(_) => complete(StatusCodes.OK)
      case Failure(_: NoSuchFileException) => complete(StatusCodes.BadRequest, s"File $fileName not found")
    }
  }

  def downloadFile(fileName: String): Route = {
    getFromFile(s"$rootDirectory/$fileName")
  }
}
