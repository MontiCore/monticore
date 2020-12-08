<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
  if(${name}.isPresent()){
    printer.member("${name}", ${name});
  }
