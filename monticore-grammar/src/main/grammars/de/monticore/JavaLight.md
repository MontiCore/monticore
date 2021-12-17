<!-- (c) https://github.com/MontiCore/monticore -->

<!-- This is a MontiCore stable explanation. -->

# JavaLight
The JavaLight language defines a subset of the Java
programming language. The JavaLight language is dedicated 
for embedding Java language elementsin arbitrary DSLs.
It is therfore defined in a compositional style,
i.e. for black box reuse (without need for copy-paste).

The JavaLight language introduces
* all forms of **attribute declarations**,
* all forms of **method declarations** (including **constructors**),
* all forms of **Java expressions** (including those with side effects, such as `i++`),
* almost all **Java statements**, with the exception of 
  statements for exception handling, continue- and break-statement, amd synchronization,
  which are omitted because there are many DSLs, where these are of no use;
* and it allows to import usable types, method, and attribute symbols,
  which may be predefined or imported from of Java-like models.

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
The example shows a Java method with one parameter and three statements. 
Expressions are supported in all their forms.

## Grammar

- The main grammar file is [`de.monticore.JavaLight`][JavaLight].
  It deals with the definition of the _method_ and _constructor signatures_, 
  and the _annotations_, while it reuses MontiCore's library components 
  `AssignmentExpressions`, `JavaClassExpressions`, `MCCommonStatements`, 
  and `MCArrayStatements` for expressions and statements.

## Extension of JavaLight

JavaLight is designed for **easy black-box reuse**.  
It can be **extended** by domain specific constructs, such as 
   1. special **statements** for message processing (`c?x; c!3+x`), 
   2. **statements** for testing such as Hoare-like `asserts` or pre/postconditions, or
   3. additional logical or otherwise interesting **expression** operators 
      (`forall x in Set:`). 
   4. The omitted Java-special statements, such as eception handling, can also 
      be addded through a predefined language library component. 

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
- JavaLight is a component grammar. To retrieve a parser it is to be 
  embedded into a full grammar. 


## Symboltable and Symbol classes
- JavaLight introduces the `JavaMethodSymbol` extending the existing `MethodSymbol`
  for general object-oriented types.
  The `JavaMethodSymbol` class carries the additional attributes:
  - annotations,
  - exceptions,
  - and Booleans for `isEllipsisParameterMethod`, `isFinal`, `isAbstract`, 
    `isSynchronized`, `isNative`, and `isStrictfp`.


## Symbols (imported and exported)
- Import: the following symbols can be imported from outside, when the symbol table 
  in the embedding language provides these symbols:
  - `VariableSymbol` for attributes and otherwise accessible variables.
  - `MethodSymbol` for method and constructor calls (this includes also `JavaMethodSymbol`).
  - `TypeSymbols` for using types, e.g., defined via classes, interfaces, and enums.
- Symbol definition and export: It is possible to define new symbols, for attributes, 
  methods, and constructors. The provided symbol table will include them as
  - `VariableSymbol` for attributes
  - `MethodSymbol` for methods and constructors 
  and thus will make the accessibility of these symbols available outside the JavaLight 
  sub-models.

## Functionality

As usual functionality is implemented in a compositional form, 
which means that in a composed language these functions should be
largely reusable as pre-compiled black-boxes.

### Context Conditions
JavaLight defines the following CoCos:
* [```ConstructorFormalParametersDifferentName```](../../../java/de/monticore/javalight/cocos/ConstructorFormalParametersDifferentName.java)  
checks if all input parameters of a constructor have distinct names.
* [```ConstructorModifiersValid```](../../../java/de/monticore/javalight/cocos/ConstructorModifiersValid.java)  
checks that a constructor is not marked as `abstract`, `final`, `static`, or `native`.
* [```ConstructorNoAccessModifierPair```](../../../java/de/monticore/javalight/cocos/ConstructorNoAccessModifierPair.java)  
checks that no duplicate visibility occurs for a constructor.
* [```ConstructorNoDuplicateModifier```](../../../java/de/monticore/javalight/cocos/ConstructorNoDuplicateModifier.java)  
checks that no duplicate modifier occurs for a constructor.
* [```MethodAbstractAndOtherModifiers```](../../../java/de/monticore/javalight/cocos/MethodAbstractAndOtherModifiers.java)  
checks that an `abstract` method is not marked as `private`, `static`, `final`, `native`, or `synchronized`.
* [```MethodBodyAbsence```](../../../java/de/monticore/javalight/cocos/MethodBodyAbsence.java)  
ensures that a method body may only be absent for `abstract` or `native` methods. 
* [```MethodBodyPresence```](../../../java/de/monticore/javalight/cocos/MethodBodyPresence.java)  
checks that a method with a present method body is neither `abstract` nor `native`. 
* [```MethodExceptionThrows```](../../../java/de/monticore/javalight/cocos/MethodExceptionThrows.java)  
ensures that thrown exceptions are of type `java.lang.Throwable`.
* [```MethodFormalParametersDifferentName```](../../../java/de/monticore/javalight/cocos/MethodFormalParametersDifferentName.java)  
checks if all input parameters of a method have distinct names.
* [```MethodNoDuplicateModifier```](../../../java/de/monticore/javalight/cocos/MethodNoDuplicateModifier.java)  
checks that no duplicate modifier occurs for a method.
* [```MethodNoNativeAndStrictfp```](../../../java/de/monticore/javalight/cocos/MethodNoNativeAndStrictfp.java)  
checks that method is not marked as `native` and `strictfp`.

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

