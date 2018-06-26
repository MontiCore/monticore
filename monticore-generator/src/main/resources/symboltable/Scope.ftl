<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","scopeRule")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign superClass = " extends de.monticore.symboltable.CommonScope ">
<#assign superInterfaces = "">
<#if scopeRule.isPresent()>
  <#if !scopeRule.get().isEmptySuperInterfaces()>
    <#assign superInterfaces = "implements " + stHelper.printGenericTypes(scopeRule.get().getSuperInterfaceList())>
  </#if>
  <#if !scopeRule.get().isEmptySuperClasss()>
    <#assign superClass = " extends " + stHelper.printGenericTypes(scopeRule.get().getSuperClassList())>
  </#if>
</#if>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;

import de.monticore.symboltable.MutableScope;

public class ${className} ${superClass} ${superInterfaces} {

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
