/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
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
      link.source().getClassProdList().forEach(c -> createCDClass(c, link));
      link.source().getAbstractProdList().forEach(c -> createCDClass(c, link));
      link.source().getInterfaceProdList().forEach(c -> createCDClass(c, link));
      link.source().getExternalProdList().forEach(c -> createCDClass(c, link));
    }
    return rootLink;
  }

  private void createCDClass(ASTProd astProd, Link<ASTMCGrammar, ASTCDDefinition> link) {
    if (isSymbolDefinition(astProd)) {
      ASTCDClass cdClass = CD4AnalysisMill.cDClassBuilder().
              setModifier(CD4AnalysisMill.modifierBuilder().build()).
              setName(astProd.getName()).uncheckedBuild();
      link.target().addCDElement(cdClass);
      new Link<>(astProd, cdClass, link);
    }
  }

  private boolean isSymbolDefinition(ASTProd grammarProd) {
    for (ASTSymbolDefinition symbolDefinition : grammarProd.getSymbolDefinitionList()) {
      if (symbolDefinition.isGenSymbol()) {
        return true;
      }
    }
    return false;
  }

}
