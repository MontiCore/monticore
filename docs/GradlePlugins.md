<!-- (c) https://github.com/MontiCore/monticore -->

This page describes how to add Gradle support for the tool of your DSL.

It is meant for tooling developers.
Usage of plugins is described [in the Gradle Usage documentation](Gradle.md).

# Rough Description

Each tool is provided as a CLI tool and thus accepts a list of CLI arguments.
Our Gradle tasks & plugins allow Gradle-native configuration of the tools.
They basically shoehorn the Gradle configuration into CLI arguments.

Due to a different logging and exit code behaviour,
we require a little bit of extra work.

This guide will explain the first steps of creating a Gradle plugin for your
DSL.

Additional resources:

* [Gradle Plugin Development Documentation](https://docs.gradle.org/current/userguide/writing_plugins.html)
* [CD4Analysis Gradle Plugin](https://github.com/MontiCore/cd4analysis/tree/dev/cdtool/cdgradle)

### 1: Build your DSL's tool

MontiCore automatically generates a `gradleMain` method
that does not initialize a log or handles exceptions or errors.
By compiling your generated code, it is usable from Gradle.

### 2: Create a mydslgradle subproject

This subproject contains the Gradle tasks and plugin calling your DSL's tool.

```
// mydslgradle/build.gradle
plugins {
  id 'java-library'
  id 'java-gradle-plugin'
  id 'maven-publish'
}
dependencies {
 implementation "de.se_rwth.commons:se-commons-gradle:$mc_version"
 compileOnly project(":mydsl") 
 testImplementation gradleTestKit()
}

publishing {...}
```

In case you can't get the compileOnly dependency on the DSL itself to work,
you can omit it, but see the caveat in step 3 below.

### 3: Create a Task class

There exists two default kinds of task:

* MCAllFilesTask: Reacts to any changes to an input set by starting generation
  on ALL inputs
    * => The Tool is called once for all files
* MCSingleFileTasK: Reacts to changes to an input set by starting generation on
  the changed inputs (and their dependants).
    * => The Tool is called once for every new generation/changed file

Extend one of those classes to create your own task.

```java

@CacheableTask
public abstract class MyDSLCompileTask extends MC_SeeAbove_Task implements ICachedQueueTask {
  public MyDSLCompileTask() {
    super("MyDSLCompileTask", null);

    // Simplest option: Define your tool class as the main class of the task
    getMainClass().convention(MyDSLTool.class.getSimpleName());
  }

  @Override
  protected void prepareWorkQueue() {
    // Use the improved shared-isolated-work-queue of se-commons
    // (this avoids a bunch of otherwise required boilerplate code)
    this.workQueue = doGetSharedQueueService().newWorkQueue(getWorkerExecutor(),
            getServiceRegistry(), getExtraClasspathElements());
  }

}
```

In case you were unable to get the compileOnly dependency to work in step 2,
use the FQN string of the _MyDSLTool_ instead of `class.getSimpleName()`.

Both task classes provide the following properties:

| Type                       | Getter                            | Description                                                            | Optional | Incremental  
|----------------------------|-----------------------------------|------------------------------------------------------------------------|----------|--------------|
 ConfigurableFileCollection | getInput()                        | The input models (without any input file, the task is skipped)         | R        | incremental  |
 ConfigurableFileCollection | getSymbolPathConfiguration()      | A configuration containing symbolpath dependencies                     | O        | full-rebuild |
 Property\<Boolean\>        | getAddConfigurationToSymbolPath() | Whether the symbolpath configuration should be added to the symbolpath | O        | full-rebuild |
 ConfigurableFileCollection | getSymbolPath()                   | Additional symbolpath elements.                                        | O        | full-rebuild |
 ConfigurableFileCollection | getIncrementalSymbolPath()        | Additional symbolpath elements, for incremental rebuilds.              | O        | incremental  |
 DirectoryProperty          | getHandWrittenCodeDir()           | Directory containing the handwritten code (TOP-mechanism)              | O        | incremental  |
 DirectoryProperty          | getTmplDir()                      | Additional templates directory                                         | O        | full-rebuild |
 Property\<String\>         | getConfigTemplate()               | Configtemplate to customize                                            | O        | full-rebuild |
 DirectoryProperty          | getOutputDir()                    | Generation output diretory                                             | R        | (output)     |
 DirectoryProperty          | getReportDir()                    | Reports output directory                                               | O        | (output)     |
 ListProperty\<String\>     | getMoreArgs()                     | Additional args passed to the CLI                                      | O        | full-rebuild |
 ConfigurableFileCollection | getExtraClasspathElements()       | Classpath containing the CLI, etc.                                     | O        | full-rebuild |
 Property\<String\>         | getMainClass()                    | Different CLI-main class                                               | O        | full-rebuild |

See below on instructions on adding your own properties and arguments.

### (Optional) Using the task, barebones as it is, without a plugin

At this point you can - technically - already use your Gradle task in other
projects.
But the sections after this one will improve upon it drastically.

```groovy
// example_project/build.gradle
buildscript {
  dependencies {
    classpath "de.se_rwth.lang:mydsl-gradle:$mc_version"
  }
}
plugins {
  id 'se.rwth.gradle.cached-queue'
}
configurations {
  mydslTool
}
dependencies {
  mydslTool "de.se_rwth.lang:mydsl:$mc_version"
}

import de.MyDSLCompileTask

tasks.register("compileMyDSL", MyDSLCompileTask.class) {
  getExtraClasspathElements().from(configurations.mydslTool)
}
```

This example project:

* loads the `mydsl-gradle` dependency (containing the task) to the build
  classpath,
* defines a new Gradle configuration,
* which contains the `mydsl` tool dependency,
* and adds it to the classpath of the compile task.

### 4.1: Dependencies Plugin

Now we add some usability by defining the mydsltool configuration from above
via a plugin.

````java
// MyDSLGradlePlugin.java
public class MyDSLGradlePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    // Set up the improved work-queue
    project.getPluginManager().apply(CachedQueueServicePlugin.class);

    // Populate the "mydslTool" configuration with the generator itself and
    project.getPluginManager().apply(MyDSLDependenciesGradlePlugin.class);
  }
}
````

````java
import de.monticore.gradle.ADependenciesGradlePlugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

// MyDSLDependenciesGradlePlugin.java
public class MyDSLDependenciesGradlePlugin extends ADependenciesGradlePlugin implements Plugin<Project> {

  /**
   * Configuration containing the classpath of the generator tool.
   */
  public static final String CONFIG_TOOL = "mydslTool";


  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(JavaLibraryPlugin.class);

    // Load plugin version (by default, the version key from the buildInfo.properties file)
    String version = this.getToolVersion();

    // Create the dependency configuration (you MUST use a unique name per tool!)
    Configuration toolConfig = project.getConfigurations().maybeCreate(CONFIG_TOOL);
    toolConfig.setCanBeResolved(true);

    // add the mydsl tool dependency to the tool's runtime configuration
    toolConfig.defaultDependencies(dependencies -> {
      dependencies.add(project.getDependencies().create("de.se_rwth:mydsl:" + version));
    });

    // and add the toolConfig as an extra classpath
    project.getTasks().withType(MyDSLCompileTask.class).configureEach(t -> t.getExtraClasspathElements()
            .from(toolConfig));
  }
}

````

````groovy
// mydslgradle/build.gradle
buildscript {
  dependencies {
    classpath "de.se_rwth.commons:se-commons-gradle:$mc_version"
  }
}

// ...
gradlePlugin {
  plugins {
    cdplugin {
      id = "de.rwth.se.mydsl"
      implementationClass = "de.monticore.mydsl.gradleplugin.MyDSLGradlePlugin"
    }
  }
}


// Write the version to the jar
import de.monticore.gradle.common.MCBuildInfoTask;

tasks.register("writeMCBuildInfo", MCBuildInfoTask.class) {
  getVersion().set(project.provider(() -> project.getVersion().toString()));
}
processResources {
  dependsOn tasks.named("writeMCBuildInfo")
}
````

### 4.2: Default Source Directory Sets Plugin

By default, Gradle compiles all _src/main/java_ Java files.
In Gradle terms: All source files within the _main_ sourceset
of the _java_ source directory set are compiled.

We can similarly compile all models from a given sourcedirectory set.

> ! This section is WIP

Please look at how the class diagram plugin works
[here](https://github.com/MontiCore/cd4analysis/blob/dev/cdtool/cdgradle/src/main/java/de/monticore/cdgen/gradleplugin/CDGenGradlePlugin.java).

### Optional: Add your own Parameters and CLI Arguments

* Read the available property types in
  Gradle [here](https://docs.gradle.org/current/userguide/properties_providers.html#mutable_managed_properties)
* Override the `createArgList` method of your super task class to add your own
  values to the returned list.

### Optional: Modifying the Incremental Tasks

> ! This section is WIP

* `MCCommonTask#getOtherInputFileCollections()`
 