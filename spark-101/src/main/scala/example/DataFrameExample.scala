package example

import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object DataFrameExample{
     def main(args: Array[String]): Unit = {
          val spark =  SparkSession.builder.appName("Learning").master("local[*]").getOrCreate()

          import spark.implicits._

          val filePath = "F:\\SparkLearning\\spark-101\\data\\sf-fire-calls.csv"
          val sfDF = spark.read.format("csv").option("header","true").option("inferSchema","true").load(filePath)

          // Load the dataframe and see what is present inside it
          // SHows top 20 rows of the dataframe
          sfDF.show()

          // printSchema wll give u schema of the current dataframe
          sfDF.printSchema()

          //There are two two types of transformations mainly wide and narrow
          // wide --> example join
          // narrow --> example filter

          // Projections 
          sfDF.select($"CallNumber").show()
          sfDF.select($"CallNumber").where($"CallType" === lit("Alarms")).show()

          // flters basic example
          sfDF.select($"CallType").filter($"CallType" === lit("Alarms")).show()

          // Renaming, adding Columns and dropping columns
          // old_column new_column_name
          sfDF.withColumnRenamed("Delay","ResponseDelayedinMins").show()

          println("DateExample")

          val tempDF = sfDF.withColumn("Month",month(to_date($"CallDate","mm/dd/yyyy")))

          tempDF.show()

          val dropDF = tempDF.drop("Month")

          dropDF.show()

          // groupBy example
          sfDF.select($"CallNumber").groupBy($"CallType")

          // Now lets see end to end example of all operatons involved!
          //What were all the different types of fire calls in 2018
          val yearDF = sfDF.withColumn("year",year(to_date($"CallDate","mm/dd/yyyy")))
          yearDF.select($"CallType").filter($"year" === lit(2018)).distinct().show()

          // What months within the year 2018 saw the highest number of fire calls
          val monthDF = yearDF.withColumn("month",month(to_date($"CallDate","mm/dd/yyyy"))).filter($"year" === lit(2018))
          println("monthDF")
          monthDF.show()
          monthDF.select($"month").groupBy($"month").count().orderBy(desc("count")).show()


          //Which neighborhood in San Francisco generated the most fire calls in 2018
          yearDF.select($"Neighborhood").filter($"year" === lit(2018)).groupBy("Neighborhood").count().orderBy(desc("count")).show()



          // Which neighborhoods had the worst response times to fire calls in 2018
          yearDF.select($"Neighborhood",$"Delay").filter($"year" === lit(2018)).filter($"CallType".contains("Fire")).groupBy("Neighborhood").sum("Delay").orderBy(desc("sum(Delay)")).show()

     
          // Which week in the year in 2018 had the most fire calls
          yearDF.withColumn("week",weekofyear(to_date($"CallDate","mm/dd/yyyy"))).select($"Neighborhood",$"week").filter($"year" === lit(2018)).filter($"CallType".contains("Fire")).groupBy("Neighborhood").sum("week").orderBy(desc("sum(week)")).show()

          // Joins unions etc higher will be shown as examples in upcoming ones

          // Say how to create ur own dataframe
          val df = Seq(("a1","b1"),("a2","b2")).toDF("A","B")
          df.show()
     }
}