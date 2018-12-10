<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","scopeRule", "symbolNames")}

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
  
  <#list symbolNames?keys as symbol>
  public Optional<${symbolNames[symbol]}> resolve${symbol}(String name) {
    return resolve(name, ${symbolNames[symbol]}.KIND);
  }
  
  </#list>
  
  <#if scopeRule.isPresent()>
    <#list scopeRule.get().getAdditionalAttributeList() as attr>
      <#assign attrName=attr.getName()>
      <#assign attrType=attr.getGenericType().getTypeName()>
      private ${genHelper.getQualifiedASTName(attrType)} ${attrName};
    </#list>

    <#list scopeRule.get().getMethodList() as meth>
      ${genHelper.printMethod(meth)}
    </#list>
  </#if>
}
