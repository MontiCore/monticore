<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "clazz", "superclasses")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign nameHelper = glex.getGlobalVar("nameHelper")>
   mill = null;
   <#list clazz.getCDAttributeList() as attribute>
     <#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
     ${attributeName} = null;
   </#list>
   <#list superclasses as superClass>
     // TODO : add after next release
     // ${superClass?lower_case}._ast.${nameHelper.getSimpleName(superClass)}Mill.reset();
   </#list>