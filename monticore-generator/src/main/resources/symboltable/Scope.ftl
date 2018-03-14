<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className")}

<#assign genHelper = glex.getGlobalVar("stHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;

import de.monticore.symboltable.MutableScope;

public class ${className} extends de.monticore.symboltable.CommonScope {

  public ${className}() {
    super();
  }

  public ${className}(boolean isShadowingScope) {
    super(isShadowingScope);
  }

  public ${className}(Optional<MutableScope> enclosingScope) {
    super(enclosingScope, true);
  }
}
