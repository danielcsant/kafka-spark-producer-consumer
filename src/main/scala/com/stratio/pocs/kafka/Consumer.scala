package com.stratio.pocs.kafka

import org.apache.kafka.clients.consumer.KafkaConsumer

/**
  * Created by fhuertas on 30/12/16.
  */


object Consumer extends KafkaConfig{
  def createConsumer: KafkaConsumer[String,String] = {
    new KafkaConsumer[String,String](props)
  }
}
