package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;

import static de.monticore.generating.templateengine.reporting.Reporting.*;

/**
 * {@link IIncrementalListener} implementation that forwards incremental AST
 * transformation events to MontiCore reporting.
 */
public class ReportingTrafoListener implements IIncrementalListener {
  
  @Nullable
  protected String currentTransformationName;
  
  /**
   * Creates a listener with explicit output paths.
   *
   * @param modelName name of the processed model
   * @param outputDir target directory for transformation reports
   * @param reportDir base directory for reporting
   */
  public ReportingTrafoListener(String modelName, Path outputDir, Path reportDir) {
    setupReporting(modelName, outputDir, reportDir);
  }
  
  /**
   * Creates a listener with default paths under {@code target/generated-sources}.
   *
   * @param modelName name of the processed model
   */
  public ReportingTrafoListener(String modelName) {
    setupReporting(modelName, Path.of("target/generated-sources/reports/transformations"),
        Path.of("target/generated-sources"));
  }
  
  /**
   * Initializes reporting for the given model.
   *
   * @param modelName name of the processed model
   * @param outputDir target directory for transformation reports
   * @param reportDir base directory for reporting
   */
  protected void setupReporting(String modelName, Path outputDir, Path reportDir) {
    ReportManager.ReportManagerFactory factory = new ReportManager.ReportManagerFactory() {
      
      @Override
      public ReportManager provide(String modelName) {
        ReportManager reports = new ReportManager(reportDir.toAbsolutePath().toString());
        TransformationReporter transformationReporter =
            new TransformationReporter(outputDir.toAbsolutePath().toString(), modelName,
                new ReportingRepository(new ASTNodeIdentHelper()));
        reports.addReportEventHandler(transformationReporter);
        return reports;
      }
    };
    
    Reporting.init(reportDir.toAbsolutePath().toString(), factory);
    Reporting.on(modelName);
  }
  
  /**
   * Flushes all collected report data for the given AST node.
   *
   * @param node AST root or relevant node used for flushing
   */
  public void flush(ASTNode node) {
    Reporting.flush(node);
  }
  
  /**
   * Stores the name of the currently running transformation for subsequent report events.
   *
   * @param transformationName name of the started transformation
   */
  @Override
  public void onTransformationStart(@Nonnull String transformationName) {
    this.currentTransformationName = transformationName;
    Reporting.reportTransformationStart(transformationName);
  }
  
  /**
   * Closes the context of the currently running transformation.
   *
   * @param transformationName name of the finished transformation
   */
  @Override
  public void onTransformationEnd(@Nonnull String transformationName) {
    this.currentTransformationName = null;
  }
  
  /**
   * Reports the creation of an AST node as a transformation event.
   *
   * @param node newly attached AST node
   * @param parent optional parent node
   */
  @Override
  public void onASTNodeAttach(@Nonnull ASTNode node, @Nullable ASTNode parent) {
    reportTransformationObjectCreation(this.currentTransformationName, node);
  }
  
  /**
   * Reports the removal of an AST node as a transformation event.
   *
   * @param node removed AST node
   * @param parent parent node of the removed child
   */
  @Override
  public void onASTNodeDetach(@Nonnull ASTNode node, @Nonnull ASTNode parent) {
    reportTransformationObjectDeletion(this.currentTransformationName, node);
  }
  
  /**
   * Reports an attribute change on an AST node, including old/new values.
   *
   * @param node affected AST node
   * @param attributeName changed attribute
   * @param modificationType kind of modification
   * @param oldValue previous value (if available)
   * @param newValue new value (if available)
   */
  @Override
  public void onASTNodeModification(@Nonnull ASTNode node, @Nonnull String attributeName,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue) {
    reportTransformationObjectChange(this.currentTransformationName, node, attributeName);
    if (modificationType == ModificationOp.UNSET || modificationType == ModificationOp.REPLACE) {
      reportTransformationOldValue(this.currentTransformationName,
          oldValue != null ? oldValue.toString() : "null");
    }
    if (modificationType == ModificationOp.SET || modificationType == ModificationOp.REPLACE) {
      reportTransformationNewValue(this.currentTransformationName,
          newValue != null ? newValue.toString() : "null");
    }
  }
  
  /**
   * Reports a list modification using the same reporting logic as scalar attributes.
   *
   * @param node affected AST node
   * @param attributeName name of the modified list attribute
   * @param idx affected list index
   * @param modificationType kind of modification
   * @param oldValue previous value (if available)
   * @param newValue new value (if available)
   */
  @Override
  public void onASTNodeListModification(@Nonnull ASTNode node, String attributeName, int idx,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue) {
    // Reporting does not differ between list and object modification
    onASTNodeModification(node, attributeName, modificationType, oldValue, newValue);
  }
}
