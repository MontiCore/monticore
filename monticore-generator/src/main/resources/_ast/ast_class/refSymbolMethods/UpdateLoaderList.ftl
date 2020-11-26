<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName", "attributeName", "symbolType")}
for (String name : ${attributeName}) {
  if (!${symbolName}.containsKey(name)) {
    ${symbolName}.put(name, getEnclosingScope().resolve${symbolType}(get${attributeName?cap_first}()));
  }
}