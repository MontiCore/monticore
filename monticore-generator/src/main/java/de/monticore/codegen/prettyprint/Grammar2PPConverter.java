// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;

public class Grammar2PPConverter {
  public ASTCDCompilationUnit doConvert(ASTMCGrammar grammar, GlobalExtensionManagement glex) {
    return new MC2PPTranslation(glex).decorate(grammar);
  }
}
