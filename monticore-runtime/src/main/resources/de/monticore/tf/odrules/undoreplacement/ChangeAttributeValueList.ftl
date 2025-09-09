<#-- (c) https://github.com/MontiCore/monticore -->
  // a composition is changed

<#if ast.attributeIterated && !ast.isPresentValue()>
  // in a list is changed
  // attribute is a list
  // no value is given -> undo deletion
    <#if ast.oldValueWithinOpt>
  for (int i = ${ast.getOldValueGetter()}.size() - 1; i >= 0; i--) {
    Optional<${ast.getType()}> d = ${ast.getOldValueGetter()}.get(i);
    if (d.isPresent()) {
      ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().add(m.${ast.getObjectName()}_${ast.getOldValue()}_before.get(d.get()), d.get());
    }
  }
    <#else>
  for (int i = ${ast.getOldValueGetter()}.size() - 1; i >= 0; i--) {
    ${ast.getType()} d = ${ast.getOldValueGetter()}.get(i);
    ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().add(m.${ast.getObjectName()}_${ast.getOldValue()}_before.get(d), d);
  }
    </#if>
<#elseif ast.attributeIterated && !ast.isValueWithinList() >
  // in a list is changed
  // attribute is a list
  // Left side in a List but right side is not
  for(int i = 0; i < ${ast.getObjectGetter()}.size(); i++){
    ${ast.getType()} d_copy = (${ast.getType()}) m.${ast.getObjectName()}_${ast.getValue()}_before.keySet().iterator().next();
    ${ast.getObjectGetter()}.get(i).${ast.getUnsetter()}(d_copy);
  }
<#elseif ast.attributeIterated && !ast.copy>
  // in a list is changed
  // attribute is a list
  // a value was given -> undo change
  for (${ast.getType()} d : m.${ast.getObjectName()}_${ast.getValue()}_before.keySet()) {
    ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)).${ast.getUnsetter()}(d);
  }
<#elseif ast.attributeIterated && ast.copy>
  // in a list is changed
  // attribute is a list
  // delete copied items
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
    for (${ast.getType()} d_copy : m.${ast.getObjectName()}_${ast.getValue()}_before.keySet()) {
      ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)).${ast.getUnsetter()}(d_copy);
    }
  }


<#elseif !ast.attributeIterated && !ast.isPresentValue()>
  // single attribute (no list)
  // no value was given -> undo deletion
  for (${ast.getObjectType()} d : m.${ast.getObjectName()}_${ast.getOldValue()}_before.keySet()) {
    d.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getOldValue()}_before.get(d));
  }
<#elseif !ast.attributeIterated && !ast.isValueWithinList()>
  // single attribute (not in a list)
  // Not possible, the right side hast to be in a list when the left side is
  for (${ast.getObjectType()} d : m.${ast.getObjectName()}_${ast.getValue()}_before.keySet()) {
    d.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getValue()}_before.get(d));
  }
<#elseif !ast.attributeIterated && !ast.copy>
  // single attribute (not in a list)
  // a value is given -> undo change
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
    ${ast.getObjectType()} ${ast.getObjectName()} = ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d));
    if (m.${ast.getObjectName()}_${ast.getValue()}_before.containsKey(${ast.getObjectName()})) {
      ${ast.getObjectName()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getValue()}_before.get(${ast.getObjectName()}));
    } else {
      ${ast.getObjectName()}.${ast.getSetter()}Absent();
    }
  }
<#elseif !ast.attributeIterated && ast.copy>
  // single attribute (not in a list)
  // undo copy
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
  ${ast.getObjectType()} ${ast.getObjectName()} = ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d));
  if (m.${ast.getObjectName()}_${ast.getValue()}_before.containsKey(${ast.getObjectName()})) {
      ${ast.getObjectName()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getValue()}_before.get(${ast.getObjectName()}));
    } else {
      ${ast.getObjectName()}.${ast.getSetter()}Absent();
    }
  }
</#if>
