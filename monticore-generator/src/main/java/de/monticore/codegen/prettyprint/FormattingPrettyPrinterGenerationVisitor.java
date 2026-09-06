// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.Map;

/**
 * Generate a formatting pretty printer, emitting token-likes to a stream.
 */
public class FormattingPrettyPrinterGenerationVisitor extends PrettyPrinterGenerationVisitor {
  public FormattingPrettyPrinterGenerationVisitor(GlobalExtensionManagement glex, ASTCDClass ppClass, Map<String, NonTermAccessorVisitorHandler.ClassProdNonTermPrettyPrintData> classProds) {
    super(glex, ppClass, classProds);
  }

  @Override
  protected String getHandleMethodTemplate() {
    // Formatting Pretty Printers use a different entry-methdo handle
    return "_prettyprinter.pp.FormattingHandleMethod";
  }

  // Yes, the PrettyPrinterGenerationVisitor runs twice now
  // In the future, only one of both should be used
}
