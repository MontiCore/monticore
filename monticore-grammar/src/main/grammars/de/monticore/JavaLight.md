<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Beta-version: This is intended to become a MontiCore stable explanation. -->

# JavaLight
The JavaLight language defines a subset of the Java
programming language. The language introduces Java
method declarations, constructor declarations,
interface method declarations, attributes, a subset
of statments and expressions, basic types, and
annotations. The JavaLight language neither defines
classes nor interfaces. However, it is easily reusable
and extensible for the creation of more complex
languages such as the complete Java programming language.

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
  
 - A VarDeclSymbol is created for formal parameters and variable declarations.
 The VarDeclSymbol is  defined in MCVarDeclarationStatements and extends
 TypeSymbols.Field.
 
## Functionality
### CoCos

### PrettyPrinter
- The basic pretty printer for JavaLight is [`de.monticore.prettyprint.JavaLightPrettyPrinter`][PrettyPrinter]

[JavaLight]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.mc4
[PrettyPrinter]: https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/java/de/monticore/prettyprint/JavaLightPrettyPrinter.java


## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

