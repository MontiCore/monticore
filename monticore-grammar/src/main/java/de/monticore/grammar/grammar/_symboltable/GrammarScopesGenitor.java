/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar._symboltable;

import com.google.common.collect.Lists;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

import static de.se_rwth.commons.Names.getQualifiedName;
import static de.se_rwth.commons.logging.Log.error;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class GrammarScopesGenitor extends GrammarScopesGenitorTOP {

  public GrammarScopesGenitor(){
    super();
  }

  protected static final String SET_SCOPE_ERROR = "Could not set enclosing scope of ASTNode \"%s\", because no scope is set yet!";

  protected MCGrammarSymbol grammarSymbol;

  @Override
  public void visit(ASTMCGrammar ast){
    MCGrammarSymbolBuilder symbolBuilder = de.monticore.grammar.grammar.GrammarMill.mCGrammarSymbolBuilder().setName(ast.getName());
    symbolBuilder.setIsComponent(ast.isComponent());
    addSuperGrammars(ast, symbolBuilder);
    MCGrammarSymbol symbol = symbolBuilder.build();
    if (getCurrentScope().isPresent()) {
      getCurrentScope().get().add(symbol);
    } else {
      Log.warn("0xAE867 Symbol cannot be added to current scope, since no scope exists.");
    }
    IGrammarScope scope = createScope(false);
    putOnStack(scope);
    symbol.setSpannedScope(scope);
    // symbol -> ast
    symbol.setAstNode(ast);

    // ast -> symbol
    ast.setSymbol(symbol);
    ast.setEnclosingScope(symbol.getEnclosingScope());

    // scope -> ast
    scope.setAstNode(ast);

    // ast -> scope
    ast.setSpannedScope(scope);
    initMCGrammarHP1(ast.getSymbol());
    grammarSymbol = ast.getSymbol();
  }

  @Override
  public void endVisit(ASTMCGrammar astGrammar) {
    // remove grammar scope
    removeCurrentScope();
    removeCurrentScope();
  }

  @Override
  public  void visit (ASTTerminal node)  {
    // only create a symbol for ASTKeyTerminals that have a usage name
    // only with usage name is shown in AST
    if(node.isPresentUsageName()){
      RuleComponentSymbolBuilder symbolBuilder = GrammarMill.ruleComponentSymbolBuilder().setName(node.getName());
      symbolBuilder.setName(node.isPresentUsageName()?node.getUsageName():"");
      RuleComponentSymbol symbol = symbolBuilder.build();
      if (getCurrentScope().isPresent()) {
        getCurrentScope().get().add(symbol);
      } else {
        Log.warn("0xAE862 Symbol cannot be added to current scope, since no scope exists.");
      }
      // symbol -> ast
      symbol.setAstNode(node);

      // ast -> symbol
      node.setSymbol(symbol);
      node.setEnclosingScope(symbol.getEnclosingScope());

      initRuleComponentHP1(node.getSymbol());
    } else {
      // must still add the scope to the ASTKeyTerminal, even if it defines no symbol
      if (getCurrentScope().isPresent()) {
        node.setEnclosingScope(getCurrentScope().get());
      }
      else {
        Log.error("Could not set enclosing scope of ASTNode \"" + node
            + "\", because no scope is set yet!");
      }
    }
  }

  @Override
  public void visit(ASTKeyTerminal node) {
    // only create a symbol for ASTKeyTerminals that have a usage name
    // only with usage name is shown in AST
    grammarSymbol.noKeywords.addAll(node.getKeyConstant().getStringList());
    if(node.isPresentUsageName()){
      RuleComponentSymbolBuilder symbolBuilder = GrammarMill.ruleComponentSymbolBuilder().setName(node.getName());
      symbolBuilder.setName(node.isPresentUsageName()?node.getUsageName():"");
      RuleComponentSymbol symbol = symbolBuilder.build();
      if (getCurrentScope().isPresent()) {
        getCurrentScope().get().add(symbol);
      } else {
        Log.warn("0xAE863 Symbol cannot be added to current scope, since no scope exists.");
      }
      // symbol -> ast
      symbol.setAstNode(node);

      // ast -> symbol
      node.setSymbol(symbol);
      node.setEnclosingScope(symbol.getEnclosingScope());

      initRuleComponentHP1(node.getSymbol());
    } else {
      // must still add the scope to the ASTKeyTerminal, even if it defines no symbol
      if (getCurrentScope().isPresent()) {
        node.setEnclosingScope(getCurrentScope().get());
      } else {
        Log.error(String.format(SET_SCOPE_ERROR, node));
      }
    }
  }

  @Override
  public void visit(ASTTokenTerminal node) {
    // only create a symbol for ASTKeyTerminals that have a usage name
    // only with usage name is shown in AST
    if(node.isPresentUsageName()){
      RuleComponentSymbolBuilder symbolBuilder = GrammarMill.ruleComponentSymbolBuilder().setName(node.getName());
      symbolBuilder.setName(node.isPresentUsageName()?node.getUsageName():"");
      RuleComponentSymbol symbol = symbolBuilder.build();
      if (getCurrentScope().isPresent()) {
        getCurrentScope().get().add(symbol);
      } else {
        Log.warn("0xAE864 Symbol cannot be added to current scope, since no scope exists.");
      }
      // symbol -> ast
      symbol.setAstNode(node);

      // ast -> symbol
      node.setSymbol(symbol);
      node.setEnclosingScope(symbol.getEnclosingScope());

      initRuleComponentHP1(node.getSymbol());
    } else {
      // must still add the scope to the ASTKeyTerminal, even if it defines no symbol
      if (getCurrentScope().isPresent()) {
        node.setEnclosingScope(getCurrentScope().get());
      } else {
        Log.error(String.format(SET_SCOPE_ERROR, node));
      }
    }
  }

  @Override
  public void visit(ASTNonTerminal node) {
    RuleComponentSymbolBuilder symbolBuilder = GrammarMill.ruleComponentSymbolBuilder().setName(node.getName());
    symbolBuilder.setName(node.isPresentUsageName()?node.getUsageName(): StringTransformations.uncapitalize(node.getName()));
    RuleComponentSymbol symbol = symbolBuilder.build();
    if (getCurrentScope().isPresent()) {
      getCurrentScope().get().add(symbol);
    } else {
      Log.warn("0xAE865 Symbol cannot be added to current scope, since no scope exists.");
    }
    // symbol -> ast
    symbol.setAstNode(node);

    // ast -> symbol
    node.setSymbol(symbol);
    node.setEnclosingScope(symbol.getEnclosingScope());

    initRuleComponentHP1(node.getSymbol());
  }

  @Override
  public void visit(ASTASTRule ast) {
    final Optional<ProdSymbol> prodSymbol = grammarSymbol.getProd(ast.getType());
    if (prodSymbol.isPresent()) {
      ast.getAdditionalAttributeList().forEach(a -> addAttributeInAST(prodSymbol.get(), a, true));
    } else {
      error(
          "0xA4076 There must not exist an AST rule for the nonterminal " + ast.getType()
              + " because there exists no production defining " + ast.getType(),
          ast.get_SourcePositionStart());
    }
    ast.setEnclosingScope(getCurrentScope().get());
  }

  @Override
  public void visit(ASTSymbolRule ast) {
    final Optional<ProdSymbol> prodSymbol = grammarSymbol.getProd(ast.getType());
    if (prodSymbol.isPresent()) {
      ast.getAdditionalAttributeList().forEach(a -> addAttributeInAST(prodSymbol.get(), a, false));
    } else {
      error(
          "0xA4077 There must not exist an symbol rule for the nonterminal " + ast.getType()
              + " because there exists no production defining " + ast.getType(),
          ast.get_SourcePositionStart());
    }
    ast.setEnclosingScope(getCurrentScope().get());
  }

  protected AdditionalAttributeSymbolBuilder createAdditionalAttribute(ASTAdditionalAttribute ast) {
    String symbolName;
    if (ast.isPresentName()) {
      symbolName = ast.getName();
    } else {
      String typeName = MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter().prettyprint(ast.getMCType());
      symbolName = StringTransformations.uncapitalize(Names.getSimpleName(typeName));
    }
    return new AdditionalAttributeSymbolBuilder().setName(symbolName);
  }

  @Override
  public void visit(ASTAdditionalAttribute ast) {
    // Do nothing: see method visit(ASTASTRule ast)
    if (getCurrentScope().isPresent()) {
      ast.setEnclosingScope(getCurrentScope().get());
    } else {
      Log.error(String.format(SET_SCOPE_ERROR, ast));
    }
  }

  @Override
  public void endVisit(ASTAdditionalAttribute node) {
    // Do nothing
  }

  @Override
  public void visit(ASTBlock ast) {
    // Do nothing:
    if (getCurrentScope().isPresent()) {
      ast.setEnclosingScope(getCurrentScope().get());
    } else {
      Log.error(String.format(SET_SCOPE_ERROR, ast));
    }
  }

  @Override
  public void endVisit(ASTBlock node) {
    // Do nothing
  }

  /**
   * Create entry for an implicit rule defined in another lexrule by using an
   * action and changing the type of the token
   */
  @Override
  public void visit(ASTLexActionOrPredicate action) {
    Grammar_WithConceptsFullPrettyPrinter prettyPrinter = new Grammar_WithConceptsFullPrettyPrinter(new IndentPrinter());
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

  @Override
  public void visit(ASTConstantGroup node) {
    RuleComponentSymbolBuilder symbolBuilder = de.monticore.grammar.grammar.GrammarMill.ruleComponentSymbolBuilder().setName(node.getName());
    symbolBuilder.setName(MCGrammarSymbolTableHelper.getConstantGroupName(node));
    RuleComponentSymbol symbol = symbolBuilder.build();
    if (getCurrentScope().isPresent()) {
      getCurrentScope().get().add(symbol);
    } else {
      Log.warn("0xAE866 Symbol cannot be added to current scope, since no scope exists.");
    }
    // symbol -> ast
    symbol.setAstNode(node);

    // ast -> symbol
    node.setSymbol(symbol);
    node.setEnclosingScope(symbol.getEnclosingScope());

    initRuleComponentHP1(node.getSymbol());
  }

  protected void addSuperGrammars(ASTMCGrammar astGrammar, MCGrammarSymbolBuilder grammarSymbol) {
    for (ASTGrammarReference ref : astGrammar.getSupergrammarList()) {
      final String superGrammarName = getQualifiedName(ref.getNameList());

      final MCGrammarSymbolSurrogate superGrammar = new MCGrammarSymbolSurrogate(
          superGrammarName);
      superGrammar.setEnclosingScope(getCurrentScope().orElse(null));

      grammarSymbol.addSuperGrammar(superGrammar);
    }
  }

  /**
   * @param mcProdSymbol
   * @param astAttribute
   */
  protected void addAttributeInAST(ProdSymbol mcProdSymbol, ASTAdditionalAttribute astAttribute, boolean isAstAttr) {
    AdditionalAttributeSymbol symbol = createAdditionalAttribute(astAttribute).setIsAstAttr(isAstAttr).build();
    symbol.setType(astAttribute.getMCType().printType(MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter()));
    mcProdSymbol.getSpannedScope().add(symbol);
    // symbol -> ast
    symbol.setAstNode(astAttribute);

    // ast -> symbol
    astAttribute.setSymbol(symbol);
    astAttribute.setEnclosingScope(symbol.getEnclosingScope());
  }

  public final Optional<ProdSymbol> getProdSymbol() {
    if (getCurrentScope().isPresent()) {
      IGrammarScope scope = getCurrentScope().get();
      if (scope.isPresentSpanningSymbol() && scope.getSpanningSymbol() instanceof ProdSymbol) {
        return of((ProdSymbol) scope.getSpanningSymbol());
      }
    }
    return empty();
  }

  protected List<String> findImplicitTypes(ASTLexActionOrPredicate action,
                                               Grammar_WithConceptsFullPrettyPrinter prettyPrinter) {
    List<String> ret = Lists.newArrayList();
    StringBuilder buffer = new StringBuilder();
    buffer.append(prettyPrinter.prettyprint(action.getExpressionPredicate()));
    String actionText = buffer.toString();
    if (actionText.contains("_ttype")) {
      String[] split = actionText.split("_ttype");

      for (int i = 1; i < split.length; i++) {
        String rest = split[i].trim();
        if (rest.length() > 1 && rest.startsWith("=")) {
          rest = rest.substring(1).trim();
          if (!rest.startsWith("Token")) {
            String string = rest.split("[ ;]")[0];
            ret.add(string);
          }
        }
      }
    }
    if (actionText.contains("$setType(")) {
      String[] split = actionText.split("[$]setType[(]");

      for (int i = 1; i < split.length; i++) {
        String rest = split[i].trim();
        if (rest.length() > 0) {

          if (!rest.startsWith("Token")) {
            String string = rest.split("[ )]")[0];
            ret.add(string);
          }
        }
      }
    }
    return ret;
  }

}
