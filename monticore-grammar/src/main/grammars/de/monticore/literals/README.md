<!-- (c) https://github.com/MontiCore/monticore -->
# MontiCore - Literals

Literals are the basis to parse Numbers, Strings and other predefined terms.
Modularization works as follows. 'MCLiteralBasis' defines the root 
non-Terminal 'Literal', but no terminals with literal terms.

Terminal symbols are defined in 
[MCCommonLiterals](MCCommonLiterals.mc4) 
and [MCJavaLiterals](MCJavaLiterals.mc4).

## Grammar - MCCommonLiterals

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

##Grammar - MCJavaLiterals

This Grammar includes rules to parse:
* '123','0734', '1001001', '0x1a'  - IntLiteral
* '2L', '0734l', '1001001L', '0x1al' - LongLiteral
* '1.23F', '1.23E4f' - FloatLiteral
* '1.23', '1.23d', '1.23E4D'  - DoubleLiteral


