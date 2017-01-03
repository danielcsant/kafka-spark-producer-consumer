package com.stratio.pocs.kafka

/**
  * Created by fhuertas on 30/12/16.
  */
object Main extends App {

  import scala.collection.JavaConverters._

  override def main(gs: Array[String]): Unit = {
    val newConsumer = Consumer.createConsumer
    newConsumer.subscribe(List("audit").asJava)
    while (true) {
      val records = newConsumer.poll(100)
      records.asScala.foreach(record =>
        printf(s"offset =${record.offset()}, key = ${record.key()}, value = ${record.value()}"))
    }
  }

}
