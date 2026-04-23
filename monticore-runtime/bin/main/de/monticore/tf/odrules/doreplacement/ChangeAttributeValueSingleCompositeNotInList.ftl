<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleClassName")}


// composition points on an object not in a list
Reporting.reportTransformationObjectChange("${ruleClassName}",${ast.getObjectGetter()}, "${ast.getAttributeName()}");

<#if ast.attributeIterated && !ast.isPresentValue()>
    // attribute is a list
    // no value is given -> deletion
    <#if ast.isOldValueWithinOpt()>if(m.${ast.getOldValue()}.isPresent()) {</#if>

    if (${ast.getOldValueGetter()} != null) {
      Reporting.reportTransformationOldValue("${ruleClassName}",${ast.getOldValueGetter()});
    }
    m.${ast.getObjectName()}_${ast.getOldValue()}_before_pos = ${ast.getObjectGetter()}.${ast.getGetter()}().indexOf(${ast.getOldValueGetter()});
    ${ast.getObjectGetter()}.${ast.getUnsetter()}(${ast.getOldValueGetter()});

    <#if ast.isOldValueWithinOpt()>}</#if>

<#elseif ast.attributeIterated && !ast.copy >
    // attribute is a list
    <#if ast.isValueWithinOpt()>if(m.${ast.getValue()}.isPresent()) {</#if>
    // a value is given -> add new object

    <#if ast.isPresentInsertPosition()>pos = ${ast.getInsertPosition()};</#if>
    ${ast.getObjectGetter()}.${ast.getSetter()}(
        <#if ast.isPresentInsertPosition()>pos,</#if>
        ${ast.getValueGetter()}
    );

    <#if ast.isValueWithinOpt()>}</#if>

<#elseif ast.attributeIterated && ast.copy >
    // attribute is a list
    // attribute needs to be copied
    <#if ast.isValueWithinOpt()>if(m.${ast.getValue()}.isPresent()) {</#if>

    ${ast.getType()} d = ${ast.getValueGetter()}.deepClone();
    <#if ast.isPresentInsertPosition()>
        pos = ${ast.getInsertPosition()};
        m.${ast.getObjectName()}_${ast.getValue()}_before.put( d, pos);
    <#else>
       m.${ast.getObjectName()}_${ast.getValue()}_before.put( d,  ${ast.getObjectGetter()}.${ast.getGetter()}().size());
    </#if>

    ${ast.getObjectGetter()}.${ast.getSetter()}(
        <#if ast.isPresentInsertPosition()>pos,</#if>
        d
    );

    <#if ast.isValueWithinOpt()>}</#if>

<#elseif !ast.attributeIterated && !ast.isPresentValue()>
    // single attribute (no list)
    // no value should be set, thus, it is a deletion.
    <#if ast.isOldValueWithinOpt()>if(m.${ast.getOldValue()}.isPresent()) {</#if>

    // deletion of a list or single object
    // setting null here results in an Optional.empty()
    <#if ast.isAttributeOptional()>
        m.${ast.getObjectName()}_${ast.getOldValue()}_before = ${ast.getObjectGetter()}.${ast.getGetter()}();
        ${ast.getObjectGetter()}.${ast.getSetter()}(null);
    </#if>

    <#if ast.isOldValueWithinOpt()>}</#if>

<#elseif !ast.attributeIterated && ast.isPresentValue()>
    // single attribute (no list)
    // a different value was given, thus, change the object
    <#if ast.isValueWithinOpt()>if (m.${ast.getValue()}.isPresent()) {</#if>

    <#if ast.isAttributeOptional()>
    if(${ast.getObjectGetter()}.${ast.getGetIsPresent()}){
    </#if>

    m.${ast.getObjectName()}_${ast.getValue()}_before = ${ast.getObjectGetter()}.${ast.getGetter()}();
    if(${ast.getObjectGetter()}.${ast.getGetter()}() != null) {
      Reporting.reportTransformationOldValue("${ruleClassName}",${ast.getObjectGetter()}.${ast.getGetter()}());
    }

    <#if ast.isAttributeOptional()>}</#if>

    ${ast.getObjectGetter()}.${ast.getSetter()}(${ast.getValueGetter()}<#if ast.copy>.deepClone()</#if>);

    <#if ast.isValueWithinOpt()>}</#if>

</#if>
