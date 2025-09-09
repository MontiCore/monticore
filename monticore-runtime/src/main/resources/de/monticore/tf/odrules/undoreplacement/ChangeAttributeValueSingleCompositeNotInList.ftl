<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleClassName")}


// composition points on an object not in a list
Reporting.reportTransformationObjectChange("${ruleClassName}",${ast.getObjectGetter()}, "${ast.getAttributeName()}");

<#if ast.attributeIterated && !ast.isPresentValue()>
    // attribute is a list
    // no value was given -> undo deletion
    <#if ast.isOldValueWithinOpt()>if(m.${ast.getOldValue()}.isPresent()) {</#if>

    if (${ast.getOldValueGetter()} != null) {
      Reporting.reportTransformationOldValue("${ruleClassName}",${ast.getOldValueGetter()});
    }
    ${ast.getObjectGetter()}.${ast.getSetter()}(
        m.${ast.getObjectName()}_${ast.getOldValue()}_before_pos,
        ${ast.getOldValueGetter()});

    <#if ast.isOldValueWithinOpt()>}</#if>

<#elseif ast.attributeIterated && !ast.copy >
    // attribute is a list
    <#if ast.isValueWithinOpt()>if(m.${ast.getValue()}.isPresent()) {</#if>
    // a value was given -> remove from new object

    ${ast.getObjectGetter()}.${ast.getUnsetter()}(
        ${ast.getValueGetter()}
    );

    <#if ast.isValueWithinOpt()>}</#if>

<#elseif ast.attributeIterated && ast.copy >
    // attribute is a list
    // value was copied, remove copy
    <#if ast.isValueWithinOpt()>if(m.${ast.getValue()}.isPresent()) {</#if>

    <#if ast.isPresentInsertPosition()>pos = ${ast.getInsertPosition()};</#if>

    ${ast.getObjectGetter()}.${ast.getUnsetter()}(
        m.${ast.getObjectName()}_${ast.getValue()}_before.keySet().iterator().next()
    );

    <#if ast.isValueWithinOpt()>}</#if>

<#elseif !ast.attributeIterated && !ast.isPresentValue()>
    // single attribute (no list)
    <#if ast.isOldValueWithinOpt()>if(m.${ast.getOldValue()}.isPresent()) {</#if>

    //undo deletion of a list or single object
    <#if ast.isAttributeOptional()>
        ${ast.getObjectGetter()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getOldValue()}_before);
    </#if>

    <#if ast.isOldValueWithinOpt()>}</#if>

<#elseif !ast.attributeIterated && ast.isPresentValue()>
    // single attribute (no list)
    // a different value was given, but change it back
    <#if ast.isValueWithinOpt()>if (m.${ast.getValue()}.isPresent()) {</#if>

    <#if ast.isAttributeOptional()>
        if(${ast.getObjectGetter()}.${ast.getGetIsPresent()}){
    </#if>

    if(${ast.getObjectGetter()}.${ast.getGetter()}() != null) {
        Reporting.reportTransformationOldValue("${ruleClassName}",${ast.getObjectGetter()}.${ast.getGetter()}());
    }

    <#if ast.isAttributeOptional()>}</#if>

    ${ast.getObjectGetter()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getValue()}_before);

    <#if ast.isValueWithinOpt()>}</#if>

</#if>
