<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleVisitorName", "superList")}
<#assign reversedList = superList?reverse>
<#list reversedList as superAST>
  getRealThis().visit((${superAST}) node);
</#list>
  ${simpleVisitorName}.super.handle(node);
<#list superList as superAST>
  getRealThis().endVisit((${superAST}) node);
</#list>
