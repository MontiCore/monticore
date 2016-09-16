/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
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

package de.monticore.grammar.symboltable;

import com.google.common.collect.Sets;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static de.se_rwth.commons.Names.getQualifiedName;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class EssentialMontiCoreGrammarSymbolTableCreator extends CommonSymbolTableCreator implements Grammar_WithConceptsVisitor {

  private final Grammar_WithConceptsPrettyPrinter prettyPrinter;

  private String packageName = "";

  private EssentialMCGrammarSymbol grammarSymbol;

  public EssentialMontiCoreGrammarSymbolTableCreator(
      ResolverConfiguration resolverConfig,
      MutableScope enclosingScope,
      Grammar_WithConceptsPrettyPrinter prettyPrinter) {
    super(resolverConfig, enclosingScope);

    this.prettyPrinter = prettyPrinter;
  }

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Scope createFromAST(ASTMCGrammar rootNode) {
    Log.errorIfNull(rootNode);
    handle(rootNode);
    return getFirstCreatedScope();
  }

  // PN is tested
  @Override
  public void visit(ASTMCGrammar astGrammar) {
    Log.debug("Building Symboltable for Grammar: " + astGrammar.getName(),
        EssentialMontiCoreGrammarSymbolTableCreator.class.getSimpleName());

    packageName = getQualifiedName(astGrammar.getPackage());

    final List<ImportStatement> imports = new ArrayList<>();
    if (astGrammar.getImportStatements() != null) {
      for (ASTMCImportStatement imp : astGrammar.getImportStatements()) {
        imports.add(new ImportStatement(getQualifiedName(imp.getImportList()),
            imp.isStar()));
      }
    }


    final ArtifactScope scope = new ArtifactScope(Optional.empty(), packageName, imports);
    putOnStack(scope);

    grammarSymbol = new EssentialMCGrammarSymbol(astGrammar.getName());
    grammarSymbol.setComponent(astGrammar.isComponent());

    addToScopeAndLinkWithNode(grammarSymbol, astGrammar);

    addSuperGrammars(astGrammar, grammarSymbol);
  }

  private void addSuperGrammars(ASTMCGrammar astGrammar, EssentialMCGrammarSymbol grammarSymbol) {
    for (ASTGrammarReference ref : astGrammar.getSupergrammar()) {
      final String superGrammarName = getQualifiedName(ref.getNames());

     final EssentialMCGrammarSymbolReference superGrammar =
         new EssentialMCGrammarSymbolReference(superGrammarName, currentScope().orElse(null));

      grammarSymbol.addSuperGrammar(superGrammar);
    }
  }

  @Override
  public void endVisit(ASTMCGrammar astGrammar) {
    setEnclosingScopeOfNodes(astGrammar);

    computeStartParserProd(astGrammar);

    // remove grammar scope
    removeCurrentScope();

    // remove artifact scope
    removeCurrentScope();
  }

  private void undefinedRuleError(String name, SourcePosition pos) {
    Log.error(pos + ": " + "0xA0964 Undefined rule: " + name);
  }

  private void computeStartParserProd(ASTMCGrammar astGrammar) {
    if (astGrammar.getStartRules().isPresent()) {
      String name = astGrammar.getStartRules().get().getRuleReference().getName();
      Optional<MCProdSymbol> prod = grammarSymbol.getProdWithInherited(name);
      if (!prod.isPresent()) {
        Log.error("0xA0243 Rule " + name + " couldn't be found!");
      }
      else {
        prod.get().setStartProd(true);
        grammarSymbol.setStartProd(prod.get());
      }
    } else {
      final Set<ASTProd> firstProductions = Sets.newLinkedHashSet();
      // The start rule for parsing is the first occurring Interface-, Abstract-
      // or Class-Production in this grammar
      if (astGrammar.getClassProds().size() != 0) {
        firstProductions.add(astGrammar.getClassProds().get(0));
      }
      if (astGrammar.getInterfaceProds().size() != 0) {
        firstProductions.add(astGrammar.getInterfaceProds().get(0));
      }
      if (astGrammar.getAbstractProds().size() != 0) {
        firstProductions.add(astGrammar.getAbstractProds().get(0));
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
          || (firstProduction.get_SourcePositionStart().compareTo(prod.get_SourcePositionStart()) > 0)) {
        firstProduction = prod;
      }
    }

    if (firstProduction != null) {
      Optional<MCProdSymbol> prod = grammarSymbol.getProdWithInherited(firstProduction.getName());
      if (!prod.isPresent()) {
        Log.error("0xA2074 Prod " + firstProduction.getName() + " couldn't be found! Pos: "
            + firstProduction.get_SourcePositionStart());
      }
      else {
        prod.get().setStartProd(true);
        grammarSymbol.setStartProd(prod.get());
      }
    }
  }

  @Override
  public void visit(ASTInterfaceProd ast) {
    MCProdSymbol prodSymbol = new MCProdSymbol(ast.getName());
    prodSymbol.setInterface(true);

    setSymbolDefinitionIfExists(prodSymbol, ast.getSymbolDefinition().orElse(null));

    setSuperProdsAndTypes(prodSymbol, Collections.emptyList(),
        Collections.emptyList(), ast.getSuperInterfaceRule(), ast.getASTSuperInterface());

    addToScopeAndLinkWithNode(prodSymbol, ast);
  }

  private void setSymbolDefinitionIfExists(MCProdSymbol prodSymbol, ASTSymbolDefinition symbolDefinition) {
    if (symbolDefinition != null) {
      String symbolKindName = prodSymbol.getName();

      if (symbolDefinition.getSymbolKind().isPresent()
          && !symbolDefinition.getSymbolKind().get().isEmpty()) {
        symbolKindName = symbolDefinition.getSymbolKind().get();
      }

      MCProdSymbolReference prodReference =
          new MCProdSymbolReference(symbolKindName, prodSymbol.getSpannedScope());
      prodSymbol.setProdDefiningSymbolKind(prodReference);
    }
  }

  @Override
  public void endVisit(ASTInterfaceProd astInterfaceProd) {
    removeCurrentScope();
  }

  @Override
  public void visit(ASTLexProd ast) {
    final MCProdSymbol prodSymbol = new MCProdSymbol(ast.getName());
    prodSymbol.setLexerProd(true);

    addToScopeAndLinkWithNode(prodSymbol, ast);
  }

  @Override
  public void endVisit(ASTLexProd astLexProd) {
    removeCurrentScope();
  }

  @Override
  public void visit(ASTClassProd ast) {
    final MCProdSymbol prodSymbol = new MCProdSymbol(ast.getName());

    setSymbolDefinitionIfExists(prodSymbol, ast.getSymbolDefinition().orElse(null));

    setSuperProdsAndTypes(prodSymbol, ast.getSuperRule(),
        ast.getASTSuperClass(), ast.getSuperInterfaceRule(), ast.getASTSuperInterface());

    addToScopeAndLinkWithNode(prodSymbol, ast);
  }

  public void endVisit(ASTClassProd astClassProd) {
    removeCurrentScope();
  }

  @Override
  public void visit(ASTAbstractProd ast) {
    MCProdSymbol prodSymbol = new MCProdSymbol(ast.getName());
    prodSymbol.setAbstract(true);

    setSymbolDefinitionIfExists(prodSymbol, ast.getSymbolDefinition().orElse(null));

    setSuperProdsAndTypes(prodSymbol, ast.getSuperRule(),
        ast.getASTSuperClass(), ast.getSuperInterfaceRule(), ast.getASTSuperInterface());

    addToScopeAndLinkWithNode(prodSymbol, ast);
  }

  private void setSuperProdsAndTypes(MCProdSymbol prodSymbol, List<ASTRuleReference> superProds,
      List<ASTGenericType> astSuperClasses, List<ASTRuleReference> superInterfaceProds, List<ASTGenericType> astSuperInterfaces) {
    final Scope enclosingScope = currentScope().get();

    // A extends B
    for (ASTRuleReference astSuperProd : superProds) {
      MCProdSymbolReference superProd =
          new MCProdSymbolReference(astSuperProd.getTypeName(), enclosingScope);
      prodSymbol.addSuperProd(superProd);
    }

    // A astextends B
    for (ASTGenericType astSuperClass : astSuperClasses) {
      MCProdOrTypeReference superClass =
          new MCProdOrTypeReference(astSuperClass.getTypeName(), enclosingScope);
      prodSymbol.addAstSuperClass(superClass);
    }

    // A implements B
    for (ASTRuleReference astInterface : superInterfaceProds) {
      MCProdSymbolReference superProd =
          new MCProdSymbolReference(astInterface.getTypeName(), enclosingScope);
      prodSymbol.addSuperInterfaceProd(superProd);
    }

    // A astimplements B
    for (ASTGenericType astInterface : astSuperInterfaces) {
      MCProdOrTypeReference superClass =
          new MCProdOrTypeReference(astInterface.getTypeName(), enclosingScope);
      prodSymbol.addAstSuperInterface(superClass);
    }
  }

  @Override
  public void endVisit(ASTAbstractProd astAbstractProd) {
    removeCurrentScope();
  }

  @Override
  public void visit(ASTExternalProd ast) {
    final MCProdSymbol prodSymbol = new MCProdSymbol(ast.getName());
    prodSymbol.setExternal(true);

    setSymbolDefinitionIfExists(prodSymbol, ast.getSymbolDefinition().orElse(null));

    addToScopeAndLinkWithNode(prodSymbol, ast);
  }

  @Override
  public void endVisit(ASTExternalProd astExternalProd) {
    removeCurrentScope();
  }

  @Override
  public void visit(ASTEnumProd ast) {
    final MCProdSymbol prodSymbol = new MCProdSymbol(ast.getName());
    prodSymbol.setEnum(true);

    addToScopeAndLinkWithNode(prodSymbol, ast);
  }

  @Override
  public void endVisit(ASTEnumProd astEnumProd) {
    removeCurrentScope();
  }

  @Override
  public void visit(ASTTerminal ast) {
    // TODO do we need symbols for nonterminals?

    final String usageName = ast.getUsageName().orElse(null);
    final String symbolName = isNullOrEmpty(usageName) ? ast.getName() : usageName;

    MCProdComponentSymbol terminalSymbol = new MCProdComponentSymbol(symbolName);
    terminalSymbol.setTerminal(true);

    setComponentMultiplicity(terminalSymbol, ast.getIteration());

    addToScopeAndLinkWithNode(terminalSymbol, ast);
  }

  @Override
  public void visit(ASTNonTerminal ast) {
    final String usageName = ast.getUsageName().orElse(null);

    final Optional<MCProdComponentSymbol> ntSymbol =
        addRuleComponent(ast.getName(), ast, usageName);

    if (ntSymbol.isPresent()) {
      final MCProdComponentSymbol sym = ntSymbol.get();
      sym.setUsageName(ast.getUsageName().orElse(null));
      sym.setNonterminal(true);

      sym.setReferencedProd(new MCProdSymbolReference(ast.getName(), currentScope().orElse(null)));
      sym.setReferencedSymbolName(ast.getReferencedSymbol().orElse(""));
      setComponentMultiplicity(sym, ast.getIteration());
    }



  }

  void setComponentMultiplicity(MCProdComponentSymbol prod, int iteration) {
    if (prod == null) {
      return;
    }

    if ((iteration == ASTConstantsGrammar.PLUS) || (iteration == ASTConstantsGrammar.STAR)) {
      prod.setList(true);
    }
    else if (iteration == ASTConstantsGrammar.QUESTION) {
      prod.setOptional(true);
    }
  }

  @Override
  public void visit(ASTLexNonTerminal astNode) {
    final Optional<MCProdComponentSymbol> sym =
        addRuleComponent(nullToEmpty(astNode.getName()), astNode, "");

    if (sym.isPresent()) {
      sym.get().setLexerNonterminal(true);
    }
  }

  @Override
  public void visit(ASTConstantGroup astNode) {
    final Optional<MCProdComponentSymbol> sym =
        addRuleComponent(astNode.getUsageName().orElse(""), astNode, astNode.getUsageName().orElse(null));

    if (sym.isPresent()) {
      sym.get().setConstantGroup(true);
    }
  }

  @Override
  public void visit(ASTConstant astNode) {
    final Optional<MCProdComponentSymbol> sym =
        addRuleComponent(astNode.getName(), astNode, astNode.getHumanName().orElse(null));

    if (sym.isPresent()) {
      sym.get().setConstant(true);
    }
  }

  private Optional<MCProdComponentSymbol> addRuleComponent(String name, ASTNode node, String usageName) {
    final Symbol currentSymbol = currentSymbol().orElse(null);

    if (currentSymbol != null) {
      final String symbolName = isNullOrEmpty(usageName) ? name : usageName;
      final MCProdComponentSymbol prodComponent = new MCProdComponentSymbol(symbolName);

      prodComponent.setUsageName(usageName);

      if (currentSymbol instanceof MCProdSymbol) {
        MCProdSymbol surroundingProd = (MCProdSymbol) currentSymbol;
        surroundingProd.addProdComponent(prodComponent);
      }
      else {
        addToScope(prodComponent);
      }

      setLinkBetweenSymbolAndNode(prodComponent, node);

      return Optional.of(prodComponent);
    }

    return Optional.empty();

  }
}
