/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.Names;

import java.io.File;

public class ODReporter extends AReporter {
  
  static final String SIMPLE_FILE_NAME = "18_ObjectDiagram";
  
  protected String modelName;
  
  protected ReportingRepository reporting;
  
  public ODReporter(String outputDir, String modelName, ReportingRepository reporting) {
    super(outputDir + File.separator
        + modelName,
        Names.getSimpleName(modelName) + "_AST", ReportingConstants.OD_FILE_EXTENSION);
    this.modelName = modelName;
    this.reporting = reporting;
  }
  
  @Override
  protected void writeHeader() {
    writeLine("/*");
    writeLine(" * ========================================================== Object diagram");
    writeLine(" */");
  }
  
  protected void writeFooter() {
    writeLine("/*");
    writeLine(" * ========================================================== Explanation");
    writeLine(" * Shows the AST with all attributes as object diagram");
    writeLine(" */");
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeContent(ast);
    writeFooter();
    super.flush(ast);
  }

  /**
   * @param ast
   */
  protected void writeContent(ASTNode ast) {
    if (ast instanceof ASTGrammarNode) {
      ASTGrammarNode mcNode = (ASTGrammarNode) ast;
      IndentPrinter pp = new IndentPrinter();
      GrammarWithConcepts2OD odPrinter = new GrammarWithConcepts2OD(pp, reporting);
      odPrinter.printObjectDiagram(Names.getSimpleName(modelName) + "_AST", mcNode);
      writeLine(pp.getContent());
    }
  }
  
}
