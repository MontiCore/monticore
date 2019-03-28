<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "scopeName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;

  /**
    * Builder for {@link ${scopeName}}.
    */

public class ${className} {

  protected ${className}() {}

  public ${scopeName} build() {
    return new ${scopeName}();
  }
}