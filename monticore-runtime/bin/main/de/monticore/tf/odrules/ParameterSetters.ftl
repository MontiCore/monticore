<#-- (c) https://github.com/MontiCore/monticore -->
<#list hierarchyHelper.getMandatoryMatchObjects(ast.getPattern().getMatchingObjectsList()) as object>
  <#if !object.isNotObject() && !object.isListObject() && !hierarchyHelper.isListChild(object)>
    public void set_${object.getObjectName()}(${object.getType()} node) {
      if (node != null) {
        this.${object.getObjectName()}_candidates = new ArrayList<>(1);
    ${object.getObjectName()}_candidates.add(node);
        this.is_${object.getObjectName()}_fix = true;
      }
    }
  </#if>
  <#if object.isListObject() && !hierarchyHelper.isListChild(object)>
    public void set_${object.getObjectName()}(List<Match${object.getObjectName()}> node) {
      if (node != null) {
        this.${object.getObjectName()}_candidates = new ArrayList<Match${object.getObjectName()}>(node);
        this.is_${object.getObjectName()}_fix = true;
      }
    }
  </#if>
  <#if !object.isNotObject() && hierarchyHelper.isListChild(object)>
    public void set_${object.getObjectName()} (List<${object.getType()}> nodes) {
      if(nodes != null && !nodes.isEmpty()) {
        this.${object.getObjectName()}_candidates = new ArrayList<>(nodes);
        this.is_${object.getObjectName()}_fix = true;
      }
    }
  </#if>
</#list>

<#list ast.getVariableList() as variable>
  public void set_${variable.getName()}(${variable.getType()} ${variable.getName()}) {
    this.${variable.getName()}_is_fix = true;
    this.${variable.getName()} = ${variable.getName()};
  }
</#list>
