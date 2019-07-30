/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class PrettyPrintingTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    String buffer = GeneratorHelper.getCDPrettyPrinter().prettyprint(rootLink.target());
    System.err.println(buffer);
    
    return rootLink;
  }
}
