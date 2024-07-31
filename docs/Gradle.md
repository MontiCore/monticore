<!-- (c) https://github.com/MontiCore/monticore -->

# Changes to the Gradle build

With MontiCore version 7.6.0,
the `monticore` gradle plugin has been replaced with the `de.monticore.generator` plugin.
It supports the default use case for the MontiCore generator out of the box.

## Default Project Setup

```gradle
plugins {
    id "java-library"
    id "de.monticore.generator" version "$mc_version"
}
dependencies {
    grammar "de.monticore:monticore-grammar:$mc_version"
}
// And thats it - no further task definitions or dependencies required (except repositories)!
```

For every Gradle [source set](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.SourceSet.html),
such as main or test, a `MCGenTask` is automatically added which builds *all* grammars
of the source set.
For example, `generateMCGrammars` builds *all* grammars in the `src/main/grammars` folder.
Unlike with the old `MCTask`, which required one task per grammar,
the new `MCGenTask` task builds all grammars incrementally.
The generated java files are automatically added to the source sets `java` sources.

A work action is created for every grammar which is required to be worked on and thus,
multiple grammars can be build in parallel.

## Example Project Setup

The following project setup combines the default project setup
 with the SE codestyle plugin and sets up jUnit tests:

```gradle
plugins {
    id "java-library" // Java, while providing specific knowledge about Java libraries
    id "de.monticore.generator" version "$mc_version" // MontiCore plugin
    id "de.se_rwth.codestyle" version "$mc_version" // Enforces & Provides SE CodeStyle
}

java {
  // Configure the java toolchain to use Java 11 (overriding the locally installed JDK)
  // https://docs.gradle.org/current/userguide/toolchains.html
  toolchain {
    languageVersion = JavaLanguageVersion.of(11)
  }
}

dependencies {
  // Depend on the MontiCore language library (which in term depends on the runtime)
  grammar "de.monticore:monticore-grammar:$mc_version"
  // and depend on the junit dependencies
  testImplementation "org.junit.jupiter:junit-jupiter-api:$junit_version"
  testRuntimeOnly "org.junit.jupiter:junit-jupiter-engine:$junit_version"
}

// Where can we find the dependencies?
repositories {
  if (("true").equals(getProperty('useLocalRepo'))) {
    mavenLocal() // in our local maven repository
  }
  maven { // in the se-nexus
    credentials.username mavenUser
    credentials.password mavenPassword
    url repo
  }
}

test {
  // Detect junit 5 tests
  useJUnitPlatform()
}
```

The following _gradle.properties_ file can be used:

```gradle.properties
mavenUser=${user}
mavenPassword=${pass}
repo=https://nexus.se.rwth-aachen.de/content/groups/public
useLocalRepo=false
mc_version=REPLACE-ME
```

## Configuration

The automatically added `generateMCGrammars` task is configured with some defaults,
which can be modified via the build file.
For example, the following build file snippet changes the output directory:

```gradle
generateMCGrammars {
    // manually change the output directory
    outputDir = project.layout.buildDirectory.dir("mc")
}
```

The following table shows the main configuration options

| Option (, alias)                           | Description                                                                                                                                                       | Type                                                                                                                      | Default                                                         |
|--------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------|
| grammar (, input)                          | The grammar files                                                                                                                                                 | [ConfigurableFileCollection](https://docs.gradle.org/current/javadoc/org/gradle/api/file/ConfigurableFileCollection.html) | `src/${sourceSet}/grammars`                                     |
| handWrittenCodeDir (, hwc, handcodedPath ) | Directories for detecting hand-written code that needs to be integrated.                                                                                          | [ConfigurableFileCollection](https://docs.gradle.org/current/javadoc/org/gradle/api/file/ConfigurableFileCollection.html) | `src/${sourceSet}/java`                                         |
| handWrittenGrammarDir (, hwg)              | Directories for detecting hand-written grammar modifications that need to be integrated.                                                                          | [ConfigurableFileCollection](https://docs.gradle.org/current/javadoc/org/gradle/api/file/ConfigurableFileCollection.html) | `src/${sourceSet}/grammarsHC`                                   |
| templatePath (, tmplDir)                   | Directories for detecting hand-written templates to integrate.                                                                                                    | [ConfigurableFileCollection](https://docs.gradle.org/current/javadoc/org/gradle/api/file/ConfigurableFileCollection.html) | `src/${sourceSet}/resources`                                    |
| configTemplate                             | template to configure the integration of hand-written templates. Thus, it can only be used in conjunction with a valid templatePath.                              | String                                                                                                                    | absent                                                          |
| script                                     | Groovy script to control the generation workflow.                                                                                                                 | String                                                                                                                    | `monticore_standard.groovy`                                     |
| modelPath                                  | Directories in which symbols or grammars are searched.                                                                                                            | [ConfigurableFileCollection](https://docs.gradle.org/current/javadoc/org/gradle/api/file/ConfigurableFileCollection.html) | absent                                                          |
| outputDir                                  | Directory to which the output should be written to                                                                                                                | [Directory](https://docs.gradle.org/current/javadoc/org/gradle/api/file/DirectoryProperty.html)                           | `$buildDir/generated-sources/monticore/${sourceSet}-sourcecode` |
| reportDir                                  | Directory to which the reports should be written to                                                                                                               | [Directory](https://docs.gradle.org/current/javadoc/org/gradle/api/file/DirectoryProperty.html)                           | `$buildDir/mc_reports/task_$taskname`                           |
| debug                                      | Enables developer logging and                                                                                                                                     | boolean                                                                                                                   | `false`                                                         |
| genDST                                     | Whether domain specific transformation infrastructure should be generated (only suitable for TR grammars)                                                         | boolean                                                                                                                   | `false`                                                         |
| genTag                                     | Whether tagging infrastructure should be generated (only suitable for TagDefinition grammars)                                                                     | boolean                                                                                                                   | `false`                                                         |
| groovyHook1                                | Groovy script that is hooked into the workflow of the standard script at hook point one, which is called after initialization, before the actual workflow begins. | [File](https://docs.gradle.org/current/javadoc/org/gradle/api/file/RegularFileProperty.html)                              | absent                                                          |
| groovyHook2                                | Groovy script that is hooked into the workflow of the standard script at hook point two, which is called before the generation step.                              | [File](https://docs.gradle.org/current/javadoc/org/gradle/api/file/RegularFileProperty.html)                              | absent                                                          |
| moreArgs                                   | Additional arguments passed to the Tool.                                                                                                                          | `List<String>`                                                                                                            | `[]`                                                            |

## Grammar Dependencies

To depend on another language project,
simply use the `grammar` configuration:

```groovy
dependencies {
  grammar "de.monticore.lang:mydsl:$mc_version"
  testGrammar "de.monticore.lang:mytestdsl:$mc_version"
}
```

This adds the java-library variant (aka implementation) to the java classpath,
and the grammar files to the symbol path.

_Note:_ Grammar dependencies are transitive,
 which includes the implementation/java dependencies as well (such as the monticore-runtime).

## Examples

In most cases,
adding the plugin should be enough.
The following snippets show examples, in which the default configuration is modified:

### Different ModelPath

```build.gradle
generateMCGrammars {
    modelPath("$projectDir/src/main/grammars", "$projectDir/src/notquitemain/grammars")
}
```

*Note:* We have introduced a breaking change to the `modelPath()` behavior:
It is no longer possible to specify multiple directories by calling `modelPath(...)` multiple times,
instead a comma-seperated list is to be used.
The legacy usage now aborts with a descriptive error.

### Different workflow script

```build.gradle
generateMCGrammars {
    script = "de/monticore/monticore_noreports.groovy"
}
```

### Specify Groovy Hooks

```build.gradle
generateMCGrammars {
    groovyHook1 = file "$projectDir/gs1.groovy"
    groovyHook2 = file "$projectDir/gs2.groovy"
}
```

### ConfigTemplate

```build.gradle
generateMCGrammars {
  configTemplate = "ct.ftl"
  templatePath "$projectDir/src/main/tpl"
}
```

*Note:* Incremental building after changes to the templatePath directory contents is not supported.
Instead, a full-rebuild is enforced.

### Transformation Grammars

Simply use the `de.monticore.generator-withtr` gradle plugin.
You can set a `genTR` property to any value different than `true` to disable the trafo generation.

To further exclude grammars, you can filter on the `extractTRGrammars` task:

```groovy
if (("true").equals(getProperty('genTR'))) {
  // Further exclude the grammar and basic tf grammars from being included in the TR grammar input set
  extractTRGrammars {
    exclude(["**/de/monticore/tf/*", "**/de/monticore/grammar/*", "**/de/monticore/siunit/*"])
  }
}
```

### Custom MCGenTask

A `generateModifiedGrammars` task to generate all grammars generated by another task
(and with an additional filter, only matching grammar files starting with a captital `A`.)

```build.gradle
// register a new MCGenTask task
tasks.register("generateModifiedGrammars", MCGenTask) {
    grammar.setFrom(tasks.named("modifyGrammars").get()
        .getOutputs().getFiles().getAsFileTree()
        .matching(patternFilterable -> patternFilterable.include( "**/A*.mc4")))
    outputDir = file myModOutDir
    modelPath.from(file modifyGrammarsOutD) // the directory hierarchy is required here
    if (findProperty("ci") != null) {
      script = "de/monticore/monticore_noreports.groovy"
    }
}
// Note: The output directory has to manually be added to the java sources, if desired
sourceSets {
  // we explicitly use the outputDir property instead of the myModOutDir variable
  // to give Gradle further insights into the build setup
  main.java.srcDirs += tasks.generateModifiedGrammars.getOutputDir()
}
```

### Custom SourceSet

Additional source sets for MontiCore grammars, such as trafos and tagging,
can be defined and published.
Note: Trafos and Tagging can be set up using a specific plugin.

```groovy
sourceSets {
  variantB {
    grammars {
      srcDir("src/main/variantBGrammars")
    }
  }
}

dependencies {
  // add grammar dependencies to variant B
  variantBGrammar "de.monticore:monticore-grammar:$mc_version"
}

monticoreGenerator {
  // publish the MC artifacts of the "variantB" source set
  publishSourceSet(project.sourceSets.variantB)
}
```

## Improvements over the old `MCTask`

* Improved incremental build support
* Added Gradle build cache support
* Added parallel task execution support
* Using Gradle task definitions which can be used for Gradle plugins of MontiCore language tools
* Transitive grammar dependencies
* Handling of test grammars (and source sets)

## Added build elements

* For every source set, the MontiCore generator plugin adds:
    * A `grammar` SourceSetDirectory extension (e.g. `sourceSets.main.grammars`) to the source set
    * A `generate${sourceSetName}MCGrammars` task building (by default) all grammars of the source set
    * Two configurations:
        * `grammar` (or `${sourceSetName}Grammar` for non-main source sets):
          Used to declare dependencies to other MontiCore language projects.
          This configuration will not be resolved, instead the `implementation` and `grammarSymbolDependencies` extend
          this
          configuration and resolve their respective variants as dependencies.
        * **(internal)** `grammarSymbolDependencies` (or `${sourceSetName}GrammarSymbolDependencies`):
          Dependencies (jars containing the .mc4 files) will be added to the ModelPath.
          This configuration extends the previous dependency-defining grammar.
          Do not declare dependencies using this configuration.
    * In case publishing is enabled (by default, only for the main source set):
        * A `grammarSymbolOutElements` (or `${sourceSetName}GrammarSymbolOutElements`) configuration & variant:
            * Contains the grammars jar as an artifact.
        * Three `Jar` tasks (with publishing their artifacts):
            * `${sourceSetName}GrammarsJar`
            * `${sourceSetName}Jar` (only for non-main source sets)
            * `${sourceSetName}sourcesJar` (only for non-main source sets)
        * An ad-hoc component `grammars${sourceSetName}`
        * An
          alias [MavenPublication](https://docs.gradle.org/current/dsl/org.gradle.api.publish.maven.MavenPublication.html)
          named identical to the source set (only for non-main source sets)
* Grammars declared in the main source set are also available in the test source sets.

## Implementation specifics

MontiCore suffers from a memory leak caused by the GroovyInterpreter.
The parallel, isolated execution of the generator intensifies this memory leak
(which is why we limit it using `--max-workers=4`).
This might be fixed by [8078641](https://github.com/openjdk/jdk/commit/21012f2bbe214955d05f8bc583dcdceb0949b601) in jdk
18+.

## Migration Guide: Using the new plugin

* Replace plugins `id "monticore" ` with `id "de.monticore.generator"`
* Remove the creation of individual MCTasks
* Modelpath adding: see above using the source sets
* Ensure the generation target is NOT added to the java sources (otherwise the TOP mechanism will fail)
* Ensure no resources.srcDirs += grammarOutDir is used (otherwise your build will depend on itself)
