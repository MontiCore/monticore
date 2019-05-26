
# MontiCore Grammars - an Overview

[MontiCore](http://www.monticore.de) is a language workbench. It uses 
grammars to describe DSLs. The extended 
grammar format allows to compose grammars, to inherit, extend, embedd 
and aggregate grammars (see the reference manual for details).

Here comes a list of grammars available in the MontiCore core project 
together with short descriptions and their status:

----

{:toc}

## Status of Grammars 

The typical status of a grammar is:

1. **MontiCore stable**:
Such a grammar is meant to be stable in the further development of 
MontiCore. The grammar is tested and assumed to be of high quality.
It may rarely happen that smaller extensions are made in a conservative 
form, which means that (1) composition with any other grammars,
(2) extensions and adaptations and (3) handwritten extensions will 
still work.

1. **Intended to become stable (Beta)**:
Such a grammar is in the process of becoming stable. One might already 
include the grammar, but some changes may still appear.
(See task list for potential changes.)

1. **Deprecated**:
The grammar should not be used anymore, it is deprecated and a newer
version of the content exists in another grammar.

1. **Unclarified**
Some of the grammars are just there to be used for example as
tests or as inspirations for your own definitions. It may be that 
such a grammar becomes stable, if enough interest exists.

The deprecated grammars are typically not listed in this overview.
There may also be further unclarfied grammars around.


## List of Grammars in package de.monticore

### MCBasics.mc4
* This grammar defines types absolute basics, such as spaces and 
  Java like comments. 
  It should be useful in many languages.

* MCBasics.mc4
   * This grammar defines types absolute basics, such as spaces and 
     Java like comments. 
     It should be useful in many languages.

* **MCBasics.mc4**
   * This grammar defines types absolute basics, such as spaces and 
     Java like comments. 
     It should be useful in many languages.



## List of Grammars in package de.monticore.types



## List of Grammars in package de.monticore.expressions






