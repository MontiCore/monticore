<#-- (c) https://github.com/MontiCore/monticore -->
// in a list is changed

<#if ast.attributeIterated && !ast.isPresentValue()>
  // attribute is a list
  // no value is given -> deletion
    <#if ast.oldValueWithinOpt>
  for (Optional<${ast.getType()}> d : ${ast.getOldValueGetter()}) {
    if(d.isPresent()) {
      int valIdx = ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().indexOf(d.get());
      m.${ast.getObjectName()}_${ast.getOldValue()}_before.put(d.get(), valIdx);

      ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().remove(d.get());

      this.modelAccessor.notifyListModification(${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)), "${ast.getAttributeName()}", valIdx, ModificationOp.UNSET, d.get(), null);
      this.modelAccessor.notifyNodeDetach(d.get(), ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)));
    }
  }
    <#else>
  for (${ast.getType()} d : ${ast.getOldValueGetter()}) {
    int valIdx = ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().indexOf(d);
    m.${ast.getObjectName()}_${ast.getOldValue()}_before.put(d, valIdx);

    ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)).${ast.getGetter()}().remove(d);

    this.modelAccessor.notifyListModification(${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)), "${ast.getAttributeName()}", valIdx, ModificationOp.UNSET, d, null);
    this.modelAccessor.notifyNodeDetach(d, ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d)));
  }
    </#if>
<#elseif ast.attributeIterated && !ast.isValueWithinList() >
  // in a list is changed
  // attribute is a list
  // Left side in a List but right side is not
  for(int i = 0; i < ${ast.getObjectGetter()}.size(); i++){
    ${ast.getType()} d_copy = ${ast.getValueGetter()}.deepClone();
    ${ast.getObjectGetter()}.get(i).${ast.getSetter()}(d_copy);

    int valIdx = ${ast.getObjectGetter()}.get(i).${ast.getGetter()}().indexOf(d_copy);
    m.${ast.getObjectName()}_${ast.getValue()}_before.put(d_copy, valIdx);

    this.modelAccessor.notifyListModification(${ast.getObjectGetter()}.get(i), "${ast.getAttributeName()}", valIdx, ModificationOp.SET, null, d_copy);
    this.modelAccessor.notifyNodeAttach(d_copy, ${ast.getObjectGetter()}.get(i));
  }
<#elseif ast.attributeIterated && !ast.copy>
  // attribute is a list
  // a value is given -> change to new objects
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
    ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)).${ast.getSetter()}(d);

    int valIdx = ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)).${ast.getGetter()}().indexOf(d);
    m.${ast.getObjectName()}_${ast.getValue()}_before.put(d, valIdx);

    // TODO: REPLACE instead of SET modification??
    this.modelAccessor.notifyListModification(${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)), "${ast.getAttributeName()}", valIdx, ModificationOp.SET, null, d);
    this.modelAccessor.notifyNodeAttach(d, ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)));
  }
<#elseif ast.attributeIterated && ast.copy>
  // attribute is a list
  // copy attribute
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
    ${ast.getType()} d_copy = d.deepClone();
    ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)).${ast.getSetter()}(d_copy);

    int valIdx = ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)).${ast.getGetter()}().indexOf(d_copy);
    m.${ast.getObjectName()}_${ast.getValue()}_before.put(d_copy, valIdx);

    // TODO: REPLACE instead of SET modification??
    this.modelAccessor.notifyListModification(${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)), "${ast.getAttributeName()}", valIdx, ModificationOp.SET, null, d_copy);
    this.modelAccessor.notifyNodeAttach(d_copy, ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d)));
  }
<#elseif !ast.attributeIterated && !ast.isPresentValue()>
  // single attribute (no list)
  // no value was given -> deletion
  for (${ast.getType()} d : ${ast.getOldValueGetter()}) {
    ${ast.getObjectType()} ${ast.getObjectName()} = ${ast.getObjectGetter()}.get(${ast.getOldValueGetter()}.indexOf(d));
    <#if ast.attributeOptional>if (${ast.getObjectName()}.${ast.getGetIsPresent()}) {</#if>
      m.${ast.getObjectName()}_${ast.getOldValue()}_before.put(${ast.getObjectName()}, ${ast.getObjectName()}.${ast.getGetter()}());

      this.modelAccessor.notifyNodeDetach(d, ${ast.getObjectName()});
    <#if ast.attributeOptional>}
    ${ast.getObjectName()}.${ast.getSetter()}Absent();

    this.modelAccessor.notifyModification(${ast.getObjectName()}, "${ast.getAttributeName()}", ModificationOp.UNSET, d, null);
</#if>
  }


<#elseif !ast.attributeIterated && !ast.isValueWithinList()>
  // single attribute (not in a list)
  // Not possible, the right side has to be in a list when the left side is
  for(int i = 0; i < ${ast.getObjectGetter()}.size(); i++){
    <#if ast.attributeOptional>if (${ast.getObjectGetter()}.get(i).${ast.getGetIsPresent()})</#if>
      m.${ast.getObjectName()}_${ast.getValue()}_before.put(${ast.getObjectGetter()}.get(i), ${ast.getObjectGetter()}.get(i).${ast.getGetter()}());
    ${ast.getObjectGetter()}.get(i).${ast.getSetter()}(${ast.getValueGetter()});

    this.modelAccessor.notifyModification(${ast.getObjectGetter()}.get(i), "${ast.getAttributeName()}", ModificationOp.REPLACE, m.${ast.getObjectName()}_${ast.getValue()}_before.get(${ast.getObjectGetter()}.get(i)), ${ast.getValueGetter()});
    this.modelAccessor.notifyNodeAttach(${ast.getValueGetter()}, ${ast.getObjectGetter()}.get(i));
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

    this.modelAccessor.notifyModification(${ast.getObjectName()}, "${ast.getAttributeName()}", ModificationOp.REPLACE, m.${ast.getObjectName()}_${ast.getValue()}_before.get(${ast.getObjectName()}), d);
    this.modelAccessor.notifyNodeAttach(d, ${ast.getObjectName()});
  }
<#elseif !ast.attributeIterated && ast.copy>
  // single attribute (not in a list)
  // Make a copy
  for (${ast.getType()} d : ${ast.getValueGetter()}) {
    ${ast.getObjectType()} ${ast.getObjectName()} = ${ast.getObjectGetter()}.get(${ast.getValueGetter()}.indexOf(d));
    <#if ast.attributeOptional>if (${ast.getObjectName()}.${ast.getGetIsPresent()}) {</#if>
      m.${ast.getObjectName()}_${ast.getValue()}_before.put(${ast.getObjectName()}, ${ast.getObjectName()}.${ast.getGetter()}());
    <#if ast.attributeOptional>}</#if>

    ${ast.getType()} d_copy = d.deepClone();
    ${ast.getObjectName()}.${ast.getSetter()}(d_copy);

    this.modelAccessor.notifyModification(${ast.getObjectName()}, "${ast.getAttributeName()}", ModificationOp.REPLACE, m.${ast.getObjectName()}_${ast.getValue()}_before.get(${ast.getObjectName()}), d_copy);
    this.modelAccessor.notifyNodeAttach(d_copy, ${ast.getObjectName()});
  }
</#if>
