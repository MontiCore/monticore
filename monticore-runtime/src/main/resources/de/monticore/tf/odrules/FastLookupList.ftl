<#-- (c) https://github.com/MontiCore/monticore -->

  public void initializeFastLookupList() {
<#list ast.getPattern().getLHSObjectsList() as object>
<#if !object.isOptObject() && !object.isListObject()>
<#if object.isNotObject() || object.isOptObject() || object.isListObject()>
    ${object.getObjectName()}_candidates_temp = new FastLookupList<>(${object.getObjectName()}_candidates);
<#else>
    ${object.getObjectName()}_candidates_temp = new FastLookupList<>(${object.getObjectName()}_candidates);
</#if>
</#if>
</#list>
  }