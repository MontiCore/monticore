/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar._symboltable;

import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.utils.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;

import static de.monticore.grammar.HelperGrammar.findImplicitTypes;
import static de.se_rwth.commons.Names.getQualifiedName;
import static de.se_rwth.commons.logging.Log.error;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class GrammarScopesGenitor extends GrammarScopesGenitorTOP {

  public GrammarScopesGenitor(){
    super();
  }

  public GrammarScopesGenitor(IGrammarScope scope){
    super(scope);
  }

  public GrammarScopesGenitor(Deque<? extends IGrammarScope> scopeStack){
    super(scopeStack);
  }

  private static final String SET_SCOPE_ERROR = "Could not set enclosing scope of ASTNode \"%s\", because no scope is set yet!";

  private MCGrammarSymbol grammarSymbol;

  private ASTMCGrammar astGrammar;

  @Override
  public void visit(ASTMCGrammar ast){
    super.visit(ast);
    grammarSymbol = ast.getSymbol();
    astGrammar = ast;
  }

  @Override
  protected MCGrammarSymbolBuilder create_MCGrammar(ASTMCGrammar ast) {
    MCGrammarSymbolBuilder symbol = super.create_MCGrammar(ast);
    symbol.setIsComponent(ast.isComponent());
    addSuperGrammars(ast, symbol);
    return symbol;
  }

  @Override
  public void endVisit(ASTMCGrammar astGrammar) {
    // Initialize during creation
    // initialize_MCGrammar(astGrammar.getSymbol(), astGrammar);
    // remove grammar scope
    removeCurrentScope();
    removeCurrentScope();
  }

  @Override
  protected RuleComponentSymbolBuilder create_Terminal(ASTTerminal ast) {
    final String symbolName = ast.isPresentUsageName()?ast.getUsageName():"";
    return new RuleComponentSymbolBuilder().setName(symbolName);
  }

  @Override
  public  void visit (ASTTerminal node)  {
    // only create a symbol for ASTKeyTerminals that have a usage name
    // only with usage name is shown in AST
    if(node.isPresentUsageName()){
      super.visit(node);
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
  protected RuleComponentSymbolBuilder create_KeyTerminal(ASTKeyTerminal ast) {
    final String symbolName = ast.isPresentUsageName()?ast.getUsageName():"";
    return new RuleComponentSymbolBuilder().setName(symbolName);
  }

  @Override
  public void visit(ASTKeyTerminal node) {
    // only create a symbol for ASTKeyTerminals that have a usage name
    // only with usage name is shown in AST
    grammarSymbol.noKeywords.addAll(node.getKeyConstant().getStringList());
    if(node.isPresentUsageName()){
      super.visit(node);
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
  protected RuleComponentSymbolBuilder create_TokenTerminal(ASTTokenTerminal ast) {
    final String symbolName = ast.isPresentUsageName()?ast.getUsageName():"";
    return new RuleComponentSymbolBuilder().setName(symbolName);
  }

  @Override
  public void visit(ASTTokenTerminal node) {
    // only create a symbol for ASTKeyTerminals that have a usage name
    // only with usage name is shown in AST
    if(node.isPresentUsageName()){
      super.visit(node);
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
  protected RuleComponentSymbolBuilder create_NonTerminal(ASTNonTerminal ast) {
    final String symbolName = ast.isPresentUsageName() ? ast.getUsageName() : StringTransformations.uncapitalize(ast.getName());
    return new RuleComponentSymbolBuilder().setName(symbolName);
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

  @Override
  protected AdditionalAttributeSymbolBuilder create_AdditionalAttribute(ASTAdditionalAttribute ast) {
    String symbolName;
    if (ast.isPresentName()) {
      symbolName = ast.getName();
    } else {
      String typeName = MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(ast.getMCType());
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
  protected RuleComponentSymbolBuilder create_ConstantGroup(ASTConstantGroup ast) {
    return new de.monticore.grammar.grammar._symboltable.RuleComponentSymbolBuilder().setName(MCGrammarSymbolTableHelper.getConstantGroupName(ast));
  }

  private void addSuperGrammars(ASTMCGrammar astGrammar, MCGrammarSymbolBuilder grammarSymbol) {
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
  private void addAttributeInAST(ProdSymbol mcProdSymbol, ASTAdditionalAttribute astAttribute, boolean isAstAttr) {
    AdditionalAttributeSymbol symbol = create_AdditionalAttribute(astAttribute).setIsAstAttr(isAstAttr).build();
    symbol.setType(astAttribute.getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()));
    mcProdSymbol.getSpannedScope().add(symbol);
    setLinkBetweenSymbolAndNode(symbol, astAttribute);
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

}
