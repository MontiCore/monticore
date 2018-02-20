<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java class
  
  @params    ASTCDClass $ast
             ASTCDClass $astBuilder
  @result    mc.javadsl.JavaDSL.CompilationUnit
  
-->
${tc.signature("ast")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  
<#-- set package -->
package ${genHelper.getAstPackage()};

<#-- handle imports from model -->
${tc.include("ast.AstImports")}

${ast.printModifier()} class ${ast.getName()} extends ${tc.include("ast.AstSuperTypes")} {
  <#-- generate all attributes -->  
  <#list ast.getCDAttributeList() as attribute>
    <#if !genHelper.isInherited(attribute)>
  ${tc.includeArgs("ast.Attribute", [attribute, ast])}
    </#if>
  </#list>
  <#-- generate all constructors -->  
  <#list ast.getCDConstructorList() as constr>
    ${tc.includeArgs("ast.Constructor", [constr, ast])}
  </#list>
  
  <#-- generate all methods -->
  <#list ast.getCDMethodList() as method>
    ${tc.includeArgs("ast.ClassMethod", [method, ast])}
  </#list>
  
}
