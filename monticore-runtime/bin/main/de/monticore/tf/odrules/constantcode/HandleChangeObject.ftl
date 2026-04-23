<#-- (c) https://github.com/MontiCore/monticore -->
<#assign changeObject = ast>
<#if !changeObject.attributeIterated>
  // not iterated
  <#if hierarchyHelper.isLhsListChild(changeObject.getObjectName())>
  private java.util.Map<
    <#if changeObject.isPresentObjectType()>${changeObject.getObjectType()}
    <#else>ASTNode
    </#if>,
    <#if changeObject.isPresentBoxingType()>${changeObject.getBoxingType()}
    <#else>${changeObject.getType()}
    </#if>> _${changeObject.getObjectName()}_${changeObject.getAttributeName()}__before;
  <#else>
  private ${changeObject.getType()} _${changeObject.getObjectName()}_${changeObject.getAttributeName()}__before;
  </#if>
  <#if changeObject.composite && changeObject.isPresentValue()&& !changeObject.isCopy()>
  // composite, value is present
  private ASTNode _${changeObject.getObjectName()}_${changeObject.getAttributeName()}__before_parent;
  private ASTNode _${changeObject.getValue()}__before;
  private ASTNode _${changeObject.getValue()}__before_parent;
  private int _${changeObject.getValue()}__before_in_List = -1;
  </#if>
<#elseif changeObject.isPresentValue()&& !changeObject.isCopy()>
  // iterated, value is present
  <#if hierarchyHelper.isLhsListChild(changeObject.getValue())>
  private java.util.List<${changeObject.getType()}> _${changeObject.getValue()}__before;
  private java.util.Map<${changeObject.getType()}, ASTNode> _${changeObject.getValue()}__before_parent;
  private java.util.Map<${changeObject.getType()}, Integer> _${changeObject.getValue()}__before_in_List;
  <#else>
  private ${changeObject.getType()} _${changeObject.getValue()}__before;
  private ASTNode _${changeObject.getValue()}__before_parent;
  private int _${changeObject.getValue()}__before_in_List = -1;
  </#if>
</#if>
