<#--
***************************************************************************************
Copyright (c) 2015, MontiCore
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
***************************************************************************************
-->
${signature("className", "directSuperCds", "rules", "hcPath")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign topAstName = genHelper.getQualifiedStartRuleName()>
<#assign astPrefix = fqn + "._ast.AST">

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${package};

import de.se_rwth.commons.logging.Log;

import ${fqn}._visitor.${genHelper.getVisitorType()};
import ${fqn}._visitor.${genHelper.getDelegatorVisitorType()};
import ${fqn}._visitor.${genHelper.getCommonDelegatorVisitorType()};
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import java.util.Deque;

public class ${className} extends de.monticore.symboltable.CommonSymbolTableCreator
         implements ${grammarName}Visitor {

  // TODO doc
  private final ${genHelper.getDelegatorVisitorType()} visitor = new ${genHelper.getCommonDelegatorVisitorType()}();

  public ${className}(
    final ResolverConfiguration resolvingConfig, final MutableScope enclosingScope) {
    super(resolvingConfig, enclosingScope);
    initSuperSTC();
  }

  public ${className}(final ResolverConfiguration resolvingConfig, final Deque<MutableScope> scopeStack) {
    super(resolvingConfig, scopeStack);
    initSuperSTC();
  }

  private void initSuperSTC() {
    // TODO doc
    <#list directSuperCds as cd>
      <#assign delegate = genHelper.getQualifiedVisitorNameAsJavaName(cd)>
      <#assign delegateType = genHelper.getQualifiedVisitorType(cd)>
      // visitor.set_${delegate}(${genHelper.getQualifiedSymTabCreator(cd.getFullName())}(resolvingConfig, scopeStack));
    </#list>
  }

  /**
  * Creates the symbol table starting from the <code>rootNode</code> and
  * returns the first scope that was created.
  *
  * @param rootNode the root node
  * @return the first scope that was created
  */
  public Scope createFromAST(${astPrefix}${grammarName}Node rootNode) {
    Log.errorIfNull(rootNode, "0xA7004${genHelper.getGeneratedErrorCode(ast)} Error by creating of the ${className} symbol table: top ast node is null");
    rootNode.accept(realThis);
    return getFirstCreatedScope();
  }

  private ${genHelper.getVisitorType()} realThis = this;

  public ${genHelper.getVisitorType()} getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(${genHelper.getVisitorType()} realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }

<#list rules as r>
  <#assign ruleName = r.getName()>
  <#assign ruleNameLower = r.getName()?uncap_first>
  <#assign symbolName = ruleName + "Symbol">
  <#if genHelper.isScopeSpanningSymbol(r)>
  ${includeArgs("symboltable.symboltablecreators.ScopeSpanningSymbolMethods", r)}
  <#elseif genHelper.isSymbol(r)>
  ${includeArgs("symboltable.symboltablecreators.SymbolMethods", r)}
  <#elseif genHelper.spansScope(r)>
  @Override
  public void visit(${astPrefix}${ruleName} ast) {
    MutableScope scope = create_${ruleName}(ast);
    initialize_${ruleName}(scope, ast);
    putOnStack(scope);
    setLinkBetweenSpannedScopeAndNode(scope, ast);
  }

  protected MutableScope create_${ruleName}(${astPrefix}${ruleName} ast) {
    <#if !genHelper.isNamed(r)>
    // creates new visibility scope
    return new de.monticore.symboltable.CommonScope(false);
    <#else>
    // creates new shadowing scope
    return new de.monticore.symboltable.CommonScope(true);
    </#if>
  }

  protected void initialize_${ruleName}(MutableScope scope, ${astPrefix}${ruleName} ast) {
    <#if !genHelper.isNamed(r)>
    // e.g., scope.setName(ast.getName())
    <#else>
    scope.setName(ast.getName());
    </#if>
  }

  @Override
  public void endVisit(${astPrefix}${ruleName} ast) {
    removeCurrentScope();
  }
  </#if>

</#list>

}
