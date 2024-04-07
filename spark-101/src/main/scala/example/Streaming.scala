package example

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._

object Streaming {
     def main(args : Array[String]): Unit = {
          // 1. Input source

          val spark = SparkSession.builder.appName("Stream").master("local[*]").getOrCreate()

          import spark.implicits._

          // specify source in format
          // option ncludes how to get it

          val df = spark.readStream.text("data/logs")

          // transform data

          // df.show()

          val streamingQuery = df.writeStream
          .format("console")
          .outputMode("append")
          .trigger(Trigger.ProcessingTime("1 second"))
          .option("checkpointLocation", "F:\\SparkLearning\\")
          .start()
          streamingQuery.awaitTermination()

     }
}