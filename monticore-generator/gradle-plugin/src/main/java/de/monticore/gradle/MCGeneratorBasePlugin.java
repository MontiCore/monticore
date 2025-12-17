/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.MCMillBuildService;
import de.monticore.MCPlugin;
import de.monticore.gradle.common.MCBuildInfoTask;
import de.monticore.gradle.dependencies.MCToolDependenciesPlugin;
import de.monticore.gradle.gen.MCGenTask;
import de.monticore.gradle.internal.ProgressLoggerService;
import de.monticore.gradle.queue.CachedQueueService;
import de.monticore.gradle.queue.CachedQueueServicePlugin;
import de.monticore.gradle.queue.ICachedQueueService;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.build.event.BuildEventsListenerRegistry;
import org.gradle.language.jvm.tasks.ProcessResources;

import javax.inject.Inject;

/**
 * Provide a "MCGenTask" task.
 */
public abstract class MCGeneratorBasePlugin implements Plugin<Project> {

  /**
   * An option to override
   */
  public final static String CONCURRENT_MC_PROPERTY = "de.monticore.gradle.max-concurrent-mcgen";

  public void apply(Project project) {
    // Set up the improved work-queue
    project.getPluginManager().apply(CachedQueueServicePlugin.class);
    // Populate the "mcTool" configuration with the generator itself
    project.getPluginManager().apply(MCToolDependenciesPlugin.class);
    // ServiceProvider to pass a ProgressLogger instance to the workers
    Provider<ProgressLoggerService> serviceProvider = project.getGradle().getSharedServices()
            .registerIfAbsent(ProgressLoggerService.class.getSimpleName(),
                    ProgressLoggerService.class, spec -> {});

    // Provide the MCGenTask easily
    project.getExtensions().getExtraProperties().set("MCGenTask", MCGenTask.class);

    // Add a task which writes the buildInfo.properties file
    TaskProvider<?> writeMCBuildInfo = project.getTasks().register("writeMCBuildInfo", MCBuildInfoTask.class, mcBuildInfoTask -> {
      // With an input property ensuring UP-TO-DATE checks consider the version
      mcBuildInfoTask.getVersion().set(project.provider(() -> project.getVersion().toString()));
    });

    // writeMCBuildInfo should always be performed before processing resources
    project.getTasks().withType(ProcessResources.class).configureEach(t -> {
      t.dependsOn(writeMCBuildInfo);
    });

    // pass the ProgressLogger ServiceProvider to the task
    project.getTasks().withType(MCGenTask.class).configureEach(t -> {
      t.dependsOn(writeMCBuildInfo);
      try {
        // "Build services cannot be serialized"
        // This results in suboptimal Gradle-reporting of the ProgressLogger-Operations
        // t.getProgressLoggerService().set(serviceProvider);
      }catch (IllegalArgumentException ignored){
        // Sometimes a "Cannot set the value of task" exception occurs
        // due to progressLoggerService being isolated somehow
      }
    });

    // Also provide the legacy MCTask (for now)
    this.applyLegacy(project);

    // Limit the maximum amount
    if (project.findProperty(CONCURRENT_MC_PROPERTY) != null) {
      var sp = project.getGradle().getSharedServices().registerIfAbsent(CachedQueueService.NAME, CachedQueueService.class, spec->{});
      ICachedQueueService.asICachedQueueService(sp.get())
              .setMaxConcurrentMC(Integer.parseInt(project.findProperty(CONCURRENT_MC_PROPERTY).toString()));
    }

    // Statistics Logging
    boolean withStats = (!project.hasProperty(StatisticListener.enable_tracking)
            || "true".equals(project.getProperties().get(StatisticListener.enable_tracking)));
    if (withStats) {
      Provider<StatisticListener> statsServiceProvider =
              project.getGradle().getSharedServices().registerIfAbsent(
                      "mcStatsProvider", StatisticListener.class, spec -> {});

      getEventsListenerRegistry().onTaskCompletion(statsServiceProvider);

      project.getGradle().addListener(statsServiceProvider.get());
    }
  }

  @Inject
  public abstract BuildEventsListenerRegistry getEventsListenerRegistry();


  protected void applyLegacy(Project project) {
    project.getExtensions().getExtraProperties().set("MCTask", de.monticore.MCTask.class);
    project.getConfigurations().maybeCreate(MCPlugin.GRAMMAR_CONFIGURATION_NAME);
    // (old) Tasks using MontiCore Mills can not run in parallel
    project.getGradle().getSharedServices().registerIfAbsent(MCPlugin.MC_MILL_BUILD_SERVICE, MCMillBuildService.class, spec -> spec.getMaxParallelUsages().set(1));
  }

}
