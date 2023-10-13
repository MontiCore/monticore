<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("lang", "productions")}
super(tagSchemaSymbol, tagConformanceChecker);

getIdentifierTraverser().add4${lang}TagDefinition(new ${lang}TagDefinitionVisitor2() {
<#list productions as p>
    ${tc.includeArgs("IdentifierTraverser", p)}
</#list>
});
