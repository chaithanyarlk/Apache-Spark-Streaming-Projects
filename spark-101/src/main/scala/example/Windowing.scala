package example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._

object Windowing{
     def main(args: Array[String]): Unit = {
    // Create a Spark session
    val spark = SparkSession.builder
      .appName("KafkaStream")
      .master("local[2]")  // Use local mode with 2 threads for demonstration
      .config("spark.sql.shuffle.partitions",3)
      .getOrCreate()
     

      val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers","localhost:9092")
      .option("subscribe","topic")
      .load()

     // kafkaDF.printSchema()

     /*
     Always below wll the structure of DataFrame in kafka as datasource
     root
     |-- key: binary (nullable = true)
     |-- value: binary (nullable = true)
     |-- topic: string (nullable = true)
     |-- partition: integer (nullable = true)
     |-- offset: long (nullable = true)
     |-- timestamp: timestamp (nullable = true)
     |-- timestampType: integer (nullable = true)
     */

     // Stock record

     // {"CreatedTime":"2019-02-05 10:05:00", "Type":"BUY", "Amount": 500, "BrokerCode": "ABC"}
     // {"CreatedTime":"2019-02-05 10:16:00", "Type":"BUY", "Amount": 500, "BrokerCode": "ABC"}
     // {"CreatedTime":"2019-02-05 10:25:00", "Type":"BUY", "Amount": 500, "BrokerCode": "ABC"}
     // {"CreatedTime":"2019-02-05 10:04:00", "Type":"BUY", "Amount": 500, "BrokerCode": "ABC"}
     // {"CreatedTime":"2019-02-05 10:35:00", "Type":"BUY", "Amount": 500, "BrokerCode": "ABC"}


     // LateComers will be updated in corresponding window

     // Wndows cant be aggregated

     val schema = new StructType(
          Array(
               StructField("CreatedTime",DataTypes.StringType,true),
               StructField("Type",DataTypes.StringType,true),
               StructField("Amount",DataTypes.IntegerType,true),
               StructField("BrokerCode",DataTypes.StringType,true)
          )
     )

     import spark.implicits._


     val valueDF = kafkaDF.select(col("value").cast("string")).select(from_json(col("value"),schema).alias("value"))

     valueDF.printSchema()

     val newDF = valueDF.select(col("value.*"))
                    .withColumn("createdAt",to_timestamp(col("CreatedTime"),"yyyy-MM-dd HH:mm:ss"))
                    .withColumn("Buy",when(col("Type") === lit("BUY"),col("Amount")).otherwise(lit(0)))
                    .withColumn("Sell",when(col("Type") === lit("SELL"),col("Amount")).otherwise(lit(0)))



     newDF.printSchema()

     val windowedDF = newDF.groupBy(window(col("createdAt"),"15 minute")).agg(sum("Buy").alias("TotalBuy"), sum("Sell").alias("TotalSell"))

     val streamingQuery = windowedDF.writeStream
     .format("console")
     .queryName("Write Stream of User Data")
     .outputMode("update")
     .option("path","F:\\SparkLearning\\output")
     .option("checkpointLocation","F:\\SparkLearning\\checkpontWindowTumble")
     .trigger(Trigger.ProcessingTime("1 minute"))
     .start()

     streamingQuery.awaitTermination()

     /*
     
     val schema = new DataTypes.createStructType(
          Array(
               DataTypes.createStructField("id",DataTypes.StringType,true),
               DataTypes.createStructField("index",DataTypes.IntegerType,true),
               DataTypes.createStructField("guid",DataTypes.StringType,true),
               DataTypes.createStructField("isActive",DataTypes.BooleanType,true),
               DataTypes.createStructField("age",DataTypes.IntegerType,true),
               DataTypes.createStructField("name",DataTypes.StringType,true),
               DataTypes.createStructField("gender",DataTypes.StringType,true),
               DataTypes.createStructField("company",DataTypes.StringType,true),
               DataTypes.createStructField("email",DataTypes.StringType,true),
               DataTypes.createStructField("registered",DataTypes.StringType,true),
               DataTypes.createStructField("latitude",DataTypes.LongType,true),
               DataTypes.createStructField("longitude",DataTypes.LongType,true),
               DataTypes.createStructField("tags",DataTypes.ArrayType(StringType),true),
               DataTypes.createStructField("friends",DataTypes.createStructType(
                    Array(
                         DataTypes.createStructField("fid",DataTypes.IntegerType,true),
                         DataTypes.createStructField("name",DataTypes.StringType,true)
                    )
               ),true)
          )
     )
     
     
     
     */



     }
}