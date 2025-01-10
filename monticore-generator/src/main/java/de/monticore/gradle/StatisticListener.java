/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.generating.templateengine.reporting.commons.StatisticsHandler;
import de.se_rwth.commons.logging.Log;
import org.gradle.BuildListener;
import org.gradle.BuildResult;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.gradle.tooling.events.FinishEvent;
import org.gradle.tooling.events.OperationCompletionListener;
import org.gradle.tooling.events.task.TaskFinishEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;

public abstract class StatisticListener
        implements BuildService<BuildServiceParameters.None>, OperationCompletionListener, AutoCloseable,
        BuildListener {
  public final static String enable_tracking = "de.monticore.gradle.performance_statistic";

  @Nullable
  protected StatisticData data;
  @Nullable
  protected Instant projectStartTime;


  @Override
  public void beforeSettings(@Nonnull Settings settings) {
    // Empty, but required
  }

  @Override
  public void settingsEvaluated(@Nonnull Settings settings) {
    // Empty, but required
  }

  @Override
  public void projectsLoaded(@Nonnull Gradle gradle) {
    // Empty, but required
  }

  /**
   * @param gradle The build which has been evaluated. Never null.
   */
  @Override
  public void projectsEvaluated(@Nonnull Gradle gradle) {
    Log.debug("projectsEvaluated", this.getClass().getName());
    this.data = new StatisticData();
    this.data.setProject(gradle.getRootProject());
    this.data.setGradle(gradle);
    this.projectStartTime = Instant.now();
  }


  /**
   * The build has finished
   */
  @Override
  public void close() {
    Log.debug("buildFinished", this.getClass().getName());
    if (this.projectStartTime != null && this.data != null) {

      this.data.addTaskTypes();
      this.data.setExecutionTime(Duration.between(this.projectStartTime, Instant.now()));

      StatisticsHandler.storeReport(this.data.toString(), "MC_GRADLE_JSON");
    } else {
      Log.info("<projectStartTime> was null. ", this.getClass().getName());
    }
    this.data = null;
  }


  /**
   * Called when an operation completes.
   *
   * @param finishEvent event
   */
  @Override
  public void onFinish(FinishEvent finishEvent) {
    if (this.data == null) { // Stats appear to be disabled
      return;
    }
    if (finishEvent instanceof TaskFinishEvent) {
      // Handle task finish event...
      StatisticData.TaskData taskData = new StatisticData.TaskData(((TaskFinishEvent) finishEvent).getDescriptor(),
                                                                   ((TaskFinishEvent) finishEvent).getResult());
      data.addTask(taskData);
    }
  }

  @Override
  public void buildFinished(@Nonnull BuildResult result) {
    // Using #close() of the BuildService instead
  }

}
