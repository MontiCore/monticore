<#-- (c)  https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop", "astImports")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign plainName = genHelper.getPlainName(ast, "")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getAstPackage()};

<#list astImports as astImport>
import ${astImport};
</#list>

public <#if isTop>abstract </#if> class ${ast.getName()} {

  protected static ${plainName} getMill() {
    if (mill == null) {
      mill = new ${plainName}();
    }
    return mill;
  }
  
  protected static ${plainName} mill = null;

  public static void initMe(${plainName} a) {
    mill = a;
    <#list ast.getCDAttributeList() as attribute>
      <#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
      ${attributeName} = a;
    </#list>
  }

<#list ast.getCDAttributeList() as attribute>
 ${tc.includeArgs("ast.Attribute", [attribute, ast])}
</#list>

  protected ${ast.getName()} () {}

<#list ast.getCDMethodList() as method>
 ${tc.includeArgs("ast.ClassMethod", [method, ast])}
</#list>

}
