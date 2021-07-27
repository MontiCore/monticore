<#-- (c) https://github.com/MontiCore/monticore -->
// in a list is changed

<#if ast.attributeIterated && !ast.isPresentValue()>
  // attribute is a list
  // no value is given -> deletion
    <#if ast.oldValueWithinOpt>
  for (Optional<${ast.getType()}> d : ${ast.getOldValueGetter()}) {
    if(d.isPresent()) {
      m.${ast.getObjectName()}_${ast.getOldValue()}_before.put(d.get(), ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().indexOf(d.get()));
      ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().remove(d.get());
    }
  }
    <#else>
  for (${ast.getType()} d : ${ast.getOldValueGetter()}) {
    m.${ast.getObjectName()}_${ast.getOldValue()}_before.put(d, ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().indexOf(d));
    ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().remove(d);
  }
    </#if>
<#elseif ast.attributeIterated && !ast.isValueWithinList() >
  // in a list is changed
  // attribute is a list
  // Left side in a List but right side is not
  for(int i = 0; i < ${ast.getObjectGetter()}.size(); i++){
    ${ast.getType()} d_copy = ${ast.getValueGetter()}.deepClone();
    ${ast.getObjectGetter()}.get(i).${ast.getSetter()}(d_copy);
    m.${ast.getObjectName()}_${ast.getValue()}_before.put(d_copy, ${ast.getObjectGetter()}.get(i).${ast.getGetter()}().indexOf(d_copy));
  }
<#elseif ast.attributeIterated && !ast.copy>
  // attribute is a list
  // a value is given -> change to new objects
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
    ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)).${ast.getSetter()}(d);
    m.${ast.getObjectName()}_${ast.getValue()}_before.put(d, ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)).${ast.getGetter()}().indexOf(d));
  }
<#elseif ast.attributeIterated && ast.copy>
  // attribute is a list
  // copy attribute
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
    ${ast.getType()} d_copy = d.deepClone();
    ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)).${ast.getSetter()}(d_copy);
    m.${ast.getObjectName()}_${ast.getValue()}_before.put(d_copy, ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)).${ast.getGetter()}().indexOf(d_copy));
  }
<#elseif !ast.attributeIterated && !ast.isPresentValue()>
  // single attribute (no list)
  // no value was given -> deletion
  for (${ast.getType()} d : ${ast.getOldValueGetter()}) {
    ${ast.getObjectType()} ${ast.getObjectName()} = ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d));
    <#if ast.attributeOptional>if (${ast.getObjectName()}.${ast.getGetIsPresent()}) {</#if>
      m.${ast.getObjectName()}_${ast.getOldValue()}_before.put(${ast.getObjectName()}, ${ast.getObjectName()}.${ast.getGetter()}());
    <#if ast.attributeOptional>}
    ${ast.getObjectName()}.${ast.getSetter()}Absent();
</#if>
  }


<#elseif !ast.attributeIterated && !ast.isValueWithinList()>
  // single attribute (not in a list)
  // Not possible, the right side hast to be in a list when the left side is
  for(int i = 0; i < ${ast.getObjectGetter()}.size(); i++){
    <#if ast.attributeOptional>if (${ast.getObjectGetter()}.get(i).${ast.getGetIsPresent()})</#if>
      m.${ast.getObjectName()}_${ast.getValue()}_before.put(${ast.getObjectGetter()}.get(i), ${ast.getObjectGetter()}.get(i).${ast.getGetter()}());
    ${ast.getObjectGetter()}.get(i).${ast.getSetter()}(${ast.getValueGetter()});
  }
<#elseif !ast.attributeIterated && !ast.copy>
  // single attribute (not in a list)
  // a value is given -> change from to new objects
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
    ${ast.getObjectType()} ${ast.getObjectName()} = ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d));
    <#if ast.attributeOptional>if (${ast.getObjectName()}.${ast.getGetIsPresent()}) {</#if>
      m.${ast.getObjectName()}_${ast.getValue()}_before.put(${ast.getObjectName()}, ${ast.getObjectName()}.${ast.getGetter()}());
    <#if ast.attributeOptional>}</#if>
    ${ast.getObjectName()}.${ast.getSetter()}(d);
  }
<#elseif !ast.attributeIterated && ast.copy>
  // single attribute (not in a list)
  // Make a copy
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
    ${ast.getObjectType()} ${ast.getObjectName()} = ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d));
    <#if ast.attributeOptional>if (${ast.getObjectName()}.${ast.getGetIsPresent()}) {</#if>
      m.${ast.getObjectName()}_${ast.getValue()}_before.put(${ast.getObjectName()}, ${ast.getObjectName()}.${ast.getGetter()}());
    <#if ast.attributeOptional>}</#if>
    ${ast.getObjectName()}.${ast.getSetter()}(d.deepClone());
  }
</#if>
