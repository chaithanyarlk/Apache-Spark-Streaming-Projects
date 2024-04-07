package example

import org.apache.spark.sql._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.functions._


object FileStream{
     def main(args :  Array[String]): Unit = {
          // Create a Spark session
          // By default inferSchema is disabled in streaming
          val spark = SparkSession.builder
               .appName("SocketStructuredStreamingExample")
               .master("local[2]")  // Use local mode with 2 threads for demonstration
               .config("spark.sql.shuffle.partitions",3)
               .config("spark.sql.streaming.schemaInference","true")
               .getOrCreate()
          
          val rawDF = spark.readStream.format("csv")
          .option("path","F:\\SparkLearning\\input")
          .option("header","true")
          .load()

          // rawDF.printSchema()
          val selectedDF = rawDF.select(col("date"))

          // Trigger

          val streamingQuery = selectedDF.writeStream
          .format("json")
          .option("path","F:\\SparkLearning\\output")
          .outputMode("append")
          .option("checkpointLocation","F:\\SparkLearning\\checkpoint")
          .trigger(Trigger.ProcessingTime("1 minute"))
          .start()

          streamingQuery.awaitTermination()


     }
}