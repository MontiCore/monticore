<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java interface
  
  @params    ASTCDInterface $ast
  @result    mc.javadsl.JavaDSL.CompilationUnit
  
-->
${tc.signature("visitorPackage", "visitorType")}

<#assign genHelper = glex.getGlobalVar("astHelper")>

import ${visitorPackage}.${visitorType};
import java.util.Optional;

public interface ${ast.getName()} extends ${tc.include("ast.AstExtendedInterfaces")} ${genHelper.getASTNodeBaseType()} {
  <#-- generate all methods -->
  <#list ast.getCDMethodList() as method>
    ${tc.includeArgs("ast.InterfaceMethod", [method, ast])}
  </#list>

  public ${genHelper.getPlainName(ast)} deepClone();

  public boolean equals(Object o);

  public boolean equalsWithComments(Object o);

  public boolean deepEquals(Object o);
  
  public boolean deepEquals(Object o, boolean forceSameOrder);

  public boolean deepEqualsWithComments(Object o);

  public boolean deepEqualsWithComments(Object o, boolean forceSameOrder);
  
  public void accept(${visitorType} visitor);
   
  <#-- member HOOK -->
  ${defineHookPoint("<Block>?InterfaceContent:Members")}
}
