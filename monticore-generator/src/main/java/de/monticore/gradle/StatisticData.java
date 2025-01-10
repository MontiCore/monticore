/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.gradle.gen.MCToolAction;
import de.monticore.symboltable.serialization.json.JsonArray;
import de.monticore.symboltable.serialization.json.JsonBoolean;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNull;
import de.monticore.symboltable.serialization.json.JsonNumber;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.TaskState;
import org.gradle.tooling.events.FailureResult;
import org.gradle.tooling.events.OperationResult;
import org.gradle.tooling.events.SuccessResult;
import org.gradle.tooling.events.task.TaskOperationDescriptor;
import org.gradle.tooling.events.task.TaskSuccessResult;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
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
    JsonObject result = new JsonObject();

    {
      JsonArray tasks = new JsonArray();
      tasks.addAll(this.tasks.stream()
          .map(TaskData::getGradleStatisticData)
          .collect(Collectors.toList()));
      result.putMember("Tasks", tasks);
    }

    result.putMember("ProjectName", new UserJsonString(project.getName()));
    result.putMember("Duration", new JsonNumber(""+executionTime.toMillis()));
    result.putMember("GradleVersion", new UserJsonString(this.gradle.getGradleVersion()));
    result.putMember("JavaVersion", new UserJsonString(System.getProperty("java.version")));

    try {
      Properties localProperties = new Properties();
      localProperties.load(this.getClass().getResourceAsStream("/buildInfo.properties"));

      result.putMember("MCVersion", new UserJsonString(localProperties.getProperty("version")));
    }catch(IOException ignored){}

    result.putMember("TotalMemory", new JsonNumber(""+Runtime.getRuntime().totalMemory()));
    result.putMember("FreeMemory", new JsonNumber(""+Runtime.getRuntime().freeMemory()));
    result.putMember("MaxMemory", new JsonNumber(""+Runtime.getRuntime().maxMemory()));
    result.putMember("AvailableProcessors", new JsonNumber(""+Runtime.getRuntime().availableProcessors()));

    result.putMember("GradleParallel", new JsonBoolean( Boolean.parseBoolean(getGradleProperty("org.gradle.parallel", "false"))));
    result.putMember("GradleCache", new JsonBoolean( Boolean.parseBoolean(getGradleProperty("org.gradle.caching", "false"))));
    result.putMember("HasBuildCacheURL", new JsonBoolean( gradle.getRootProject().getProperties().containsKey("buildCacheURL")));
    result.putMember("IsCi", new JsonBoolean( gradle.getRootProject().getProperties().containsKey("ci")));
    result.putMember("MaxConcurrentMC", new JsonNumber("" + MCToolAction.getMaxConcurrentMC()));

    result.putMember("Tags", new UserJsonString(getGradleProperty("de.monticore.gradle.tags", "")));

    return result.toString();
  }

  /**
   * TaskOperationDescriptor does not contain the type,
   *  which is why we add them in a second them
   */
  public void addTaskTypes() {
    for (TaskData t: this.tasks) {
      if (!t.data.hasMember("Type")) {
        try {
          Task task = project.getTasks().getByPath(t.data.getStringMember("Path"));
          t.data.putMember("Type", new UserJsonString(task.getClass().getName()));
        } catch (UnknownTaskException ignored) {}
      }
    }
  }

  public static class TaskData implements GradleTaskStatistic{
    protected final JsonObject data;

    @Deprecated(forRemoval = true)
    public TaskData(Task task, TaskState taskState, Duration executionTime) {
      data = new JsonObject();
      data.putMember("Name", new UserJsonString(task.getName()));
      data.putMember("ProjectName", new UserJsonString(task.getProject().getDisplayName()));
      data.putMember("Type", new UserJsonString(task.getClass().getName()));

      data.putMember("Duration", new JsonNumber(""+executionTime.toMillis()));
      data.putMember("UpToDate", new JsonBoolean(taskState.getUpToDate()));
      data.putMember("Cached", new JsonBoolean(Objects.equals(taskState.getSkipMessage(), "FROM CACHE")));
      data.putMember("DidWork", new JsonBoolean(taskState.getDidWork()));
      data.putMember("hasError", new JsonBoolean(taskState.getFailure() != null));

      {
        JsonElement taskStats;
        if(task instanceof GradleTaskStatistic){
          taskStats = ((GradleTaskStatistic) task).getGradleStatisticData();
        } else{
          taskStats = new JsonNull();
        }
        data.putMember("TaskStatistik", taskStats);
      }
    }

    public TaskData(TaskOperationDescriptor taskOpDesc, OperationResult result) {
      data = new JsonObject();
      String name = taskOpDesc.getName();
      int pathIndex = name.lastIndexOf(':');
      data.putMember("Name", new UserJsonString(pathIndex >= 0 ? name.substring(pathIndex + 1) : name));
      data.putMember("Path", new UserJsonString(taskOpDesc.getTaskPath()));
      data.putMember("Duration", new JsonNumber("" + (result.getEndTime() - result.getStartTime())));
      if (result instanceof SuccessResult) {
        data.putMember("UpToDate", new JsonBoolean(((TaskSuccessResult)result).isUpToDate()));
        data.putMember("Cached", new JsonBoolean(((TaskSuccessResult)result).isFromCache()));
        data.putMember("hasError", new JsonBoolean(false));
      } else if (result instanceof FailureResult){
        data.putMember("UpToDate", new JsonBoolean(false));
        data.putMember("Cached", new JsonBoolean(false));
        data.putMember("hasError", new JsonBoolean(true));
      } else {
        throw new IllegalStateException("Unexpected result type: " + result.getClass());
      }

      data.putMember("TaskStatistik", new JsonNull());
    }

    @Override
    public JsonElement getGradleStatisticData() {
      return data;
    }
  }
}

