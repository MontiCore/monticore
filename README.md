<!-- (c) https://github.com/MontiCore/monticore -->
[![Build Status](https://travis-ci.org/MontiCore/monticore.svg?branch=master)](https://travis-ci.org/MontiCore/monticore)


# MontiCore - Language Workbench And Tool Framework 

[MontiCore](http://www.monticore.de) is a language workbench for an efficient 
development of domain-specific languages (DSLs). It processes an extended 
grammar format which defines the DSL and generates  components for processing 
the documents written in the DSL. Examples for these components are parser, 
AST classes, symboltables or pretty 
printers. This enables a user to rapidly define a language and use it together 
with the MontiCore-framework to build domain specific tools. 

Some MontiCore advantages are the reusability of predefined language 
components, conservative extension and composition mechanisms and an 
optimal integration of hand-written code into the generated tools. Its 
grammar languages is rather comfortable. 

## License overview (informal description) 

Summary: This project is freely available software; you can redistribute 
the MontiCore language workbench according to the following rules.

The MontiCore Languag Workbench deals with three levels of code 
(MontiCore, tool derivates, product code). Each with different 
licenses: 

* Product code: when you use a MontiCore tool derivate to generate 
code, the generated code is absolutely free for each form of use 
including commercial use without any license. 

* Tool derivate: when you derive a tool using the MontiCore language 
workbench, then you mention that it is a MontiCore derivate. There is 
no other restriction. (BSD 3 Clause license) 

* MontiCore adaptations: you may also freely adapt MontiCore itself, 
but then you have to mention MontiCore AND the resulting code is to be 
pushed back into this LPGL repository (LGPL license). 

As a consequence using MontiCore during development is rather flexible 
and the final products do not have any restriction.

 
## MontiCore 3-Level License on files (informal description)

The MontiCore language workbench contains three kinds of artifacts: 

* Java-files that are executed in the MontiCore LWB. They are under 
LGPL licence.

* Java-files that belong to the runtime environment (RTE) and are thus 
copied to the generated code. They are under BSD 3 Clause license.

* Templates executed during generation. They are also under BSD 3 
Clause license, because parts of them are copied to the generated code. 

This approach achieves the goals described above.

Please note that tool builders design their own templates and RTE to 
generate the final product. 
 
If questions appear e.g. on building an interpreter, please contact 
monticore@se-rwth.de. 


## General disclaimer

(Repeated from the the BSD 3 Clause license): 

This software is provided by the copyright holders and contributors
"as is" and any expressed or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.


## Included Software

This product includes the following software:
* [AntLR](http://www.antlr.org/)
* [FreeMarker](http://freemarker.org/)

## Contribution 

When you want to contribute: Please make sure that your complete workspace only 
uses UNIX line endings (LF) and all files are UTF-8 without BOM. On Windows you should 
configure git to not automatically replace LF with CRLF during checkout 
by executing the following configuration: 

    git config --global core.autocrlf input
    
## Build MontiCore

MontiCore is currently partially still built using maven, but partially 
already migrated to gradle. It is recommended to use the MontiCore internal gradle
wrapper (`gradlew`).

Please note that from the top level build script, not everything is built and 
all tests executed. It is a deliberate decision, to exclude some of the longer 
lasting tasks.

* build the productive code (including the unit tests, ~8 min)
`mvn install`
  * skipping the unit tests: `mvn install -Dmaven.test.skip=true`
* run integration tests (which are not included in the unit tests, ~30 min)   
  * Integration tests of the generator: 
    * maven (deprecated): `mvn install -f monticore-generator/it/pom.xml` or 
    * gradle: in `monticore-generator/it/` call `gradlew build`
  * EMF Integration tests of the generator: 
    * maven (deprecated): `mvn install -f monticore-generator/it/pom.xml -P emf-it-tests` or 
    * gradle: in `monticore-generator/it/` call `gradlew build -PbuildProfile=emf`
  * Experiments (from the Reference Manual) as integration tests:
    * maven (deprecated): `mvn install -f monticore-generator/it/experiments/pom.xml` or
    * gradle: in `monticore-generator/it/experiments/` call `gradlew build`
  * Grammar integration tests:
     * in `monticore-grammar/monticore-grammar-it` call `gradlew build`
  * TemplateClassGenerator integration tests 
    * maven (deprecated): `mvn install -f /monticore-templateclassgenerator/it/monticore-templateclassgenerator-it/pom.xml` or 
    * gradle: in `/monticore-templateclassgenerator/it/monticore-templateclassgenerator-it` call `gradlew build`
* clean:
  * call `mvn clean`
  * cleaning integration tests:
    * using maven (deprecated): `mvn clean` (including the `-f` argument, see above) 
    * using gradle `gradlew clean` within the corresponding directory (see above)

  
## Further Information

* [**List of grammars**](monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
   allowing to define your own language efficiently

* [**MontiCore Reference Manual**](http://www.monticore.de/)

* [Changelog](CHANGELOG.md) - Release Notes

* [Licenses](00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md) - MontiCore 3-Level License
