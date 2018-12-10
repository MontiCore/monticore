<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "symbolName")}
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

  protected String name;

  protected ${className}() {}

  public ${symbolName}Symbol build() {
    return new ${symbolName}Symbol(name);
  }

  public ${className} name(String name) {
    this.name = name;
    return this;
  }
}