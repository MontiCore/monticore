<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "directSuperCds", "rules", "hcPath")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign topAstName = genHelper.getQualifiedStartRuleName()>

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

  protected Deque<I${grammarName}Scope> scopeStack = new ArrayDeque<>();
  
  /**
   * The first scope OTHER THAN THE GLOBAL SCOPE that has been created, i.e., added  to the scope
   * stack. This information helps to determine the top scope within this creation process.
   */
  protected I${grammarName}Scope firstCreatedScope;
  
  private ${grammarName}Visitor realThis = this;

  public ${className}(
    final IAutomataScope enclosingScope) {
  
    putOnStack(Log.errorIfNull(enclosingScope));
  }
  
  public ${className}(final Deque<I${grammarName}Scope> scopeStack) {
    this.scopeStack = Log.errorIfNull(scopeStack);
  }

  /**
    * Creates the symbol table starting from the <code>rootNode</code> and
    * returns the first scope that was created.
    *
    * @param rootNode the root node
    * @return the first scope that was created
    */
  public I${grammarName}Scope createFromAST(${topAstName} rootNode) {
    Log.errorIfNull(rootNode, "0xA7004${genHelper.getGeneratedErrorCode(ast)} Error by creating of the ${className} symbol table: top ast node is null");
    rootNode.accept(realThis);
    return getFirstCreatedScope();
  }
  
  public I${grammarName}Scope getFirstCreatedScope() {
    return firstCreatedScope;
  }
  
  public void putOnStack(I${grammarName}Scope scope) {
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

<#list rules as ruleSymbol>
  <#assign ruleName = ruleSymbol.getName()>
  <#assign symbolName = genHelper.getQualifiedProdName(ruleSymbol) + "Symbol">
  <#assign astName = genHelper.getQualifiedASTName(ruleSymbol)>

  <#if genHelper.isScopeSpanningSymbol(ruleSymbol)>
    ${includeArgs("symboltable.symboltablecreators.ScopeSpanningSymbolMethods", ruleSymbol, symbolName, ruleName, astName)}
  <#elseif genHelper.isSymbol(ruleSymbol)>
    ${includeArgs("symboltable.symboltablecreators.SymbolMethods", ruleSymbol, symbolName, ruleName, astName)}
  <#elseif genHelper.spansScope(ruleSymbol)>
    ${includeArgs("symboltable.symboltablecreators.ScopeMethods", ruleSymbol, ruleName, astName)}
  <#elseif genHelper.isStartRule(ruleSymbol)>
  @Override
  public void endVisit(${astName} ast) {
    setEnclosingScopeOfNodes(ast);
  }
  </#if>
</#list>

    }
