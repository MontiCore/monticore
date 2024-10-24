<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleClassName")}

// Composition points on an object in a list
<#if ast.attributeIterated && !ast.isPresentValue()>
    // attribute is a list
    // restore old values
    for (int i = ${ast.getOldValueGetter()}.size() - 1; i >= 0; i--) {
      ${ast.getType()} d = ${ast.getOldValueGetter()}.get(i);
      ${ast.getObjectGetter()}.${ast.getGetter()}().add(m.${ast.getObjectName()}_${ast.getOldValue()}_before.get(d), d);
    }
<#elseif ast.attributeIterated && !ast.copy>
    // attribute is a list
    // a value was given -> change to old objects
    for (${ast.getType()} d : m.${ast.getObjectName()}_${ast.getValue()}_before.keySet()) {
      ${ast.getObjectGetter()}.${ast.getUnsetter()}(d);
    }
<#elseif ast.attributeIterated && ast.copy>
    // attribute is a list
    // remove old copies
    for (${ast.getType()} d : m.${ast.getObjectName()}_${ast.getValue()}_before.keySet()) {
      ${ast.getObjectGetter()}.${ast.getUnsetter()}(d);
    }
<#elseif !ast.attributeIterated && !ast.isPresentValue()>
    // single attribute (no list)
    // undo deletion
    if (m.${ast.getObjectName()}_${ast.getOldValue()}_before != null) {
      ${ast.getObjectGetter()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getOldValue()}_before);
    }
<#elseif !ast.attributeIterated && !ast.copy>
    // single attribute (no list)
    // undo change
    if (m.${ast.getObjectName()}_${ast.getValue()}_before != null) {
      ${ast.getObjectGetter()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getValue()}_before);
    }
    <#if ast.attributeOptional>
    else {
      ${ast.getObjectGetter()}.${ast.getSetter()}Absent();
    }
    </#if>
<#elseif !ast.attributeIterated && ast.copy>
    // single attribute (no list)
    // undo copy
    if (m.${ast.getObjectName()}_${ast.getValue()}_before != null) {
      ${ast.getObjectGetter()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getValue()}_before);
    }
    <#if ast.attributeOptional>
    else {
      ${ast.getObjectGetter()}.${ast.getSetter()}Absent();
    }
    </#if>
</#if>
