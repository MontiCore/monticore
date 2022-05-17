package de.monticore.gradle;

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
  protected StatisticData data = new StatisticData();

  private static synchronized StatisticListener getSingleton() {
    return singleton;
  }

  public synchronized static void registerOnce(Project project) {
    if (alreadyRegistered.compareAndSet(false, true)
        && project.hasProperty(StatisticListener.enable_tracking)
        && "true".equals(project.getProperties().get(StatisticListener.enable_tracking))) {
      project.getGradle().addListener(getSingleton());
    }
  }

  @Override
  public void buildStarted(Gradle gradle) {
    System.out.println("buildStarted");
  }

  @Override
  public void settingsEvaluated(Settings settings) {
    System.out.println("settingsEvaluated");
  }

  @Override
  public void projectsLoaded(Gradle gradle) {
    System.out.println("projectsLoaded");
  }

  @Override
  public void projectsEvaluated(Gradle gradle) {
    Log.debug("projectsEvaluated", this.getClass().getName());
    System.out.println("Performance statistic of this build are tracked by the Software-Engineering Chair at RWTH Aachen. \n" +
        "The data will help to improve the monticore plugin. \n "
    );

    projectStartTime = Instant.now();
    this.data.setProject(gradle.getRootProject());
    this.data.setGradle(gradle);
  }

  @Override
  public void buildFinished(BuildResult buildResult) {
    Log.debug("buildFinished", this.getClass().getName());

    if ("true".equals(buildResult.getGradle().getRootProject().getProperties().get(show_report))) {
      data.setExecutionTime(Duration.between(projectStartTime, Instant.now()));
      System.out.println(data.toString());
    }

    alreadyRegistered.set(false);   // Reset is necessary, otherwise Listener is not used in next build
  }

  @Override
  public void beforeExecute(Task task) {
    startTime.put(task, Instant.now());
  }

  @Override
  public void afterExecute(Task task, TaskState taskState) {
    Duration duration = Duration.between(startTime.remove(task), Instant.now());
    StatisticData.TaskData taskData = new StatisticData.TaskData(task, taskState, duration);
    data.addTask(taskData);
  }

}
