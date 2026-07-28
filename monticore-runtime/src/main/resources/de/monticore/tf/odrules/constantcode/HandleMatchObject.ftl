<#-- (c) https://github.com/MontiCore/monticore -->
<#assign matchObject = ast>
<#if matchObject.isListObject()>
  protected ${matchObject.getListtype()} ${matchObject.getObjectName()}_candidates, ${matchObject.getObjectName()}_candidates_temp;
  protected ${matchObject.getListtype()} ${matchObject.getObjectName()}_cand;
<#else>
  protected List<ASTNode> ${matchObject.getObjectName()}_candidates, ${matchObject.getObjectName()}_candidates_temp;
  protected ${matchObject.getType()} ${matchObject.getObjectName()}_cand;
</#if>
<#if !matchObject.isNotObject()>
  protected boolean is_${matchObject.getObjectName()}_fix = false;
</#if>
