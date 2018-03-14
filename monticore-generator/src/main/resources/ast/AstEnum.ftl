<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java interface
  
  @params    ASTCDEnum $ast
  @result    mc.javadsl.JavaDSL.CompilationUnit
  
-->
<#assign genHelper = glex.getGlobalVar("astHelper")>
${defineHookPoint("EnumContent:addComment")}
<#-- set package -->
package ${genHelper.getAstPackage()};
<#-- Imports hook --> 
${defineHookPoint("<Block>?EnumContent:addImports")}

<#assign interfaces = ast.printInterfaces()>

public enum ${ast.getName()}<#if interfaces?has_content> implements ${interfaces}</#if> {
  <#assign count = 0>
  <#list ast.getCDEnumConstantList() as constant>
    <#if count == 0>
      ${constant.getName()}(ASTConstants${genHelper.getCdName()}.${constant.getName()}) 
    <#else>
      ,${constant.getName()}(ASTConstants${genHelper.getCdName()}.${constant.getName()})
    </#if>
    <#assign count = count + 1>
  </#list>
  ;
 
  protected int intValue;
  
  private ${ast.getName()}(int intValue){
    this.intValue=intValue;
  }
  
  public int intValue() {
    return intValue;
  }
  
<#list ast.getCDMethodList() as method>
  ${tc.includeArgs("ast.ClassMethod", [method, ast])}
</#list>
}
