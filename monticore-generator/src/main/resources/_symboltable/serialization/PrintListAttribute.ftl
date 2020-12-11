<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
  de.monticore.symboltable.serialization.JsonPrinter p = s2j.getJsonPrinter();
  p.beginArray("${name}");
  ${name}.stream().forEach(e -> p.value(e));
  p.endArray();