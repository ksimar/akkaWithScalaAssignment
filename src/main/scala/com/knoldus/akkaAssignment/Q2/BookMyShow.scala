package com.knoldus.akkaAssignment.Q2

import akka.actor.{Actor, ActorSystem, Props}
import com.knoldus.akkaAssignment.Q2.BookMyShow.Seat
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration.DurationInt
//import com.knoldus.akkaAssignment.Q2.BookMyShow.Seat

import scala.collection.mutable.ListBuffer

class BookMyShow extends Actor{

  val seats = ListBuffer(0,0,0,0,0,0,0,0,0,0)

  def bookTicket(seatNo: Int) = {
    if(seats(seatNo)==1)
      println(s"Sorry, seat no $seatNo  is already booked " +
        "Please select some other seat")
    else {
      seats(seatNo) = 1
      println(s"Seat number $seatNo Booked")
    }
  }

  def isAvailable: Boolean = {
    val result = seats.filter(_==0)
    if(result.isEmpty)
      false
    else true
  }

  override def receive = {
    case Seat(x: Int) =>
      bookTicket(x)
  }

}

object BookMyShow extends App{
  case class Seat(seatNo: Int)

  val system = ActorSystem("BookMyShow")
  val ref1 = system.actorOf(Props[BookMyShow])

  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout = Timeout(1000 seconds)


  val request1 = ref1 ? Seat(1)
  val request2 = ref1 ? Seat(1)
  val request3 = ref1 ? Seat(4)
  val request4 = ref1 ? Seat(1)
  val request5 = ref1 ? Seat(1)

  request1 foreach{println("Request1"); println(_); }
  request2 foreach{println("Request2");println(_)}
  request3 foreach{println("Request3");println(_)}
  request4 foreach{println("Request4");println(_)}
  request5 foreach{println("Request5");println(_)}

}


