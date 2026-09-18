<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
this.${attribute.getName()} = new de.monticore.symboltable.__internal__Supplier<>(() -> Optional.ofNullable(${attribute.getName()}));
return this.realBuilder;
