/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdAttributeSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.Link;

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
    for (Entry<ASTProd, List<ASTNonTerminal>> entry : GeneratorHelper.getInheritedNonTerminals(link.source())
        .entrySet()) {
      for (ASTNonTerminal nonTerminal : entry.getValue()) {
        ASTCDAttribute cdAttribute = createCDAttribute(link.source(), entry.getKey());
        link.target().getCDAttributeList().add(cdAttribute);
        new Link<>(nonTerminal, cdAttribute, link);
      }
    }
  }
  
  private void handleInheritedAttributeInASTs(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, Collection<MCProdAttributeSymbol>> entry : getInheritedAttributeInASTs(
        link.source()).entrySet()) {
      for (MCProdAttributeSymbol attributeInAST : entry.getValue()) {
        ASTCDAttribute cdAttribute = createCDAttribute(link.source(), entry.getKey());
        link.target().getCDAttributeList().add(cdAttribute);
        if (attributeInAST.getAstNode().isPresent()) {
          new Link<>(attributeInAST.getAstNode().get(), cdAttribute, link);
        }
      }
    }
  }

  private ASTCDAttribute createCDAttribute(ASTNode inheritingNode, ASTNode definingNode) {
    List<ASTInterfaceProd> interfacesWithoutImplementation = getAllInterfacesWithoutImplementation(
        inheritingNode);
    
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

  
  private Map<ASTProd, Collection<MCProdAttributeSymbol>> getInheritedAttributeInASTs(
      ASTNode astNode) {
    return GeneratorHelper.getAllSuperProds(astNode).stream()
        .distinct()
        .collect(Collectors.toMap(Function.identity(), astProd -> astProd.getSymbol()
            .flatMap(this::getTypeSymbol)
            .map(MCProdSymbol::getProdAttributes)
            .orElse(Collections.emptyList())));
  }
  
  private Optional<MCProdSymbol> getTypeSymbol(Symbol symbol) {
    if (symbol instanceof MCProdSymbol) {
      return Optional.of(((MCProdSymbol) symbol));
    }
    return Optional.empty();
  }

  /**
  * @return a list of interfaces that aren't already implemented by another
   * class higher up in the type hierarchy. (the list includes interfaces
   * extended transitively by other interfaces)
   */
  private List<ASTInterfaceProd> getAllInterfacesWithoutImplementation(ASTNode astNode) {
    List<ASTInterfaceProd> directInterfaces = GeneratorHelper.getDirectSuperProds(astNode).stream()
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

  
}
