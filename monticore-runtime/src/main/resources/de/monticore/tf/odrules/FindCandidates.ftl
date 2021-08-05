<#-- (c) https://github.com/MontiCore/monticore -->
<#list hierarchyHelper.getMandatoryMatchObjects(ast.getPattern().getMatchingObjectsList()) as object>
  <#if !object.isListObject()>

private List<ASTNode> find_${object.getObjectName()}_candidates(){
  <#--This can only be applied on non not-objects-->
    <#if !object.isNotObject()>
  // test if object is set fix
  if (is_${object.getObjectName()}_fix) {
    // if object is set return only the set candidates
    return ${object.getObjectName()}_candidates;
  }
    </#if>
  <#--tests if ODlinks are used-->
    <#if (ast.getPattern().getLinkConditionsList()?size >0)>
    <#--creates an if-statement for each condition for the object the method is created for-->
      <#list ast.getPattern().getLinkConditionsList() as link>
        <#if link.getObjectName() == object.getObjectName()>

    if(${link.getDependency().getContent()}_cand != null){
        <#-- for ODlinks of type link-->
          <#if link.getLinktype() == "link">
      if(${link.getConditionString()} == null){
        return new ArrayList<>();
      } else {
        return ${link.getConditionString()};
      }
    }
          </#if>
        <#--for ODLinks of type composition-->
          <#if link.getLinktype() = "composition" >
            ${link.getConditionString()}
    }
          </#if>
        </#if>
      </#list>
    </#if>
    return ${object.getObjectName()}_candidates;
  }

  </#if>
</#list>
