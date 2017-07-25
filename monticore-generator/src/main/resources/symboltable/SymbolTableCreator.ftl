<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
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
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import java.util.Deque;

public class ${className} extends de.monticore.symboltable.CommonSymbolTableCreator
         implements ${grammarName}Visitor {

  public ${className}(
    final ResolvingConfiguration resolvingConfig, final MutableScope enclosingScope) {
    super(resolvingConfig, enclosingScope);
  }

  public ${className}(final ResolvingConfiguration resolvingConfig, final Deque<MutableScope> scopeStack) {
    super(resolvingConfig, scopeStack);
  }

  private void initSuperSTC() {
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
    }
  }

<#list rules as ruleSymbol>
  <#assign ruleName = ruleSymbol.getName()>
  <#assign ruleNameLower = ruleSymbol.getName()?uncap_first>
  <#assign symbolName = ruleName + "Symbol">
  <#if genHelper.isScopeSpanningSymbol(ruleSymbol)>
  ${includeArgs("symboltable.symboltablecreators.ScopeSpanningSymbolMethods", ruleSymbol)}
  <#elseif genHelper.isSymbol(ruleSymbol)>
  ${includeArgs("symboltable.symboltablecreators.SymbolMethods", ruleSymbol)}
  <#elseif genHelper.spansScope(ruleSymbol)>
  ${includeArgs("symboltable.symboltablecreators.ScopeMethods", ruleSymbol)}
  <#elseif genHelper.isStartRule(ruleSymbol)>
  @Override
  public void endVisit(${astPrefix}${ruleName} ast) {
    setEnclosingScopeOfNodes(ast);
  }
  </#if>
</#list>

}
