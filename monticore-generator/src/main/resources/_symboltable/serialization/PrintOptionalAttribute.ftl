<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
  if(${name}.isPresent()){
    s2j.getJsonPrinter().member("${name}", ${name});
  }
