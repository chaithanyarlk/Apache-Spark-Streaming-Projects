package example

import org.apache.spark._

object CompleteRDD {

     def parseLines(s: String): (Int, Int) = {
          val splitted = s.split(",")
          return (s(2).toInt, s(3).toInt)
     }

     def parseTemperature(s : String): (String, String, Float) = {
          val splitted = s.split(",")
          return(splitted(0),splitted(2),splitted(3).toFloat)
     }

     def parseCustomerrdd(s : String): (String, Float) = {
          val splitted = s.split(",")
          return (splitted(0),splitted(2).toFloat)
     }

     def minTemp(x: Float, y: Float): Float = {
          if (x > y) return y;
          return x;
     }
     def main(args : Array[String]): Unit = {
          // Lets take an example of fake friends example
          // RDD is resileint distrinbuted dataset

          // lets load RDD each new line is treated as a row in this

          val sparkConf = new SparkConf().setAppName("learning").setMaster("local[*]")
          // In above line we are setting appName and master of the spark app
          // local[*] --> means use all the possible cores given to create a sparkContext
          // Generally multple can be created with taking each core and running [parallely]
          // Here we are saying to create a context by using any one of the core

          val spark = new SparkContext(sparkConf)
          // This creates our saprkContext 
          // What is sparkContext? exaplined in md file


          /*
          First let us understand how to create a RDD
          given an array of data to sparkContext asking it
          hey! can u create an RDD with this data and behind the scenes what spark does is that
          it will distribute the work as chunks of data so that each of them can be operated solely and [parallely 
          not sequentially when and RDD is returned various operations can be performed


          Remember the row of RDD is just a string in case of a testFile that u will read!
          */
          // read a words list and parallelize
          val data = Array(1,2,3)
          val wordsRDD = spark.parallelize(data)
          val count = wordsRDD.count()

          println("Count is "+count)



          val squarerdd = wordsRDD.map(x => {x * x})

          val result = squarerdd.collect()

          println("Values "+result.mkString(", "))

          val friendsRDD =  spark.textFile("F:\\SparkScalaCourse\\SparkScalaCourse\\data\\fakefriends.csv")

          val  mappedRDD = friendsRDD.map(parseLines)

          val mapForRDD = mappedRDD.mapValues(x => {(x,1)})

          val reducedRDD = mapForRDD.reduceByKey((x,y) => {(x._1 + y._1, x._2 + y._2)})

          val avgRDD = reducedRDD.mapValues(x => {x._1/x._2})

          val avgResult = avgRDD.collect()

          println("Result "+avgResult.mkString(", "))


          val temperatureRdd = spark.textFile("F:\\SparkScalaCourse\\SparkScalaCourse\\data\\1800.csv")

          val parsedrdd = temperatureRdd.map(parseTemperature)

          val mappedTemp = parsedrdd.filter(x => {x._2 == "TMIN"})

          val minTemprdd = mappedTemp.map(x => {(x._1,x._3)})

          val reducedTempRDD = minTemprdd.reduceByKey((x,y) => {minTemp(x,y)})

          val resultTemp = reducedTempRDD.collect()

          println("ResultTemp "+resultTemp.mkString(", "))

          val bookrdd = spark.textFile("F:\\SparkScalaCourse\\SparkScalaCourse\\data\\book.txt")

          val flattenrdd = bookrdd.flatMap(x => x.split(" "))

          println("Bookrdd copunt "+bookrdd.count())

          println("Flattenrdd count "+flattenrdd.count())

          val wordsCount = flattenrdd.countByValue()

          println("Count of words by value "+wordsCount)

          val customerrdd = spark.textFile("F:\\SparkScalaCourse\\SparkScalaCourse\\data\\customer-orders.csv")

          val mapedCustomerrdd = customerrdd.map(parseCustomerrdd)


          val totalCustomerrdd = mapedCustomerrdd.reduceByKey((x,y)=>{x + y})

          val resultCustomer = totalCustomerrdd.collect()

          println("Customer spent "+resultCustomer.mkString(", "))


     }
}