/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import java.util.function.UnaryOperator;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

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
