package main.scala.com.stratio.pocs.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils

/**
  * https://github.com/apache/spark/pull/10953
  *
  */
object MarkgroverPatchConsumer extends App {

  override def main(args: Array[String]): Unit = {
    val brokers = "localhost:9092"
    val topics = "test"

    // Create context with 2 second batch interval
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("DirectKafkaWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet

    val kafkaParams = Map[String, String](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG ->
        "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG ->
        "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "false"
    )

    val kafkaSslParams = Map[String, String](
      "bootstrap.servers"->"localhost:9092",
      "security.protocol"->"SSL",
      "ssl.protocol"->"TLS",
      "ssl.keystore.location"-> this.getClass.getResource("../../../../../../ssl/server.keystore.jks").getPath,
      "ssl.keystore.password"->"test1234",
      "ssl.key.password"->"test1234",
      "ssl.truststore.location"-> this.getClass.getResource("../../../../../../ssl/server.truststore.jks").getPath,
      "ssl.truststore.password"->"test1234",
      "ssl.client.auth"->"required",
      "ssl.enabled.protocols"->"TLSv1.2,TLSv1.1,TLSv1",
      "ssl.keystore.type"->"JKS",
      "ssl.truststore.type"->"JKS"
    )
    val messages = KafkaUtils.createNewDirectStream[String, String](
      ssc, kafkaParams ++ kafkaSslParams, topicsSet)

    // Get the lines, split them into words, count the words and print
    val lines = messages.map(_._2)
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
    wordCounts.print()

    // Start the computation
    ssc.start()
    ssc.awaitTermination()

  }


}
