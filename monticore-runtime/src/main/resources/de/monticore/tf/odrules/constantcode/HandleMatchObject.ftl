<#-- (c) https://github.com/MontiCore/monticore -->
<#assign matchObject = ast>
<#if matchObject.isListObject()>
  private ${matchObject.getListtype()} ${matchObject.getObjectName()}_candidates, ${matchObject.getObjectName()}_candidates_temp;
  private ${matchObject.getListtype()} ${matchObject.getObjectName()}_cand;
<#else>
  private List<ASTNode> ${matchObject.getObjectName()}_candidates, ${matchObject.getObjectName()}_candidates_temp;
  private ${matchObject.getType()} ${matchObject.getObjectName()}_cand;
</#if>
<#if !matchObject.isNotObject()>
  private boolean is_${matchObject.getObjectName()}_fix = false;
</#if>
