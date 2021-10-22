<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Beta-version: This is intended to become a MontiCore stable explanation. -->

# JavaLight
The JavaLight language defines a subset of the Java
programming language. The language introduces Java
method declarations, constructor declarations,
interface method declarations, attributes, a subset
of statements and expressions, includes all basic types, and
allows annotations. 
The JavaLight language does not define
classes and interfaces. 

The JavaLight language allows to parse
* all forms of **attribute and method declarations**.
* all forms of **Java expressions**, (including short forms of incrementation, such as `i++`)
* and almost all **Java statements**, with the exception of 
  statements for exception handling, continue- and break-statement, amd synchronization,
  which are omitted because there are many DSLs, where these are of no use.

## Example
```
public int print(String name, Set<Person> p) {
  int a = 2 + name.length();
  if(a < p.size()) {
    System.out.println("Hello " + name);
  }
  return a;
}
```
The example shows a simple Java method with one parameter and three statements. 
Expressions are supported completely and from the statements only the Java specific
forms for exception handling, continue- and break-statements are omitted.

## Grammar

- The grammar file is [`de.monticore.JavaLight`][JavaLight].
  It mainly deals with the definition of the method and constructor signatures, 
  and the annotations, while it reuses MontiCore's library components 
  `AssignmentExpressions`, `JavaClassExpressions`, `MCCommonStatements`, 
  and `MCArrayStatements` for expressions and statements.

## Extension of JavaLight

JavaLight is designed for _easy reuse_.  
It can be **extended** by domain specific constructs, such as 
   1. special **statements** for message processing (`c?x; c!3+x`), or
   2. **statements** for testing such as Hoare-like `asserts` or pre/postconditions 
   3. additional logical or otherwise interesting **expression** operators 
      (`forall x in Set:`) 
   4. The omitted Java-special statements, such as eception handling can be addded
      through a predefined language library component. 

JavaLight is fully compatible with various expression language components
that [MontiCore's library](XXXurlToMD-File) provides. These extensions can 
simply be added by MontiCore's language composition mechanism 
(see [Handbook](http://monticore.de/handbook.pdf)).

## Embedding of JavaLight `Expression` or `Statement`

JavaLight's **expressions** can be embedded as expression sublanguage in any 
other interesting domain specific language, such as Automata, Activity
Diagrams, etc. (even MontiCore's primary language uses this).
The **statements** can also be embedded as sublanguage e.g. as actions in 
StateCharts.
   
JavaLight is a strict subset of the Java programming language and
thus can be mapped directly to itself when generating code for Java.

## Parser
- JavaLight is a component grammar. To gretrieve a parser it is to be embedded into a full grammar. 

## Symboltable
- JavaLight introduces the `JavaMethodSymbol` extending the provided `MethodSymbol`
 for general object-oriented types.
 The `JavaMethodSymbol` class carries the additional attributes:
  - annotations
  - exceptions
  - and Booleans for isEllipsisParameterMethod, isFinal, isAbstract, isSynchronized, isNative, and isStrictfp


## Symbols
- Import: the following symbols can be used from outside, when the symbol table 
  in the embedding language provides these symbols:
  - `VariableSymbol` for attributes and otherwise accessible variables
  - `MethodSymbol` for method and constructor calls 
  - `TypeSymbols` for using types, e.g., defined via classes, interfaces 
    (used for example in constructor statements and type definitions), and enums.
- Symbol definition and export: It is possible to define new symbols, for attributes, 
  methods, and constructors. The provided symbol table will include them as
  - `VariableSymbol` for attributes
  - `MethodSymbol` for methods and constructors 
  and thus will make the accessibility of these symbols available outside the JavaLight 
  sub-models.

## Functionality
### CoCos
- CoCos are currently being explored and implemented.

### PrettyPrinter
- The basic pretty printer for JavaLight is [`de.monticore.prettyprint.JavaLightPrettyPrinter`][PrettyPrinter]

- When the expression language is used as high-level language, it might make sense to map attribute
  access to get-functions respectively also use set-functions for modification.
  This can be done using a more elaborated pretty printer.

[JavaLight]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.mc4
[PrettyPrinter]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/java/de/monticore/prettyprint/JavaLightPrettyPrinter.java


## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [License definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

