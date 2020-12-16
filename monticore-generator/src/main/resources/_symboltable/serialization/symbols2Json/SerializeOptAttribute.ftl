<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("optAttr")}
  if (${optAttr.getName()}.isPresent()) {
    printer.member("${optAttr.getName()}", ${optAttr.getName()}.get());
  }