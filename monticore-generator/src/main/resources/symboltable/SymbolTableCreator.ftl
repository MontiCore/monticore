<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "directSuperCds", "symbolDefiningRules", "nonSymbolDefiningRules", "kinds", "hcPath")}

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
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.references.ISymbolReference;
import de.se_rwth.commons.logging.Log;

import java.util.*;

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

  @SuppressWarnings("unchecked")
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
  public ${grammarName}ArtifactScope createFromAST(${topAstName} rootNode) {
    Log.errorIfNull(rootNode, "0xA7004${genHelper.getGeneratedErrorCode(ast)} Error by creating of the ${className} symbol table: top ast node is null");
    ${grammarName}ArtifactScope artifactScope = new ${grammarName}ArtifactScope(Optional.empty(), "", new ArrayList<>());
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
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
    return Optional.ofNullable(scopeStack.pollLast());
  }

  protected void set${grammarName}ScopeStack(final Deque<${scopeName}> scopeStack) {
    this.scopeStack = scopeStack;
  }

  public ${scopeName} createScope(boolean shadowing) {
    ${scopeName} scope = ${grammarName}SymTabMill.${grammarName?uncap_first+"Scope"}Builder().build();
    scope.setShadowing(shadowing);
    return scope;
  }

<#list symbolDefiningRules as ruleSymbol, symbolName>
  <#assign ruleName = ruleSymbol.getName()>
  <#assign astName = genHelper.getQualifiedASTName(ruleSymbol)>
  <#assign isScopeSpanning = genHelper.spansScope(ruleSymbol)>
  <#assign qualifiedSymbolName = genHelper.getQualifiedSymbolName(ruleSymbol.getEnclosingScope(), symbolName )>
  // Methods for ${ruleSymbol.getName()}: ${symbolName}
<#if !ruleSymbol.isInterface()>
  @Override
  public void visit(${astName} ast) {
    ${qualifiedSymbolName} symbol = create_${ruleName}(ast);
    initialize_${ruleName}(symbol, ast);
    addToScopeAndLinkWithNode(symbol, ast);
  }

  protected ${qualifiedSymbolName} create_${ruleName}(${astName} ast) {
      return new ${qualifiedSymbolName}(ast.getName());
  }

  protected void initialize_${ruleName}(${qualifiedSymbolName} symbol, ${astName} ast) {
  }

  @Override
  public void endVisit(${astName} ast) {
<#if isScopeSpanning>
    removeCurrent${grammarName}Scope();
</#if>
  }

  public void addToScopeAndLinkWithNode(${qualifiedSymbolName} symbol, ${astName} astNode) {
    addToScope(symbol);
    setLinkBetweenSymbolAndNode(symbol, astNode);
<#if isScopeSpanning>
  ${scopeName} scope = createScope(false);
    putOnStack(scope);
    symbol.setSpannedScope(scope);
</#if>
  }

  public void setLinkBetweenSymbolAndNode(${qualifiedSymbolName} symbol, ${astName} astNode) {
    // symbol -> ast
    symbol.setAstNode(astNode);

    // ast -> symbol
    astNode.setSymbol(symbol);
    astNode.set${genHelper.getSymbolNameFromQualifiedSymbol(qualifiedSymbolName)}(symbol);
    astNode.setEnclosingScope(symbol.getEnclosingScope());

<#if isScopeSpanning>
    // ast -> spannedScope
      astNode.setSpannedScope(symbol.getSpannedScope());
</#if>
  }

  <#if isScopeSpanning>
  public void setLinkBetweenSpannedScopeAndNode(${scopeName} scope, ${astName} astNode) {
    // scope -> ast
    scope.setAstNode(astNode);

    // ast -> scope
    astNode.setSpannedScope((${grammarName}Scope) scope);
  }
  </#if>

</#if>
</#list>

<#list nonSymbolDefiningRules as ruleSymbol>
  <#assign isScopeSpanning = genHelper.spansScope(ruleSymbol)>
  @Override
  public void visit(AST${ruleSymbol.getName()} node) {
    if (getCurrentScope().isPresent()) {
      node.setEnclosingScope(getCurrentScope().get());
    }
    else {
      Log.error("Could not set enclosing scope of ASTNode \"" + node
          + "\", because no scope is set yet!");
    }
    <#if isScopeSpanning>
        ${scopeName} scope = create_${ruleSymbol.getName()}(node);
      initialize_${ruleSymbol.getName()}(scope, node);
      putOnStack(scope);
      setLinkBetweenSpannedScopeAndNode(scope, node);
    </#if>
  }

  <#if isScopeSpanning>
    protected ${scopeName} create_${ruleSymbol.getName()}(AST${ruleSymbol.getName()} ast) {
      <#if !genHelper.isNamed(ruleSymbol)>
        // creates new visibility scope
        return createScope(false);
      <#else>
        // creates new shadowing scope
        return createScope(true);
      </#if>
    }

    protected void initialize_${ruleSymbol.getName()}(${scopeName} scope, AST${ruleSymbol.getName()} ast) {
      <#if !genHelper.isNamed(ruleSymbol)>
        // e.g., scope.setName(ast.getName())
      <#else>
        scope.setName(ast.getName());
      </#if>
    }

    public void setLinkBetweenSpannedScopeAndNode(${scopeName} scope, AST${ruleSymbol.getName()} astNode) {
      // scope -> ast
      scope.setAstNode(astNode);

      // ast -> scope
      astNode.setSpannedScope((${grammarName}Scope) scope);
    }
  </#if>
</#list>

<#list kinds as kind>
  public void addToScope(${kind}Symbol symbol) {
    if (!(symbol instanceof ISymbolReference)) {
      if (getCurrentScope().isPresent()) {
        getCurrentScope().get().add(symbol);
      } else {
        Log.warn("0xA50212 Symbol cannot be added to current scope, since no scope exists.");
      }
    }
  }
</#list>
}
