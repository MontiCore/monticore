<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("loaderAttributeName", "atributeName", "attributeType", "wasOptional")}
<#if !wasOptional>
    if (${loaderAttributeName} == null) {
      ${loaderAttributeName} = new ${attributeType}(this.get${atributeName?cap_first}());
      ${loaderAttributeName}.setEnclosingScope(this.getEnclosingScope());
    } else {
        if (get${atributeName?cap_first}() != null && !get${atributeName?cap_first}().equals(${loaderAttributeName}.getName())) {
          ${loaderAttributeName}.setName(get${atributeName?cap_first}());
        } else if (get${atributeName?cap_first}() == null && ${loaderAttributeName}.getName() != null) {
          ${loaderAttributeName}.setName(null);
        }
<#else>
    if (${loaderAttributeName} == null) {
      ${loaderAttributeName} = new ${attributeType}(isPresent${atributeName?cap_first}() ? this.get${atributeName?cap_first}() : null);
      ${loaderAttributeName}.setEnclosingScope(this.getEnclosingScope());
    } else {
        if (isPresent${atributeName?cap_first}() && !get${atributeName?cap_first}().equals(${loaderAttributeName}.getName())) {
          ${loaderAttributeName}.setName(get${atributeName?cap_first}());
        } else if (!isPresent${atributeName?cap_first}() && ${loaderAttributeName}.getName() != null) {
          ${loaderAttributeName}.setName(null);
        }
</#if>
        if (getEnclosingScope() != null && !getEnclosingScope().equals(${loaderAttributeName}.getEnclosingScope())) {
          ${loaderAttributeName}.setEnclosingScope(getEnclosingScope());
        }else if (getEnclosingScope() == null && ${loaderAttributeName}.getEnclosingScope() != null) {
          ${loaderAttributeName}.setEnclosingScope(null);
        }
    }