package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class CreateSymbolProds implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTMCGrammar, ASTCDDefinition> link : rootLink.getLinks(ASTMCGrammar.class,
        ASTCDDefinition.class)) {

      createClassProdToCDClassLinks(link);
      createAbstractClassProdToCDAbstractClassLinks(link);
      createInterfaceProdToCDInterfaceLinks(link);
      createExternalProdToCDExternalLinks(link);
    }
    return rootLink;
  }

  private void createClassProdToCDClassLinks(Link<ASTMCGrammar, ASTCDDefinition> link) {
    for (ASTClassProd classProd : link.source().getClassProdList()) {
      if (isSymbolDefinition(classProd)) {
        createCDClass(classProd, link);
      }
    }
  }

  private void createAbstractClassProdToCDAbstractClassLinks(Link<ASTMCGrammar, ASTCDDefinition> link) {
    for (ASTAbstractProd classProd : link.source().getAbstractProdList()) {
      if (isSymbolDefinition(classProd)) {
        createCDClass(classProd, link);
      }
    }
  }

  private void createCDClass(ASTProd astProd, Link<ASTMCGrammar, ASTCDDefinition> link) {
    ASTCDClass cdClass = CD4AnalysisNodeFactory.createASTCDClass();
    cdClass.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    cdClass.setName(astProd.getName());
    link.target().getCDClassList().add(cdClass);
    new Link<>(astProd, cdClass, link);
  }

  private void createInterfaceProdToCDInterfaceLinks(Link<ASTMCGrammar, ASTCDDefinition> link) {
    for (ASTInterfaceProd interfaceProd : link.source().getInterfaceProdList()) {
      if (isSymbolDefinition(interfaceProd)) {
        createCDInterface(interfaceProd, link);
      }
    }
  }

  private void createExternalProdToCDExternalLinks(Link<ASTMCGrammar, ASTCDDefinition> link) {
    for (ASTExternalProd externalProd : link.source().getExternalProdList()) {
      if (isSymbolDefinition(externalProd)) {
        createCDInterface(externalProd, link);
      }
    }
  }


  private void createCDInterface(ASTProd astProd, Link<ASTMCGrammar, ASTCDDefinition> link) {
    ASTCDInterface cdInterface = CD4AnalysisNodeFactory.createASTCDInterface();
    cdInterface.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    cdInterface.setName(astProd.getName());
    link.target().getCDInterfaceList().add(cdInterface);
    new Link<>(astProd, cdInterface, link);
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