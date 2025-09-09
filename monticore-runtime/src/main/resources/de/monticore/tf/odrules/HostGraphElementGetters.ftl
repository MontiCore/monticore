<#-- (c) https://github.com/MontiCore/monticore -->
<#assign mandatoryObjects = hierarchyHelper.getMandatoryObjectsWithoutOptAndListChilds(ast.getPattern().getMatchingObjectsList())>
<#list mandatoryObjects as object>
  // ${object.getObjectName()}
  <#if !object.isListObject()>
    <#if hierarchyHelper.isWithinOptionalStructure(object.getObjectName())>
    public Optional<${object.getType()}>  get_${object.getObjectName()}() {
      return getMatches().get(0).${object.getObjectName()};
    }
    <#else>
    public ${object.getType()} get_${object.getObjectName()}() {
      return getMatches().get(0).${object.getObjectName()};
    }
    </#if>
  </#if>
</#list>
<#list hierarchyHelper.getListObjects(ast.getPattern().getMatchingObjectsList()) as list>
// ${list.getObjectName()}
<#-- ListObjects and their childs-->
  <#assign ListTree = hierarchyHelper.getListTree(list.getObjectName())>
  <#if hierarchyHelper.isWithinOptionalStructure(list.getObjectName())>
    public Optional<List<Match${list.getObjectName()}>> get_${list.getObjectName()}() {
    <#if !hierarchyHelper.isListChild(list)>
        return getMatches().get(0).${list.getObjectName()};
    <#else>
        List<Match${list.getObjectName()}> ${list.getObjectName()} = new ArrayList<Match${list.getObjectName()}>();
      <#assign currentlevel = "getMatches().get(0)">
      <#list ListTree as level>
        if(${currentlevel}.${level}.isPresent())
          for (Match${level} ${level}: ${currentlevel}.${level}.get()) {
        <#assign currentlevel = level>
      </#list>
            if(${currentlevel}.${list.getObjectName()}.isPresent()) {
      ${list.getObjectName()}.add(${currentlevel}.${list.getObjectName()}.get());
            }
      <#list ListTree as level>
          }
        }
      </#list>
      if (!${list.getObjectName()}.isEmpty()) {
        return Optional.ofNullable(${list.getObjectName()});
      } else { return Optional.ofNullable(null); }
    </#if>
    }
    <#assign listchilds = hierarchyHelper.getListChilds(ast.getPattern().getMatchingObjectsList(), list)>
    <#list listchilds as listchild>
    public Optional<List<${listchild.getType()}>> get_${listchild.getObjectName()}() {
      List<${listchild.getType()}> ${listchild.getObjectName()} = new ArrayList<${listchild.getType()}>();
      <#assign currentlevel = "getMatches().get(0)">
      <#list hierarchyHelper.getListTree(listchild.getObjectName()) as level>
      if (${currentlevel}.${level}.isPresent()) {
        for (Match${level} ${level}: ${currentlevel}.${level}.get()) {
        <#assign currentlevel = level>
      </#list>
        if(${currentlevel}.${listchild.getObjectName()}.isPresent()) {
      ${listchild.getObjectName()}.add(${currentlevel}.${listchild.getObjectName()}.get());
        }
      <#list hierarchyHelper.getListTree(listchild.getObjectName()) as level>
        }
      }
      </#list>
      if(!${listchild.getObjectName()}.isEmpty()) {
        return Optional.ofNullable(${listchild.getObjectName()});
      } else { return Optional.ofNullable(null); }
    }
    </#list>
  <#else>
    public List<Match${list.getObjectName()}> get_${list.getObjectName()}() {
    <#if !hierarchyHelper.isListChild(list)>
        return getMatches().get(0).${list.getObjectName()};
    <#else>
        <List<Match${list.getObjectName()}> ${list.getObjectName()} = new ArrayList<Match${list.getObjectName()}>();
      <#assign currentlevel = "getMatches().get(0)">
      <#list ListTree as level>
          for (Match${level} ${level}: ${currentlevel}.${level}) {
        <#assign currentlevel = level>
      </#list>
      ${list.getObjectName()}.add(${currentlevel}.${list.getObjectName()});
      <#list ListTree as level>
          }
      </#list>
        return ${list.getObjectName()};
    </#if>
    }

    <#assign listchilds = hierarchyHelper.getListChilds(ast.getPattern().getMatchingObjectsList(), list)>
    <#list listchilds as listchild>
      <#if hierarchyHelper.isWithinOptionalStructure(listchild.getObjectName())>
        public List
          <Optional<${listchild.getType()}>> get_${listchild.getObjectName()}() {
        List
          <Optional<${listchild.getType()}>> ${listchild.getObjectName()} = new ArrayList
          <Optional<${listchild.getType()}>>();
      <#else>
      public List<${listchild.getType()}> get_${listchild.getObjectName()}() {
        List<${listchild.getType()}> ${listchild.getObjectName()} = new ArrayList<${listchild.getType()}>();
      </#if>
      <#assign currentlevel = "getMatches().get(0)">
      <#list hierarchyHelper.getListTree(listchild.getObjectName()) as level>
        for (Match${level} ${level}: ${currentlevel}.${level}) {
        <#assign currentlevel = level>
      </#list>
      ${listchild.getObjectName()}.add(${currentlevel}.${listchild.getObjectName()});
      <#list hierarchyHelper.getListTree(listchild.getObjectName()) as level>
        }
      </#list>
        return ${listchild.getObjectName()};
      }
    </#list>
  </#if>
</#list>

<#list ast.getVariableList() as variable>
  <#if hierarchyHelper.isWithinOptionalStructure(variable.getName())>
  public Optional<${variable.getType()}> get_${variable.getName()}() {
      return this.${variable.getName()};
  }
  <#else>
  public ${variable.getType()} get_${variable.getName()}() {
      return this.${variable.getName()};
  }
  </#if>
</#list>
  
