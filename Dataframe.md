### DataFrame vs Dataset

• If you want to tell Spark what to do, not how to do it, use DataFrames or Datasets.
• If you want rich semantics, high-level abstractions, and DSL operators, use Data‐
Frames or Datasets.
• If you want strict compile-time type safety and don’t mind creating multiple case
classes for a specific Dataset[T], use Datasets.
• If your processing demands high-level expressions, filters, maps, aggregations,
computing averages or sums, SQL queries, columnar access, or use of relational
operators on semi-structured data, use DataFrames or Datasets.
• If your processing dictates relational transformations similar to SQL-like queries,
use DataFrames.
• If you want to take advantage of and benefit from Tungsten’s efficient serializa‐
tion with Encoders, , use Datasets.
• If you want unification, code optimization, and simplification of APIs across
Spark components, use DataFrames
• If you are an R user, use DataFrames.
• If you are a Python user, use DataFrames and drop down to RDDs if you need
more control.
• If you want space and speed efficiency, use DataFrames.
• If you want errors caught during compilation rather than at runtime, choose the
appropriate API as depicted in Figure 3-2.

![Alt text](image.png)

### How does spark achieve ths ?

![Alt text](image-1.png)

![Alt text](image-2.png)

![Alt text](image-3.png)