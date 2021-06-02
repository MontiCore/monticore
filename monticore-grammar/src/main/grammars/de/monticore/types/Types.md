<!-- (c) https://github.com/MontiCore/monticore -->

<!-- This is a MontiCore stable explanation. -->

# MontiCore - Types

Type systems are available in a variety of (programming) languages and 
facilitate programming because typing errors can already be detected at compile 
time. To express type usages a language component 
hierarchy for modeling types was developed for MontiCore-based languages. It 
consists of five language components
[`MCBasicTypes`](MCBasicTypes.mc4), 
[`MCCollectionTypes`](MCCollectionTypes.mc4), 
[`MCSimpleGenericTypes`](MCSimpleGenericTypes.mc4),
[`MCFullGenericTypes`](MCFullGenericTypes.mc4), and
[`MCArrayTypes`](MCArrayTypes.mc4).
 
## MCBasicTypes

The most basic language component is [`MCBasicTypes`](MCBasicTypes.mc4) , which provides the central 
interface nonterminal `MCType`. Besides this interface 
nonterminal, this language component provides nonterminals that enable 
modeling primitive types and qualified as well as non-qualified types. 
Furthermore, it provides a return type, which can be an `MCType` or 
`void`. These nonterminals are bundled as it provides a relativity small 
yet useful collection of types for modeling.
When using this language component, types such as `int`, 
`Person`, or `java.lang.String` are expressible.

## MCCollectionTypes

The [`MCCollectionTypes`](MCCollectionTypes.mc4) language component builds upon the 
basic types language component and enables to model four kinds of generics: 
`Set`, `List`, `Map`, and `Optional`. 
These generics cannot be nested as the purpose of this language component is to 
provide some commonly used collection types but limit their functionality such 
that it is useful for high-level models. Using this language component, types 
such as `List<Integer>`, `Set<char>`, or 
`Map<java.lang.String, Person>` are expressible.

## MCSimpleGenericTypes

The language component [`MCSimpleGenericTypes`](MCSimpleGenericTypes.mc4)
 extends the language 
component `MCCollectionTypes` and extends the expressible types with 
custom generics of arbitrary classes with 
arbitrary arguments. When using this language component, types such 
as `Person<String>` or `Map<Person<String>, Integer>` are 
expressible. Please note that these types still do not cover all 
possible types 
from Java, as Java additionally supports inner types of generic types, 
which is not expressible using the language component 
`SimpleGenericTypes`, e.g., types such as `a.b.C<D>.E.F<G>.H` are 
not expressible. 

## MCFullGenericTypes

The language component [`MCFullGenericTypes`](MCFullGenericTypes.mc4) 
extends the language 
component `MCSimpleGenericTypes` and extends the expressible types with 
inner generics types of arbitrary classes with 
arbitrary arguments including wild card types. When using this language component,
types such as `Person<?>`, `Map<Person<String>, ? extends Person>` or `a.b.C<D>.E.F<G>.H` are expressible.

## MCArrayTypes

Arrays are orthogonal to the generic extensions. Thus
they can be combined with any of the above variants.
Language component [`MCArrayTypes`](MCArrayTypes.mc4) provides 
possibilities to add arrays, such as `Person[]` or `int[][]`.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
