<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleClassName")}

// primitive type, String or String list
<#if ast.isObjectWithinList()>
    // in a list is changed
    for (${ast.getObjectType()} d : ${ast.getObjectGetter()}) {
      ${ast.getValueType()} oldValue = d.${ast.getGetter()}();
      d.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getAttributeName()}_before.get(d));
      this.modelAccessor.notifyModification(d, "${ast.getAttributeName()}", ModificationOp.REPLACE, oldValue, d.${ast.getGetter()}());
    }
<#else>
    // single attribute (not in a list)
    <#assign valueName = ast.getObjectGetter()?remove_beginning("m.")?remove_ending(".get()")>
    ${ast.getValueType()} ${valueName}_${ast.getAttributeName()}_oldValue = ${ast.getObjectGetter()}.${ast.getGetter()}();
    ${ast.getObjectGetter()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getAttributeName()}_before);
    this.modelAccessor.notifyModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ModificationOp.REPLACE, ${valueName}_${ast.getAttributeName()}_oldValue, ${ast.getObjectGetter()}.${ast.getGetter()}());
</#if>
