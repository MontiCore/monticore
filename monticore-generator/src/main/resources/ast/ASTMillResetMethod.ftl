<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz", "superclasses")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign nameHelper = glex.getGlobalVar("nameHelper")>
   mill = null;
   <#list clazz.getCDAttributeList() as attribute>
     <#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
     ${attributeName} = null;
   </#list>
   <#list superclasses as superClass>
     ${superClass?lower_case}._ast.${nameHelper.getSimpleName(superClass)}Mill.reset();
   </#list>