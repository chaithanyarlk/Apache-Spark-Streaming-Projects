package example

import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object Sql {
     def main(args: Array[String]): Unit = {
          val spark =  SparkSession.builder.appName("Learning").master("local[*]").getOrCreate()

          import spark.implicits._

          val filePath = "F:\\SparkLearning\\spark-101\\data\\sf-fire-calls.csv"
          val sfDF = spark.read.format("csv").option("header","true").option("inferSchema","true").load(filePath)

          sfDF.createOrReplaceTempView("sf_df")
          // Basically there are two types of view creation one is Temp view and other is global
          // Why two kinds of views?
          // Because always the data will not come from single source in those situations
          // We need to create a global View to get accessed by more than one sparkSession
          // Why we need more than one sparkSession?
          // https://oreil.ly/YbTFa I advise u to read this article which explains clearly the requirement with
          // example on how to create it

          spark.sql("SELECT * FROM sf_df where City = 'CALIFORNIA'").show()

          // Spark create database
          // But where does it reside??
          // spark.conf.get("spark.sql.warehouse.dir") --> it creates a warehouse folder in which it saves the db and related tables

          spark.stop()
     }
}