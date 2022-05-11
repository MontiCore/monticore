package de.monticore.gradle;

import org.gradle.BuildListener;
import org.gradle.BuildResult;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.TaskState;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class StatisticListener implements BuildListener, TaskExecutionListener {
  public final static String enable_tracking = "de.monticore.gradle.performance_statistic";
  public final static String show_report = "de.monticore.gradle.show_performance_statistic";

  private static volatile AtomicBoolean alreadyRegistered = new AtomicBoolean(false);
  private static volatile StatisticListener singleton;

  private static synchronized StatisticListener getSingleton(){
    if(singleton == null){
      singleton = new StatisticListener();
    }
    return singleton;
  }
  public synchronized static void registerOnce(Project project){
    System.out.println(ManagementFactory.getRuntimeMXBean().getName());
    System.out.println(project.getName() + project.getDepth());
    System.out.println("===== " + alreadyRegistered.get() + "\t Property:" + project.getProperties().get(StatisticListener.enable_tracking));
    if(alreadyRegistered.compareAndSet(false, true)
        && project.hasProperty(StatisticListener.enable_tracking)
        && "true".equals(project.getProperties().get(StatisticListener.enable_tracking))) {
      System.out.println("REGISTERING!!!");
      project.getGradle().addListener(getSingleton());
    }
  }

  protected Map<Task, Instant> startTime = new HashMap<>();
  protected Instant projectStartTime;
  protected StatisticData data = new StatisticData();

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
      System.out.println(this);
      System.out.println("projectsEvaluated");
      System.out.println("Performance statistic of this build are tracked by the Software-Engineering Chair at RWTH Aachen. " +
          "The data will help to improve the speed of the monticore gradle plugin. \n "
      );

      projectStartTime = Instant.now();
  }

  @Override
  public void buildFinished(BuildResult buildResult) {
      System.out.println("buildFinished " + this);

      if ("true".equals(buildResult.getGradle().getRootProject().getProperties().get(show_report))) {
        data.setExecutionTime(Duration.between(projectStartTime, Instant.now()));
        System.out.println(data.toString());
      }

    alreadyRegistered.set(false);
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
