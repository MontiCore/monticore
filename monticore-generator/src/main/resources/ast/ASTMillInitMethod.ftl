<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "clazz", "origName", "millName", "overridden")}
   mill = new ${millName}();
   <#list overridden as superClass>
   ${superClass.getFullName()?lower_case}._ast.${superClass.getName()}Mill.initMe(new ${superClass.getName()}MillFor${origName}());
   </#list>