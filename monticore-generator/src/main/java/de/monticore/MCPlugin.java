/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.gradle.StatisticListener;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.build.event.BuildEventsListenerRegistry;

import javax.inject.Inject;

/**
 * This class realizes the plugin itself.
 * The plugin is only used to provide task types
 * MCTask and GroovyTask but no predefined task instances
 */
public abstract class MCPlugin implements Plugin<Project> {

  public static final String GRAMMAR_CONFIGURATION_NAME = "grammar";
  public static final String MC_MILL_BUILD_SERVICE = "MCMillService";

  public void apply(Project project) {
    project.getExtensions().getExtraProperties().set("MCTask", de.monticore.MCTask.class);
    project.getExtensions().getExtraProperties().set("MontiTransExec", de.monticore.MontiTransExec.class);
    project.getConfigurations().maybeCreate(GRAMMAR_CONFIGURATION_NAME);

    // Tasks using MontiCore Mills can not run in parallel
    project.getGradle().getSharedServices().registerIfAbsent(MC_MILL_BUILD_SERVICE, MCMillBuildService.class, spec -> spec.getMaxParallelUsages().set(1));


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


}
