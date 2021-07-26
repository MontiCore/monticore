/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar._symboltable;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import de.monticore.grammar.Multiplicity;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;
import static de.monticore.grammar.Multiplicity.*;
import static de.se_rwth.commons.logging.Log.error;
import static java.util.Collections.emptyList;
import static java.util.Collections.max;


public class GrammarSTCompleteTypes implements GrammarVisitor2 {

  protected MCGrammarSymbol grammarSymbol;
  protected ASTMCGrammar astGrammar;

  @Override
  public void visit(ASTSplitRule ast) {
    grammarSymbol.addAllSplitRules(ast.getStringList());
  }

  @Override
  public void visit(ASTMCGrammar ast){
    this.astGrammar = ast;
    this.grammarSymbol = ast.getSymbol();
  }

  @Override
  public void endVisit(ASTMCGrammar ast) {
    setComponentsCardinality(ast);

    computeStartParserProd(ast);
  }

  @Override
  public void visit(ASTKeywordRule ast) {
    grammarSymbol.addAllNoKeywords(ast.getStringList());
  }

  @Override
  public void visit(ASTTokenConstant node) {
    grammarSymbol.splitRules.add(node.getString());
  }

  @Override
  public void visit(ASTKeyConstant node) {
    grammarSymbol.noKeywords.addAll(node.getStringList());
  }

  @Override
  public void visit(ASTTokenMode node) {
    node.streamTokenName().forEach(t -> grammarSymbol.addMode(node.getName(), t));
  }

  @Override
  public void visit(ASTLexProd node) {
    GrammarVisitor2.super.visit(node);
    if (node.isEmptyMode()) {
      grammarSymbol.addMode(MCGrammarSymbol.DEFAULT_MODE, node.getName());
    } else {
      node.streamMode().forEach(m -> grammarSymbol.addMode(m, node.getName()));
    }
  }

  @Override
  public void endVisit(ASTTerminal node){
    if(node.isPresentUsageName()) {
      RuleComponentSymbol prodComponent = node.getSymbol();
      prodComponent.setIsTerminal(true);
      setComponentMultiplicity(prodComponent, node);
    }
  }

  @Override
  public void endVisit(ASTKeyTerminal node){
    if(node.isPresentUsageName()) {
      RuleComponentSymbol prodComponent = node.getSymbol();
      prodComponent.setIsTerminal(true);
      setComponentMultiplicity(prodComponent, node);
    }
  }

  @Override
  public void endVisit(ASTTokenTerminal node) {
    if (node.isPresentUsageName()) {
      RuleComponentSymbol prodComponent = node.getSymbol();
      prodComponent.setIsTerminal(true);
      setComponentMultiplicity(prodComponent, node);
    }
  }

  @Override
  public void endVisit(ASTNonTerminal node){
    RuleComponentSymbol prodComponent = node.getSymbol();
    prodComponent.setReferencedType(node.getName());
    prodComponent.setIsNonterminal(true);
  }

  @Override
  public void endVisit(ASTLexNonTerminal node){
    RuleComponentSymbol prodComponent = node.getSymbol();
    prodComponent.setReferencedType(node.getName());
    prodComponent.setIsLexerNonterminal(true);
  }

  @Override
  public void endVisit(ASTInterfaceProd node){
    ProdSymbol prodSymbol = node.getSymbol();
    prodSymbol.setIsInterface(true);

    setSymbolDefinition(prodSymbol, node.getSymbolDefinitionList());

    setSuperProdsAndTypes(prodSymbol, emptyList(),
        emptyList(), node.getSuperInterfaceRuleList(), node.getASTSuperInterfaceList());
  }

  @Override
  public void endVisit(ASTLexProd node){
    ProdSymbol prodSymbol = node.getSymbol();
    prodSymbol.setIsLexerProd(true);
  }

  @Override
  public void endVisit(ASTEnumProd node) {
    ProdSymbol prodSymbol = node.getSymbol();
    prodSymbol.setIsEnum(true);
    // TODO Behandlung der Constants fehlt noch
  }

  @Override
  public void endVisit(ASTClassProd ast) {
    ProdSymbol prodSymbol = ast.getSymbol();
    setSymbolDefinition(prodSymbol, ast.getSymbolDefinitionList());

    setSuperProdsAndTypes(prodSymbol, ast.getSuperRuleList(),
        ast.getASTSuperClassList(), ast.getSuperInterfaceRuleList(), ast.getASTSuperInterfaceList());
  }

  @Override
  public void endVisit(ASTAbstractProd ast) {
    ProdSymbol prodSymbol = ast.getSymbol();
    prodSymbol.setIsAbstract(true);

    setSymbolDefinition(prodSymbol, ast.getSymbolDefinitionList());

    setSuperProdsAndTypes(prodSymbol, ast.getSuperRuleList(),
        ast.getASTSuperClassList(), ast.getSuperInterfaceRuleList(), ast.getASTSuperInterfaceList());
  }

  @Override
  public void endVisit(ASTExternalProd ast) {
    ProdSymbol prodSymbol = ast.getSymbol();
    prodSymbol.setIsExternal(true);

    setSymbolDefinition(prodSymbol, ast.getSymbolDefinitionList());
  }

  @Override
  public void endVisit(ASTConstantGroup node){
    RuleComponentSymbol symbol = node.getSymbol();
    symbol.setIsConstantGroup(true);
    for (ASTConstant c : node.getConstantList()) {
      if (c.isPresentUsageName()) {
        symbol.addSubProds(c.getUsageName());
      } else if (c.isPresentKeyConstant()) {
        symbol.addSubProds(c.getKeyConstant().getString(0));
      } else {
        symbol.addSubProds(c.getName());
      }
    }
  }

  protected void setComponentMultiplicity(RuleComponentSymbol prod, ASTRuleComponent ast) {
    Multiplicity multiplicity = determineMultiplicity(astGrammar, ast);
    if (multiplicity == LIST) {
      prod.setIsList(true);
    } else if (multiplicity == OPTIONAL) {
      prod.setIsOptional(true);
    }
  }

  protected void setSymbolDefinition(ProdSymbol prodSymbol,
                                   List<ASTSymbolDefinition> listOfDefs) {
    for (ASTSymbolDefinition symbolDefinition : listOfDefs) {
      if (symbolDefinition.isGenSymbol()) {
        prodSymbol.setIsSymbolDefinition(true);
      }
      if (symbolDefinition.isGenScope()) {
        prodSymbol.setIsScopeSpanning(true);
      }
    }
  }

  protected void setSuperProdsAndTypes(ProdSymbol prodSymbol, List<ASTRuleReference> superProds,
                                     List<ASTMCType> astSuperClasses, List<ASTRuleReference> superInterfaceProds,
                                     List<ASTMCType> astSuperInterfaces) {
    final IGrammarScope enclosingScope = grammarSymbol.getSpannedScope();

    // A extends B
    for (ASTRuleReference astSuperProd : superProds) {
      ProdSymbolSurrogate superProd = new ProdSymbolSurrogate(astSuperProd.getTypeName());
      superProd.setEnclosingScope(enclosingScope);
      prodSymbol.addSuperProd(superProd);
    }

    // A astextends B
    for (ASTMCType astSuperClass : astSuperClasses) {
      ProdSymbolSurrogate superClass = new ProdSymbolSurrogate(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(astSuperClass));
      superClass.setEnclosingScope(enclosingScope);
      prodSymbol.addAstSuperClass(superClass);
    }

    // A implements B
    for (ASTRuleReference astInterface : superInterfaceProds) {
      ProdSymbolSurrogate superProd = new ProdSymbolSurrogate(astInterface.getTypeName());
      superProd.setEnclosingScope(enclosingScope);
      prodSymbol.addSuperInterfaceProd(superProd);
    }

    // A astimplements B
    for (ASTMCType astInterface : astSuperInterfaces) {
      ProdSymbolSurrogate superClass = new ProdSymbolSurrogate(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(astInterface));
      superClass.setEnclosingScope(enclosingScope);
      prodSymbol.addAstSuperInterface(superClass);
    }
  }

  /**
   * Set cardinality of all grammar's nonterminals
   */
  protected void setComponentsCardinality(ASTMCGrammar astGrammar) {
    for (ProdSymbol prodSymbol : astGrammar.getSymbol().getProds()) {
      Collection<AdditionalAttributeSymbol> astAttributes = prodSymbol.getSpannedScope().getLocalAdditionalAttributeSymbols();
      LinkedListMultimap<String, RuleComponentSymbol> map = prodSymbol.getSpannedScope().getRuleComponentSymbols();
      for (String compName : prodSymbol.getSpannedScope().getRuleComponentSymbols().keySet()) {
        Optional<AdditionalAttributeSymbol> attribute = astAttributes.stream()
            .filter(a -> a.getName().equals(compName)).findAny();
        Multiplicity multiplicity = STANDARD;
        if (attribute.isPresent()) {
          multiplicity = determineMultiplicity(attribute.get().getAstNode());
        } else {
          for (RuleComponentSymbol component : prodSymbol.getSpannedScope().getRuleComponentSymbols().get(compName)) {
            if (component.isIsNonterminal()) {
              Multiplicity mult = determineMultiplicity(component.getAstNode());
              multiplicity = max(Lists.newArrayList(mult, multiplicity));
            }
          }
        }
        for (RuleComponentSymbol component: prodSymbol.getSpannedScope().getRuleComponentSymbols().get(compName)) {
          if (component.isIsNonterminal()) {
            component.setIsList(multiplicity == LIST);
            component.setIsOptional(multiplicity == OPTIONAL);
          }
        }
      }
    }
  }

  protected void computeStartParserProd(ASTMCGrammar astGrammar) {
    if (astGrammar.isPresentStartRule()) {
      String name = astGrammar.getStartRule().getName();
      Optional<ProdSymbol> prod = astGrammar.getSymbol().getProdWithInherited(name);
      if (!prod.isPresent()) {
        error("0xA0243 Rule " + name + " couldn't be found!");
      } else {
        prod.get().setIsStartProd(true);
        astGrammar.getSymbol().setStartProd(prod.get());
      }
    } else {
      final Set<ASTProd> firstProductions = newLinkedHashSet();
      // The start rule for parsing is the first occurring Interface-, Abstract-
      // or Class-Production in this grammar
      if (!astGrammar.getClassProdList().isEmpty()) {
        firstProductions.add(astGrammar.getClassProdList().get(0));
      }
      if (!astGrammar.getInterfaceProdList().isEmpty()) {
        firstProductions.add(astGrammar.getInterfaceProdList().get(0));
      }
      if (!astGrammar.getAbstractProdList().isEmpty()) {
        firstProductions.add(astGrammar.getAbstractProdList().get(0));
      }
      setStartProd(astGrammar, firstProductions);
    }
  }

  /**
   * Set start parser production
   */
  protected void setStartProd(ASTMCGrammar astGrammar, Set<ASTProd> firstProductions) {
    // Set start parser rule
    ASTProd firstProduction = null;
    for (ASTProd prod : firstProductions) {
      if ((firstProduction == null)
          || (firstProduction.get_SourcePositionStart()
          .compareTo(prod.get_SourcePositionStart()) > 0)) {
        firstProduction = prod;
      }
    }

    if (firstProduction != null) {
      ProdSymbol prod = firstProduction.getSymbol();
      prod.setIsStartProd(true);
      astGrammar.getSymbol().setStartProd(prod);
    }
  }

}
