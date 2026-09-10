<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "nativeAttributeName")}
${defineHookPoint("Setter:Before")}
this.${attribute.getName()} = new de.monticore.symboltable.__internal__Supplier<>(() -> Optional.ofNullable(${attribute.getName()}));
${defineHookPoint("Setter:After")}
