<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeAttr")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  de.monticore.symboltable.serialization.JsonPrinter printer = s2j.getJsonPrinter();
  printer.member("generated-using","www.MontiCore.de technology");
  if(toSerialize.isPresentName()) {
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, toSerialize.getName());
  }
  if(!toSerialize.getPackageName().isEmpty()) {
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.PACKAGE, toSerialize.getPackageName());
  }
<#list scopeAttr as attr>
  serialize${attr.name?cap_first}(toSerialize.${genHelper.getPlainGetter(attr)}(), s2j);
</#list>
  return printer.toString();
