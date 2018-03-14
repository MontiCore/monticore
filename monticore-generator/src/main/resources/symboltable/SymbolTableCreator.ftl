<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "directSuperCds", "rules", "hcPath")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign topAstName = genHelper.getQualifiedStartRuleName()>
<#assign astPrefix = fqn + "._ast.AST">

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

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
