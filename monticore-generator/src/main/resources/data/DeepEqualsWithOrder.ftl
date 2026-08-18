<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("allAttributes", "simpleClassName")}
   <#assign genHelper = glex.getGlobalVar("astHelper")>
  ${simpleClassName} comp;
  if ((o instanceof ${simpleClassName})) {
    comp = (${simpleClassName}) o;
  } else {
    return false;
  }
  if (!equalAttributes(comp)) {
    return false;
  }
  <#list allAttributes  as attribute>
    <#assign attrName = attribute.getName()>
    <#if genHelper.isOptionalAstNode(attribute)>
    // comparing ${attrName}   
    if ( this.${attrName}.isPresent() != comp.${attrName}.isPresent() ||
      (this.${attrName}.isPresent() && !this.${attrName}.get().deepEquals(comp.${attrName}.get(), forceSameOrder)) ) {
      return false;
    }
       <#elseif genHelper.isListAstNode(attribute)>
    // comparing ${attrName}
    if (this.${attrName}.size() != comp.${attrName}.size()) {
      return false;
    } else {
      <#assign astChildTypeName = genHelper.getNativeTypeName(attribute.getMCType())>
      if (forceSameOrder) {
        Iterator<${astChildTypeName}> it1 = this.${attrName}.iterator();
        Iterator<${astChildTypeName}> it2 = comp.${attrName}.iterator();
        while (it1.hasNext() && it2.hasNext()) {
          if (!it1.next().deepEquals(it2.next(), forceSameOrder)) {
            return false;
          }
        }
      } else {
        for (${astChildTypeName} oneNext : this.${attrName}) {
          boolean matchFound = false;
          for (${astChildTypeName} annotation : comp.${attrName}) {
            if (oneNext.deepEquals(annotation, forceSameOrder)) {
              matchFound = true;
              break;
            }
          }
          if (!matchFound) {
            return false;
          }
        }
      }
    }
    <#elseif genHelper.isSimpleAstNode(attribute)>
    // comparing ${attrName}
    if ( (this.${attrName} == null && comp.${attrName} != null) ||
      (this.${attrName} != null && !this.${attrName}.deepEquals(comp.${attrName}, forceSameOrder)) ) {
      return false;
    }
    </#if>
  </#list>
  return true;