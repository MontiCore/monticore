/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.AdditionalAttributeSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
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
      //inherited
      handleInheritedNonTerminals(link);
      handleInheritedConstantGroup(link);
      handleInheritedTerminals(link);
      handleInheritedKeyTerminals(link);
      handleInheritedAttributeInASTs(link);
      //overwritten
      Optional<ASTProd> overwrittenProdIfNoNewRightSide = getOverwrittenProdIfNoNewRightSide(link.source());
      overwrittenProdIfNoNewRightSide.ifPresent(astProd -> handleOverwrittenRuleComponents(link, astProd));
    }
    return rootLink;
  }

  /**
   * handleInherited method for each RuleComponent type
   */
  private void handleInheritedNonTerminals(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, List<ASTNonTerminal>> entry : getInheritedNonTerminal(link.source())
        .entrySet()) {
      handleInheritedRuleComponents(link, entry.getKey(), entry.getValue());
    }
  }

  private void handleInheritedConstantGroup(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, List<ASTConstantGroup>> entry : getInheritedConstantGroup(link.source())
        .entrySet()) {
      handleInheritedRuleComponents(link, entry.getKey(), entry.getValue());
    }
  }

  private void handleInheritedTerminals(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, List<ASTTerminal>> entry : getInheritedTerminal(link.source())
        .entrySet()) {
      // only attributes for terminals with a usage name
      List<ASTTerminal> terminalWithUsageName = entry.getValue()
          .stream()
          .filter(ASTTerminal::isPresentUsageName)
          .collect(Collectors.toList());
      handleInheritedRuleComponents(link, entry.getKey(), terminalWithUsageName);
    }
  }

  private void handleInheritedKeyTerminals(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, List<ASTKeyTerminal>> entry : getInheritedKeyTerminal(link.source())
        .entrySet()) {
      // only attributes for keyTerminals with a usage name
      List<ASTKeyTerminal> keyTerminalWithUsageName = entry.getValue()
          .stream()
          .filter(ASTKeyTerminal::isPresentUsageName)
          .collect(Collectors.toList());
      handleInheritedRuleComponents(link, entry.getKey(), keyTerminalWithUsageName);
    }
  }

  private void handleInheritedRuleComponents(Link<ASTClassProd, ASTCDClass> link, ASTProd astProd,
                                             List<? extends ASTRuleComponent> ruleComponents) {
    for (ASTRuleComponent ruleComponent : ruleComponents) {
      ASTCDAttribute cdAttribute = createCDAttribute(link.source(), astProd);
      link.target().getCDAttributeList().add(cdAttribute);
      new Link<>(ruleComponent, cdAttribute, link);
    }
  }

  /**
   * handleInherited method for astrules
   */
  private void handleInheritedAttributeInASTs(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, Collection<AdditionalAttributeSymbol>> entry : getInheritedAttributeInASTs(
        link.source()).entrySet()) {
      for (AdditionalAttributeSymbol attributeInAST : entry.getValue()) {
        ASTCDAttribute cdAttribute = createCDAttribute(link.source(), entry.getKey());
        link.target().getCDAttributeList().add(cdAttribute);
        if (attributeInAST.isPresentAstNode()) {
          new Link<>(attributeInAST.getAstNode(), cdAttribute, link);
        }
      }
    }
  }

  /**
   * Methods to get special RuleComponent Types for a Prod
   */

  private Map<ASTProd, List<ASTNonTerminal>> getInheritedNonTerminal(ASTProd sourceNode) {
    return TransformationHelper.getAllSuperProds(sourceNode).stream()
        .distinct()
        .collect(Collectors.toMap(Function.identity(),
            astProd -> ASTNodes.getSuccessors(astProd, ASTNonTerminal.class)));
  }

  private Map<ASTProd, List<ASTConstantGroup>> getInheritedConstantGroup(ASTProd sourceNode) {
    return TransformationHelper.getAllSuperProds(sourceNode).stream()
        .distinct()
        .collect(Collectors.toMap(Function.identity(),
            astProd -> ASTNodes.getSuccessors(astProd, ASTConstantGroup.class)));
  }

  private Map<ASTProd, List<ASTTerminal>> getInheritedTerminal(ASTProd sourceNode) {
    return TransformationHelper.getAllSuperProds(sourceNode).stream()
        .distinct()
        .collect(Collectors.toMap(Function.identity(),
            astProd -> ASTNodes.getSuccessors(astProd, ASTTerminal.class)));
  }

  private Map<ASTProd, List<ASTKeyTerminal>> getInheritedKeyTerminal(ASTProd sourceNode) {
    return TransformationHelper.getAllSuperProds(sourceNode).stream()
        .distinct()
        .collect(Collectors.toMap(Function.identity(),
            astProd -> ASTNodes.getSuccessors(astProd, ASTKeyTerminal.class)));
  }


  /**
   * all attributes from a astrule for a Prod
   */
  private Map<ASTProd, Collection<AdditionalAttributeSymbol>> getInheritedAttributeInASTs(
      ASTProd astNode) {
    return TransformationHelper.getAllSuperProds(astNode).stream()
        .distinct()
        .collect(Collectors.toMap(Function.identity(), prod -> prod.isPresentSymbol() ?
            prod.getSymbol().getSpannedScope().getLocalAdditionalAttributeSymbols() : Collections.emptyList()));
  }

  /**
   * create Attribute with a inherited flag
   */
  private ASTCDAttribute createCDAttribute(ASTProd inheritingNode, ASTProd definingNode) {
    List<ASTInterfaceProd> interfacesWithoutImplementation = getAllInterfacesWithoutImplementation(
        inheritingNode);

    String superGrammarName = MCGrammarSymbolTableHelper.getMCGrammarSymbol(definingNode.getEnclosingScope())
        .map(MCGrammarSymbol::getFullName)
        .orElse("");

    ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    if (!interfacesWithoutImplementation.contains(definingNode)) {
      TransformationHelper.addStereoType(
          cdAttribute, MC2CDStereotypes.INHERITED.toString(), superGrammarName);
    }
    return cdAttribute;
  }

  /**
   * handleOverwritten method for each RuleComponent type
   */
  private void handleOverwrittenRuleComponents(Link<ASTClassProd, ASTCDClass> link, ASTProd superProd) {
    handleInheritedRuleComponents(link, superProd, ASTNodes.getSuccessors(superProd, ASTNonTerminal.class));
    handleInheritedRuleComponents(link, superProd, ASTNodes.getSuccessors(superProd, ASTConstantGroup.class));
    List<ASTTerminal> overwrittenTerminals = ASTNodes.getSuccessors(superProd, ASTTerminal.class)
        .stream()
        .filter(ASTTerminal::isPresentUsageName)
        .collect(Collectors.toList());
    handleInheritedRuleComponents(link, superProd, overwrittenTerminals);
    List<ASTTerminal> overwrittenKeyTerminals = ASTNodes.getSuccessors(superProd, ASTTerminal.class)
        .stream()
        .filter(ASTTerminal::isPresentUsageName)
        .collect(Collectors.toList());
    handleInheritedRuleComponents(link, superProd, overwrittenKeyTerminals);
  }

  /**
   * checks if the Prod is overwriting a Prod of the super grammar with the same name
   * and does not change the implementation by not adding a right hand side
   * e.g. Prod Foo
   * grammar A { Foo = Name;}
   * grammar B extends A {interface Bar; Foo implements Bar;}
   */
  private Optional<ASTProd> getOverwrittenProdIfNoNewRightSide(ASTClassProd astProd) {
    Optional<ProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper
        .resolveRuleInSupersOnly(
            astProd,
            astProd.getName());
    if (ruleSymbol.isPresent() && !ruleSymbol.get().isIsExternal()
        && ruleSymbol.get().isPresentAstNode()
        && ASTNodes.getSuccessors(astProd, ASTRuleComponent.class).isEmpty()) {
      return Optional.of(ruleSymbol.get().getAstNode());
    }
    return Optional.empty();
  }

  /**
   * @return a list of interfaces that aren't already implemented by another
   * class higher up in the type hierarchy. (the list includes interfaces
   * extended transitively by other interfaces)
   */
  private List<ASTInterfaceProd> getAllInterfacesWithoutImplementation(ASTProd astNode) {
    List<ASTInterfaceProd> directInterfaces = TransformationHelper.getDirectSuperProds(astNode).stream()
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
