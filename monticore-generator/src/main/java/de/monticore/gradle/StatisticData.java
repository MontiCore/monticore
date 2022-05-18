package de.monticore.gradle;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.TaskState;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticData {

  protected Project project;
  protected Gradle gradle;
  protected Duration executionTime;
  protected List<TaskData> tasks = new ArrayList<>();

  public void setExecutionTime(Duration between) {
    this.executionTime = between;
  }

  public void addTask(TaskData t) {
    tasks.add(t);
  }

  public void setGradle(Gradle gradle) {
    this.gradle = gradle;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  private String getGradleProperty(String key, String pDefault){
    String value = (String) gradle.getRootProject().getProperties().get(key);
    if(value == null) {
      value = pDefault;
    }
    return value;
  }

  public String toString() {
    Map<String, Object> result = new LinkedHashMap<>();


    result.put("Tasks", this.tasks.stream()
        .map(TaskData::toString)
        .collect(Collectors.joining()));


    result.put("ProjectName", '"' + this.project.getName() + '"');
    result.put("Duration", this.executionTime.toMillis());

    result.put("GradleVersion", '"' + this.gradle.getGradleVersion() + '"');
    result.put("JavaVersion", '"' + System.getProperty("java.version") + '"');

    try {
      Properties localProperties = new Properties();
      localProperties.load(this.getClass().getResourceAsStream("/buildInfo.properties"));

      result.put("MCVersion", '"' + localProperties.getProperty("version") + '"');
    }catch(IOException ignored){}


    result.put("TotalMemory", Runtime.getRuntime().totalMemory());
    result.put("FreeMemory", Runtime.getRuntime().freeMemory());
    result.put("MaxMemory", Runtime.getRuntime().maxMemory());
    result.put("AvailableProcessors", Runtime.getRuntime().availableProcessors());


    result.put("GradleParallel", getGradleProperty("org.gradle.parallel", "false"));
    result.put("GradleCache", getGradleProperty("org.gradle.caching", "false"));
    result.put("HasBuildCacheURL", gradle.getRootProject().getProperties().containsKey("buildCacheURL"));
    result.put("IsCi", System.getenv().containsKey("CI"));

    result.put("Tags", '"' + getGradleProperty("de.monticore.gradle.tags", "") + '"');

    return result.entrySet().stream()
        .map(e -> e.getKey() + ": " + e.getValue())
        .map(s -> s + "\n")
        .collect(Collectors.joining());
  }

  public static class TaskData {
    protected String name;
    protected String projectName;
    protected Class<? extends Task> type;
    protected boolean cacheEnabled;
    protected boolean isUpToDate;
    protected boolean isCached;
    protected boolean skipped;
    protected boolean didWork;
    protected boolean hasError;
    protected Duration executionTime;

    public TaskData(Task task, TaskState taskState, Duration executionTime) {
      this.name = task.getName();
      this.projectName = task.getProject().getDisplayName();
      this.type = task.getClass();

      this.executionTime = executionTime;

      this.skipped = taskState.getSkipped();
      this.didWork = taskState.getDidWork();
      this.hasError = taskState.getFailure() != null;
      this.isUpToDate = taskState.getUpToDate();
      this.isCached = Objects.equals(taskState.getSkipMessage(), "FROM CACHE");
    }

    public String toString() {
      Map<String, Object> result = new LinkedHashMap<>();

      result.put("Name", '"' + this.name + '"');
      result.put("ProjektName", '"' + this.projectName + '"');
      result.put("Type", '"' + this.type.getName() + '"');
      result.put("Duration", this.executionTime.toMillis());
      result.put("UpToDate", this.isUpToDate);
      result.put("Cached", this.isCached);
      result.put("Skipped", this.skipped);
      result.put("DidWork", this.didWork);
      result.put("hasError", this.hasError);

      return "\n-" +
          result.entrySet().stream()
          .map(e -> e.getKey() + ": " + e.getValue())
          .map(s -> "    " + s + "\n")
          .collect(Collectors.joining())
          .substring(1);
    }
  }

}

