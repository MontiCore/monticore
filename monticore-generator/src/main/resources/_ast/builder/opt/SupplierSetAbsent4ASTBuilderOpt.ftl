<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
this.${attribute.getName()} = new de.monticore.symboltable.__internal__Supplier<>(() -> Optional.empty());
return this.realBuilder;
