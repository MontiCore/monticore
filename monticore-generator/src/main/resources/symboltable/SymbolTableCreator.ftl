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

import ${fqn}._visitor.${genHelper.getVisitorType()};
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import java.util.Deque;

public class ${className} extends de.monticore.symboltable.CommonSymbolTableCreator
         implements ${grammarName}Visitor {

  public ${className}(
    final ResolvingConfiguration resolvingConfig, final Scope enclosingScope) {
    super(resolvingConfig, enclosingScope);
  }

  public ${className}(final ResolvingConfiguration resolvingConfig, final Deque<Scope> scopeStack) {
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
    public Scope createFromAST(${topAstName} rootNode) {
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
