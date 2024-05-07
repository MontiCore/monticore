<!-- (c) https://github.com/MontiCore/monticore -->

# SIUnits

This language introduces SI units and allows language developers to
integrate literals enriched with an SI unit to their language. Furthermore,
this language provides functionality for unit compatibility and type
checking.

[[_TOC_]]

## The grammar files are (see details below):

* [SIUnits.mc4][SIUnitGrammar]
* [SIUnitLiterals.mc4][SIUnitLiteralsGrammar]
* [SIUnitTypes4Math.mc4][SIUnitTypes4MathGrammar]
* [SIUnitTypes4Computing.mc4][SIUnitTypes4ComputingGrammar]


## [SIUnits.mc4][SIUnitGrammar]

This is the base grammar of the SIUnits language. It introduces SI
units and other units that can be derived from them.
This grammar defines SI units with their prefixes and complex,
composed SI units allowing us to parse simple and derived units
such as `m`, `km`, `km^2` or `km^2/VA^2h`.

Some Si units are ambiguous, e.g.,
`ms` can be interpreted as metre times second, or as milliseconds.
In ambiguous cases, the interpretation with the prefix is preferred, e.g.,
by the typecheck.
To write metre times second, `m^1s` can be used.

## [SIUnitLiterals.mc4][SIUnitLiteralsGrammar]

The SIUnitLiterals allow to describe concrete values, such as
`5km` or `23.4m/s` that can be used within ordinary expressions.
For that purpose, they combine a NumericalLiteral resp.
SignedNumericalLiteral from the
[MCCommonLiterals.mc4][MCCommonLiteralsGrammar]
grammar with a SIUnit from the [SIUnits](#siunitsmc4) grammar.

SIUnitLiterals allows us to parse literals of the following forms.
Literals in combination with a unit. The unit may be of any form allowed
by the SIUnits.mc4 grammar, i.e. including unit expressions:
* Integer with unit: `5km` or `5 km/h`
* Long with unit: `5l km`
* Float with unit: `5.0km` or `5.0 km`; The space is obligatory for Farads to
  avoid confusion with floats (`5.0 F`)
* Float (explicit) with unit: `5.0f kg` or `5.0F kg`
    * Caution: A space is obligatory for liters to
      avoid confusion with longs (`5 l`)
      Standard unitless literals are parsed as Literals as provided by the
      MCCommonLiterals grammar:
* Unitless integer: `5`
* Unitless long: `5l` or `5L`
* Unitless float: `5.0`
* Unitless float (explicit): `5.0f` or `5.0F`

## [SIUnitTypes4Math.mc4][SIUnitTypes4MathGrammar]

The SIUnitTypes interpret the SIUnits as a type in the MontiCore type universe,
such as `[m]`.
Therefore, the grammar extends
[SIUnits.mc4][SIUnitGrammar]
and [de.monticore.types.MCBasicTypes.mc4][MCBasicTypesGrammar].
A SIUnitType implements the MCType and can therefore be used wherever a type is used,
e.g., when a variable is declared or a method parameter is typed.

The idea of this grammar is that the types are used in a mathematically ideal world (e.g.,
also in early stages of systems development), where no limitations on
the type of numbers plays a role yet. This is opposed to the below-defined
SIUnitTypes4Computing grammar, where concrete types of numbers can be added as well.

## [SIUnitTypes4Computing.mc4][SIUnitTypes4ComputingGrammar]

SIUnitType4Computing interprets the SIUnits as a generic type that
 have exactly one parameter, which is of a numeric type.
This allows one to specify SI unit and underlying numeric type, such as `[km/h]<float>`
or `[m]<long>` in combination and use that as type.
This is more oriented toward computation because it allows one to model
numeric restrictions. Please note that e.g. `[km]<int>` has a different behavior that
`[m]<int>` in terms of rounding and overflow.

Technically, SIUnitType4Computing defines a type expression with an
SI unit as a generic type (not SymTypeOfGenerics)
and a MCPrimitiveType as an argument.
The primitive part must be a numeric type.

Remark: while the syntax of SI Units is very carefully standardized,
the use of SI Units as type definitions, and especially as generic types
is an invention by the MontiCore team. Alternative syntaxes are definitely
possible.

## TypeCheck

There are two main approaches how to handle si units: at compile-time
or at runtime. The former variant is highly preferable as all unit-related
checks and conversions are performed at compile-time and unit information
can be thrown away completely at runtime. This approach is only feasible
if our type system is static, i.e., it is not possible to change the unit 
of a variable at runtime.
A variable with type `[km/h]` will always be interpreted as `[km/h]`.
Assignments of compatible variables, e.g., typed as `[m/s]` lead
to an implicit conversion by the compiler or code generator.

If static typing is not desired in an SI unit-based language and types can be altered
dynamically at runtime, type information needs to be carried at runtime.
Consequentially, all the compatibility checks and conversions need to be
performed by the generated code. This approach is much less efficient,
and we recommend to prefer static typing instead if possible.

* With the [TypeCheck][TypeCheck] class [SymTypeExpressions][SymTypeExpression]
  (MontiCore's internal form of storage for types) can be
  synthesized from a `MCType` and returns a [SymTypeOfSIUnit][SymTypeOfSIUnit]
  or a [SymTypeOfNumericWithSIUnit][SymTypeOfNumericWithSIUnit] for a
  `SIUnitType` or `SIUnitType4Computing`.
* With the [TypeCheck][TypeCheck] class [SymTypeExpressions][SymTypeExpression]
  can be derived from a `Literal` or an `Expression` and returns a [SymTypeOfSIUnit][SymTypeOfSIUnit]
  or a [SymTypeOfNumericWithSIUnit][SymTypeOfNumericWithSIUnit] for expressions
  containing `SIUnits`.
  The resulting types of the most common expressions is defined as follows:
    * The type of a SIUnitLiteral is a `SymTypeOfNumericWithSIUnit`
      as a combination of the types of its NumericLiteral and its SIUnit. \
      E.g. `typeOf(3.2f m) -> [m]<float>`
    * The multiplication of two `SymTypeOfNumericWithSIUnit` results in the
      combination of the multiplication of their numeric parts and
      of their SI unit parts. \
      E.g. `typeOf(3 m * 1.5 s) -> [m^s]<double>`
    * The division of two `SymTypeOfNumericWithSIUnit` results in the
      combination of the division of their numeric parts
      of their SI unit parts. \
      E.g. `typeOf(3 m / 2l s) -> [m/s]<long>`
    * The addition of two `SymTypeOfNumericWithSIUnit` results in the
      combination of the addition of their numeric parts and
      their SI unit parts. The addition of the SI unit parts is only
      defined if the units are compatible (same base units with different prefix)
      and results in the _smaller_ unit. \
      E.g. `typeOf(3 m + 2 s) -> undefined` (results in an error) \
      `typeOf(1.25 h + 30 min) -> [min]<double>` \
      `typeOf(30 min + 1.25 h) -> [min]<double>`
    * Subtraction and modulo are defined in the same way as the addition.

The TypeCheck classes here are:

* [SIUnitBasic][SIUnitBasic]
  which defines the SymType of a single SI unit basis, e.g. `m` or `ms^2`,
  but not `K^2m`, as it contains two SI unit basis.
* [SymTypeOfSIUnit][SymTypeOfSIUnit]
  which contains a numerator and denominator list of SIUnitBasic.
* [SymTypeOfNumericWithSIUnit][SymTypeOfNumericWithSIUnit]
  which contains a numeric type and a SymTypeOfSIUnit.

For more info, see [TypeCheck][Types].

## Runtime: Executing Calculations

While the previous part described, how SI Units are used in grammars
and thus mainly for the concrete and abstract syntax of a modeling language,
we here also discuss possibilities to execute calculations.

There are mainly two approaches, while the latter also comes in variants:

1. Due to the strong typing system that is provided, the generator in principle
   can throw all type information away after consistency has been checked.
   This is efficient and type safe, given a correctly implemented type checker.
   All needed scalar transformations can be explicitly added during the generation
   process, SI Unit information is not present in the values anymore.
   (efficient compilers do exactly that with their datatypes today).
   I.e., `[km/h]<int> x = 100 * 30m / 100sec` would be translated to
   `int x =  (100 * 30 / 60)  * 3600 / 1000` where the later multiplication
   and division handles the scaling. (Further optimization is not needed here,
   but handled by a subsequent compiler.)

2. In some target languages, such as C, C++, or python,
   strong typing doesn't really exist, and people
   have implemented frameworks that make units explicit. I.e., instead of storing
   a value for `[km/h]<int> x` in form of a simple `int` value, an object
   with attributes like `int value`, `String siUnit`, and `int scaleFactor` are
   carried around at runtime.
   [Unit][JavaUnit]
   is such a framework for Java.

    * As a side effect, the SI Units are themselves encoded as values
      (whereas in our language, we treat them as types, what they actually are)
      and thus the typing system is encoded explicitly — a common approach,
      when the programming language doesn't provide a strong typing system.

It is up to the developer of a generator to decide which solution to take.

[SIUnitGrammar]: SIUnits.mc4
[SIUnitLiteralsGrammar]: SIUnitLiterals.mc4
[SIUnitTypes4MathGrammar]: SIUnitTypes4Math.mc4
[SIUnitTypes4ComputingGrammar]: SIUnitTypes4Computing.mc4
[MCCommonLiteralsGrammar]: https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/literals/MCCommonLiterals.mc4
[MCBasicTypesGrammar]: https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/types/MCBasicTypes.mc4
[OOSymbols]: https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/symbols/OOSymbols.mc4

[SIUnitBasic]: ../../../../java/de/monticore/types/check/SIUnitBasic.java
[SymTypeOfSIUnit]: ../../../../java/de/monticore/types/check/SymTypeOfSIUnit.java
[SymTypeOfNumericWithSIUnit]: ../../../../java/de/monticore/types/check/SymTypeOfNumericWithSIUnit.java
[SymTypeExpression]: ../../../../java/de/monticore/types/check/SymTypeExpression.java
[TypeCheck]: ../../../../java/de/monticore/types/check/TypeCalculator.java

[Types]: ../../../../java/de/monticore/types3/TypeSystem3.md

[JavaUnit]: http://unitsofmeasurement.github.io/unit-api/site/apidocs/javax/measure/Unit.html
[JavaMeasure]: http://unitsofmeasurement.github.io/unit-api/site/apidocs/javax/measure/package-summary.html

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
