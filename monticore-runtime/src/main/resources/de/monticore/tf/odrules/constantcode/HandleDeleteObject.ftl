<#-- (c) https://github.com/MontiCore/monticore -->
<#assign deleteObject = ast>
<#if hierarchyHelper.isLhsListChild(deleteObject.getName())>
  private java.util.List<${deleteObject.getType()}> _${deleteObject.getName()}__before;
  private java.util.Map<${deleteObject.getType()}, ASTNode> _${deleteObject.getName()}__before_parent;
  private java.util.Map<${deleteObject.getType()},Integer> _${deleteObject.getName()}__before_in_List;
<#else>
  private ${deleteObject.getType()} _${deleteObject.getName()}__before;
  private ASTNode _${deleteObject.getName()}__before_parent;
  private int _${deleteObject.getName()}__before_in_List = -1;
</#if>

<#list deleteObject.possibleParents?keys as possibleParent>
  <#list deleteObject.possibleParents[possibleParent] as possibleAttribute>
    <#if !deleteObject.isList()>
  private boolean _${deleteObject.getName()}__before_in_${possibleParent}_${possibleAttribute} = false;
    <#else>
  private java.util.Map<${deleteObject.getType()}, Boolean> _${deleteObject.getName()}__before_in_${possibleParent}_${possibleAttribute};
    </#if>
  </#list>
</#list>
