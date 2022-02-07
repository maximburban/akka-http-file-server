package com.example

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Future

class FileRouteSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest with MockitoSugar {

  lazy val testKit: ActorTestKit = ActorTestKit()

  implicit def typedSystem: ActorSystem[Nothing] = testKit.system

  override def createActorSystem(): akka.actor.ActorSystem = testKit.system.classicSystem

  val fileService: FileService = mock[FileService]
  lazy val routes: Route = new FileRoutes(fileService).fileRoutes

  "FileRoutes" should {

    "return list of files" in {
      when(fileService.getFilesList).thenReturn(Future(List("file.one", "file.two")))

      Get("/file/list") ~> routes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""["file.one","file.two"]""")
      }
    }

    "delete file" in {
      val fileName = "file.one"
      when(fileService.deleteFile(fileName)).thenReturn(complete(StatusCodes.OK))

      Delete(s"/file/$fileName") ~> routes ~> check {
        status should ===(StatusCodes.OK)
        entityAs[String] should ===("OK")
      }
    }

    "return error message when delete a file" in {
      val fileName = "file.one"
      when(fileService.deleteFile(fileName)).thenReturn(complete(StatusCodes.BadRequest, s"File $fileName not found"))

      Delete(s"/file/$fileName") ~> routes ~> check {
        status should ===(StatusCodes.BadRequest)
        entityAs[String] should ===(s"File $fileName not found")
      }
    }
  }
}
