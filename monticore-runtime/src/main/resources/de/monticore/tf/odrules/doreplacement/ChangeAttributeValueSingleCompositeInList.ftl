<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleClassName")}

// Composition points on an object in a list
<#if ast.attributeIterated && !ast.isPresentValue()>
    // attribute is a list
    // no value is given -> deletion
    <#if ast.isOldValueWithinOpt()>if(${ast.getOldValueGetter()?replace(".get()",".isPresent()")})</#if>
    for (${ast.getType()} d : ${ast.getOldValueGetter()}) {
      m.${ast.getObjectName()}_${ast.getOldValue()}_before.put(d, ${ast.getObjectGetter()}.${ast.getGetter()}().indexOf(d));
      ${ast.getObjectGetter()}.${ast.getGetter()}().remove(d);
    }
<#elseif ast.attributeIterated && !ast.copy>
    // attribute is a list
    // a value is given -> change to new objects
    <#if ast.isValueWithinOpt()>if(${ast.getValueGetter()?replace(".get()",".isPresent()")})</#if>
    for (${ast.getType()} d : ${ast.getValueGetter()}) {
      ${ast.getObjectGetter()}.${ast.getSetter()}(d);
      m.${ast.getObjectName()}_${ast.getValue()}_before.put(d, ${ast.getObjectGetter()}.${ast.getGetter()}().indexOf(d));
    }
<#elseif ast.attributeIterated && ast.copy>
    // attribute is a list
    // Make a copy
    <#if ast.isValueWithinOpt()>if(${ast.getValueGetter()?replace(".get()",".isPresent()")})</#if>
    for (${ast.getType()} d : ${ast.getValueGetter()}) {
      ${ast.getType()} d_copy = d.deepClone();
      ${ast.getObjectGetter()}.${ast.getSetter()}(d_copy);
      m.${ast.getObjectName()}_${ast.getValue()}_before.put(d_copy, ${ast.getObjectGetter()}.${ast.getGetter()}().indexOf(d_copy));
    }
<#elseif !ast.attributeIterated && !ast.isPresentValue()>
    // single attribute (no list)
    // no value is given -> deletion
    <#if ast.isOldValueWithinOpt()>if(${ast.getOldValueGetter()?replace(".get()",".isPresent()")})</#if>
    for (${ast.getType()} d : ${ast.getOldValueGetter()}) {
      m.${ast.getObjectName()}_${ast.getOldValue()}_before = d;
      ${ast.getObjectGetter()}.${ast.getSetter()}Absent();
    }
<#elseif !ast.attributeIterated && !ast.copy>
    // single attribute (no list)
    // a different value was given, thus, change the object
    <#if ast.isValueWithinOpt()>if(${ast.getValueGetter()?replace(".get()",".isPresent()")})</#if>
    for (${ast.getType()} d : ${ast.getValueGetter()}) {
      <#if ast.attributeOptional>if(${ast.getObjectGetter()}.${ast.getGetIsPresent()})</#if>
      m.${ast.getObjectName()}_${ast.getValue()}_before = ${ast.getObjectGetter()}.${ast.getGetter()}();
      ${ast.getObjectGetter()}.${ast.getSetter()}(d);
    }
<#elseif !ast.attributeIterated && ast.copy>
    // single attribute (no list)
    // Make a copy
    <#if ast.isValueWithinOpt()>if(${ast.getValueGetter()?replace(".get()",".isPresent()")})</#if>
    for (${ast.getType()} d : ${ast.getValueGetter()}) {
      <#if ast.attributeOptional>if(${ast.getObjectGetter()}.${ast.getGetIsPresent()})</#if>
      m.${ast.getObjectName()}_${ast.getValue()}_before = ${ast.getObjectGetter()}.${ast.getGetter()}();
      ${ast.getObjectGetter()}.${ast.getSetter()}(d.deepClone());
    }
</#if>
