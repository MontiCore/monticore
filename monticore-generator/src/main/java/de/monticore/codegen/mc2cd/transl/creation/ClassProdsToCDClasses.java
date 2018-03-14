/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import java.util.function.UnaryOperator;

import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.Link;

public class ClassProdsToCDClasses implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTMCGrammar, ASTCDDefinition> link : rootLink.getLinks(ASTMCGrammar.class,
        ASTCDDefinition.class)) {
      createClassProdToCDClassLinks(link);
    }
    return rootLink;
  }
  
  private void createClassProdToCDClassLinks(Link<ASTMCGrammar, ASTCDDefinition> link) {
    for (ASTClassProd classProd : link.source().getClassProdList()) {
      ASTCDClass cdClass = CD4AnalysisNodeFactory.createASTCDClass();
      cdClass.setModifier(CD4AnalysisNodeFactory.createASTModifier());
      link.target().getCDClassList().add(cdClass);
      new Link<>(classProd, cdClass, link);
    }
  }
}
