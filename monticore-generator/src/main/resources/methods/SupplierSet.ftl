<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
${defineHookPoint("Setter:Before")}
this.${attribute.getName()} = new de.monticore.symboltable.__internal__Supplier<>(() -> ${attribute.getName()});
${defineHookPoint("Setter:After")}
