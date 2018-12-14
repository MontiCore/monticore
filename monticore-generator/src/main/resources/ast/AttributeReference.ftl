<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "astType")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
<#if genHelper.isListType(attribute.printType())>
  ${attribute.printModifier()} java.util.Map<String, Optional<${genHelper.getReferencedSymbolName(attribute)}>> ${attributeName}Map = new java.util.HashMap<>();
<#else >
  ${attribute.printModifier()} Optional<${genHelper.getReferencedSymbolName(attribute)}> ${attributeName}Symbol = Optional.empty();
</#if>
