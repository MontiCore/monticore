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

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.languages.grammar.*;
import de.monticore.symboltable.Symbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class InheritedAttributesTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      handleInheritedNonTerminals(link);
      handleInheritedAttributeInASTs(link);
    }
    return rootLink;
  }

  private void handleInheritedNonTerminals(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, List<ASTNonTerminal>> entry : getInheritedNonTerminals(link.source())
        .entrySet()) {
      for (ASTNonTerminal nonTerminal : entry.getValue()) {
        ASTCDAttribute cdAttribute = createCDAttribute(link.source(), entry.getKey());
        link.target().getCDAttributes().add(cdAttribute);
        new Link<>(nonTerminal, cdAttribute, link);
      }
    }
  }

  private void handleInheritedAttributeInASTs(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, List<ASTAttributeInAST>> entry :
        getInheritedAttributeInASTs(link.source()).entrySet()) {
      for (ASTAttributeInAST attributeInAST : entry.getValue()) {
        ASTCDAttribute cdAttribute = createCDAttribute(link.source(), entry.getKey());
        link.target().getCDAttributes().add(cdAttribute);
        new Link<>(attributeInAST, cdAttribute, link);
      }
    }
  }

  private ASTCDAttribute createCDAttribute(ASTNode inheritingNode, ASTNode definingNode) {
    List<ASTInterfaceProd> interfacesWithoutImplementation =
        getAllInterfacesWithoutImplementation(inheritingNode);

    String superGrammarName = MCGrammarSymbolTableHelper.getMCGrammarSymbol(definingNode)
        .map(MCGrammarSymbol::getFullName)
        .orElse("");

    ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    if (!interfacesWithoutImplementation.contains(definingNode)) {
      TransformationHelper.addStereoType(
          cdAttribute, MC2CDStereotypes.INHERITED.toString(), superGrammarName);
    }
    return cdAttribute;
  }

  private Map<ASTProd, List<ASTNonTerminal>> getInheritedNonTerminals(ASTProd sourceNode) {
    return getAllSuperProds(sourceNode).stream()
        .distinct()
        .collect(Collectors.toMap(Function.identity(), astProd ->
            ASTNodes.getSuccessors(astProd, ASTNonTerminal.class)));
  }

  private Map<ASTProd, List<ASTAttributeInAST>> getInheritedAttributeInASTs(ASTNode astNode) {
    return getAllSuperProds(astNode).stream()
        .distinct()
        .collect(Collectors.toMap(Function.identity(), astProd ->
            astProd.getSymbol()
                .flatMap(this::getTypeSymbol)
                .map(MCTypeSymbol::getAttributeInASTs)
                .orElse(Collections.emptyList())));
  }

  private Optional<MCTypeSymbol> getTypeSymbol(Symbol symbol) {
    if (symbol instanceof MCClassRuleSymbol) {
      return Optional.of(((MCClassRuleSymbol) symbol).getType());
    }
    else if (symbol instanceof MCInterfaceOrAbstractRuleSymbol) {
      return Optional.of(((MCInterfaceOrAbstractRuleSymbol) symbol).getType());
    }
    return Optional.empty();
  }

  /**
   * @return the super productions defined in all super grammars (including transitive super grammars)
   */
  private List<ASTProd> getAllSuperProds(ASTNode astNode) {
    List<ASTProd> directSuperRules = getDirectSuperProds(astNode);
    List<ASTProd> allSuperRules = new ArrayList<>();
    for (ASTProd superRule : directSuperRules) {
      allSuperRules.addAll(getAllSuperProds(superRule));
    }
    allSuperRules.addAll(directSuperRules);
    return allSuperRules;
  }

  /**
   * @return a list of interfaces that aren't already implemented by another class higher up in the
   * type hierarchy. (the list includes interfaces extended transitively by other interfaces)
   */
  private List<ASTInterfaceProd> getAllInterfacesWithoutImplementation(ASTNode astNode) {
    List<ASTInterfaceProd> directInterfaces = getDirectSuperProds(astNode).stream()
        .filter(ASTInterfaceProd.class::isInstance)
        .map(ASTInterfaceProd.class::cast)
        .collect(Collectors.toList());
    List<ASTInterfaceProd> allSuperRules = new ArrayList<>();
    for (ASTInterfaceProd superInterface : directInterfaces) {
      allSuperRules.addAll(getAllInterfacesWithoutImplementation(superInterface));
    }
    allSuperRules.addAll(directInterfaces);
    return allSuperRules;
  }

  /**
   * @return the super productions defined in direct super grammars
   */
  private List<ASTProd> getDirectSuperProds(ASTNode astNode) {
    if (astNode instanceof ASTClassProd) {
      List<ASTProd> directSuperProds = resolveRuleReferences(
          ((ASTClassProd) astNode).getSuperRule(), astNode);
      directSuperProds.addAll(
          resolveRuleReferences(((ASTClassProd) astNode).getSuperInterfaceRule(), astNode));
      return directSuperProds;
    }
    else if (astNode instanceof ASTInterfaceProd) {
      return resolveRuleReferences(((ASTInterfaceProd) astNode).getSuperInterfaceRule(), astNode);
    }
    return Collections.emptyList();
  }

  /**
   * @return the production definitions of B & C in "A extends B, C"
   */
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
