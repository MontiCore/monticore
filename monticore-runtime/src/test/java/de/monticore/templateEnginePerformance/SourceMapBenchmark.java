/* (c) https://github.com/MontiCore/monticore */
package de.monticore.templateEnginePerformance;

import com.google.common.base.Stopwatch;
import de.monticore.ast.ASTNodeMock;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.reporter.TemplateSourceMappingReporter;
import de.monticore.generating.templateengine.sourcemap.SourceMapCalculator;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Disabled("For manual investigation of SourceMap generation performance overhead")
public class SourceMapBenchmark {

  protected static final File OUTPUT_DIR = new File("target/sourcemap-benchmark");
  protected static final Path OUTPUT_FILE = Path.of("Generated.java");

  protected static final long WARMUP_SECONDS = 2;
  protected static final long MEASURE_SECONDS = 10;

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.getFindings().clear();
    OUTPUT_DIR.mkdirs();
    Reporting.off();
    Reporting.clearReportHooks();
    SourceMapCalculator.reset();
  }

  @AfterEach
  public void tearDown() {
    Reporting.off();
    Reporting.clearReportHooks();
    SourceMapCalculator.reset();
  }

  @Test
  public void measureSmallTemplate() {
    compareTemplatePerformance("templateEnginePerformance.Small");
  }

  @Test
  public void measureMediumTemplate() {
    compareTemplatePerformance("templateEnginePerformance.Medium");
  }

  @Test
  public void measureLargeTemplate() {
    compareTemplatePerformance("templateEnginePerformance.Large");
  }

  @Test
  public void measureInterpolationsTemplate() {
    compareTemplatePerformance("templateEnginePerformance.Interpolations", "InterpolationsBench", 50);
  }

  @Test
  public void measureIncludesTemplate() {
    compareTemplatePerformance("templateEnginePerformance.Includes", "IncludesBench", 50);
  }

  @Test
  public void measureCompressTemplate() {
    compareTemplatePerformance("templateEnginePerformance.Compress", "CompressBench", 50);
  }

  @Test
  public void measureRealisticTemplate() {
    compareTemplatePerformance("templateEnginePerformance.Realistic", "RealisticBench", 30);
  }

  @Test
  public void measureAll() {
    measureSmallTemplate();
    measureMediumTemplate();
    measureLargeTemplate();
    measureInterpolationsTemplate();
    measureIncludesTemplate();
    measureCompressTemplate();
    measureRealisticTemplate();
  }

  protected void compareTemplatePerformance(String templateName, Object... templateArgs) {
    BenchmarkResult baseline = measureMode(templateName, false, false, templateArgs);
    BenchmarkResult withMapNoContent = measureMode(templateName, true, false, templateArgs);
    BenchmarkResult withMapAndContent = measureMode(templateName, true, true, templateArgs);

    System.out.println("=== " + templateName + " ===");
    System.out.printf(Locale.ROOT,
        "  Baseline (OFF)               : Took %6dms for %6d iterations (%.4f ms/iter)%n",
        baseline.totalMillis, baseline.iterations, baseline.avgMillis());
    printComparison("SourceMap (addContent=false)", baseline, withMapNoContent);
    printComparison("SourceMap (addContent=true) ", baseline, withMapAndContent);
    System.out.println();
  }

  protected BenchmarkResult measureMode(String templateName, boolean enableSourceMap, boolean addContent, Object... templateArgs) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(OUTPUT_DIR);
    setup.setTracing(false);
    GeneratorEngine generatorEngine = new GeneratorEngine(setup);

    try {
      if (enableSourceMap) {
        ReportManager.ReportManagerFactory factory = modelName -> {
          ReportManager reports = new ReportManager(OUTPUT_DIR.getPath());
          reports.addReportEventHandler(new TemplateSourceMappingReporter(OUTPUT_DIR.getPath(), addContent));
          return reports;
        };
        Reporting.init(OUTPUT_DIR.getPath(), factory);
        Reporting.on("BenchmarkModel");
      }

      Stopwatch warmup = Stopwatch.createStarted();
      while (warmup.elapsed(TimeUnit.SECONDS) < WARMUP_SECONDS) {
        generatorEngine.generate(templateName, OUTPUT_FILE, ASTNodeMock.INSTANCE, templateArgs);
      }

      // Timed measurement
      Stopwatch stopwatch = Stopwatch.createStarted();
      long iterations = 0;
      while (stopwatch.elapsed(TimeUnit.SECONDS) < MEASURE_SECONDS) {
        generatorEngine.generate(templateName, OUTPUT_FILE, ASTNodeMock.INSTANCE, templateArgs);
        iterations++;
      }
      long totalMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
      return new BenchmarkResult(totalMillis, iterations);
    } finally {
      Reporting.off();
      Reporting.clearReportHooks();
      SourceMapCalculator.reset();
    }
  }

  protected void printComparison(String label, BenchmarkResult baseline, BenchmarkResult measured) {
    double factor = measured.avgMillis() / baseline.avgMillis();
    double overheadPercent = (factor - 1.0) * 100.0;
    System.out.printf(Locale.ROOT,
        "  %s : Took %6dms for %6d iterations (%.4f ms/iter)  [%.2fx / %+.1f%%]%n",
        label, measured.totalMillis, measured.iterations, measured.avgMillis(), factor, overheadPercent);
  }

  protected record BenchmarkResult(long totalMillis, long iterations) {
    double avgMillis() {
      return iterations > 0 ? (double) totalMillis / iterations : Double.NaN;
    }
  }
}
