<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java interface
  
  @params    ASTCDInterface $ast
  @result    mc.javadsl.JavaDSL.CompilationUnit
  
-->
${tc.signature("visitorPackage", "visitorType")}

<#assign genHelper = glex.getGlobalVar("astHelper")>
${defineHookPoint("InterfaceContent:addComment")}
<#-- set package -->
package ${genHelper.getAstPackage()};
<#-- Imports hook --> 
${defineHookPoint("<Block>?InterfaceContent:addImports")}

${tc.includeArgs("ast.AstInterfaceContent", [visitorPackage, visitorType])}

