/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.gradle.gen.MCGenTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;

/**
 * This class realizes the plugin itself.
 * We provide a "MCGenTask" task,
 *  but also add an instance of this task named generateMCGrammars by default
 */
@SuppressWarnings("unused")
public class MCGeneratorPlugin implements Plugin<Project> {

  public void apply(Project project) {
    project.getPlugins().apply("java");
    project.getPlugins().apply(MCGeneratorBasePlugin.class);

    // Add a MCGenTask by default
    TaskProvider<MCGenTask> generateMCGrammarsTask = createDefaultMCGenTask(project);

    // Add the generateMCGrammarsTask output directory (lazily) to the main.java source set
    project.getExtensions().getByType(SourceSetContainer.class).named("main")
            .configure(sourceSet -> generateMCGrammarsTask
                    .configure(mcGenTask -> sourceSet.getJava().srcDir(mcGenTask.getOutputDir())));

    /*
      We would expect the following explicit dependsOn configuration to be unnecessary.
      But in case of empty src/main/java the generateMCGrammars task is skipped
      https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
     */

    // Generate MC must run before JavaCompile
    project.getTasks().named("compileJava").configure(compile -> {
      compile.dependsOn(generateMCGrammarsTask);
    });
    // Generate MC must run before ProcessResources (e.g. for mc4 grammars)
    project.getTasks().named("processResources").configure(compile -> {
      compile.dependsOn(generateMCGrammarsTask);
    });

  }


  /**
   * Provide the default generateMCGrammars MCGenTask,
   * which consumes src/main/grammars and outputs to
   * $out/generated-sources/monticore/sourcecode
   * The default properties of this task can easily be changed from within the
   * build.gradle
   */
  protected TaskProvider<MCGenTask> createDefaultMCGenTask(final Project project) {
    return project.getTasks().register("generateMCGrammars", MCGenTask.class, mcTask -> {
      mcTask.setGroup("mc");
      // Always set output dir (Gradle creates it, if not already exists)
      // ensure to use a lazy-provider here
      mcTask.getOutputDir().set(project.getLayout().getBuildDirectory()
              .dir("generated-sources/monticore/sourcecode"));
    });
  }
}
