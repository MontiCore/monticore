<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
${defineHookPoint("Setter:Before")}
this.${attribute.getName()} = de.monticore.symboltable.__internal__Supplier.of(${attribute.getName()});
${defineHookPoint("Setter:After")}
