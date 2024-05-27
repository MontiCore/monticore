/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.MCMillBuildService;
import de.monticore.MCPlugin;
import de.monticore.gradle.common.MCBuildInfoTask;
import de.monticore.gradle.gen.MCGenTask;
import de.monticore.gradle.internal.ProgressLoggerService;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.jvm.tasks.ProcessResources;

/**
 * Provide a "MCGenTask" task.
 */
public class MCGeneratorBasePlugin implements Plugin<Project> {

  public void apply(Project project) {
    // ServiceProvider to pass a ProgressLogger instance to the workers
    Provider<ProgressLoggerService> serviceProvider = project.getGradle().getSharedServices()
            .registerIfAbsent(ProgressLoggerService.class.getSimpleName(),
                    ProgressLoggerService.class, spec -> {});

    // Provide the MCGenTask easily
    project.getExtensions().getExtraProperties().set("MCGenTask", MCGenTask.class);

    // Add a task which writes the buildInfo.properties file
    TaskProvider<?> writeMCBuildInfo = project.getTasks().register("writeMCBuildInfo", MCBuildInfoTask.class);

    // writeMCBuildInfo should always be performed before processing resources
    project.getTasks().withType(ProcessResources.class).configureEach(t -> {
      t.dependsOn(writeMCBuildInfo);
    });

    // pass the ProgressLogger ServiceProvider to the task
    project.getTasks().withType(MCGenTask.class).configureEach(t -> {
      t.dependsOn(writeMCBuildInfo);
      try {
        t.getProgressLoggerService().set(serviceProvider);
      }catch (IllegalArgumentException ignored){
        // Sometimes a "Cannot set the value of task" exception occurs
        // due to progressLoggerService being isolated somehow
      }
    });

    // Also provide the legacy MCTask (for now)
    this.applyLegacy(project);

    StatisticListener.registerOnce(project);
  }

  protected void applyLegacy(Project project) {
    project.getExtensions().getExtraProperties().set("MCTask", de.monticore.MCTask.class);
    project.getConfigurations().maybeCreate(MCPlugin.GRAMMAR_CONFIGURATION_NAME);
    // (old) Tasks using MontiCore Mills can not run in parallel
    project.getGradle().getSharedServices().registerIfAbsent(MCPlugin.MC_MILL_BUILD_SERVICE, MCMillBuildService.class, spec -> spec.getMaxParallelUsages().set(1));
  }

}
