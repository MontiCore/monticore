<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
  printer.beginArray("${name}");
  ${name}.stream().forEach(e -> printer.value(e));
  printer.endArray();