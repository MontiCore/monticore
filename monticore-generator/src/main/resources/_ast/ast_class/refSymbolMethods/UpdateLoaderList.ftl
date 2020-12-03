<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName", "attributeName", "symbolType")}
if (getEnclosingScope() != null) {
  for (String name : ${attributeName}) {
    if (!${symbolName}.containsKey(name)) {
      getEnclosingScope().resolve${symbolType}(name).ifPresent(s ->${symbolName}.put(name, s));
    }
  }
}