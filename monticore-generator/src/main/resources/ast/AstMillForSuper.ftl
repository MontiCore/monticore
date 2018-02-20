<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop", "astImports", "superClass")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign plainName = genHelper.getPlainName(ast, "")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getAstPackage()};

<#list astImports as astImport>
import ${astImport};
</#list>

public <#if isTop>abstract </#if> class ${ast.getName()} extends ${superClass} {
   

<#list ast.getCDMethodList() as method>
 ${tc.includeArgs("ast.ClassMethod", [method, ast])}
</#list>

}
