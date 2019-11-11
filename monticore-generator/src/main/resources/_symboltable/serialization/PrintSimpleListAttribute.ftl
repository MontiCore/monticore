<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attr")}
  //printer.beginArray();
  ${attr}.stream().forEach(e -> printer.value(e));
  //printer.endArray();