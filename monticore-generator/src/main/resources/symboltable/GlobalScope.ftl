<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "languageName", "interfaceName", "hasHWC")}

<#assign genHelper = glex.getGlobalVar("stHelper")>


<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import de.monticore.io.paths.ModelPath;
import java.util.Optional;
import de.se_rwth.commons.logging.Log;

public <#if hasHWC>abstract</#if> class ${className} extends ${languageName}Scope implements ${interfaceName} {

  protected ModelPath modelPath;

  protected ${languageName}Language ${languageName?lower_case}Language;

  public ${className}(ModelPath modelPath, ${languageName}Language ${languageName?lower_case}Language) {
    this.modelPath = Log.errorIfNull(modelPath);
    this.${languageName?lower_case}Language = Log.errorIfNull(${languageName?lower_case}Language);
  }


  public ModelPath getModelPath() {
    return modelPath;
  }

  public ${languageName}Language get${languageName}Language() {
    return ${languageName?lower_case}Language;
  }

  @Override
  public Optional<String> getName() {
    return Optional.empty();
  }

}
