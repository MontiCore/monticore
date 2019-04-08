<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "languageName")}

<#assign genHelper = glex.getGlobalVar("stHelper")>


<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import de.monticore.io.paths.ModelPath;
import java.util.Optional;
import de.se_rwth.commons.logging.Log;

public class ${className} implements I${className} {

  protected ModelPath modelPath;
  
  protected ${languageName}Language ${languageName?lower_case}Language;
  
  public ${className}(ModelPath modelPath, ${languageName}Language ${languageName?lower_case}Language) {
    this.modelPath = Log.errorIfNull(modelPath);
    this.${languageName?lower_case}Language = Log.errorIfNull(${languageName?lower_case}Language);
  }
  
  public ${className}(ModelPath modelPath) {
    this(modelPath, new ${languageName}Language());
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
