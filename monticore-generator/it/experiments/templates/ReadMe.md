<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Template Experiment

This experiment demonstrates how to write and test templates. 

The experiment consists of three phases:

1. The generator uses automata, e.g. `PingPong.aut`, to generate a 
set of Java classes.

1. The resulting classes are compiled. This is a first test to see 
whether the generator actually produces syntactically valid code. 

1. Handwritten tests, e.g. `PingPongTest.java` are executed against 
the generated code to see whether the generated methods actually 
behave as desired.

The rationale behind this form of procedures consists of two 
observations: (1) It is easier and also more robust against changes 
to execute the generated code than to examine the sources for textual 
patterns. (2) Generators usually produce code in a systematic manner, 
so even if the handwritten tests are only valid for a specific model 
(here `PingPong.aut`) the general behavioral patterns are valid for 
other models too. However, pathological cases as well as exceptional 
cases needs to be exercised by the handwritten tests -- please 
"cover" the templates with appropriate tests by appropriate models 
and Java test code.

Remark 1: The experiment is based on the automaton experiment, where 
the Automata language is defined and the tooling fronted (parser, 
etc.) are derived. 

Remark 2: Neither the written nor the resulting code are optimized
according to software engineering best practices, but for presentable
results (e.g. in slides).


## Technical realization 

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

