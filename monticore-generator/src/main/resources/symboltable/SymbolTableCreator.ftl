<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "directSuperCds", "rules", "hcPath")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign topAstName = genHelper.getQualifiedStartRuleName()>
<#assign scopeName = "I"+grammarName+"Scope">

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${package};

import de.se_rwth.commons.logging.Log;

import ${fqn}._ast.*;
import ${fqn}._visitor.${genHelper.getVisitorType()};
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.EnclosingScopeOfNodesInitializer;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.references.ISymbolReference;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class ${className} implements ${grammarName}Visitor {

  protected Deque<${scopeName}> scopeStack = new ArrayDeque<>();

  /**
   * The first scope OTHER THAN THE GLOBAL SCOPE that has been created, i.e., added  to the scope
   * stack. This information helps to determine the top scope within this creation process.
   */
  protected ${scopeName} firstCreatedScope;

  private ${grammarName}Visitor realThis = this;

  public ${className}(
    final ${scopeName} enclosingScope) {

    putOnStack(Log.errorIfNull(enclosingScope));
  }

  public ${className}(final Deque<? extends ${scopeName}> scopeStack) {
    this.scopeStack = Log.errorIfNull((Deque<${scopeName}>)scopeStack);
  }

  /**
    * Creates the symbol table starting from the <code>rootNode</code> and
    * returns the first scope that was created.
    *
    * @param rootNode the root node
    * @return the first scope that was created
    */
  public ${scopeName} createFromAST(${topAstName} rootNode) {
    Log.errorIfNull(rootNode, "0xA7004${genHelper.getGeneratedErrorCode(ast)} Error by creating of the ${className} symbol table: top ast node is null");
    rootNode.accept(realThis);
    return getFirstCreatedScope();
  }

  public ${scopeName} getFirstCreatedScope() {
    return firstCreatedScope;
  }

  public void putOnStack(${scopeName} scope) {
    Log.errorIfNull(scope);

    if (!scope.getEnclosingScope().isPresent() && getCurrentScope().isPresent()) {
      scope.setEnclosingScope(getCurrentScope().get());
      getCurrentScope().get().addSubScope(scope);
    } else if (scope.getEnclosingScope().isPresent() && getCurrentScope().isPresent()) {
      if (scope.getEnclosingScope().get() != getCurrentScope().get()) {
        Log.warn("0xA1043 The enclosing scope is not the same as the current scope on the stack.");
      }
    }

    if (firstCreatedScope == null) {
      firstCreatedScope = scope;
    }

    scopeStack.addLast(scope);
  }

  public ${genHelper.getVisitorType()} getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(${genHelper.getVisitorType()} realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
    }
  }

  public final Optional<${scopeName}> getCurrentScope() {
    return Optional.ofNullable(scopeStack.peekLast());
  }

  public final Optional<${scopeName}> removeCurrent${grammarName}Scope() {
    return Optional.of(scopeStack.pollLast());
  }

  protected void set${grammarName}ScopeStack(final Deque<${scopeName}> scopeStack) {
    this.scopeStack = scopeStack;
  }

  public void setEnclosingScopeOfNodes(ASTNode root) {
    EnclosingScopeOfNodesInitializer v = new EnclosingScopeOfNodesInitializer();
    v.handle(root);
  }

  public ${scopeName} createScope() {
    return new ${grammarName+"Scope"}Builder().build();
  }

<#list rules as ruleSymbol>
  <#assign ruleName = ruleSymbol.getName()>
  <#assign symbolName = genHelper.getQualifiedProdName(ruleSymbol)+"Symbol">
  <#assign astName = genHelper.getQualifiedASTName(ruleSymbol)>
  <#assign isScopeSpanning = genHelper.isScopeSpanningSymbol(ruleSymbol)>
  // Methods for ${symbolName}
  @Override
  public void visit(${astName} ast) {
    ${symbolName} symbol = create_${ruleName}(ast);
    initialize_${ruleName}(symbol, ast);
    addToScopeAndLinkWithNode(symbol, ast);
  }

  protected ${symbolName} create_${ruleName}(${astName} ast) {
      return new ${symbolName}(ast.getName());
  }

  protected void initialize_${ruleName}(${symbolName} symbol, ${astName} ast) {
  }

  @Override
  public void endVisit(${astName} ast) {
<#if isScopeSpanning>
    removeCurrent${grammarName}Scope();
</#if>
  }

  public void addToScopeAndLinkWithNode(${symbolName} symbol, ${astName} astNode) {
    addToScope(symbol);
    setLinkBetweenSymbolAndNode(symbol, astNode);
<#if isScopeSpanning>
  ${scopeName} scope = createScope();
    putOnStack(scope);
    symbol.setSpannedScope(scope);
</#if>
  }

  public void setLinkBetweenSymbolAndNode(${symbolName} symbol, ${astName} astNode) {
    // symbol -> ast
    symbol.setAstNode(astNode);

    // ast -> symbol
    astNode.setSymbol2(symbol);
    astNode.set${genHelper.getSymbolNameFromQualifiedSymbol(symbolName)}(symbol);
    astNode.setEnclosingScope2(symbol.getEnclosingScope());

<#if isScopeSpanning>
    // ast -> spannedScope
      astNode.setSpannedScope2(symbol.getSpannedScope());
</#if>
  }

  public void addToScope(${symbolName} symbol) {
    if (!(symbol instanceof ISymbolReference)) {
      if (getCurrentScope().isPresent()) {
        getCurrentScope().get().add(symbol);
      } else {
        Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
      }
    }
  }

<#if isScopeSpanning>
  public void setLinkBetweenSpannedScopeAndNode(${scopeName} scope, ${astName} astNode) {
    // scope -> ast
    scope.setAstNode(astNode);

    // ast -> scope
    astNode.setSpannedScope2((${grammarName}Scope) scope);
  }
</#if>
</#list>
}
