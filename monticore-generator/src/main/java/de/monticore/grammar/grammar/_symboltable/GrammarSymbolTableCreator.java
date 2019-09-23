/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.Multiplicity;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.FullGenericTypesPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper.*;
import static de.monticore.grammar.HelperGrammar.findImplicitTypes;
import static de.monticore.grammar.Multiplicity.*;
import static de.se_rwth.commons.Names.getQualifiedName;
import static de.se_rwth.commons.StringTransformations.uncapitalize;
import static de.se_rwth.commons.logging.Log.error;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class GrammarSymbolTableCreator extends GrammarSymbolTableCreatorTOP {

  private String packageName = "";

  private MCGrammarSymbol grammarSymbol;

  private ASTMCGrammar astGrammar;

  public GrammarSymbolTableCreator(IGrammarScope enclosingScope) {
    super(enclosingScope);
  }

  public GrammarSymbolTableCreator(Deque<? extends IGrammarScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public void initialize_MCGrammar(MCGrammarSymbol symbol, ASTMCGrammar astGrammar) {
    this.packageName = getQualifiedName(astGrammar.getPackageList());
    this.astGrammar = astGrammar;
    this.grammarSymbol = symbol;

    symbol.setIsComponent(astGrammar.isComponent());

    addSuperGrammars(astGrammar, symbol);
  }

  @Override
  public void endVisit(ASTMCGrammar astGrammar) {

    setComponentsCardinality();

    computeStartParserProd(astGrammar);

    // remove grammar scope
    removeCurrentGrammarScope();

  }

  @Override
  public void initialize_InterfaceProd(ProdSymbol prodSymbol, ASTInterfaceProd ast) {
    prodSymbol.setIsInterface(true);

    setSymbolDefinition(prodSymbol, ast.getSymbolDefinitionList());

    setSuperProdsAndTypes(prodSymbol, emptyList(),
        emptyList(), ast.getSuperInterfaceRuleList(), ast.getASTSuperInterfaceList());
  }

  @Override
  public void initialize_LexProd(ProdSymbol prodSymbol, ASTLexProd ast) {
    prodSymbol.setIsLexerProd(true);
  }

  @Override
  public void initialize_ClassProd(ProdSymbol prodSymbol, ASTClassProd ast) {
    setSymbolDefinition(prodSymbol, ast.getSymbolDefinitionList());

    setSuperProdsAndTypes(prodSymbol, ast.getSuperRuleList(),
        ast.getASTSuperClassList(), ast.getSuperInterfaceRuleList(), ast.getASTSuperInterfaceList());
  }


  @Override
  public void initialize_AbstractProd(ProdSymbol prodSymbol, ASTAbstractProd ast) {
    prodSymbol.setIsAbstract(true);

    setSymbolDefinition(prodSymbol, ast.getSymbolDefinitionList());

    setSuperProdsAndTypes(prodSymbol, ast.getSuperRuleList(),
        ast.getASTSuperClassList(), ast.getSuperInterfaceRuleList(), ast.getASTSuperInterfaceList());

  }

  @Override
  public void initialize_ExternalProd(ProdSymbol prodSymbol, ASTExternalProd ast) {
    prodSymbol.setIsExternal(true);

    setSymbolDefinition(prodSymbol, ast.getSymbolDefinitionList());
  }

  @Override
  public void initialize_EnumProd(ProdSymbol prodSymbol, ASTEnumProd ast) {
    prodSymbol.setIsEnum(true);
    // TODO Behandlung der Constants fehlt noch
  }

  @Override
  protected RuleComponentSymbol create_Terminal(ASTTerminal ast) {
    final String symbolName = ast.getUsageNameOpt().orElse(ast.getName());
    return new RuleComponentSymbol(symbolName);
  }

  @Override
  public void initialize_Terminal(RuleComponentSymbol prodComponent, ASTTerminal ast) {
    final String usageName = ast.getUsageNameOpt().orElse(null);
    Optional<ProdSymbol> currentSymbol = getProdSymbol();

    if (currentSymbol.isPresent()) {
      prodComponent.setUsageName(usageName);
      prodComponent.setIsTerminal(true);
      setComponentMultiplicity(prodComponent, ast);
      prodComponent = currentSymbol.get().addProdComponent(prodComponent);

    }
  }

  @Override
  protected RuleComponentSymbol create_KeyTerminal(ASTKeyTerminal ast) {
    final String symbolName = ast.getUsageNameOpt().orElse(ast.getName());
    return new RuleComponentSymbol(symbolName);
  }

  @Override
  public void initialize_KeyTerminal(RuleComponentSymbol prodComponent, ASTKeyTerminal ast) {
    final String usageName = ast.getUsageNameOpt().orElse(null);
    Optional<ProdSymbol> currentSymbol = getProdSymbol();

    if (currentSymbol.isPresent()) {
      prodComponent.setUsageName(usageName);
      prodComponent.setIsTerminal(true);
      setComponentMultiplicity(prodComponent, ast);
      prodComponent = currentSymbol.get().addProdComponent(prodComponent);

    }
  }

  @Override
  public void addToScopeAndLinkWithNode(de.monticore.grammar.grammar._symboltable.RuleComponentSymbol symbol, de.monticore.grammar.grammar._ast.ASTTerminal astNode) {
    if (getCurrentScope().isPresent()) {
      symbol.setEnclosingScope(getCurrentScope().get());
    }
    setLinkBetweenSymbolAndNode(symbol, astNode);
  }

  @Override
  public void visit(ASTNonTerminal ast) {
    final String usageName = ast.getUsageNameOpt().orElse(null);
    final ProdSymbol currentSymbol = getProdSymbol().orElse(null);

    if (currentSymbol != null) {
      final String symbolName = isNullOrEmpty(usageName) ? ast.getName() : usageName;
      RuleComponentSymbol prodComponent = new
          RuleComponentSymbol(symbolName);

      prodComponent.setUsageName(usageName);
      ProdSymbolReference symRef = new ProdSymbolReference(ast.getName(),
          getCurrentScope().orElse(null));
      prodComponent.setReferencedProd(symRef);


      RuleComponentSymbol prevProdComp = currentSymbol
          .getProdComponent(prodComponent.getName()).orElse(null);

      Optional<ProdSymbol> byReference = resolveRule(astGrammar, ast.getName());
      if (!byReference.isPresent() || !byReference.get().isLexerProd()) {

        if (prevProdComp != null && prevProdComp.getReferencedProd().isPresent()) {
          boolean sameType = prevProdComp.getReferencedProd().get().getName()
              .equals(ast.getName());
          if (!sameType) {
            boolean subType = isSubType(prevProdComp.getReferencedProd().get(),
                symRef)
                || isSubType(symRef, prevProdComp.getReferencedProd().get());
            if (!subType) {
              error("0xA4077 The production " + currentSymbol.getName()
                  + " must not use the attribute name " + symbolName +
                  " for different nonterminals.");
            }
          }
        }
      }
      prodComponent = currentSymbol.addProdComponent(prodComponent);
      setLinkBetweenSymbolAndNode(prodComponent, ast);

      prodComponent.setIsNonterminal(true);
      prodComponent.setReferencedSymbolName(ast.getReferencedSymbolOpt().orElse(""));
    }
  }

  @Override
  public void visit(ASTASTRule ast) {
    final Optional<ProdSymbol> prodSymbol = grammarSymbol.getProdWithInherited(ast.getType());
    if (!prodSymbol.isPresent()) {
      error(
          "0xA4076 There must not exist an AST rule for the nonterminal " + ast.getType()
              + " because there exists no production defining " + ast.getType(),
          ast.get_SourcePositionStart());
    }
    ast.getAdditionalAttributeList().forEach(a -> addAttributeInAST(prodSymbol.get(), a));
    ast.setEnclosingScope(getCurrentScope().get());
  }

  @Override
  public void visit(ASTAdditionalAttribute ast) {
    // Do nothing: see method visit(ASTASTRule ast)
    if (getCurrentScope().isPresent()) {
      ast.setEnclosingScope(getCurrentScope().get());
    } else {
      Log.error("Could not set enclosing scope of ASTNode \"" + ast
          + "\", because no scope is set yet!");
    }
  }

  @Override
  public void visit(ASTBlock ast) {
    // Do nothing:
    if (getCurrentScope().isPresent()) {
      ast.setEnclosingScope(getCurrentScope().get());
    } else {
      Log.error("Could not set enclosing scope of ASTNode \"" + ast
          + "\", because no scope is set yet!");
    }
  }

  void setComponentMultiplicity(RuleComponentSymbol prod, ASTNode ast) {
    Multiplicity multiplicity = determineMultiplicity(astGrammar, ast);
    if (multiplicity == LIST) {
      prod.setIsList(true);
    } else if (multiplicity == OPTIONAL) {
      prod.setIsOptional(true);
    }
  }

  @Override
  public void visit(ASTLexNonTerminal astNode) {
    final Optional<RuleComponentSymbol> sym = addRuleComponent(nullToEmpty(astNode.getName()),
        astNode, "");


    if (sym.isPresent()) {
      sym.get().setIsLexerNonterminal(true);
      addToScopeAndLinkWithNode(sym.get(), astNode);
    }
  }

  @Override
  public void visit(ASTConstantGroup astNode) {
    Optional<String> attrName = getConstantName(astNode,
        getProdSymbol());

    final String usageName = astNode.getUsageNameOpt().orElse(null);
    final ProdSymbol currentSymbol = getProdSymbol().orElse(null);

    if (currentSymbol != null && attrName.isPresent()) {
      RuleComponentSymbol prodComponent = new RuleComponentSymbol(attrName.get());
      prodComponent.setIsConstantGroup(true);
      prodComponent.setUsageName(usageName);

      final String symbolName = isNullOrEmpty(usageName)
          ? attrName.get()
          : usageName;
      Optional<RuleComponentSymbol> prevProdComp = currentSymbol
          .getProdComponent(symbolName);

      if (prevProdComp.isPresent() && !prevProdComp.get().isConstantGroup()) {
        error("0xA4006 The production " + currentSymbol.getName()
            + " must not use the attribute name " + attrName.get() +
            " for constant group and nonterminals.");
      }
      if (prevProdComp.isPresent()) {
        prodComponent = prevProdComp.get();
        prodComponent.setIsList(true);
        setLinkBetweenSymbolAndNode(prodComponent, astNode);
      } else {
        addToScopeAndLinkWithNode(prodComponent, astNode);
      }
      for (ASTConstant c : astNode.getConstantList()) {
        prodComponent.addSubProdComponent(c.getHumanNameOpt().orElse(c.getName()));
      }
    }
  }

  /**
   * Create entry for an implicit rule defined in another lexrule by using an
   * action and changing the type of the token
   */
  @Override
  public void visit(ASTLexActionOrPredicate action) {
    Grammar_WithConceptsPrettyPrinter prettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());
    for (String typeName : findImplicitTypes(action, prettyPrinter)) {
      // Create rule if needed
      Optional<ProdSymbol> rule = grammarSymbol.getProd(typeName);
      if (!rule.isPresent()) {
        // Create entry for an implicit rule
        final ProdSymbol prodSymbol = new ProdSymbol(typeName);
        prodSymbol.setIsLexerProd(true);
      }
    }
    super.visit(action);
  }

  private Optional<RuleComponentSymbol> addRuleComponent(String name, ASTRuleComponent node,
                                                         String usageName) {
    final ProdSymbol currentSymbol = getProdSymbol().orElse(null);

    if (currentSymbol != null) {
      final String symbolName = isNullOrEmpty(usageName) ? name : usageName;
      RuleComponentSymbol prodComponent = new RuleComponentSymbol(symbolName);

      prodComponent.setUsageName(usageName);

      ProdSymbol surroundingProd = (ProdSymbol) currentSymbol;
      prodComponent = surroundingProd.addProdComponent(prodComponent);
      return of(prodComponent);
    }
    return empty();

  }

  private void addSuperGrammars(ASTMCGrammar astGrammar, MCGrammarSymbol grammarSymbol) {
    for (ASTGrammarReference ref : astGrammar.getSupergrammarList()) {
      final String superGrammarName = getQualifiedName(ref.getNameList());

      final MCGrammarSymbolReference superGrammar = new MCGrammarSymbolReference(
          superGrammarName, getCurrentScope().orElse(null));

      grammarSymbol.addSuperGrammar(superGrammar);
    }
  }

  private void setSuperProdsAndTypes(ProdSymbol prodSymbol, List<ASTRuleReference> superProds,
                                     List<ASTMCType> astSuperClasses, List<ASTRuleReference> superInterfaceProds,
                                     List<ASTMCType> astSuperInterfaces) {
    final IGrammarScope enclosingScope = getCurrentScope().get();

    // A extends B
    for (ASTRuleReference astSuperProd : superProds) {
      ProdSymbolReference superProd = new ProdSymbolReference(astSuperProd.getTypeName(),
          enclosingScope);
      prodSymbol.addSuperProd(superProd);
    }

    // A astextends B
    for (ASTMCType astSuperClass : astSuperClasses) {
      MCProdOrTypeReference superClass = new MCProdOrTypeReference(FullGenericTypesPrinter.printType(astSuperClass),
          enclosingScope);
      prodSymbol.addAstSuperClass(superClass);
    }

    // A implements B
    for (ASTRuleReference astInterface : superInterfaceProds) {
      ProdSymbolReference superProd = new ProdSymbolReference(astInterface.getTypeName(),
          enclosingScope);
      prodSymbol.addSuperInterfaceProd(superProd);
    }

    // A astimplements B
    for (ASTMCType astInterface : astSuperInterfaces) {
      MCProdOrTypeReference superClass = new MCProdOrTypeReference(FullGenericTypesPrinter.printType(astInterface),
          enclosingScope);
      prodSymbol.addAstSuperInterface(superClass);
    }
  }

  /**
   * Set cardinality of all grammar's nonterminals
   */
  private void setComponentsCardinality() {
    for (ProdSymbol prodSymbol : grammarSymbol.getProdsWithInherited().values()) {
      Collection<AdditionalAttributeSymbol> astAttributes = prodSymbol.getProdAttributes();
      for (RuleComponentSymbol component : prodSymbol.getProdComponents()) {
        if (component.isNonterminal()) {
          if (!component.getAstNode().isPresent()) {
            System.out.println("SS");
          }
          setComponentMultiplicity(component, component.getAstNode().get());
          Optional<AdditionalAttributeSymbol> attribute = astAttributes.stream()
              .filter(a -> a.getName().equals(component.getName())).findAny();
          if (attribute.isPresent()) {
            Multiplicity multiplicity = multiplicityOfAttributeInAST(
                (ASTAdditionalAttribute) attribute.get().getAstNode().get());
            component.setIsList(multiplicity == LIST);
            component.setIsOptional(multiplicity == OPTIONAL);
          }
        }
      }
    }
  }

  private void setSymbolDefinition(ProdSymbol prodSymbol,
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

  private void computeStartParserProd(ASTMCGrammar astGrammar) {
    if (astGrammar.getStartRulesOpt().isPresent()) {
      String name = astGrammar.getStartRules().getName();
      Optional<ProdSymbol> prod = grammarSymbol.getProdWithInherited(name);
      if (!prod.isPresent()) {
        error("0xA0243 Rule " + name + " couldn't be found!");
      } else {
        prod.get().setIsStartProd(true);
        grammarSymbol.setStartProd(prod.get());
      }
    } else {
      final Set<ASTProd> firstProductions = newLinkedHashSet();
      // The start rule for parsing is the first occurring Interface-, Abstract-
      // or Class-Production in this grammar
      if (astGrammar.getClassProdList().size() != 0) {
        firstProductions.add(astGrammar.getClassProdList().get(0));
      }
      if (astGrammar.getInterfaceProdList().size() != 0) {
        firstProductions.add(astGrammar.getInterfaceProdList().get(0));
      }
      if (astGrammar.getAbstractProdList().size() != 0) {
        firstProductions.add(astGrammar.getAbstractProdList().get(0));
      }
      setStartProd(firstProductions);
    }
  }

  /**
   * Set start parser production
   */
  private void setStartProd(Set<ASTProd> firstProductions) {
    // Set start parser rule
    ASTProd firstProduction = null;
    for (ASTProd prod : firstProductions) {
      // TODO: add a common interface to the MC grammar for all these
      // productions and remove this hack
      if ((firstProduction == null)
          || (firstProduction.get_SourcePositionStart()
          .compareTo(prod.get_SourcePositionStart()) > 0)) {
        firstProduction = prod;
      }
    }

    if (firstProduction != null) {
      Optional<ProdSymbol> prod = grammarSymbol.getProdWithInherited(firstProduction.getName());
      if (!prod.isPresent()) {
        error("0xA2174 Prod " + firstProduction.getName() + " couldn't be found! Pos: "
            + firstProduction.get_SourcePositionStart());
      } else {
        prod.get().setIsStartProd(true);
        grammarSymbol.setStartProd(prod.get());
      }
    }
  }


  /**
   * @param mcProdSymbol
   * @param astAttribute
   */
  private void addAttributeInAST(ProdSymbol mcProdSymbol, ASTAdditionalAttribute astAttribute) {
    String attributeName = astAttribute.getNameOpt()
        .orElse(uncapitalize(FullGenericTypesPrinter.printType(astAttribute.getMCType())));

    AdditionalAttributeSymbol astAttributeSymbol = new AdditionalAttributeSymbol(attributeName);
    MCProdOrTypeReference attributeType = new MCProdOrTypeReference(
        FullGenericTypesPrinter.printType(astAttribute.getMCType()), mcProdSymbol.getSpannedScope());
    astAttributeSymbol.setTypeReference(attributeType);

    mcProdSymbol.addProdAttribute(astAttributeSymbol);
    //
    // Optional<MCProdComponentSymbol> mcComponent =
    // mcProdSymbol.getProdComponent(attributeName);
    // astAttributeSymbol.setReferencedComponent(mcComponent);

    setLinkBetweenSymbolAndNode(astAttributeSymbol, astAttribute);

  }


  public final Optional<ProdSymbol> getProdSymbol() {
    if (getCurrentScope().isPresent()) {
      IGrammarScope scope = getCurrentScope().get();
      if (scope.getSpanningSymbol().isPresent() && scope.getSpanningSymbol().get() instanceof ProdSymbol) {
        return of((ProdSymbol) scope.getSpanningSymbol().get());
      }
    }
    return empty();
  }

}
