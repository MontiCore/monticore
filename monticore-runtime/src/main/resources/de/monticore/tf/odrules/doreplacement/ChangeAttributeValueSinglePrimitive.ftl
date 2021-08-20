<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleClassName")}

// primitive type, String or String list
<#if ast.isObjectWithinList()>
    // in a list is changed
    for (${ast.getObjectType()} d : ${ast.getObjectGetter()}) {
      m.${ast.getObjectName()}_${ast.getAttributeName()}_before.put(d, d.${ast.getGetter()}());
      d.${ast.getSetter()}(${ast.getValue()});
    }
<#else>
    // single attribute (not in a list)
    Reporting.reportTransformationObjectChange("${ruleClassName}",${ast.getObjectGetter()}, "${ast.getAttributeName()}");

    <#if ast.isPresentOldValue()>
        Reporting.reportTransformationOldValue("${ruleClassName}",<#if ast.composite>m.</#if>${ast.getOldValueGetter()?keep_after("m.")}<#if ast.isValueStringList()>.toString()</#if>);
    </#if>

    <#if ast.isAttributeOptional()>
        if (${ast.getObjectGetter()}.isPresent${ast.getAttributeName()?cap_first}()){
    </#if>

    <#if !ast.isPrimitiveType()>
        if(${ast.getObjectGetter()}.${ast.getGetter()}() != null) {
          Reporting.reportTransformationOldValue("${ruleClassName}",${ast.getObjectGetter()}.${ast.getGetter()}()<#if ast.isValueStringList()>.toString()</#if>);
        }
    <#else>
        Reporting.reportTransformationOldValue("${ruleClassName}",""+${ast.getObjectGetter()}.${ast.getGetter()}()<#if ast.isValueStringList()>.toString()</#if>);
    </#if>

    <#if ast.isAttributeOptional()>
        }
    </#if>
    m.${ast.getObjectName()}_${ast.getAttributeName()}_before = ${ast.getObjectGetter()}.${ast.getGetter()}();
    ${ast.getObjectGetter()}.${ast.getSetter()}(${ast.getValue()});
</#if>
