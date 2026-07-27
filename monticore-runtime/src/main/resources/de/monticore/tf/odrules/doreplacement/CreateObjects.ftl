<#-- (c) https://github.com/MontiCore/monticore -->
// get all required LHS values
<#list ast.getReplacement().getRequirementsList() as requirement>
  ${requirement.type} _${requirement.getObject()}_${requirement.attribute} = m.${requirement.getObject()}.${requirement.getGetter()}();
</#list>

<#list ast.getReplacement().getCreateObjectsList() as create>
<#assign isWithinOpt = hierarchyHelper.isWithinOptionalStructure(create.getName())>
<#assign isWithinList = hierarchyHelper.isWithinListStructure(create.getName())>

  <#if !isWithinList>
  if (!is_${create.getName()}_fix) {
    <#if create.getFactoryName() != "__missing">
      List<Consumer<ASTNode>> delayedAttachmentNotifications = new ArrayList<>();
      ${create.getType()}Builder builder = ${create.getFactoryName()}.${create.getSimpleType()?keep_after("AST")?uncap_first}Builder();
      <#list ast.getReplacement().getChangesList() as change>
        <#if change.getObjectName() == create.getName()>
          <#if change.isPresentValue()>
            <#if change.isPrimitiveType()>
              <#assign changeGetValue = change.getValue()>
            <#elseif change.isValueStringList()>
              <#assign changeGetValue = change.getValue()>
            <#else>
              <#assign changeGetValue = "m.${change.getValue()}">
              <#if hierarchyHelper.isWithinOptionalStructure(change.getObjectName()) || change.valueWithinOpt>
                if(${changeGetValue}.isPresent())
                <#assign changeGetValue += ".get()">
              </#if>
            </#if>
      <#-- In case we attach an AST node to a newly created object, the ModelAccessor must be notified
           However this can only happen, after the new parent node is fully constructed by calling
           the build method.
           To circumvent this, we delay the notifications by storing them in an intermediate supplier
           that is applied after the object is fully constructed. -->
            <#if change.isCopy()>
      ${change.getValueType()} cloneObj = ${changeGetValue}.deepClone();
      builder.${change.getSetter()}(cloneObj);
      delayedAttachmentNotifications.add(p -> this.modelAccessor.notifyModification(p, "${change.getAttributeName()}", ModificationOp.SET, null, cloneObj));
      delayedAttachmentNotifications.add(p -> this.modelAccessor.notifyNodeAttach(cloneObj, p));
            <#else>
      builder.${change.getSetter()}(${changeGetValue});
      delayedAttachmentNotifications.add(p -> this.modelAccessor.notifyModification(p, "${change.getAttributeName()}", ModificationOp.SET, null, ${changeGetValue}));
              <#if hierarchyHelper.isCreatedObject(ast.getReplacement(), change.getValue())>
      delayedAttachmentNotifications.add(p -> this.modelAccessor.notifyNodeAttach(${changeGetValue}, p));
              </#if>
            </#if>
          <#else>
      builder.${change.getSetter()}();
          </#if>
        </#if>
      </#list>
      m.${create.getName()} = <#if isWithinOpt>Optional.of(</#if>builder.build()<#if isWithinOpt>)</#if>;
      delayedAttachmentNotifications.forEach(n -> n.accept(m.${create.getName()}<#if isWithinOpt>.get()</#if>));
  <#else>
    // TODO: There exists no builder for ${create.getType()}s - check if this is set from external
  </#if>
  } else {
    m.${create.getName()} = <#if isWithinOpt>Optional.of(</#if>(${create.getType()}) ${create.getName()}_candidates.get(0)<#if isWithinOpt>)</#if>;
  }
  <#else>
  <#assign listParent = hierarchyHelper.getListParent(create.getName())>
  if (!is_${create.getName()}_fix) {
    for (Match${listParent} list : get_${listParent}()) {
      list.${create.getName()} = <#if isWithinOpt>Optional.of(</#if>${create.getFactoryName()}.create${create.getSimpleType()}()<#if isWithinOpt>)</#if>;
    }
  } else {
    for (Match${listParent} list : get_${listParent}()) {
      list.${create.getName()} = <#if isWithinOpt>Optional.of(</#if>(${create.getType()}) ${create.getName()}_candidates.get(get_${listParent}().indexOf(list))<#if isWithinOpt>)</#if>;
    }
  }
  </#if>
  </#list>
