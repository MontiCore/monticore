<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName", "attributeName", "symbolType", "wasOptional")}
<#if !wasOptional>
  if (getEnclosingScope() != null && (${symbolName} == null || !get${attributeName?cap_first}().equals(${symbolName}.getName()))) {
    ${symbolName} = getEnclosingScope().resolve${symbolType}(get${attributeName?cap_first}()).orElse(null);
  }
<#else>
  if (isPresent${attributeName?cap_first}()) {
    if (getEnclosingScope() != null && (${symbolName} == null || !get${attributeName?cap_first}().equals(${symbolName}.getName()))) {
      ${symbolName} = getEnclosingScope().resolve${symbolType}(get${attributeName?cap_first}()).orElse(null);
    }
  }
</#if>