package example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._

object MultiKafka{
     def main(args: Array[String]): Unit = {
    // Create a Spark session
    val spark = SparkSession.builder
      .appName("KafkaStream")
      .master("local[2]")  // Use local mode with 2 threads for demonstration
      .config("spark.sql.shuffle.partitions",3)
      .getOrCreate()

      spark.conf.set("spark.sql.repl.eagerEval.enabled", true)
     

      // Trick if u want to show the dataFrame is to make readStream to read and see for once
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


     val schema = new StructType(
          Array(
               StructField("id",DataTypes.StringType,true),
               StructField("index",DataTypes.IntegerType,true),
               StructField("guid",DataTypes.StringType,true),
               StructField("isActive",DataTypes.BooleanType,true),
               StructField("age",DataTypes.IntegerType,true),
               StructField("name",DataTypes.StringType,true),
               StructField("gender",DataTypes.StringType,true),
               StructField("company",DataTypes.StringType,true),
               StructField("email",DataTypes.StringType,true),
               StructField("registered",DataTypes.StringType,true),
               StructField("latitude",DataTypes.DoubleType,true),
               StructField("longitude",DataTypes.DoubleType,true),
               StructField("tags",ArrayType(StringType),true),
               StructField("friends",ArrayType(
                    StructType(
                         Array(
                              StructField("fid",DataTypes.IntegerType,true),
                              StructField("fname",DataTypes.StringType,true)
                         )
                    )
               ),true)
          )
     )

     import spark.implicits._


     val valueDF = kafkaDF.select(col("value").cast("string")).select(from_json(col("value"),schema).alias("value"))

     valueDF.printSchema()

     val newDF = valueDF.select(col("value.*"))
                    .select(col("id"),col("index"),col("guid"),col("isActive"),
                         col("age"),col("name"),col("gender"),col("company"),
                         col("email"),col("registered"),col("latitude"),col("longitude"),
                         col("tags"),explode(col("friends")).as("frnds")
                    ).select(
                         col("id"),col("index"),col("guid"),col("isActive"),
                         col("age"),col("name"),col("gender"),col("company"),
                         col("email"),col("registered"),col("latitude"),col("longitude"),
                         col("tags"),col("frnds.*")
                    )

     newDF.printSchema()

     // we need to make it as key value pair
     // As soon as user data comes the longitude and latitude must be sent to kafka

     val kafkaTargetDF = newDF.select(col("id").as("key"),struct(
          col("name").as("name"),
          col("latitude").as("latitude"),
          col("longitude").as("longitude")
     ).as("data"))
     val tempDf = kafkaTargetDF.select(col("key"),to_json(col("data")).as("value"))

     val streamingQuery = tempDf.writeStream
     .format("kafka")
     .queryName("Write Stream of User Data")
     .outputMode("append")
     .option("kafka.bootstrap.servers","localhost:9092")
     .option("topic","notif")
     .option("checkpointLocation","F:\\SparkLearning\\checkpointMultiKafka")
     .trigger(Trigger.ProcessingTime("1 minute"))
     .start()


     val streamingQueryFile = newDF.writeStream
     .format("json")
     .queryName("Write Stream of User Data")
     .outputMode("append")
     .option("path","F:\\SparkLearning\\output")
     .option("checkpointLocation","F:\\SparkLearning\\checkpointMultiFile")
     .trigger(Trigger.ProcessingTime("1 minute"))
     .start()

     // spark.streams.awaitAnyTermianton()

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