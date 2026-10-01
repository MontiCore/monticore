<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleClassName")}

// Composition points on an object in a list
<#if ast.attributeIterated && !ast.isPresentValue()>
    // attribute is a list
    // restore old values
    for (int i = ${ast.getOldValueGetter()}.size() - 1; i >= 0; i--) {
      ${ast.getType()} d = ${ast.getOldValueGetter()}.get(i);
      ${ast.getObjectGetter()}.${ast.getGetter()}().add(m.${ast.getObjectName()}_${ast.getOldValue()}_before.get(d), d);

      // TODO: REPLACE instead of SET
      this.modelAccessor.notifyListModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", m.${ast.getObjectName()}_${ast.getOldValue()}_before.get(d), ModificationOp.SET, null, d);
      this.modelAccessor.notifyNodeAttach(d, ${ast.getObjectGetter()});
    }
<#elseif ast.attributeIterated && !ast.copy>
    // attribute is a list
    // a value was given -> change to old objects
    for (${ast.getType()} d : m.${ast.getObjectName()}_${ast.getValue()}_before.keySet()) {
      int valIdx = ${ast.getObjectGetter()}.${ast.getGetter()}().indexOf(d);
      ${ast.getObjectGetter()}.${ast.getUnsetter()}(d);

      this.modelAccessor.notifyListModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", valIdx, ModificationOp.UNSET, d, null);
      this.modelAccessor.notifyNodeDetach(d, ${ast.getObjectGetter()});
    }
<#elseif ast.attributeIterated && ast.copy>
    // attribute is a list
    // remove old copies
    for (${ast.getType()} d : m.${ast.getObjectName()}_${ast.getValue()}_before.keySet()) {
      int valIdx = ${ast.getObjectGetter()}.${ast.getGetter()}().indexOf(d);
      ${ast.getObjectGetter()}.${ast.getUnsetter()}(d);

      this.modelAccessor.notifyListModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", valIdx, ModificationOp.UNSET, d, null);
      this.modelAccessor.notifyNodeDetach(d, ${ast.getObjectGetter()});
    }
<#elseif !ast.attributeIterated && !ast.isPresentValue()>
    // single attribute (no list)
    // undo deletion
    if (m.${ast.getObjectName()}_${ast.getOldValue()}_before != null) {
      ${ast.getObjectGetter()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getOldValue()}_before);

      this.modelAccessor.notifyModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ModificationOp.SET, null, ${ast.getObjectGetter()}.${ast.getGetter()}());
      this.modelAccessor.notifyNodeAttach(${ast.getObjectGetter()}.${ast.getGetter()}(), ${ast.getObjectGetter()});
    }
<#elseif !ast.attributeIterated && !ast.copy>
    // single attribute (no list)
    // undo change
    if (m.${ast.getObjectName()}_${ast.getValue()}_before != null) {
      this.modelAccessor.notifyNodeDetach(${ast.getObjectGetter()}.${ast.getGetter()}(), ${ast.getObjectGetter()});
      this.modelAccessor.notifyModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ModificationOp.REPLACE, ${ast.getObjectGetter()}.${ast.getGetter()}(), m.${ast.getObjectName()}_${ast.getValue()}_before);

      ${ast.getObjectGetter()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getValue()}_before);

      this.modelAccessor.notifyNodeAttach(${ast.getObjectGetter()}.${ast.getGetter()}(), ${ast.getObjectGetter()});
    }
    <#if ast.attributeOptional>
    else {
      ${ast.getValueType()} oldValue = ${ast.getObjectGetter()}.${ast.getGetter()}();
      ${ast.getObjectGetter()}.${ast.getSetter()}Absent();
      this.modelAccessor.notifyNodeDetach(oldValue, ${ast.getObjectGetter()});
      this.modelAccessor.notifyModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ModificationOp.UNSET, oldValue, null);
    }
    </#if>
<#elseif !ast.attributeIterated && ast.copy>
    // single attribute (no list)
    // undo copy
    if (m.${ast.getObjectName()}_${ast.getValue()}_before != null) {
      this.modelAccessor.notifyNodeDetach(${ast.getObjectGetter()}.${ast.getGetter()}(), ${ast.getObjectGetter()});
      this.modelAccessor.notifyModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ModificationOp.REPLACE, ${ast.getObjectGetter()}.${ast.getGetter()}(), m.${ast.getObjectName()}_${ast.getValue()}_before);

      ${ast.getObjectGetter()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getValue()}_before);

      this.modelAccessor.notifyNodeAttach(${ast.getObjectGetter()}.${ast.getGetter()}(), ${ast.getObjectGetter()});
    }
    <#if ast.attributeOptional>
    else {
      ${ast.getValueType()} oldValue = ${ast.getObjectGetter()}.${ast.getGetter()}();
      ${ast.getObjectGetter()}.${ast.getSetter()}Absent();
      this.modelAccessor.notifyNodeDetach(oldValue, ${ast.getObjectGetter()});
      this.modelAccessor.notifyModification(${ast.getObjectGetter()}, "${ast.getAttributeName()}", ModificationOp.UNSET, oldValue, null);
    }
    </#if>
</#if>
