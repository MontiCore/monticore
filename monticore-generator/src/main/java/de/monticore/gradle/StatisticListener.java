/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.generating.templateengine.reporting.commons.StatisticsHandler;
import de.se_rwth.commons.logging.Log;
import org.gradle.BuildListener;
import org.gradle.BuildResult;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.TaskState;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class StatisticListener implements BuildListener, TaskExecutionListener {
  public final static String enable_tracking = "de.monticore.gradle.performance_statistic";
  public final static String show_report = "de.monticore.gradle.show_performance_statistic";

  private static final AtomicBoolean alreadyRegistered = new AtomicBoolean(false);
  private static final StatisticListener singleton = new StatisticListener();

  protected Map<Task, Instant> startTime = new HashMap<>();
  protected Instant projectStartTime;
  protected StatisticData data;

  private static synchronized StatisticListener getSingleton() {
    return singleton;
  }

  public synchronized static void registerOnce(Project project) {
    if (alreadyRegistered.compareAndSet(false, true)
      && (!project.hasProperty(StatisticListener.enable_tracking)
      || "true".equals(project.getProperties().get(StatisticListener.enable_tracking)))) {
      project.getGradle().addListener(getSingleton());
    }
  }

  // buildStarted was replaced by beforeSettings in Gradle 7.
  // To ensure compatibility with both Gradle 6 and 7 both methods are overridden without an Annotation.
  public void buildStarted(Gradle gradle) {

  }

  public void beforeSettings(Settings settings) {

  }

  @Override
  public void settingsEvaluated(Settings settings) {

  }

  @Override
  public void projectsLoaded(Gradle gradle) {

  }

  @Override
  public void projectsEvaluated(Gradle gradle) {
    Log.debug("projectsEvaluated", this.getClass().getName());
    this.data = new StatisticData();
    this.data.setProject(gradle.getRootProject());
    this.data.setGradle(gradle);

    projectStartTime = Instant.now();
  }

  @Override
  public void buildFinished(BuildResult buildResult) {
    Log.debug("buildFinished", this.getClass().getName());
    alreadyRegistered.set(false);   // Reset is necessary, otherwise Listener is not used in next build

    if (projectStartTime != null) {
      data.setExecutionTime(Duration.between(projectStartTime, Instant.now()));


      if ("true".equals(buildResult.getGradle().getRootProject().getProperties().get(show_report))) {
        System.out.println(data.toString());
      }
      StatisticsHandler.storeReport(data.toString(), "MC_GRADLE_JSON");
    } else {
      Log.info("<projectStartTime> was null. ", this.getClass().getName());
    }
  }

  @Override
  public void beforeExecute(Task task) {
    Log.trace(
      "Start before task execution for Task `"
        + task.getName()
        + "`",
      this.getClass().getName()
    );

    startTime.put(task, Instant.now());

    Log.trace(
      "Finish before task execution for Task `"
        + task.getName()
        + "`",
      this.getClass().getName()
    );
  }

  @Override
  public void afterExecute(Task task, TaskState taskState) {
    Log.trace(
      "Start after task execution for Task `"
        + task.getName()
        + "`",
      this.getClass().getName()
    );

    Instant taskStartTime = startTime.remove(task);

    if (taskStartTime != null) {
      Duration duration = Duration.between(taskStartTime, Instant.now());
      StatisticData.TaskData taskData = new StatisticData.TaskData(task, taskState, duration);
      data.addTask(taskData);
    } else {
      Log.debug(
        "The <taskStartTime> of Task `"
          + task.getName()
          + "` was null.",
        this.getClass().getName()
      );
    }

    Log.trace(
      "Finish after task execution for Task `"
        + task.getName()
        + "`",
      this.getClass().getName()
    );
  }

}
