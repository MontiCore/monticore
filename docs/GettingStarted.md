# MontiCore - Getting Started

---------------------
In the following you will learn how to get started with MontiCore. 
Before you start please make sure that Java is installed on your system as described 
in the [prerequisites](#prerequisites). Now that your system is ready you have three options to get started. 
Just choose one of the following guides:

-  [Command Line](#command-line)

-  [Eclipse](#eclipse)

-  [IntelliJ](#intellij)


## Prerequisites

--------------------
1.  Install the Java 8 [JDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html).
2.  Make sure the environment variable JAVA_HOME points to the installed JDK, 
NOT the JRE (e.g. ‹/usr/lib/jvm/java-8-openjdk› on UNIX or 
‹C:\Program Files\Java\jdk1.8.0_51› on Windows). You will need this in order to run the Java 
compiler for compiling the generated Java source files; see [here](https://stackoverflow.com/questions/tagged/java-home) for more information.
3.  Also make sure that the PATH variable is set such that the Java compiler is available.
 JDK installations on UNIX systems do this automatically. On Windows systems the ‹bin› directory of 
 the JDK installation needs to be appended to the PATH variable, e.g. ‹%PATH%;%JAVA_HOME%\bin› 
 (see also [here](https://stackoverflow.com/questions/2079635/how-can-i-set-the-path-variable-for-javac-so-i-can-manually-compile-my-java-wor)).

## Command Line

--------------------
MontiCore supports different environments. For a quick peek, the command line version can be tried out with an exemplary automata DSL using the following instructions:

### In a nutshell

| | |
|---|---|
|Prerequisites | Install the Java Development Kit (JDK) 8.|
|Installation | Download the MontiCore distribution file, unzip it, and change to the extracted directory.|
|Running MontiCore | Execute MontiCore on the provided language definition ‹Automata.mc4›.|
|Compiling the Product | Compile all the generated and supplied handwritten Java source files.|
|Running the Product | Execute the automata tool on an example model ‹example/PingPong.aut›.|

### Detailed description

### Installation
1.  Download the MontiCore [zip distribution file](http://www.monticore.de/gettingstarted/monticore-cli-6.0.0.zip).
2.  Unzip the distribution. It will unzip a directory called ‹mc-workspace› containing the executable MontiCore CLI (short for command line interface) JAR along with a directory ‹src› containing handwritten automata DSL infrastructure, a directory ‹hwc› containing handwritten code that will be incorporated into the generated code, and a directory ‹example› containing an example automata model.

### Run MontiCore
1.  Open a command line interface and change to the unzipped directory ‹mc-workspace›).
2.  The distribution contains the sources of an automata DSL consisting of the automata grammar and handwritten Java files in the directory ‹src›. Execute the following command in order to generate the language infrastructure of the specified automata DSL:

**java -jar monticore-cli.jar Automata.mc4 -hcp hwc/**

The only required argument ‹Automata.mc4› denotes the input grammar 
for MontiCore to process and generate the language infrastructure for. 
The second argument denotes the path to look for handwritten code that is to be 
incorporated into the generated infrastructure.
MontiCore will be launched and the following steps will be executed:

1.  The specified grammar will be parsed and processed by MontiCore.
2.  Java source files for the corresponding DSL infrastructure will be generated into the default output directory ‹out›. This infrastructure consists of:
    1.  **out/automata/_ast** containing the abstract syntax representation of the automata DSL.
    2.  **out/automata/_cocos** containing infrastructure for context conditions of the automata DSL.
    3.  **out/automata/_od** containing infrastructure for printing object diagrams of the automata DSL.
    4.  **out/automata/_parser** containing the generated parsers which are based on [ANTLR](https://www.antlr.org/).
    5.  **out/automata/_symboltable** containing infrastructure for the symbol table of the automata DSL.
    6.  **out/automata/_visitor** containing infrastructure for visitors of the automata DSL.
    7.  **out/reports/Automata** containing reports created during the processing of the automata grammar.
3.  The output directory will also contain a log file of the executed generation process ‹monticore.YYYY-MM-DD-HHmmss.log›.
### Compile and run
1.  Compiling the automata DSL
*  Execute the command:
**javac -cp monticore-cli.jar -sourcepath "src/;out/;hwc/" src/automata/AutomataTool.java**
**Please note:** on Unix systems paths are separated using ":" (colon) instead of semicolons.
This will compile all generated classes located in ‹out/› and all handwritten classes located in ‹src/› and ‹hwc/›. Please note that the structure of the handwritten classes follows (though not necessarily) the package layout of the generated code, i.e. there are the following sub directories (Java packages):
    1.  **src/automata** contains the top level language realization for using the generated DSL infrastructure. In this case the class ‹src/automata/AutomataTool.java› constitutes a main class executable for processing automata models with the automata DSL (inspect the class and see below for how to execute it).
    2.  **src/automata/cocos** contains infrastructure for context condition of the automata DSL.
    3.  **src/automata/prettyprint** contains an exemplary use of the generated visitor infrastructure for processing the parsed model. Here: for pretty printing.
    4.  **src/automata/visitors contains** an exemplary analysis using the visitor infrastructure. The exemplary analysis counts the states contained in the parsed automata model.
    5.  **hwc/automata/_ast** contains an exemplary usage of the handwritten code integration mechanism for modifying the AST for the automata DSL.
    6.  **hwc/automata/_symboltable** contains handwritten extensions of the generated symbol table infrastructure.
2.  Running the automata DSL tool
    *  Execute the command: <br>
    **java -cp "src/;out/;hwc/;monticore-cli.jar" automata.AutomataTool example/PingPong.aut** <br>
    **Please note:** on Unix systems paths are separated using ":" (colon) instead of semicolons.
    This will run the automata DSL tool. The argument ‹example/PingPong.aut› is passed to the automata DSL tool as input file. Examine the output on the command line which shows the processing of the example automata model.

### Experiment:
The shipped example automata DSL (all sources contained in ‹mc-workspace/src› and ‹mc-workspace/hwc›) can be used as a starting point. It can easily be altered to specify your own DSL by adjusting the grammar and the handwritten Java sources and rerunning MontiCore as described above.

## Eclipse

----------------------
For getting started with MontiCore using Eclipse do the following:

### Setting up Eclipse
1.  Download and install Eclipse (or use an existing one)
2.  Open Eclipse
3.  Install needed Plugins
    *  Help > Install new Software
    *  Make sure the M2E (Maven 2 Eclipse) Plugin is installed
    *  M2E can be found here
    *  Install the following MontiCore M2E Extension found here:
    *  Install MontiCore 5 (or higher) extension
4. Make sure to confire Eclipse to use a JDK instead of an JRE
    *  Window > Preferences > Java > Installed JREs

### Importing the Example
1.  Clone the github project or download the zip for the Automata Example 
[here](https://github.com/MontiCore/automaton).
2.  Select
    *  File
    *  Import...
    *  Maven
    *  Existing Maven Projects
    *  Click next
    *  Click on the Browse.. button and use the folder holding the pom.xml

### Running MontiCore
1.  Right click on the project
2.  Run as > Maven install
3.  Add ‹../target/generated-sources/monticore/sourcecode› to your build path
    *    Right click on the folder > Build Path > Use as Source Folder

MontiCore will be launched and the following steps will be executed:

1.  The specified grammar will be parsed and processed by MontiCore.
2.  Java source files for the corresponding DSL infrastructure will be generated into the default output directory ‹../target/generated-sources/monticore/sourcecode›. This infrastructure consists of:
    1.  **/automata/_ast** containing the abstract syntax representation of the automata DSL.
    2.  **/automata/_cocos** containing infrastructure for context conditions of the automata DSL.
    3.  **/automata/_od** containing infrastructure for printing object diagrams of the automata DSL.
    4.  **/automata/_parser** containing the generated parsers which are based on [ANTLR](https://www.antlr.org/).
    5.  **/automata/_symboltable** containing infrastructure for the symbol table of the automata DSL.
    6.  **/automata/_visitor** containing infrastructure for visitors of the automata DSL.
    7.  **/reports/Automata** containing reports created during the processing of the automata grammar.
3.  The output directory will also contain a log file of the executed generation process ‹monticore.YYYY-MM-DD-HHmmss.log›.

## IntelliJ

--------------------
For getting started with MontiCore using IntelliJ do the following:

### Setting up IntelliJ IDEA
1. Download and install IntelliJ IDEA (or use your existing installation)
    *  Hint for Students: You get the Ultimate version of IntelliJ for free
2. Open IntelliJ IDEA

### Importing the Example
1.  Clone the github project or download the zip for the Automata Example 
[here](https://github.com/MontiCore/automaton).
2.  Select
    * File
    * Open
    * Select the folder holding the pom.xml

### Running MontiCore
From the Maven Projects Menu on the right select

1.  Automata
2.  Lifecycle
3.  install (double click)

MontiCore will be launched and the following steps will be executed:

1.  The specified grammar will be parsed and processed by MontiCore.
2.  Java source files for the corresponding DSL infrastructure will be generated into the default output directory ‹../target/generated-sources/monticore/sourcecode›. This infrastructure consists of:
    1.  **/automata/_ast** containing the abstract syntax representation of the automata DSL.
    2.  **/automata/_cocos** containing infrastructure for context conditions of the automata DSL.
    3.  **/automata/_od** containing infrastructure for printing object diagrams of the automata DSL.
    4.  **/automata/_parser** containing the generated parsers which are based on [ANTLR](https://www.antlr.org/).
    5.  **/automata/_symboltable** containing infrastructure for the symbol table of the automata DSL.
    6.  **/automata/_visitor** containing infrastructure for visitors of the automata DSL.
    7.  **/reports/Automata** containing reports created during the processing of the automata grammar.
3.  The output directory will also contain a log file of the executed generation process ‹monticore.YYYY-MM-DD-HHmmss.log›.

## Troubleshooting

----------------------
*  If an error occurs and you cannot solve the problem you may take a look into the MontiCore 
log file located in the respectively specified output directory 
(e.g. ‹out/monticore.YYYY-MM-DD-HHmmss.log› by default). 
It contains more verbose and developer-oriented, technical output than the CLI output.
*  Please report any unknown issues to <bugreport@monticore.de>. 
Please include the processed grammar, model, and the log file.

## Downloads

---------------------------------
*  [MontiCore zip with automata DSL example](http://www.monticore.de/gettingstarted/monticore-cli-6.0.0.zip)
*  [Executable MontiCore Java archive (without example)](http://www.monticore.de/gettingstarted/monticore-cli-6.0.0.zip)
 	