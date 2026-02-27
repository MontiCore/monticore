<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

This runtime contains non-language-specific classes
for expressions, and statements generated from MontiCore models.
E.g., A subset of MontiCore languages supports tuples
and they can be represented in Java
using the corresponding runtime classes;

```java
Tuple2<Integer, String> pair = Tuple2.of(42, "Hello");
```

There are generated classes (s. below),
as well as handwritten ones.

## Handwritten Runtime Classes

* [FCollection](/src/main/java/de/monticore/rte/collections/FCollection.java)
  (A collection interface for side effect free collections)
   * [FList](/src/main/java/de/monticore/rte/collections/FList.java) 
     (A list interface for side effect free lists)
      * [FLinkedList](/src/main/java/de/monticore/rte/collections/FLinkedList.java) 
        (A linked list implementation of FList)
   * [FSet](/src/main/java/de/monticore/rte/collections/FSet.java) 
     (A set interface for side effect free sets)
      * [FHashSet](/src/main/java/de/monticore/rte/collections/FHashSet.java) 
        (A hash-set implementation of FSet)
   * [FMap](/src/main/java/de/monticore/rte/collections/FMap.java) 
     (A map interface for side effect free maps)
     * [FHashMap](/src/main/java/de/monticore/rte/collections/FHashMap.java) 
       (A hash-map implementation of FMap)
* [Stream](/src/main/java/de/monticore/rte/streams/Stream.java)
  (A potentially infinite stream of values to describe,
  e.g., the input and output of components)   
   * [EventStream](/src/main/java/de/monticore/rte/streams/EventStream.java)
     (A stream of values, separated into time units of time)
   * [SyncStream](/src/main/java/de/monticore/rte/streams/SyncStream.java)
     (A time synchronous stream of values, 
     where exactly one value is present for each unit of time)
   * [ToptStream](/src/main/java/de/monticore/rte/streams/ToptStream.java)
     (A time synchronous stream of values,
     where either a value is present or not for each unit of time)
   * [UntimedStream](/src/main/java/de/monticore/rte/streams/UntimedStream.java)
     (A stream of values without further timing information)

## Code Generation

Some runtime classes are generated.
The generator's entry point is located in
[Generator.java](src/generator/java/de/monticore/rte/generator/Generator.java).

Generated are
* Tuple classes, e.g., `Tuple2<T0, T1>`
  (a tuple with two elements)
* Function classes, e.g., `Function1<R, T0>`
  (a function with one parameter of type `T0` and return type `R`)
* Action classes, e.g., `Action2<T0, T1>`
  (a function with two parameters of type `T0` and `T1` and no return type)

## References

* [A Little Synopsis on Streams, Stream Processing Functions, and State-Based Stream Processing](https://www.se-rwth.de/publications/A-Little-Synopsis-on-Streams-Stream-Processing-Functions-and-State-Based-Stream-Processing.pdf)