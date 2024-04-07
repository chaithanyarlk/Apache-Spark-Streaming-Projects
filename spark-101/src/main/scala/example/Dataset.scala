package example

import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object Dataset{
     case class Delay(date : String, delay: Int, distance: Int,origin:String,destination:String)
     def main(args : Array[String]) :Unit = {
          val spark = SparkSession.builder.appName("Dataset").master("local[*]").getOrCreate()

          import spark.implicits._

          // Datasets are like dataframes but wth difference with more Object oriented perspective
          //In Spark Scala, a Dataset is a distributed collection of data organized into named columns. 
          //It provides a type-safe, object-oriented programming interface and is part of Spark's higher-level API for structured and semi-structured data processing.

          // The names of the fields in the Scala case class or Java class defini‐
          // tion must match the order in the data source. The column names
          // for each row in the data are automatically mapped to the corre‐
          // sponding names in the class and the types are automatically
          // preserved.
          val csvPath = "F:\\SparkLearning\\spark-101\\data\\departuredelays.csv"
          
          val dataset = spark.read.format("csv").option("header","true").option("inferSchema","true").load(csvPath).as[Delay]

          dataset.show()

          // filter example
          dataset.filter(d => d.delay > 10).show()

          // it makes the higher order functions easier to write
          

          val data = Array(Delay("12345",12345,12345,"abc","abc"))

          val createdDataset = spark.createDataset(data)

          createdDataset.show()

          spark.stop()
     }
}