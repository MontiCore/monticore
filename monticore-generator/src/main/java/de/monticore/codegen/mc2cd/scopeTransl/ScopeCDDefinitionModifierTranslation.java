/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class ScopeCDDefinitionModifierTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTMCGrammar, ASTCDDefinition> link : rootLink.getLinks(ASTMCGrammar.class,
        ASTCDDefinition.class)) {
      link.target().setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    return rootLink;

  }
}
