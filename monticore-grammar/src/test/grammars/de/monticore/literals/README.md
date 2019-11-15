<!-- (c) https://github.com/MontiCore/monticore -->
# MontiCore - Literals

Literals are the basis to parse Numbers, Strings and other predefined terms.
Modularization works as follows. 'MCLiteralBasis' defines the root 
non-Terminal 'Literal', but no terminals with literal terms.

Terminal symbols are defined in 'MCCommonLiterals' and 'MCJavaLiterals'

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


