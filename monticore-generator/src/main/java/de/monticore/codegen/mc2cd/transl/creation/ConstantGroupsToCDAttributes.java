/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.utils.Link;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class ConstantGroupsToCDAttributes implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      createAttributeFromConstantGroup(link);
    }

    for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(ASTInterfaceProd.class,
        ASTCDInterface.class)) {
      createAttributeFromConstantGroup(link);
    }

    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(ASTAbstractProd.class,
        ASTCDClass.class)) {
      createAttributeFromConstantGroup(link);
    }
    return rootLink;
  }

  protected void createAttributeFromConstantGroup(Link<? extends ASTProd, ? extends ASTCDType> link) {
    Optional<ProdSymbol> typeProd = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(link.source().getEnclosingScope()).get()
        .getSpannedScope()
        .resolveProd(link.source().getName());
    if (!typeProd.isPresent()) {
      Log.debug("Unknown type of the grammar rule "
          + link.source().getName() + " in the grammar "
          + MCGrammarSymbolTableHelper.getMCGrammarSymbol(link.source().getEnclosingScope()).get()
          .getFullName()
          + "\n Check if this a kind of rule A:B=... ", ConstantGroupsToCDAttributes.class.getName());
      return;
    }

    ProdSymbol prodSymbol = typeProd.get();
    for (RuleComponentSymbol prodComponent : prodSymbol.getProdComponents()) {
      if (prodComponent.isIsConstantGroup() && prodComponent.isPresentAstNode()
          && prodComponent.getAstNode() instanceof ASTConstantGroup) {
        ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().setModifier(CD4CodeMill.modifierBuilder().build()).uncheckedBuild();
        link.target().addCDMember(cdAttribute);
        ASTConstantGroup astConstantGroup = (ASTConstantGroup) prodComponent.getAstNode();
        new Link<>(astConstantGroup, cdAttribute, link);
      }
    }
  }
}

