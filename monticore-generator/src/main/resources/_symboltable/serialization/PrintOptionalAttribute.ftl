<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
  if(${name}.isPresent()){
    s2j.getJsonPrinter().memberNoDef("${name}", ${name}.get());
  }
