<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Hook Experiment

This experiment demonstrates how to adapt a template based generation mechanism.

For this purpose, we reuse the template-based generation of
statement and code from state machines.
The entire code is given in the Template experiment and
we only add adaptations. 

Remark 1: The experiment is based on the automaton experiment, where 
the Automata language is defined and the tooling fronted (parser, 
etc.) are derived. 

Remark 2: The experiment is based on the templates experiment, where 
the tooling backend (generator, templates, 
etc.) are derived. 

Remark 3: Neither the written nor the resulting code are optimized
according to software engineering best practices, but for presentable
results (e.g. in slides).

## Technical realization 

TODO XXX

* Main class 
[`AutomataTool`](src/main/java/automata/AutomataTool.java) reads in 
an automata model and produces Java code that implements the well 
known state design pattern. The generator utilizes the templates in 
[`src/main/resources`](src/main/resources). 

* The test 
[`GeneratorTest`](src/test/java/automata/GeneratorTest.java) 
demonstrates the use of the main class. The two contained tests 
execute the generator for the `PingPong` model and the `Simple12` 
model located in 
[`src/test/resources/example`](src/test/resources/example). This 
class doesn't really test, but using Junit test infrastructure is 
convenient and allows to add tests if desired.

* The produced code is located in `target/PingPong`, etc. 

* This code is tested by the classes located in 
[`src/extendedTest/java`](src/extendedTest/java). 

* The TOP mechanism (which allows for extension of the generated 
code) is tested by the handwritten class 
[`PingPong`](src/extendedTest/java/PingPong.java) that extends the 
corresponding generated class. 

* The functionality of the state pattern implementation is tested in 
[`PingPongTest`](src/extendedTest/java/PingPongTest.java) using some 
unit tests.  

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
