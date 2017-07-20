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
${signature("className")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign topAstName = genHelper.getQualifiedStartRuleName()>
<#assign skipSTGen = glex.getGlobalVar("skipSTGen")>

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

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
