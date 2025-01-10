/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.tasks.TaskStateInternal;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.TaskState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatisticListenerTest {

  @Test
  void shouldStoreTaskExecutionTime() {
    // Given
    String taskName = "Task1";

    Task task = mock(Task.class);
    when(task.getName()).thenReturn(taskName);
    when(task.getProject()).thenReturn(mock(Project.class));

    TaskState state = new TaskStateInternal();

    StatisticListener listener = new StatisticListener();
    listener.projectsEvaluated(mock(Gradle.class));

    // Call beforeExecute before afterExecute
    listener.beforeExecute(task);

    int sizePre = listener.data.tasks.size();

    // When
    listener.afterExecute(task, state);

    // Then
    int size = listener.data.tasks.size();

    Assertions.assertEquals(size, sizePre + 1);
    Assertions.assertEquals(listener.data.tasks.get(size - 1)
      .data.getMember("Name").getAsJsonString().getValue(), taskName);
  }

  @Test
  void shouldNotStoreTaskExecutionTime() {
    // Given
    String taskName = "Task1";

    Task task = mock(Task.class);
    when(task.getName()).thenReturn(taskName);
    when(task.getProject()).thenReturn(mock(Project.class));

    TaskState state = new TaskStateInternal();

    StatisticListener listener = new StatisticListener();
    listener.projectsEvaluated(mock(Gradle.class));

    // Do not call beforeExecute
    // listener.beforeExecute(task);

    int sizePre = listener.data.tasks.size();

    // When
    listener.afterExecute(task, state);

    // Then
    int size = listener.data.tasks.size();

    Assertions.assertEquals(size, sizePre);
  }
}
