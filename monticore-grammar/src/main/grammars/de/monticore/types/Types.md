<!-- (c) https://github.com/MontiCore/monticore -->

<!-- This is a MontiCore stable explanation. -->

# MontiCore - Types

Type systems are available in a variety of (programming) languages and
facilitate programming because they allow for detecting typing errors already 
at compile time. To express type usages in MontiCore-based languages a language
component hierarchy for type modeling was developed. The hierarchy consists of
the following language components:

* [`MCBasicTypes`](MCBasicTypes.mc4)
* [`MCCollectionTypes`](MCCollectionTypes.mc4)
* [`MCSimpleGenericTypes`](MCSimpleGenericTypes.mc4)
* [`MCFullGenericTypes`](MCFullGenericTypes.mc4)
* [`MCArrayTypes`](MCArrayTypes.mc4)
* [`MCFunctionTypes`](MCFunctionTypes.mc4)
* [`MCStructuralTypes`](MCStructuralTypes.mc4)
* [`SIUnitTypes4Math`](../siunit/SIUnitTypes4Math.mc4)
* [`SIUnitTypes4Computing`](../siunit/SIUnitTypes4Computing.mc4)
* [`RegExType`](../regex/RegExType.mc4)
 
## [`MCBasicTypes`](MCBasicTypes.mc4)

`MCBasicTypes` is the most basic language component. It provides the central
interface nonterminal `MCType`. Additionally, it defines nonterminals that
enable modeling primitive types as well as qualified and non-qualified
types. Furthermore, the component comprises a rule that covers return types
which can be `MCType`s or `void`s. In general, the component represents a
relativity small, yet useful, collection of rules for type modeling that
supports statements such as `int`, `Person`, and `java.lang.String`.

## [`MCCollectionTypes`](MCCollectionTypes.mc4)

This language component builds upon `MCBasicTypes` and enables to model four
kinds of generics:

* `Set`
* `List`
* `Map`
* `Optional`

These generics cannot be nested as the purpose of the `MCCollectionTypes`
language component is the provisioning of some commonly used collection types
whose functionality is limited to support the construction of high-level models.
With the language component types such as `List<Integer>`, `Set<char>`, or
`Map<java.lang.String, Person>` become expressible.

## [`MCSimpleGenericTypes`](MCSimpleGenericTypes.mc4)

This language component extends the `MCCollectionTypes` component to allow the
expression of types with custom generics of arbitrary classes with arbitrary
arguments. When using the component, types such as `Person<String>` or
`Map<Person<String>, Integer>` are expressible. Please note that these types
still do not cover all possible Java types as Java additionally supports inner
types of generic types. Specifically, types such as `a.b.C<D>.E.F<G>.H` are
not expressible by `MCSimpleGenericTypes`.

## [`MCFullGenericTypes`](MCFullGenericTypes.mc4)

This language component extends the `MCSimpleGenericTypes` component to allow
the expression of inner generic types of arbitrary classes with arbitrary
arguments including wild card types. When using this language component,
types such as `Person<?>`, `Map<Person<String>, ? extends Person>`, and
`a.b.C<D>.E.F<G>.H` are expressible.

## [`MCArrayTypes`](MCArrayTypes.mc4)

This language component allows to express array types like `Person[]` or
`int[][]`. As array types are orthogonal to other kinds of 
types, `MCArrayTypes` can be
combined with any of the above language components.

## [`MCFunctionTypes`](MCFunctionTypes.mc4)

This language component allows developers to express function types
like `int -> int` or `(String -> int) -> void`.
Function types are independent from and not interfering with 
generic and array types,
`MCFunctionTypes` can be combined with any of the above language components.

## [`MCStructuralTypes`](MCStructuralTypes.mc4)

This language component allows to express additional types
that are defined by the combination of other types,
like the union type `Foo | Bar` which is the type that is "`Foo` or `Bar`".
As structural types are orthogonal to other kinds of types,
`MCStructuralTypes` can be combined with any of the above
language components.

## [`SIUnitTypes4Math`](../siunit/SIUnitTypes4Math.mc4)

When using this language component,
SI unit types like `[km/h]` become expressible.
The documentation can be found in [`SIUnits`](../siunit/SIUnits.md).

## [`SIUnitTypes4Computing`](../siunit/SIUnitTypes4Computing.mc4)

When using this language component,
SI unit types like `[km/h]<int>` become expressible.
The documentation can be found in [`SIUnits`](../siunit/SIUnits.md).

## [`RegExType`](../regex/RegExType.mc4) 

When using this language component,
RegEx types like `R"H(a|e)llo"` become expressible.
This is documented in [`RegEx`](../regex/RegEx.md).

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
