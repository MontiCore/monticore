/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class CreateScopeProd implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTMCGrammar, ASTCDDefinition> link : rootLink.getLinks(ASTMCGrammar.class,
        ASTCDDefinition.class)) {
      createScopeClass(link);
    }
    return rootLink;
  }


  private void createScopeClass(Link<ASTMCGrammar, ASTCDDefinition> link) {
    ASTCDClass cdClass = CD4AnalysisNodeFactory.createASTCDClass();
    cdClass.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    cdClass.setName(link.source().getName());
    link.target().getCDClassList().add(cdClass);
    new Link<>(link.source(), cdClass, link);
  }
}
