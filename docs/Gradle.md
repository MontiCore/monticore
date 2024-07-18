<!-- (c) https://github.com/MontiCore/monticore -->
# Changes to the Gradle build

With MontiCore version 7.6.0, 
 the `monticore` gradle plugin has been replaced with the `de.monticore.generator` plugin.
It supports the default use case for the MontiCore generator out of the box.

## Default Project Setup

```gradle
plugins {
    id "de.monticore.generator" version "$version"
    id "java-library"
}
// And thats it - no further task definitions required!
```

The `de.monticore.generator` plugin automatically adds a `MCGenTask` 
 named `generateMCGrammars` task,
 which builds *all* grammars in the `src/main/grammars` folder.
Unlike with the old `MCTask`, which required one task per grammar, 
 the new `MCGenTask` task builds all grammars incrementally.
The generated java files are automatically added to the `main.java` sources.

A work action is created for every grammar which is required to be worked on and thus,
 multiple grammars can be build in parallel.

## Configuration

The automatically added `generateMCGrammars` task is configured with some defaults,
 which can be modified via the build file.
For example, the following build file snippet changes the output directory:

```gradle
generateMCGrammars {
    outputDir = file "$projectDir/target/mc"
}
```

The following table shows the main configuration options

| Option                                     | Description                                                                                                                                                       | Type                                                                                                                      | Default                                                                                                   |
|--------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| grammar (, input)                          | The grammar files                                                                                                                                                 | [ConfigurableFileCollection](https://docs.gradle.org/current/javadoc/org/gradle/api/file/ConfigurableFileCollection.html) | `src/main/grammars`                                                                                       |
| handWrittenCodeDir (, hwc, handcodedPath ) | Directories for detecting hand-written code that needs to be integrated.                                                                                          | [ConfigurableFileCollection](https://docs.gradle.org/current/javadoc/org/gradle/api/file/ConfigurableFileCollection.html) | `src/main/java`                                                                                           |
| handWrittenGrammarDir (, hwg)              | Directories for detecting hand-written grammar modifications that need to be integrated.                                                                          | [ConfigurableFileCollection](https://docs.gradle.org/current/javadoc/org/gradle/api/file/ConfigurableFileCollection.html) | `src/main/grammarsHC`                                                                                     |
| templatePath (, tmplDir)                   | Directories for detecting hand-written templates to integrate.                                                                                                    | [ConfigurableFileCollection](https://docs.gradle.org/current/javadoc/org/gradle/api/file/ConfigurableFileCollection.html) | `src/main/resources`                                                                                      |
| configTemplate                             | template to configure the integration of hand-written templates. Thus, it can only be used in conjunction with a valid templatePath.                              | String                                                                                                                    | absent                                                                                                    |
| script                                     | Groovy script to control the generation workflow.                                                                                                                 | String                                                                                                                    | `monticore_standard.groovy`                                                                               |
| modelPath                                  | Directories in which symbols or grammars are searched.                                                                                                            | [ConfigurableFileCollection](https://docs.gradle.org/current/javadoc/org/gradle/api/file/ConfigurableFileCollection.html) | absent                                                                                                    |
| outputDir                                  | Directory to which the output should be written to                                                                                                                | [Directory](https://docs.gradle.org/current/javadoc/org/gradle/api/file/DirectoryProperty.html)                           | `$buildDir/generated-sources/monticore/sourcecode` (only added for the default `generateMCGrammars` task) |
| reportDir                                  | Directory to which the reports should be written to                                                                                                               | [Directory](https://docs.gradle.org/current/javadoc/org/gradle/api/file/DirectoryProperty.html)                           | `$buildDir/mc_reports/task_$taskname`                                                                     |
| debug                                      | Enables developer logging and                                                                                                                                     | boolean                                                                                                                   | `false`                                                                                                   |
| genDST                                     | Whether domain specific transformation infrastructure should be generated (only suitable for TR grammars)                                                         | boolean                                                                                                                   | `false`                                                                                                   |
| genTag                                     | Whether tagging infrastructure should be generated (only suitable for TagDefinition grammars)                                                                     | boolean                                                                                                                   | `false`                                                                                                   |
| groovyHook1                                | Groovy script that is hooked into the workflow of the standard script at hook point one, which is called after initialization, before the actual workflow begins. | [File](https://docs.gradle.org/current/javadoc/org/gradle/api/file/RegularFileProperty.html)                              | absent                                                                                                    |
| groovyHook2                                | Groovy script that is hooked into the workflow of the standard script at hook point two, which is called before the generation step.                              | [File](https://docs.gradle.org/current/javadoc/org/gradle/api/file/RegularFileProperty.html)                              | absent                                                                                                    |
| moreArgs                                   | Additional arguments passed to the Tool.                                                                                                                          | `List<String>`                                                                                                            | `[]`                                                                                                      |

## Examples

In most cases, 
 adding the plugin should be enough.
The following snippets show examples, in which the default configuration is modified:

### Different ModelPath

```build.gradle
generateMCGrammars {
    modelPath("$projectDir/src/main/grammars", "$projectDir/src/notquitemain/grammars")
}
````

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

### Custom MCGenTask 
 A `generateTRGrammars` task to generate TR grammars:


```build.gradle
tasks.register("generateTRGrammars", MCGenTask) {
    grammar.setFrom(tasks.named("generateMCGrammars", MCGenTask.class).get().getTROutput())
    outputDir = file derivOutDir
    modelPath.from(file grammarOutDir) // the directory hierarchy is required here
    if (findProperty("ci") != null) {
      script = "de/monticore/monticore_noreports.groovy"
    }
}
// Note: The output directory has to manually be added to the java sources 
sourceSets {
  main.java.srcDirs += derivOutDir
}

// In case the derivOutDir does not contribute to the main source set, make sure to add the dependsOn
compileTRJava.dependsOn (tasks.named("generateTRGrammars"))

```

## Improvements over the old `MCTask`
* Improved incremental build support
* Added Gradle build cache support
* Added parallel task execution support
* Using Gradle task definitions which can be used for Gradle plugins of MontiCore language tools  

## Implementation specifics
MontiCore suffers from a memory leak caused by the GroovyInterpreter.
The parallel, isolated execution of the generator intensifies this  memory leak 
(which is why we limit it using `--max-workers=4`).
This might be fixed by [8078641](https://github.com/openjdk/jdk/commit/21012f2bbe214955d05f8bc583dcdceb0949b601) in jdk 18+.

