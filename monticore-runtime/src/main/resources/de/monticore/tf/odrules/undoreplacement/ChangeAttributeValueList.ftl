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

      this.modelAccessor.notifyListModification(${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)), "${ast.getAttributeName()}", ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().size(), ModificationOp.SET, null, d.get());
      this.modelAccessor.notifyNodeAttach(d.get(), ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)));
    }
  }
    <#else>
  for (int i = ${ast.getOldValueGetter()}.size() - 1; i >= 0; i--) {
    ${ast.getType()} d = ${ast.getOldValueGetter()}.get(i);
    ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().add(m.${ast.getObjectName()}_${ast.getOldValue()}_before.get(d), d);

    this.modelAccessor.notifyListModification(${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)), "${ast.getAttributeName()}", ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().size(), ModificationOp.SET, null, d);
    this.modelAccessor.notifyNodeAttach(d, ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)));
  }
    </#if>
<#elseif ast.attributeIterated && !ast.isValueWithinList() >
  // in a list is changed
  // attribute is a list
  // Left side in a List but right side is not
  for(int i = 0; i < ${ast.getObjectGetter()}.size(); i++){
    ${ast.getType()} d_copy = (${ast.getType()}) m.${ast.getObjectName()}_${ast.getValue()}_before.keySet().iterator().next();
    int valIdx = ${ast.getObjectGetter()}.get(i).${ast.getGetter()}().indexOf(d_copy);
    ${ast.getObjectGetter()}.get(i).${ast.getUnsetter()}(d_copy);

    this.modelAccessor.notifyListModification(${ast.getObjectGetter()}.get(i), "${ast.getAttributeName()}", valIdx, ModificationOp.UNSET, d_copy, null);
    this.modelAccessor.notifyNodeDetach(d_copy, ${ast.getObjectGetter()}.get(i));
  }
<#elseif ast.attributeIterated && !ast.copy>
  // in a list is changed
  // attribute is a list
  // a value was given -> undo change
  for (${ast.getType()} d : m.${ast.getObjectName()}_${ast.getValue()}_before.keySet()) {
    int baseValIdx = ${ast.getValueGetter()}.indexOf(d);
    int valIdx = ${ast.getObjectGetter()}.get(baseValIdx).${ast.getGetter()}().indexOf(d);
    ${ast.getObjectGetter()}.get(baseValIdx).${ast.getUnsetter()}(d);

    this.modelAccessor.notifyListModification(${ast.getObjectGetter()}.get(baseValIdx), "${ast.getAttributeName()}", valIdx, ModificationOp.UNSET, d, null);
    this.modelAccessor.notifyNodeDetach(d, ${ast.getObjectGetter()}.get(baseValIdx));
  }
<#elseif ast.attributeIterated && ast.copy>
  // in a list is changed
  // attribute is a list
  // delete copied items
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
    for (${ast.getType()} d_copy : m.${ast.getObjectName()}_${ast.getValue()}_before.keySet()) {
      int valIdx = ${ast.getValueGetter()}.indexOf(d);
      ${ast.getObjectGetter()}.get(valIdx).${ast.getUnsetter()}(d_copy);

      this.modelAccessor.notifyListModification(${ast.getObjectGetter()}.get(valIdx), "${ast.getAttributeName()}", ${ast.getObjectGetter()}.get(valIdx).${ast.getGetter()}().indexOf(d_copy), ModificationOp.UNSET, d_copy, null);
      this.modelAccessor.notifyNodeDetach(d_copy, ${ast.getObjectGetter()}.get(valIdx));
    }
  }


<#elseif !ast.attributeIterated && !ast.isPresentValue()>
  // single attribute (no list)
  // no value was given -> undo deletion
  for (${ast.getObjectType()} d : m.${ast.getObjectName()}_${ast.getOldValue()}_before.keySet()) {
    d.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getOldValue()}_before.get(d));

    this.modelAccessor.notifyNodeAttach(d.${ast.getGetter()}(), d);
    this.modelAccessor.notifyModification(d, "${ast.getAttributeName()}", ModificationOp.SET, null, d.${ast.getGetter()}());
  }
<#elseif !ast.attributeIterated && !ast.isValueWithinList()>
  // single attribute (not in a list)
  // Not possible, the right side hast to be in a list when the left side is
  for (${ast.getObjectType()} d : m.${ast.getObjectName()}_${ast.getValue()}_before.keySet()) {
    ${ast.getValueType()} oldVal = d.${ast.getGetter()};
    d.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getValue()}_before.get(d));

    this.modelAccessor.notifyModification(d, "${ast.getAttributeName()}", ModificationOp.REPLACE, oldVal, d.${ast.getGetter()});
    this.modelAccessor.notifyNodeAttach(m.${ast.getObjectName()}_${ast.getValue()}_before.get(d), d);
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
    this.modelAccessor.notifyModification(${ast.getObjectName()}, "${ast.getAttributeName()}", ModificationOp.REPLACE, d, m.${ast.getObjectName()}_${ast.getValue()}_before.get(${ast.getObjectName()}));
    this.modelAccessor.notifyNodeDetach(d, ${ast.getObjectName()});
  }
<#elseif !ast.attributeIterated && ast.copy>
  // single attribute (not in a list)
  // undo copy
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
  ${ast.getObjectType()} ${ast.getObjectName()} = ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d));
    if (m.${ast.getObjectName()}_${ast.getValue()}_before.containsKey(${ast.getObjectName()})) {
      this.modelAccessor.notifyNodeDetach(${ast.getObjectName()}.${ast.getGetter()}(), ${ast.getObjectName()});
      this.modelAccessor.notifyModification(${ast.getObjectName()}, "${ast.getAttributeName()}", ModificationOp.REPLACE, ${ast.getObjectName()}.${ast.getGetter()}(), m.${ast.getObjectName()}_${ast.getValue()}_before.get(${ast.getObjectName()}));

      ${ast.getObjectName()}.${ast.getSetter()}(m.${ast.getObjectName()}_${ast.getValue()}_before.get(${ast.getObjectName()}));

      this.modelAccessor.notifyNodeAttach(${ast.getObjectName()}.${ast.getGetter()}(), ${ast.getObjectName()});
    } else {
      if(${ast.getObjectName()}.${ast.getGetIsPresent()}) {
        this.modelAccessor.notifyModification(${ast.getObjectName()}, "${ast.getAttributeName()}", ModificationOp.UNSET, ${ast.getObjectName()}.${ast.getGetter()}(), null);
        this.modelAccessor.notifyNodeDetach(${ast.getObjectName()}.${ast.getGetter()}(), ${ast.getObjectName()});
      }
      ${ast.getObjectName()}.${ast.getSetter()}Absent();
    }
  }
</#if>
