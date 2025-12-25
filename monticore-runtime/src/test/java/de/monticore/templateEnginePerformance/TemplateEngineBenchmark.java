package de.monticore.templateEnginePerformance;

import com.google.common.base.Stopwatch;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Disabled("For manual investigation of performance improvements/regressions between versions")
public class TemplateEngineBenchmark {

  @Test
  public void measurePerformanceSmallTemplate() {
    measurePerformance("templateEnginePerformance.Small");
  }

  @Test
  public void measurePerformanceMediumTemplate() {
    measurePerformance("templateEnginePerformance.Medium");
  }

  @Test
  public void measurePerformanceLargeTemplate() {
    measurePerformance("templateEnginePerformance.Large");
  }

  public void measurePerformance(String templateName){
    final TemplateHookPoint templateHookPoint = new TemplateHookPoint(templateName);
    GeneratorSetup config = new GeneratorSetup();
    TemplateController controller = new TemplateController(config, templateName);
    Stopwatch stopwatch = Stopwatch.createStarted();

    long i = 0;
    while (stopwatch.elapsed(TimeUnit.SECONDS) < 10){
      templateHookPoint.processValue(controller, List.of());
      i++;
    }
    long totalDur = stopwatch.elapsed(TimeUnit.MILLISECONDS);

    System.out.println("Took " + totalDur + "ms for " + i + " iterations. " + ((double)totalDur / i) + "ms avg");
  }
}
