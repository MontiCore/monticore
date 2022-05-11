package de.monticore.gradle;

import org.gradle.api.Task;
import org.gradle.api.tasks.TaskState;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StatisticData {
  protected String projectName;
  protected Duration executionTime;
  protected List<TaskData> tasks = new ArrayList<>();

  public void setExecutionTime(Duration between) {
    this.executionTime = between;
  }

  public static class TaskData {
    protected String name;
    protected Class<? extends Task> type;
    protected boolean cacheEnabled;
    protected boolean isUpToDate;
    protected boolean isCached;
    protected Duration executionTime;

    public void setExecutionTime(Duration executionTime) {
      this.executionTime = executionTime;
    }

    public TaskData(Task task, TaskState taskState, Duration executionTime){
      this.name = task.getName();
      this.type = task.getClass();

      this.executionTime = executionTime;

      this.isUpToDate = taskState.getUpToDate();
      this.isCached = Objects.equals(taskState.getSkipMessage(), "FROM CACHE");
    }

    public String toString(){
      StringBuilder result = new StringBuilder();
      result.append("-- Task " + this.name + "\n");
      result.append("\t Type " + this.type.getName() + "\n");
      result.append("\t Duration " + this.executionTime.toMillis() + "\n");
      result.append("\t UpToDate " + this.isUpToDate + "\n");
      return result.toString();
    }
  }

  public void addTask(TaskData t){
    tasks.add(t);
  }

  public String toString(){
    StringBuilder result = new StringBuilder();
    result.append("Project Name: \t").append(this.projectName).append("\n");
    result.append("Duration: \t").append(this.executionTime.toMillis()).append("\n");

    for(TaskData d: this.tasks){
      result.append(d.toString());
    }
    return result.toString();
  }

}

