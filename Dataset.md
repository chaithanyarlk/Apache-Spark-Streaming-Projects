### Case class

In Scala, a case class is a special type of class that is primarily used for immutable data modeling. Case classes provide several conveniences and features that make them particularly suitable for representing data structures. Here are some key characteristics and differences between case classes and normal classes:

### Case Class:

1. **Conciseness:**
   - Case classes are concise and provide a convenient syntax for declaring classes with minimal boilerplate code.

   ```scala
   case class Person(name: String, age: Int)
   ```

2. **Immutable by Default:**
   - Case classes are immutable by default. All fields declared in a case class are automatically `val` (immutable).

   ```scala
   val person = Person("Alice", 25)
   // person.name = "Bob" // Compilation error - fields are immutable
   ```

3. **Automatic toString, equals, and hashCode:**
   - Case classes automatically generate implementations for `toString`, `equals`, and `hashCode` based on the class's fields.

   ```scala
   val person1 = Person("Alice", 25)
   val person2 = Person("Alice", 25)
   println(person1 == person2)  // true
   ```

4. **Pattern Matching:**
   - Case classes are often used with pattern matching, making it easy to destructure and extract values.

   ```scala
   val person = Person("Alice", 25)
   person match {
     case Person(name, age) => println(s"Name: $name, Age: $age")
   }
   ```

5. **Companion Object with Apply Method:**
   - Case classes automatically have a companion object with an `apply` method, allowing for convenient instantiation.

   ```scala
   val person = Person("Alice", 25)
   ```

### Normal Class:

1. **Mutable by Default:**
   - Normal classes do not have the immutability guarantee. Fields need to be explicitly declared as `val` for immutability.

   ```scala
   class Person(var name: String, var age: Int)
   ```

2. **Manual toString, equals, and hashCode:**
   - In a normal class, you need to manually implement `toString`, `equals`, and `hashCode` methods if you want customized behavior.

   ```scala
   class Person(name: String, age: Int) {
     override def toString: String = s"Person($name, $age)"
   }
   ```

3. **Pattern Matching:**
   - While normal classes can be used in pattern matching, the syntax is often more verbose, and you need to provide an extractor.

   ```scala
   class Person(val name: String, val age: Int)

   object Person {
     def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))
   }

   val person = new Person("Alice", 25)
   person match {
     case Person(name, age) => println(s"Name: $name, Age: $age")
   }
   ```

4. **No Automatic Companion Object with Apply:**
   - Normal classes do not automatically come with a companion object and an `apply` method for easy instantiation.

   ```scala
   // Need to manually create an apply method in the companion object
   object Person {
     def apply(name: String, age: Int): Person = new Person(name, age)
   }

   val person = Person("Alice", 25)
   ```

In summary, case classes are a shorthand notation for creating immutable, data-focused classes in Scala, and they come with various conveniences such as automatic generation of methods and a companion object with an apply method. Normal classes, on the other hand, provide more flexibility but often require more boilerplate code for common tasks. The choice between case classes and normal classes depends on the specific requirements of your use case.


We are using typed JVM objects as arguments to functions.
• We are using dot notation (from object-oriented programming) to access indi‐
vidual fields within the typed JVM object, making it easier to read.
• Some of our functions and lambda signatures can be type-safe, ensuring compiletime error detection and instructing Spark what data types to work on, what
operations to perform, etc.
• Our code is readable, expressive, and concise, using Java or Scala language fea‐
tures in lambda expressions.
• Spark provides the equivalent of map() and filter() without higher-order func‐
tional constructs in both Java and Scala, so you are not forced to use functional
programming with Datasets or DataFrames. Instead, you can simply use condi‐
tional DSL operators or SQL expressions: for example, dsUsage.filter("usage
> 900") or dsUsage($"usage" > 900). (For more on this, see “Costs of Using
Datasets” on page 170.)
• For Datasets we use encoders, a mechanism to efficiently convert data between
JVM and Spark’s internal binary format for its data types (more on that in “Data‐
set Encoders” on page 1






The statement is emphasizing the inefficiency of the default Java serialization mechanism, particularly when dealing with Java objects in the heap memory. Let's break down the key points:

1. **Built-in Java Serializer and Deserializer:**
   - Java provides a default serialization and deserialization mechanism through the `java.io.Serializable` interface.
   - This mechanism allows objects to be converted into a stream of bytes for storage or transmission and then reconstructed back into objects.

2. **Inefficiency of Default Java Serialization:**
   - The default Java serialization process can be inefficient in terms of both speed and space utilization.
   - The serialization process involves converting the entire object state, including metadata, into a byte stream.
   - The default serialization can lead to bloated representations, especially for complex or deeply nested objects.

3. **Java Objects in Heap Memory:**
   - Objects created by the JVM are stored in the heap memory.
   - The heap memory is where dynamically allocated objects reside during the execution of a Java program.

4. **Bloating of Java Objects:**
   - The term "bloated" refers to the fact that the serialized representation of Java objects can be larger than necessary.
   - This bloating is a result of including not just the essential data but also additional metadata and information.

5. **Impact on Performance:**
   - The inefficiency of the default Java serialization has a direct impact on the performance of serialization and deserialization operations.
   - The process can be slow due to the extra time required to serialize and deserialize larger byte streams.

6. **Alternative Serialization Mechanisms:**
   - To address the inefficiencies of default Java serialization, alternative serialization mechanisms may be used.
   - For example, frameworks like Apache Avro, Protocol Buffers, or Apache Parquet offer more efficient serialization formats and provide better performance.

In summary, the inefficiency mentioned in the statement is related to the default Java serialization process, which may result in bloated representations of objects and slow serialization/deserialization operations. Alternative serialization mechanisms are often employed in scenarios where performance and efficiency are critical, especially when dealing with large-scale data processing or communication between distributed systems.

Costs of Using Datasets
In “DataFrames Versus Datasets” on page 74 in Chapter 3, we outlined some of the
benefits of using Datasets—but these benefits come at a cost. As noted in the
preceding section, when Datasets are passed to higher-order functions such as fil
ter(), map(), or flatMap() that take lambdas and functional arguments, there is a
cost associated with deserializing from Spark’s internal Tungsten format into the JVM
object.
Compared to other serializers used before encoders were introduced in Spark, this
cost is minor and tolerable. However, over larger data sets and many queries, this cost
accrues and can affect performance.

### Read for costs using Datasets