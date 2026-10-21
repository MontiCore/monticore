<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleClassName")}


// composition points on an object not in a list
<#if ast.attributeIterated && !ast.isPresentValue()>
    // attribute is a list
    // no value was given -> undo deletion
    <#if ast.isOldValueWithinOpt()>if(m.${ast.getOldValue()}.isPresent()) {</#if>
    ${ast.getObjectGetter()}.${ast.getSetter()}(
        m.${ast.getObjectName()}_${ast.getOldValue()}_before_pos,
        ${ast.getOldValueGetter()});

    this.modelAccessor.notifyListModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", m.${ast.getObjectName()}_${ast.getOldValue()}_before_pos, ModificationOp.SET, null, ${ast.getObjectGetter()}.${ast.getGetter()}());
    this.modelAccessor.notifyNodeAttach(${ast.getOldValueGetter()}, ${ast.getObjectGetter()});

    <#if ast.isOldValueWithinOpt()>}</#if>

<#elseif ast.attributeIterated && !ast.copy >
    // attribute is a list
    <#if ast.isValueWithinOpt()>if(m.${ast.getValue()}.isPresent()) {</#if>
    // a value was given -> remove from new object
    <#assign valueName = ast.getValueGetter()?remove_beginning("m.")?remove_ending(".get()")>
    int ${valueName}_${ast.getAttributeName()}_valueIdx = ${ast.getObjectGetter()}.${ast.getGetter()}().indexOf(${ast.getValueGetter()});
    ${ast.getObjectGetter()}.${ast.getUnsetter()}(${valueName}_${ast.getAttributeName()}_valueIdx);

    this.modelAccessor.notifyListModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ${valueName}_${ast.getAttributeName()}_valueIdx, ModificationOp.UNSET, ${ast.getValueGetter()}, null);
    this.modelAccessor.notifyNodeDetach(${ast.getValueGetter()}, ${ast.getObjectGetter()});

    <#if ast.isValueWithinOpt()>}</#if>

<#elseif ast.attributeIterated && ast.copy >
    // attribute is a list
    // value was copied, remove copy
    <#if ast.isValueWithinOpt()>if(m.${ast.getValue()}.isPresent()) {</#if>

    <#assign valueName = ast.getValueGetter()?remove_beginning("m.")?remove_ending(".get()")>
    ${ast.getValueType()} ${valueName}_${ast.getAttributeName()}_value = m.${ast.getObjectName()}_${ast.getValue()}_before.keySet().iterator().next();
    int ${valueName}_${ast.getAttributeName()}_valueIdx = ${ast.getObjectGetter()}.${ast.getGetter()}().indexOf(${valueName}_${ast.getAttributeName()}_value);
    ${ast.getObjectGetter()}.${ast.getUnsetter()}(${valueName}_${ast.getAttributeName()}_valueIdx);

    this.modelAccessor.notifyListModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ${valueName}_${ast.getAttributeName()}_valueIdx, ModificationOp.UNSET, ${valueName}_${ast.getAttributeName()}_value, null);
    this.modelAccessor.notifyNodeDetach(${valueName}_${ast.getAttributeName()}_value, ${ast.getObjectGetter()});

    <#if ast.isValueWithinOpt()>}</#if>

<#elseif !ast.attributeIterated && !ast.isPresentValue()>
    // single attribute (no list)
    <#if ast.isOldValueWithinOpt()>if(m.${ast.getOldValue()}.isPresent()) {</#if>

    //undo deletion of a list or single object
    <#if ast.isAttributeOptional()>
        ${ast.getObjectGetter()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getOldValue()}_before);
        this.modelAccessor.notifyModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ModificationOp.SET, null, m.${ast.getObjectName()}_${ast.getOldValue()}_before);
    </#if>

    <#if ast.isOldValueWithinOpt()>}</#if>

<#elseif !ast.attributeIterated && ast.isPresentValue()>
    // single attribute (no list)
    // a different value was given, but change it back
    <#if ast.isValueWithinOpt()>if (m.${ast.getValue()}.isPresent()) {</#if>

    <#assign valueName = ast.getValueGetter()?remove_beginning("m.")?remove_ending(".get()")>
    ${ast.getValueType()} ${valueName}_${ast.getAttributeName()}_oldValue = (${ast.getValueType()}) ${ast.getObjectGetter()}.${ast.getGetter()}();

    this.modelAccessor.notifyNodeDetach(${valueName}_${ast.getAttributeName()}_oldValue, ${ast.getObjectGetter()});

    ${ast.getObjectGetter()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getValue()}_before);

    <#if ast.isAttributeOptional()>
        if(${ast.getObjectGetter()}.${ast.getGetIsPresent()}){
          this.modelAccessor.notifyModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ModificationOp.REPLACE, ${valueName}_${ast.getAttributeName()}_oldValue, ${ast.getObjectGetter()}.${ast.getGetter()}());
          this.modelAccessor.notifyNodeAttach(m.${ast.getObjectName()}_${ast.getValue()}_before, ${ast.getObjectGetter()});
        } else {
          this.modelAccessor.notifyModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ModificationOp.UNSET, ${valueName}_${ast.getAttributeName()}_oldValue, null);
        }
    <#else>
        this.modelAccessor.notifyModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ModificationOp.REPLACE, ${valueName}_${ast.getAttributeName()}_oldValue, ${ast.getObjectGetter()}.${ast.getGetter()}());
        this.modelAccessor.notifyNodeAttach(m.${ast.getObjectName()}_${ast.getValue()}_before, ${ast.getObjectGetter()});
    </#if>
    <#if ast.isValueWithinOpt()>}</#if>

</#if>
