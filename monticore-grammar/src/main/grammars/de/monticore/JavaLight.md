<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

# JavaLight
JavaLight is a subset of Java. JavaLight provides methods, a subset 
of statments and expressions and basic types. It is possible to enrich the
language with more complex types, expressions and statements.

The grammar file is [`de.monticore.JavaLight`][JavaLight].

##Example
```
public void print(String name) {
  System.out.println("Hello " + name);
}
```
The example shows a simple method with one parameter. Some statements 
(statements for exception handling, continue- and break-statement, etc.) are not
supported. But is it possible to extend the language accordingly.

## Parser
- JavaLight is a component grammar, no parser is generated

## Symboltable
- JavaLight introduces the MethOrConstrSymbol extending TypeSymbols.MethodSymbol.
The symbol receives the additional attributes:
  - annotations
  - exceptions
  - isEllipsisParameterMethod
  - isFinal
  - isAbstract
  - isSynchronized
  - isNative
  - isStrictfp
  
 - A VariableSymbol is created for formal parameters and variable declarations.
 The VariableSymbol is  defined in MCVarDeclarationStatements and extends
 TypeSymbols.Field.
 
## Functionality
### CoCos

### PrettyPrinter
- The basic pretty printer for JavaLight is [`de.monticore.prettyprint.JavaLightPrettyPrinter`][PrettyPrinter]

[JavaLight]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.mc4
[PrettyPrinter]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/java/de/monticore/prettyprint/JavaLightPrettyPrinter.java
