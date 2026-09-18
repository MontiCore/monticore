<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
${defineHookPoint("Setter:Before")}
<#-- avoid double-wrapping when the caller passes back an already-wrapped supplier -->
if (${attribute.getName()} instanceof de.monticore.symboltable.__internal__Supplier) {
  this.${attribute.getName()} = (${genHelper.printType(attribute.getMCType())}) ${attribute.getName()};
} else {
  this.${attribute.getName()} = new de.monticore.symboltable.__internal__Supplier<>(${attribute.getName()});
}
${defineHookPoint("Setter:After")}
