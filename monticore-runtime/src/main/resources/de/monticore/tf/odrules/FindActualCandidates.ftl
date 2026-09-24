<#-- (c) https://github.com/MontiCore/monticore -->
  protected void findActualCandidates(String peek) {
  <#-- for each object create a methodcall to updates candidates-->
    //update the candidates for the object with the name that is saved in peek
    switch(peek) {
  <#list hierarchyHelper.getMandatoryMatchObjects(ast.getPattern().getLHSObjectsList()) as object><#if !object.isListObject()>
      case "${object.getObjectName()}" -> ${object.getObjectName()}_candidates = find_${object.getObjectName()}_candidates();
  </#if>
  </#list>
    }
  }
