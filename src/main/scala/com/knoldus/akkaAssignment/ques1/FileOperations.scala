package com.knoldus.akkaAssignment.ques1

import java.io.File
import akka.pattern.pipe
import akka.pattern.ask
import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import akka.util.Timeout
import com.knoldus.akkaAssignment.ques1.FileOperations.State
import scala.concurrent.duration.DurationInt
import scala.io.Source

class FileOperations extends Actor {

  override def receive = {
    case file: File =>
      implicit val timeout = Timeout(1000 seconds)
      import scala.concurrent.ExecutionContext.Implicits.global
      val lines = readLines(file)
      for(line <- lines) {
        val wordCount = self ? countWords(line)
        wordCount.foreach(println(_))
        wordCount pipeTo self
      }
    case oneLineWordCount: Int =>
      FileOperations.noOfWords += oneLineWordCount
    case State =>
      sender() ! FileOperations.noOfWords
  }

  private def readLines(file: File): List[String] = {
    println("Reading file----")
    val fileSource = Source.fromFile(file)
    fileSource.getLines().toList
  }

  private def countWords(line: String): Int = {
    println("Counting words-----")
    line.split("\\W+").toList.size
  }

}

object FileOperations extends App {
  case object State
  var noOfWords = 0
  val file = new File("/home/simar/CarbonData/project/AkkaAssignment/src/main/resources/input.txt")
  val config = ConfigFactory.parseString(
    """
      |akka.actor.deployment {
      | /poolRouter {
      |   router = balancing-pool
      |   nr-of-instances = 5
      | }
      |}
    """.stripMargin
  )
  val system = ActorSystem("RouterSystem", config)
  val router = system.actorOf(FromConfig.props(Props[FileOperations]), "poolRouter")
  router ! file

  Thread.sleep(2000)
  implicit val timeout = Timeout(1000 seconds)
  import scala.concurrent.ExecutionContext.Implicits.global

  val wordCount = (router ? State)
  println("Word Count")
  wordCount.foreach(println(_))

}
