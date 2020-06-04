/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.transl.InheritedAttributesTranslation;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._symboltable.AdditionalAttributeSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.utils.Link;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InheritedSymbolAttributesTranslation extends InheritedAttributesTranslation {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTProd, ASTCDClass> link : rootLink.getLinks(ASTProd.class,
            ASTCDClass.class)) {
      //inherited
      handleInheritedAttributeInSymbolRules(link);
    }
    return rootLink;
  }


  /**
   * handleInherited method for symbolrules
   */
  private void handleInheritedAttributeInSymbolRules(Link<ASTProd, ASTCDClass> link) {
    ProdSymbol symbol = link.source().getSymbol();

    for (Entry<ASTProd, Collection<AdditionalAttributeSymbol>> entry : getInheritedAttributeInSymbols(link.source()).entrySet()) {
      for (AdditionalAttributeSymbol attributeInAST : entry.getValue()) {
        ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder()
                .setName(attributeInAST.getName())
                .setMCType(MCTypeFacade.getInstance().createQualifiedType(attributeInAST.getType()))
                .build();
        Optional<String> superGrammarName = MCGrammarSymbolTableHelper.getMCGrammarSymbol(entry.getKey().getEnclosingScope())
                .map(MCGrammarSymbol::getFullName);
        if (superGrammarName.isPresent()) {
          TransformationHelper.addStereoType(cdAttribute, MC2CDStereotypes.INHERITED.toString(), superGrammarName.get());
        }
        link.target().getCDAttributeList().add(cdAttribute);
        if (attributeInAST.isPresentAstNode()) {
          new Link<>(attributeInAST.getAstNode(), cdAttribute, link);
        }
      }
    }
  }

  /**
   * all attributes from a symbolrule for a Prod
   */
  protected Map<ASTProd, Collection<AdditionalAttributeSymbol>> getInheritedAttributeInSymbols(
          ASTProd astNode) {
    return TransformationHelper.getAllSuperProds(astNode).stream()
            .distinct()
            .collect(Collectors.toMap(Function.identity(), prod -> prod.isPresentSymbol() ?
                    prod.getSymbol().getSpannedScope().getSymbolAttributeList() : Collections.emptyList()));
  }

}
