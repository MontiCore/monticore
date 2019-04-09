<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "existsHW")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign skipSTGen = glex.getGlobalVar("skipSTGen")>
<#assign symbols = genHelper.getAllSymbolDefiningRules()>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${package};

import java.util.LinkedHashSet;
import java.util.Set;
import de.monticore.IModelingLanguage;

<#if !stHelper.getGrammarSymbol().isComponent()>
import ${fqn}._parser.${grammarName}Parser;
</#if>

public abstract class ${className} implements IModelingLanguage<${grammarName}ModelLoader> {
  
  private ${grammarName}ModelLoader modelLoader;
  
  private String name;
  
  private String fileEnding;

  public ${className}(String langName, String fileEnding) {
    this.name = langName;
    this.fileEnding = fileEnding;
    this.modelLoader = provideModelLoader();
  }

<#if !stHelper.getGrammarSymbol().isComponent()>
  @Override
  public ${grammarName}Parser getParser() {
    return new ${grammarName}Parser();
  }
</#if>

<#if !skipSTGen>
  public ${grammarName}SymbolTableCreator getSymbolTableCreator(I${grammarName}Scope enclosingScope) {
    return new ${grammarName}SymbolTableCreator(enclosingScope);
  }
</#if>

  public ${grammarName}ModelLoader getModelLoader() {
     return modelLoader;
   }
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public String getFileExtension() {
    return fileEnding;
  }

  <#if existsHW>  
  abstract protected ${grammarName}ModelLoader provideModelLoader();
  <#else>
  protected ${grammarName}ModelLoader provideModelLoader() {
    return new ${grammarName}ModelLoader(this);
  }
  </#if>
  
<#list symbols as symbol>
  protected Set<String> calculateModelNamesFor${symbol.name}(String name) {
    final Set<String> modelNames = new LinkedHashSet<>();
    modelNames.add(name);
    return modelNames;
  }
  
</#list>

}
