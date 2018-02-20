<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "builder", "imports")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

<#list imports as import>
import ${import};
</#list>

public class ${className} {

  private static ${className} getMill() {
    if (mill == null) {
      mill = new ${className}();
    }
    return mill;
  }

  protected static ${className} mill = null;

  <#list builder?keys as attribute>
    ${tc.includeArgs("symboltable.mill.Attribute", [className, attribute])}
  </#list>

  protected ${className}() {}

  <#list builder?keys as attribute>
    <#assign builderName = builder[attribute]>
    ${tc.includeArgs("symboltable.mill.Method", [className, attribute, builderName])}
  </#list>

}