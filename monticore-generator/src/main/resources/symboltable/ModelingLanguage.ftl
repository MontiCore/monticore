<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "ruleNames", "existsHW")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign skipSTGen = glex.getGlobalVar("skipSTGen")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${package};

import java.util.Optional;
<#if !stHelper.getGrammarSymbol().isComponent()>
import ${fqn}._parser.${grammarName}Parser;
</#if>
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

public abstract class ${className} extends de.monticore.CommonModelingLanguage {

  public ${className}(String langName, String fileEnding) {
    super(langName, fileEnding);

    initResolvingFilters();
<#if !skipSTGen>
    setModelNameCalculator(new ${grammarName}ModelNameCalculator());
</#if>
  }

  <#if !stHelper.getGrammarSymbol().isComponent()>
  @Override
  public ${grammarName}Parser getParser() {
    return new ${grammarName}Parser();
  }
  </#if>

  <#if !skipSTGen>
  @Override
  public Optional<${grammarName}SymbolTableCreator> getSymbolTableCreator(
      ResolvingConfiguration resolvingConfiguration, MutableScope enclosingScope) {
    return Optional.of(new ${grammarName}SymbolTableCreator(resolvingConfiguration, enclosingScope));
  }
  </#if>

  @Override
  public ${grammarName}ModelLoader getModelLoader() {
    return (${grammarName}ModelLoader) super.getModelLoader();
  }

  <#if existsHW>/*</#if>
  @Override
  protected ${grammarName}ModelLoader provideModelLoader() {
    return new ${grammarName}ModelLoader(this);
  }
  <#if existsHW>*/</#if>

  protected void initResolvingFilters() {
    <#list ruleNames as ruleName>
    addResolvingFilter(new ${ruleName?cap_first}ResolvingFilter());
    </#list>
  }
}
