<#-- (c) https://github.com/MontiCore/monticore -->
<#assign changeObject = ast>
<#if !changeObject.attributeIterated>
  // not iterated
  <#if hierarchyHelper.isLhsListChild(changeObject.getObjectName())>
  protected java.util.Map<
    <#if changeObject.isPresentObjectType()>${changeObject.getObjectType()}
    <#else>ASTNode
    </#if>,
    <#if changeObject.isPresentBoxingType()>${changeObject.getBoxingType()}
    <#else>${changeObject.getType()}
    </#if>> _${changeObject.getObjectName()}_${changeObject.getAttributeName()}__before;
  <#else>
  protected ${changeObject.getType()} _${changeObject.getObjectName()}_${changeObject.getAttributeName()}__before;
  </#if>
  <#if changeObject.composite && changeObject.isPresentValue()&& !changeObject.isCopy()>
  // composite, value is present
  protected ASTNode _${changeObject.getObjectName()}_${changeObject.getAttributeName()}__before_parent;
  protected ASTNode _${changeObject.getValue()}__before;
  protected ASTNode _${changeObject.getValue()}__before_parent;
  protected int _${changeObject.getValue()}__before_in_List = -1;
  </#if>
<#elseif changeObject.isPresentValue()&& !changeObject.isCopy()>
  // iterated, value is present
  <#if hierarchyHelper.isLhsListChild(changeObject.getValue())>
  protected java.util.List<${changeObject.getType()}> _${changeObject.getValue()}__before;
  protected java.util.Map<${changeObject.getType()}, ASTNode> _${changeObject.getValue()}__before_parent;
  protected java.util.Map<${changeObject.getType()}, Integer> _${changeObject.getValue()}__before_in_List;
  <#else>
  protected ${changeObject.getType()} _${changeObject.getValue()}__before;
  protected ASTNode _${changeObject.getValue()}__before_parent;
  protected int _${changeObject.getValue()}__before_in_List = -1;
  </#if>
</#if>
