<!-- (c) https://github.com/MontiCore/monticore -->

<!-- This is a MontiCore stable explanation. -->

# MontiCore - Literals

Literals are the basis to parse Numbers, Strings and other 
atomic language elements.
Modularization works as follows. 'MCLiteralBasis' defines the root 
nonterminal 'Literal', but no terminals with literal terms.

Concrete terminal symbols are defined in 
[MCCommonLiterals](MCCommonLiterals.mc4) 
and [MCJavaLiterals](MCJavaLiterals.mc4)
and can be included into a concrete language as desired.

## Grammar MCCommonLiterals.mc4

This Grammar includes rules to parse:

* 'null' - NullLiteral
* 'true' & 'false' - BooleanLiteral
* 'a', ... , 'Z' - CharLiteral
* '"..."' - StringLiteral
* '123' - NatLiteral
* '-13' - SignedNatLiteral
* '6L', '6l' - BasicLongLiteral
* '-6L', '-6l', '6L', '6l' - SignedBasicLongLiteral
* '1.2F', '1.2f' - BasicFloatLiteral
* '-1.2F', '-1.2f', '1.2F', '1.2f' - SignedBasicFloatLiteral

## Grammar MCJavaLiterals.mc4

This Grammar extends MCCommonLiterals.mc4 and includes rules to parse:

* '123','0734', '1001001', '0x1a'  - IntLiteral
* '2L', '0734l', '1001001L', '0x1al' - LongLiteral
* '1.23F', '1.23E4f' - FloatLiteral
* '1.23', '1.23d', '1.23E4D'  - DoubleLiteral

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

