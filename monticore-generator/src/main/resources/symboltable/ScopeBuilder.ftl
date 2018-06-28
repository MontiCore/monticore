<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "builderName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;

  /**
    * Builder for {@link ${className}}.
    */

public class ${className} {

  protected ${className}() {}

  public ${builderName} build() {
    return new ${builderName}();
  }
}