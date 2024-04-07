package example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._

object StreaminJoin{
     def main(args: Array[String]): Unit = {
    // Create a Spark session
    val spark = SparkSession.builder
      .appName("KafkaStream")
      .master("local[2]")  // Use local mode with 2 threads for demonstration
      .config("spark.sql.shuffle.partitions",3)
      .getOrCreate()
     

     

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

     val schemaA = new StructType(
          Array(
               StructField("AID",StringType,true),
               StructField("Aname",StringType,true)
          )
     )

     val schemaB = new StructType(
          Array(
               StructField("BID",StringType,true),
               StructField("Bname",StringType,true)
          )
     )

     import spark.implicits._

     val kafkaDF = spark.readStream
     .format("kafka")
     .option("kafka.bootstrap.servers","localhost:9092")
     .option("subscribe","topic")
     .load()

     val valueDF = kafkaDF.select(col("value").cast("string")).select(from_json(col("value"),schemaA).alias("value"))

     valueDF.printSchema()

     val newADF = valueDF.select(col("value.*"))


     val kafkaDFB = spark.readStream
     .format("kafka")
     .option("kafka.bootstrap.servers","localhost:9092")
     .option("subscribe","notif")
     .load()

     val valueDFB = kafkaDFB.select(col("value").cast("string")).select(from_json(col("value"),schemaB).alias("value"))

     valueDFB.printSchema()

     val newBDF = valueDFB.select(col("value.*"))



     newADF.printSchema()

     newBDF.printSchema()

     val joinedDF = newADF.join(newBDF,newADF("AID") === newBDF("BID"),"inner")


     val streamingQuery = joinedDF.writeStream
     .format("console")
     .queryName("Write Stream of User Data")
     .outputMode("append")
     .option("path","F:\\SparkLearning\\output")
     .option("checkpointLocation","F:\\SparkLearning\\checkpointStreamin")
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