<#-- (c) https://github.com/MontiCore/monticore -->
<#assign deleteObject = ast>
<#if hierarchyHelper.isLhsListChild(deleteObject.getName())>
  protected java.util.List<${deleteObject.getType()}> _${deleteObject.getName()}__before;
  protected java.util.Map<${deleteObject.getType()}, ASTNode> _${deleteObject.getName()}__before_parent;
  protected java.util.Map<${deleteObject.getType()},Integer> _${deleteObject.getName()}__before_in_List;
<#else>
  protected ${deleteObject.getType()} _${deleteObject.getName()}__before;
  protected ASTNode _${deleteObject.getName()}__before_parent;
  protected int _${deleteObject.getName()}__before_in_List = -1;
</#if>

<#list deleteObject.possibleParents?keys as possibleParent>
  <#list deleteObject.possibleParents[possibleParent] as possibleAttribute>
    <#if !deleteObject.isList()>
  protected boolean _${deleteObject.getName()}__before_in_${possibleParent}_${possibleAttribute} = false;
    <#else>
  protected java.util.Map<${deleteObject.getType()}, Boolean> _${deleteObject.getName()}__before_in_${possibleParent}_${possibleAttribute};
    </#if>
  </#list>
</#list>
