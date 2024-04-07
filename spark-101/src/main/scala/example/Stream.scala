package example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Stream {
  def main(args: Array[String]): Unit = {
    // Create a Spark session
    val spark = SparkSession.builder
      .appName("SocketStructuredStreamingExample")
      .master("local[2]")  // Use local mode with 2 threads for demonstration
      .config("spark.sql.shuffle.partitions",3)
      .getOrCreate()

    // Define the socket source

    /*
    Microsoft Windows [Version 10.0.19044.3086]
    (c) Microsoft Corporation. All rights reserved.

    C:\Users\hp>ncat -lk 9999
    H

    GH ERJG
    AS DF

    AS AS DF
    AS AS AS DF
    AS AS AS AS SPARK
    
    */
    val lines = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9999)
      .load()

    // Split the lines into words
    val words = lines.select(explode(split(col("value"), " ")).alias("word"))

    // Perform word count
    val wordCount = words.groupBy("word").count()

    // Display the word count to the console
    val query = wordCount.writeStream
      .outputMode("complete")
      .format("console")
      .start()

    // Wait for the streaming query to terminate
    query.awaitTermination()
  }
}
