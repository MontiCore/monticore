<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
${defineHookPoint("Setter:Before")}
this.${attribute.getName()} = () -> ${attribute.getName()};
${defineHookPoint("Setter:After")}
