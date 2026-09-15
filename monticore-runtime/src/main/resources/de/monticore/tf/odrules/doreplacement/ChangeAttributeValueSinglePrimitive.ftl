<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleClassName")}

// primitive type, String or String list
<#if ast.isObjectWithinList()>
    // in a list is changed
    for (${ast.getObjectType()} d : ${ast.getObjectGetter()}) {
      m.${ast.getObjectName()}_${ast.getAttributeName()}_before.put(d, d.${ast.getGetter()}());
      d.${ast.getSetter()}(${ast.getValue()});
      this.modelAccessor.notifyModification(d, "${ast.getAttributeName()}", ModificationOp.REPLACE, m.${ast.getObjectName()}_${ast.getAttributeName()}_before.get(d), ${ast.getValue()});
    }
<#else>
    // single attribute (not in a list)
    m.${ast.getObjectName()}_${ast.getAttributeName()}_before = ${ast.getObjectGetter()}.${ast.getGetter()}();
    ${ast.getObjectGetter()}.${ast.getSetter()}(${ast.getValue()});
    this.modelAccessor.notifyModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ModificationOp.REPLACE, m.${ast.getObjectName()}_${ast.getAttributeName()}_before, ${ast.getValue()});
</#if>
