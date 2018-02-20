<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign topAstName = genHelper.getQualifiedStartRuleName()>
<#assign skipSTGen = glex.getGlobalVar("skipSTGen")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${package};

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

<#if !skipSTGen>
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
</#if>

public class ${className} extends de.monticore.modelloader.ModelingLanguageModelLoader<${topAstName}> {

  public ${className}(${grammarName}Language language) {
    super(language);
  }

  @Override
  protected void createSymbolTableFromAST(final ${topAstName} ast, final String modelName,
    final MutableScope enclosingScope, final ResolvingConfiguration resolvingConfiguration) {
    <#if !skipSTGen>
    final ${grammarName}SymbolTableCreator symbolTableCreator =
            getModelingLanguage().getSymbolTableCreator(resolvingConfiguration, enclosingScope).orElse(null);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
          ${grammarName}ModelLoader.class.getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        Log.warn("0xA7001${genHelper.getGeneratedErrorCode(ast)} Top scope of model " + modelName + " is expected to be an artifact scope, but"
          + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", ${grammarName}ModelLoader.class.getSimpleName());
    }
    else {
      Log.warn("0xA7002${genHelper.getGeneratedErrorCode(ast)} No symbol created, because '" + getModelingLanguage().getName()
        + "' does not define a symbol table creator.");
    }
    </#if>
  }

  @Override
  public ${grammarName}Language getModelingLanguage() {
    return (${grammarName}Language) super.getModelingLanguage();
  }
}
