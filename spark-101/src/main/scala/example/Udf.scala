package example

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.UserDefinedFunction

object Udf{
     def cube():UserDefinedFunction = udf((a: Int) => {
          a*a*a
     })
     def main(args : Array[String]): Unit = {
          val spark = SparkSession.builder.appName("UDF").master("local[*]").getOrCreate()
          import spark.implicits._

          val df = Seq((1,2),(3,4)).toDF("A","B")

          val tempDF = df.withColumn("cubed",lit(cube()($"A")))
          tempDF.show()


          // Let us see simple examples of Join and union

          val employeeDF = Seq(("1","John","2"),("2","Wick","3"),("5","Stark","9")).toDF("EPIID","EPNAME","DEPTID")
          val departDF = Seq(("2","IT"),("9","Finance")).toDF("ID","Name")

          // join inner on deptId
          // Always broadcast smaller DF 
          // Condition df1("column1") === df2("column2")
          val innerJoin = employeeDF.join(broadcast(departDF),employeeDF("DEPTID") === departDF("ID"),"inner")
          innerJoin.show()

          // left join
          val leftJoin = employeeDF.join(broadcast(departDF),employeeDF("DEPTID") === departDF("ID"),"left")
          leftJoin.show()

          // right join
          val rightJoin = employeeDF.join(broadcast(departDF),employeeDF("DEPTID") === departDF("ID"),"right")
          rightJoin.show()

          // outer join
          val outerJoin = employeeDF.join(broadcast(departDF),employeeDF("DEPTID") === departDF("ID"),"outer")
          outerJoin.show()

          // union
          val employerDF = Seq(("55","Tony","Boss")).toDF("EPIID","EPNAME","DESG")
          val unonDF = employeeDF.unionByName(employerDF, allowMissingColumns=true)
          unonDF.show()

     }
}