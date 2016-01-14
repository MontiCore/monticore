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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.UnaryOperator;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTProd;
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
      for (Entry<String, List<ASTNonTerminal>> entry : getInheritedNonTerminals(link.source())
          .entrySet()) {
        String ruleName = entry.getKey();
        MCRuleSymbol ruleSymbol = MCGrammarSymbolTableHelper.resolveRule(rootLink.source(),
            ruleName).get();
        String superGrammarName = ruleSymbol.getGrammarSymbol().getFullName();
        for (ASTNonTerminal nonTerminal : entry.getValue()) {
          ASTCDAttribute cdAttribute = createStereoTypedCDAttribute(
              MC2CDStereotypes.INHERITED.toString(), superGrammarName);
          link.target().getCDAttributes().add(cdAttribute);
          new Link<>(nonTerminal, cdAttribute, link);
        }
      }
    }
      
    return rootLink;
  }
  
  private ASTCDAttribute createStereoTypedCDAttribute(String stereotypeName,
      String stereotypeValue) {
    ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    TransformationHelper.addStereoType(cdAttribute, stereotypeName, stereotypeValue);
    return cdAttribute;
  }
  
  private Map<String, List<ASTNonTerminal>> getInheritedNonTerminals(ASTProd sourceNode) {
    Map<String, List<ASTNonTerminal>> inheritedNonTerminals = new HashMap<>();
    for (ASTProd superRule : getAllSuperRules(sourceNode)) {
      inheritedNonTerminals.put(superRule.getName(),
          ASTNodes.getSuccessors(superRule, ASTNonTerminal.class));
    }
    return inheritedNonTerminals;
  }
  
  private List<ASTProd> getAllSuperRules(ASTNode astNode) {
    List<ASTProd> directSuperRules = getDirectSuperRules(astNode);
    List<ASTProd> allSuperRules = new ArrayList<>();
    for (ASTProd superRule : directSuperRules) {
      allSuperRules.addAll(getAllSuperRules(superRule));
    }
    allSuperRules.addAll(directSuperRules);
    return allSuperRules;
  }
  
  private List<ASTProd> getDirectSuperRules(ASTNode astNode) {
    if (astNode instanceof ASTClassProd) {
      return resolveRuleReferences(((ASTClassProd) astNode).getSuperRule(), astNode);
    }
    return Collections.emptyList(); 
  }
  
  private List<ASTProd> resolveRuleReferences(List<ASTRuleReference> ruleReferences,
      ASTNode nodeWithSymbol) {
    List<ASTProd> superRuleNodes = new ArrayList<>();
    for (ASTRuleReference superRule : ruleReferences) {
      Optional<MCRuleSymbol> symbol = MCGrammarSymbolTableHelper.resolveRule(nodeWithSymbol,
          superRule.getName());
      if (symbol.isPresent() && symbol.get().getAstNode().isPresent()) {
        superRuleNodes.add((ASTProd) symbol.get().getAstNode().get());
      }
    }
    return superRuleNodes;
  }
}
