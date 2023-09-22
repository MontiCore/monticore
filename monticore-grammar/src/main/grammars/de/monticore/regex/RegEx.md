<!-- (c) https://github.com/MontiCore/monticore -->
<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

# Regular Expressions and Regular Expression Types

This document describes the MontiCore language **RegEx**,
which introduces regular expressions. 
Based thereon, it introduces a new form of types `RegExType`s that are
used as subtypes of `String` and help to make model even more typesafe.

A regular expression describe *patterns* that can be applied to text
in order to allow/deny certain sequences of characters
and also to find specific sequences within the text. 

## Motivation and Basic Examples

In theoretical computer science,
a regular expression is a character string that is used to
describe sets of character strings with the help of certain syntactic rules.
Regular expressions are used in a variety of applications.
In addition to implementations in many programming languages,
many text editors also process regular expressions
in the *Find and Replace* function.
A simple use case of regular expressions are wildcards.

Regular expressions can be used as filter criteria in text search
by matching the text with the regular expression pattern.
This process is also called pattern matching.
For example, it is possible to find all words from a word list
that begin with S and end with D
without having to explicitly specify the intervening letters or their number.

This project introduces regular expressions as a MontiCore grammar,
allowing modelers to write regular expressions.
The regular expressions which are defined by the grammars in this project
adhere to the definition which can be found at
https://en.wikipedia.org/wiki/Regular_expression#Formal_language_theory.
More specifically,
it is modeled after the Java syntax for regular expressions.

## Examples for Regular Expressions

Below are a few examples for regular expressions
along with words they match
(taken and adapted from https://cs.lmu.edu/~ray/notes/regex/).

| **Regular Expression** | **Example Match**es                        |
| ---------------------- | ------------------------------------------ |
| hello                  | hello                                      |
| gray\|grey             | gray, grey                                 |
| gr(a\|e)y              | gray, grey                                 |
| gr[ae]y                | gray, grey                                 |
| b[aeiou]bble           | babble, bebble, bibble, bobble, bubble     |
| [b-chm-pP]at\|ot       | bat, cat, hat, mat, nat, oat, pat, Pat, ot |
| colou?r                | color, colour                              |
| rege(x(es)?\|xps?)     | regex, regexes, regexp, regexps            |
| go*gle                 | ggle, gogle, google, gooogle, ..           |
| go+gle                 | gogle, google, gooogle, ...                |
| g(oog)+le              | google, googoogle, googoogoogle, ...       |
| z{3}                   | zzz                                        |
| z{3,6}                 | zzz, zzzz, zzzzz, zzzzzz                   |
| z{3,}                  | zzz, zzzz, zzzzz, ...                      |
| [Bb]rainch\\\*\\\*k    | Brainch\*\*k, brainch\*\*k                 |
| \d                     | 0,1,2,3,4,5,6,7,8,9                        |
| \d{5}(-\d{4})?         | 12345-1234, 67890                          |
| 1\d{10}                | 11231231231, 19999999999                   |
| [2-9]\|[12]\d\|3[0-6]  | 2, 3, 4, ..., 34, 35, 36                   |
| Hello\nworld           | Hello **[newline]** world                  |
| mi.....ft              | microsoft, mi.....ft, mi123456ft           |
| \d+(\.\d\d)?           | 123.45, 6.78, 9                            |
| [^i*&2@]               | *anything besides i, \*, &, 2 or @*        |
| //\[^\r\n]*[\r\n]      | //hello world, //I am a comment            |
| ^dog                   | dog *at the start of a line*               |
| dog$                   | dog *at the end of a line*                 |
| ^dog$                  | *a line which only contains* dog           |

## Language Components for Regular Expressions

The regular expressions which are defined by these grammars
adhere to the definition which can be found at 
https://en.wikipedia.org/wiki/Regular_expression#Formal_language_theory.
More specifically,
it is modeled after the Java syntax for regular expressions.

This RegEx language component contains

* two grammars, 
* context conditions for a regular expression
* a symbol table infrastructure, and
* pretty-printers for both grammars, resp. their main nonterminals.

## Grammars

The RegEx language component contains two grammars:
1. [RegularExpressions](./RegularExpressions.mc4),
1. [RegExType](./RegExType.mc4)

# RegularExpressions

The grammar [RegularExpressions](./RegularExpressions.mc4)
is a component grammar which defines the syntax of regular expressions.
The grammar is supposed to be embedded in DSLs
where a regular expression is of use,
e.g. when describing allowed input patterns.

The grammar only extends the grammar [MCCommonLiterals][MCCommonLiteralsRef]
as it uses the *Digit* token as well as the *Name* nonterminal.
Although the main nonterminals is called *RegularExpression*,
this grammar and its nonterminals are defined
independent of MontiCore's expression grammars.

The grammar uses a special mode `REGEX`
to distinguish regular expressions from, e.g., from algebraic expressions.

The nonterminal **RegularExpression** is designed in such a way
that it assumes that the mode `REGEX` has already been switched on
(and will be switched off) by the calling nonterminal.
This is provided by the nonterminal `RegExLiteral` in the grammar.

The [TestRegularExpressions](../../../../../test/grammars/de/monticore/regex/TestRegularExpressions.mc4)
grammar contains an example that uses
the [RegularExpressions](./RegularExpressions.mc4) grammar.

## Context Conditions for the RegularExpressions language

* [```RangeHasLowerOrUpperBound```](../../../../java/de/monticore/regex/regularexpressions/_cocos/RageHasLowerOrUpperBound.java)  
ensures that the nonterminal **RangeQualification**
* is bounded in at least one direction.
* [```RegExTypeCheckCoCo```](../../../../java/de/monticore/regex/types/check/cocos/RegExTypeCheckCoCo.java)  
checks compatibility of types for the usage of regular expression types.

## Symbol Table for the RegularExpressions language

RegularExpressions introduces symbols for
* named capturing groups ([```NamedCapturingGroupSymbol```](./RegularExpressions.mc4)), which can then, e.g., be used to select a part of the accepted pattern for later repeated matching.

# RegExType

The grammar [RegExType](./RegExType.mc4) extends the *MCType* hierarchy
by the notion of regular types.
The new nonterminal *RegExType* consists of a regular expression
embedded in `R"..."`.
It is used to define a subtype of `String`
which contains only the text accepted by the regular expressions. 

Therefore, this grammar is used to define a type system for strings,
which can be used to define variables
that can only have values of a subset of all strings.
This is, e.g., helpful in notoriously unsafe infrastructures,
such as Internet-Of-Things or otherwise distributed systems,
where permanently strings are accepted from unsafe external sources.

The grammar [RegExType](./RegExType.mc4) extends the grammars
* [RegularExpressions](./RegularExpressions.mc4) to use regular expressions, and 
* [MCBasicTypes][MCBasicTypesRef] for the nonterminal *MCType*


## TypeCheck for RegExType

TypeChecks are important when using the **RegExType** grammar,
as they prevent the assignments of irregular values
to variables with a regular expression type.
Unfortunately, the type checks for our regular expressions
cannot be completely performed at compile-time,
as checking whether two different expression types are compatible
is not solvable in general,
because of the extended capabilities of the language,
which in fact goes beyond mere regularity.
However, if variables/fields with a regular expression type get assigned
a static string, the compatibility can be checked at compile-time.
And also, in a number of cases, when a string of one known regular type
is assigned to a variable of a second regular type,
the subtyping property can be statically resolved.

The TypeCheck classes in this project provide functionality
using MontiCore's type checking mechanism.
A new SymTypeExpression **SymTypeOfRegEx** is introduced,
which carries information about which type of regular expression it accepts
and is a subtype of the SymTypeExpression *String*.

For more info see [TypeSystem][Types].

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

[MCCommonLiteralsRef]:https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/literals/MCCommonLiterals.mc4
[MCBasicTypesRef]:https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCBasicTypes.mc4
[Types]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/java/de/monticore/types3/TypeSystem3.md
[SymTypeExpression]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/java/de/monticore/types/check/SymTypeExpression.java
