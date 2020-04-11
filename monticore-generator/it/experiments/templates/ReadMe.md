# Template Example

The main class 
[`AutomataTool`](src/main/java/automata/AutomataTool.java) reads in 
an automata model and produces an implementation of the state 
pattern. The generator utilizes the templates in 
[`src/main/resources`](src/main/resources). 

The test [`GeneratorTest`](src/test/java/automata/GeneratorTest.java) 
demonstrates the use of the main class. The two contained tests 
execute the generator for the `PingPong` model and the `Simple12` 
model located in 
[`src/test/resources/example`](src/test/resources/example). 

After executing the 
[`GeneratorTest`](src/test/java/automata/GeneratorTest.java), the 
produced code is located in  
[`target/statepattern`](target/statepattern). 

The generator result is tested by the classes located in 
[`src/extendedTest/java`](src/extendedTest/java). The TOP mechanism 
as well as the state pattern implementation are tested. For the TOP 
mechanism a class [`PingPong`](src/extendedTest/java/PingPong.java) 
is implemented that replaces the corresponding generated class. The 
state pattern implementation is unit tested in 
[`PingPongTest`](src/extendedTest/java/PingPongTest.java).  

