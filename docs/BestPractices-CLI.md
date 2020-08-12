<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - Designing Command Line Interfaces

[[_TOC_]]

Some DSL tools require a command line interface (CLI) to enable general accessibility. 
When designing a CLI, we recommend some standard guide lines.

## Designing a CLI
The CLI provides a general interface for the functionalities developed for a language. 
This includes all features such as parsing of models, saving and loading of symbol 
tables, pretty printing, reporting, or export as object diagram.

### Default Options
The available options are of course language-specific. 
However, we suggest some default arguments for standardized access. 

```
-h,--help                    Prints this help dialog
-i,--input <file>            Reads the source file (mandatory) and parses the
                             contents of the model
-path <file>                 Sets the artifact path for imported symbols
-pp,--prettyprint <file>     Prints the AST to stdout or the specified output 
                             file (optional)
-s, -symboltable <file>      Serializes and prints the symbol table to stdout 
                             or the specified output file (optional) 
-r,--report <dir>            Prints reports of the parsed artifact to the
                             specified directory (optional). Available reports
                             are language-specific
-so,--syntaxobjects <file>   Prints an object diagram of the AST to stdout or
                             the specified file (optional)
```

An example of a complete yet relatively small CLI example can be found in the 
[JSON project](https://git.rwth-aachen.de/monticore/languages/json).

## Automatically Generating a CLI-JAR
To automatically derive an executable JAR from the Gradle build process for the 
corresponding CLI, the following template can be used.

```
// all in one cli-jar
shadowJar {
    manifest {
        attributes "Main-Class": "de.monticore.${archiveBaseName.get().capitalize()}CLI"
    }
    archiveFileName = "${archiveBaseName.get()}-cli.${archiveExtension.get()}"
    minimize()
}

jar.dependsOn shadowJar
```
This blueprint can be used in the `build.gradle` script to derive a JAR for the CLI. 
The packed JAR already contains all necessary dependencies. 
The template defines the main class and name of the JAR. 
To foster automated reuse, the template has already been configured to generate 
a suitable CLI-JAR for each language project without manual adjustments. 
However, this requires adhering to the following conventions:
* The name of the main class is equal to the language project name (usually defined 
  in the `settings.gradle`) with the suffic *CLI*. 
  Furthermore, the first letter of the main class is always capitalized to adhere 
  to the Java code conventions
* The package of the main class is `de.monticore`  
* The generated JAR can be found in 'target/libs'

**Example:**  
For a language projekt `myLang` we have to implement the `MyLangCLI.java` located 
in the package `de.monticore`.
This automatically generates the executable JAR `myLang-cli.jar`

In general, the template can be customized by specifying the corresponding main 
class and JAR name definitions.
However, we recommend to use the predefined automatic approach.

## Functional Approach
When implementing the CLI, we recommend a functional paradigm to provide the 
desired functionalities, as the CLI-class is not about data structures but only 
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

* [Overview Best Practices](BestPractices.md)
* [MontiCore project](../README.md) - MontiCore
* [MontiCore documentation](http://www.monticore.de/)

* [**List of languages**](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)



