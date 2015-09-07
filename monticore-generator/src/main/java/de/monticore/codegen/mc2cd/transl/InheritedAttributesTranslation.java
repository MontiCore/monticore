/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.codegen.mc2cd.transl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

public class InheritedAttributesTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      for (ASTNonTerminal nonTerminal : getInheritedNonTerminals(link.source())) {
        MCRuleSymbol ruleSymbol = MCGrammarSymbolTableHelper.resolveRule(rootLink.source(),
            nonTerminal.getName()).get();
        String superGrammarName = ruleSymbol.getGrammarSymbol().getName();
        ASTCDAttribute cdAttribute = createStereoTypedCDAttribute("inherited", superGrammarName);
        link.target().getCDAttributes().add(cdAttribute);
        new Link<>(nonTerminal, cdAttribute, link);
      }
    }
    
    return rootLink;
  }
  
  private ASTCDAttribute createStereoTypedCDAttribute(String stereotypeName, String stereotypeValue) {
    ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    TransformationHelper.addStereoType(cdAttribute, stereotypeName, stereotypeValue);
    return cdAttribute;
  }
  
  private List<ASTNonTerminal> getInheritedNonTerminals(ASTNode sourceNode) {
    List<ASTNonTerminal> inheritedNonTerminals = new ArrayList<>();
    for (ASTNode superRule : getAllSuperRules(sourceNode)) {
      inheritedNonTerminals.addAll(ASTNodes.getSuccessors(superRule, ASTNonTerminal.class));
    }
    return inheritedNonTerminals;
  }
  
  private List<ASTNode> getAllSuperRules(ASTNode astNode) {
    List<ASTNode> directSuperRules = getDirectSuperRules(astNode);
    List<ASTNode> allSuperRules = new ArrayList<>();
    for (ASTNode superRule : directSuperRules) {
      allSuperRules.addAll(getAllSuperRules(superRule));
    }
    allSuperRules.addAll(directSuperRules);
    return allSuperRules;
  }
  
  private List<ASTNode> getDirectSuperRules(ASTNode astNode) {
    if (astNode instanceof ASTClassProd) {
      return resolveRuleReferences(((ASTClassProd) astNode).getSuperRule(), astNode);
    }
    return Collections.emptyList();
  }
  
  private List<ASTNode> resolveRuleReferences(List<ASTRuleReference> ruleReferences, ASTNode nodeWithSymbol) {
    List<ASTNode> superRuleNodes = new ArrayList<>();
    for (ASTRuleReference superRule : ruleReferences) {
      Optional<MCRuleSymbol> symbol = MCGrammarSymbolTableHelper.resolveRule(nodeWithSymbol,
          superRule.getName());
      if (symbol.isPresent() && symbol.get().getAstNode().isPresent()) {
        superRuleNodes.add(symbol.get().getAstNode().get());
      }
    }
    return superRuleNodes;
  }
}
