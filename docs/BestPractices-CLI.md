<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - Designing Tools for Command Line Interfaces

[[_TOC_]]

Some DSLs require a tool to enable general accessibility via the command line interface (CLI). 
When designing a tool, we recommend some standard guidelines.

## Designing a Tool
The tool provides a general interface for the functionalities developed for a language. 
This includes all features such as parsing of models, saving and loading of symbol 
tables, pretty printing, reporting, or export as object diagram.

### Default Options
The available options are of course language-specific. 
However, we suggest some default arguments for standardized access. 

```
-h,--help                    Prints this help dialog
-i,--input <file>            Reads the (mandatory) source file resp. the
                             contents of the model
-path <dirlist>              Sets the artifact path for imported symbols, space separated
-modelpath <dirlist>         Sets the artifact path for imported models, space separated
-pp,--prettyprint <file>     Prints the AST to stdout or the specified output 
                             file (optional)
-s, --symboltable <file>     Serializes and prints the symbol table to stdout 
                             or the specified output file (optional) 
-r,--report <dir>            Prints reports of the parsed artifact to the
                             specified directory (optional). Available reports
                             are language-specific
-o,--output <dir>            Path of generated files (optional)
-so,--syntaxobjects <file>   Prints an object diagram of the AST to stdout or
                             the specified file (optional)
-sc,--script <file>          Advanced configuration 2: through a groovy script 
                             that allows to adapt and extend the tool workflow (optional) 
                             (only some tools provide groovy scripting)
-ct, --configtemplate        Advanced configuration 1: through a Freemarker template
                             that allows to adapt the generation process (optional)
                             (only some tools provide a template configuration call)
```

An example of a complete yet relatively small tool example can be found in the 
[JSON project](https://github.com/MontiCore/json).

Some explanation to the arguments:
* The tool is meant for handling one individual model (`-i`) and store the
  results appropriately in files. 
* Typical results are 
  * (1) generated files (`-o`) that are used in the next step of 
    the build process (e.g. for compilation).
  * (2) the symboltable (`-s`) that is then used by other tools to import symbols
  * (3) reports (`-r`) and internal information (`-so`), like the AST of the 
    parsed model usable for developers to understand what happened
  * (4) and potentially also internal information on used input and generated 
    output files
    that allows the calling build script to understand whether a redo is 
    needed (as part of a
    larger incremental and efficient development process).
* Directories in `-path` are separated via spaces, i.e. each path is an argument on its own. 
  Example: `-path a/b x/y`.
* Directories in the above options `-path`, `-o` describe the root
  structure that is further refined  by packages (like in Java). 
  That means with `-path a/b x/y`
  the actual symboltable for a Statechart `de.mine.Door` is found in 
  `a/b/de/mine/Door.scsym` or `x/y/de/mine/Door.scsym` (in that order)
* Languages typically only load other symbols rather than other models. Therefore, the argument 
  `-path` that identifies only paths containing symbols should be implemented by most languages, whereas 
  the argument `-modelpath` for identifying paths containing models is typically not required.
* Groovy-scripting (`-sc`, `--script`): A Groovy Script is meant to describe the tool internal 
  workflow. It controls parsing, symbol construction, reporting, code generation etc.
  This kind of scripting should only become necessary when various alternative
  configurations are possible. Thus, not every tool provides Groovy scripting.
* Template-scripting (`-ct`, `--configtemplate`): 
  It is possible to add a custom template script right before
  the full generation process starts. This template is useful to customize the 
  generation process e.g. by defining hook points and thus injection more templates
  or switching verbosity on/off.


## Usage of the Tool-JAR

A note to the tool usage: 
Tools do not organize the correct order of their calls. If embedded in a larger
build process, an appropriate gradle (preferred) or make it is useful for 
incremental efficiency.

This organisation is above the tool, due to the efficiency of the 
(grade or make) buildscript itself, which must be able to decide, whether a redo
is needed. If the tool was called to decide that, too much time was already wasted.

For a build script to decide whether to call the tool or not, a tool call should
(and actually MontiCore does) provide among others a list of files it used for input. 

## Automatically Generating a Tool-JAR

Note to the tool development:
To automatically derive an executable JAR from the Gradle build process for the 
corresponding tool, the following template can be used.

```
// all in one tool-jar
shadowJar {
    manifest {
        attributes "Main-Class": "de.monticore.${archiveBaseName.get().capitalize()}Tool"
    }
    archiveFileName = "MC${archiveBaseName.get()}.${archiveExtension.get()}"
    minimize()
    archiveClassifier = "mc-tool"
}

jar.dependsOn shadowJar
```
This blueprint can be used in the `build.gradle` script to derive a JAR for the tool 
class and its provided command line functionalities. 
The packed JAR already contains all the necessary dependencies. 
The template defines the main class and name of the JAR. 
To foster automated reuse, the template has already been configured to generate 
a suitable JAR for each language project without manual adjustments. 
However, this requires adhering to the following conventions:
* The name of the main class is equal to the language project name (usually defined 
  in the `settings.gradle`) with the suffix *Tool*. 
  Furthermore, the first letter of the main class is always capitalized to adhere 
  to the Java code conventions
* The package of the main class is `de.monticore`  
* The generated JAR can be found in 'target/libs'

**Example:**  
For a language project `MyLang` we have to implement the `MyLangTool.java` located 
in the package `de.monticore`.
This automatically generates the executable JAR `MCMyLang.jar`

In general, the template can be customized by specifying the corresponding main 
class and JAR name definitions.
However, we recommend to use the predefined automatic approach.

## Functional Approach
When implementing the tool, we recommend a functional paradigm to provide the 
desired functionalities, as the too class is not about data structures but only 
exists to make functions available. 
In this case it would be counterproductive to store the arguments of the available 
functions as attributes.
Instead, it makes more sense to pass these arguments as parameters when calling 
the respective methods.
This yields several advantages:

* Values that have not yet been set do not have to be displayed with Optionals 
* As a result. tedious unwrapping of Optionals with corresponding error messages 
  is no longer necessary
* get/set methods for attributes are not required
* Facilitates reusability of modular functions

Of course, there are always trade-offs, but a more explicit functional way of 
thinking should be considered more intensively, especially when it is not about 
data structures but about the functions.
For instance, if intermediate results are stored  for efficiency reasons, this 
might a good argument to do it differently.


## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [License definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

