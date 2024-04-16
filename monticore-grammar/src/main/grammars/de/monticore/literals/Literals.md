<!-- (c) https://github.com/MontiCore/monticore -->

<!-- This is a MontiCore stable explanation. -->

# MontiCore - Literals

Literals are the basis to parse numbers, strings and other atomic language
elements. The language module `MCLiteralBasis` defines the root nonterminal
`Literal` but no other terminals representing literal terms.
[MCCommonLiterals](MCCommonLiterals.mc4),
[MCJavaLiterals](MCJavaLiterals.mc4), and
[SIUnitLiterals](../siunit/SIUnitLiterals.mc4)
define concrete literal terms that can 
be included in a MontiCore-based language as desired.

## Grammar [`MCCommonLiterals.mc4`](MCCommonLiterals.mc4)

This grammar includes the following parser rules:

* `NullLiteral`: Recognizes `null`.
* `BooleanLiteral`: Recognizes `true` and `false`.
* `CharLiteral`: Recognizes `a`, ... , `Z`.
* `StringLiteral`: Recognizes `"..."`.
* `NatLiteral`: Recognizes literals like `123`.
* `SignedNatLiteral`: Recognizes literals like `-13`.
* `BasicLongLiteral`: Recognizes literals like `6L` and `6l`.
* `SignedBasicLongLiteral`: Recognizes literals like `-6L`, `-6l`, `6L?`, and
`6l`.
* `BasicFloatLiteral`: Recognizes literals like `1.2F` and `1.2f`.
* `SignedBasicFloatLiteral`: Recognizes literals like `-1.2F`, `-1.2f`, `1.2F`,
and `1.2f`.

## Grammar [`MCJavaLiterals.mc4`](MCJavaLiterals.mc4)

This grammar extends `MCCommonLiterals` and includes the following parser
rules:

* `IntLiteral`: Recognizes literals like `123`, `0734`, `1001001`, and `0x1a`.
* `LongLiteral`: Recognizes literals like `2L`, `0734l`, `1001001L`, and
`0x1al`.
* `FloatLiteral`: Recognizes literals like `1.23F` and `1.23E4f`.
* `DoubleLiteral`: Recognizes literals like `1.23`, `1.23d`, `1.23E4D`.

## Grammar [`SIUnitLiterals.mc4`](../siunit/SIUnitLiterals.mc4)

This grammar allows recognition of SI unit literals like `1km/h`.
This is documented in [`SIUnits`](../siunit/SIUnits.md).

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

