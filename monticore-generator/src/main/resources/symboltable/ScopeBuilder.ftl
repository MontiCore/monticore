<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;

  /**
    * Builder for {@link ${className}}.
    */

public class ${className}Builder {

  protected ${className}Builder() {}

  public ${className} build() {
    return new ${className}();
  }
}