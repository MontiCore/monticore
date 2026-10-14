<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
${defineHookPoint("Setter:Before")}
this.${attribute.getName()} = de.monticore.symboltable.SuppliedList.fromValues(${attribute.getName()});
${defineHookPoint("Setter:After")}
return this.realBuilder;
