<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "ruleName")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign symbolName = ruleName?cap_first+"Symbol">

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class ${className} extends CommonResolvingFilter<${symbolName}> {

  public ${className}() {
    super(${symbolName}.KIND);
  }
}
