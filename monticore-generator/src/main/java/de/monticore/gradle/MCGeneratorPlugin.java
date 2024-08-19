/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.gradle.dependencies.MCPublishingPlugin;
import de.monticore.gradle.dependencies.MCSourceSets;
import de.monticore.gradle.gen.MCGenTask;
import de.monticore.gradle.sources.MCGrammarsSourceDirectorySet;
import de.monticore.gradle.sources.MCSourcesPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * This class realizes the plugin itself.
 * We provide a "MCGenTask" task,
 * but also add an instance of this task named generateMCGrammars by default
 */
@SuppressWarnings("unused")
public class MCGeneratorPlugin implements Plugin<Project> {

  public void apply(Project project) {
    project.getPlugins().apply("java");
    project.getPlugins().apply(MCGeneratorBasePlugin.class);
    project.getPlugins().apply(MCSourcesPlugin.class);
    project.getPlugins().apply(MCPublishingPlugin.class);

    // Add a MCGenTask by default to each source set
    project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().all(sourceSet -> {
      createMCGenTaskForSourceSet(project, sourceSet);

      if (SourceSet.isMain(sourceSet)) {
        // Find the jar task for the grammars of this source set
        String grammarsJarTaskName = sourceSet.getTaskName(null, "grammarsJar");
        // And configure it to use the grammars source directory set
        project.getTasks().named(grammarsJarTaskName, Jar.class)
                .configure(jar -> jar.from(MCGrammarsSourceDirectorySet.getGrammars(sourceSet)));
      }
    });

    // Make the test-grammar-declaring-configuration extend from the main one
    letTestExtendMain(project);
  }


  /**
   * Provide the default generate${sourceSetName}MCGrammars MCGenTasks,
   * which consumes src/${sourceset}/grammars and outputs to the grammars output directory
   * The default properties of this task can easily be changed from within the
   * build.gradle
   */
  protected TaskProvider<MCGenTask> createMCGenTaskForSourceSet(final Project project, final SourceSet sourceSet) {
    // Construct the task name: generate${sourceSetName}MCGrammars
    String taskName = sourceSet.getTaskName("generate", "MCGrammars");

    // Register the MCGenTask on the source set
    TaskProvider<MCGenTask> mcGenTaskProvider = project.getTasks().register(taskName, MCGenTask.class, mcTask -> {
      mcTask.setGroup("mc");

      // And set a bunch of defaults on the task:

      // Fetch the grammars-extension source directory set (e.g. sourcesets.main.grammars)
      MCGrammarsSourceDirectorySet grammars = MCGrammarsSourceDirectorySet.getGrammars(sourceSet);

      // Use the classes-output of the source directory set (lazily via a provider)
      mcTask.getOutputDir().set(grammars.getClassesDirectory());
      // and the grammar source as both task input & model path
      mcTask.getGrammar().setFrom(grammars.getSourceDirectories());
      mcTask.getModelPath().setFrom(grammars.getSourceDirectories());

      // Set the handwritten code dir from the source sets java source directories
      mcTask.getHandWrittenCodeDir().setFrom(sourceSet.getJava().getSrcDirs());

      // Do not set handwritten grammars directory by default

      // add the generated output to the SourceSet#allJava, such that they are compiled too
      sourceSet.getJava().srcDir(mcTask.getOutputDir());

      // Set the symbol path input configuration
      mcTask.getSymbolPathConfiguration().from(project.getConfigurations().getByName(MCSourceSets.getSymbolDependencyConfigName(sourceSet)));
    });

       /*
      We would expect the following explicit dependsOn configuration to be unnecessary.
      But in case of empty src/main/java the generateMCGrammars task is skipped
      https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
     */

    // Generate MC must run before JavaCompile
    project.getTasks().named(sourceSet.getCompileJavaTaskName()).configure(compile -> {
      compile.dependsOn(mcGenTaskProvider);
    });
    // Generate MC must run before ProcessResources (e.g. for mc4 grammars)
    project.getTasks().named(sourceSet.getProcessResourcesTaskName()).configure(processResources -> {
      processResources.dependsOn(mcGenTaskProvider);
    });

    // Tell gradle that the MC source directory set is "compiled" by the MCGenTask
    MCGrammarsSourceDirectorySet.getGrammars(sourceSet).compiledBy(mcGenTaskProvider, MCGenTask::getOutputDir);

    return mcGenTaskProvider;
  }


  /**
   * Ensure the test grammar models are aware of the main grammars (and their dependencies)
   *  - let the test-dependency-declaration-config extens the main-dependency-declaration-config
   *  - add a dependency from the test-symbol(!!!)-dependency-declaration-config to the main project (but only for grammar symbols)
   * @param project
   */
  void letTestExtendMain(Project project) {
    project.getPlugins().withId("java", javaPlugin -> {
      SourceSetContainer sourceSets = project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets();

      SourceSet main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
      SourceSet test = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME);

      // testGrammar extendsFrom grammar
      project.getConfigurations().named(MCSourceSets.getDependencyDeclarationConfigName(test))
              .configure(testGrammar -> {
                testGrammar.extendsFrom(
                        project.getConfigurations().getByName(MCSourceSets.getDependencyDeclarationConfigName(main)));
              });

      // testGrammarSymbolDependencies dependency on files(main.grammars.srcDirs)
      project.getConfigurations().named(MCSourceSets.getSymbolDependencyConfigName(test))
              .configure(testGrammar -> {
                FileCollection grammarSrc = MCGrammarsSourceDirectorySet.getGrammars(main).getSourceDirectories();
                Dependency localFilesDependency = project.getDependencies().create(grammarSrc);
                testGrammar.getDependencies().add(localFilesDependency);
              });
    });
  }

}
