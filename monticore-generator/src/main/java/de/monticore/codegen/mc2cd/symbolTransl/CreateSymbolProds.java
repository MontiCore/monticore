/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._ast.ASTSymbolDefinition;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class CreateSymbolProds implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTMCGrammar, ASTCDDefinition> link : rootLink.getLinks(ASTMCGrammar.class,
        ASTCDDefinition.class)) {
      link.source().getClassProdsList().forEach(c -> createCDClass(c, link));
      link.source().getAbstractProdsList().forEach(c -> createCDClass(c, link));
      link.source().getInterfaceProdsList().forEach(c -> createCDClass(c, link));
      link.source().getExternalProdsList().forEach(c -> createCDClass(c, link));
    }
    return rootLink;
  }

  private void createCDClass(ASTProd astProd, Link<ASTMCGrammar, ASTCDDefinition> link) {
    if (isSymbolDefinition(astProd)) {
      ASTCDClass cdClass = CD4AnalysisNodeFactory.createASTCDClass();
      cdClass.setModifier(CD4AnalysisNodeFactory.createASTModifier());
      cdClass.setName(astProd.getName());
      link.target().getCDClasssList().add(cdClass);
      new Link<>(astProd, cdClass, link);
    }
  }

  private boolean isSymbolDefinition(ASTProd grammarProd) {
    for (ASTSymbolDefinition symbolDefinition : grammarProd.getSymbolDefinitionsList()) {
      if (symbolDefinition.isGenSymbol()) {
        return true;
      }
    }
    return false;
  }

}
