package com.stratio.pocs.kafka

import java.util.Properties

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by fhuertas on 30/12/16.
  */
trait KafkaConfig {
  import scala.collection.JavaConversions._
  val conf: Config = ConfigFactory.load()
  val props: Properties = loadDefaultProperties()
  private def loadDefaultProperties(): Properties = {

    val propsMap = conf.getConfig("kafka").entrySet().map(entry =>
      entry.getKey -> entry.getValue.unwrapped()).toMap
    val props = new Properties()
    props.putAll(propsMap)
    props
  }
}
