<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("lang", "attribute")}
if (${attribute} == null) {
    ${attribute} = ${lang}TagDefinitionMill.traverser();
}
return ${attribute};