<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Grammar Status Plans - an Overview

[MontiCore](https://www.monticore.de) uses grammars as primary mechanism 
to describe DSLs and DSL components. The extended 
grammar format allows to **compose language components** by
(1) inheriting, (2) extending, (3) embedding 
and (4) aggregating grammars (see the reference manual for details).
From the grammars a lot of infrastructructure is generated, that is as well
composable, can be **extended with handwrittten code** and most imprtandly, these
extensions and the grammar composition are compatible, which
leads to optimal forms of **reuse**.

To improve understanding, what will happen with a grammar, we define the 
following set of stati and mention the status of each grammar,
both in the explanation and in the grammar itself:

## Status of a Grammar 

1. **MontiCore stable**:
Such a grammar is meant to be stable in the further development of 
MontiCore. The grammar is tested and assumed to be of high quality.
It may rarely happen that smaller extensions are made in a conservative 
form, which means that (1) composition with any other grammars,
(2) extensions and adaptations and (3) handwritten extensions will 
still work.

1. **Beta: In Stabilization**:
Such a grammar is in the process of becoming stable. One might already 
include the grammar, but some changes may still appear.
(See task list for potential changes.)

1. **Alpha: Intention to become stable**:
Such a grammar is relatively fresh, but intended to become stable 
and useful. Changes may occur, e.g. when restructuring or bug fixing.
Or it may be taken out of the process and become one of the following:

1. **Example**:
The grammar serves as working example, but will not have high priority on
keeping the grammar up to date. One might use it as inspiration for their
own developments.

1. **Deprecated**:
The grammar should not be used anymore, it is deprecated, and only 
there for compatibility. Normally a newer version of the content 
exists in other, often decomposed grammars, allowing to configure
which part of the grammar to be used.
Deprecated grammars are not listed in any overview.


## Marking the Status of Grammars

A comment of the following form within the grammar defines this status:

1. `/* This is a MontiCore stable grammar.`
    ` * Adaptations -- if any -- are conservative. */`
2. `/* Beta-version: This is intended to become a MontiCore stable grammar. */`
2. `/* Alpha-version: This is intended to become a MontiCore stable grammar. */`
   (but sometimes also omitted)

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

