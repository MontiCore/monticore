<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("prodname", "grammarname", "package", "isSymbol")}
<#-- isSymbol: true for symbols -->
astTagUnit.addTags(
    TagsMill.targetElementBuilder()
        .addModelElementIdentifier(
            <#if isSymbol>
            TagsMill.defaultIdentBuilder().setMCQualifiedName(
                TagsMill.mCQualifiedNameBuilder().setPartsList(de.se_rwth.commons.Splitters.QUALIFIED_NAME_DELIMITERS.splitToList(model.getSymbol().getFullName())).build()
            ).build()
            <#else>
            ${package}tagdefinition.${grammarname}TagDefinitionMill.${prodname?uncap_first}IdentifierBuilder().setIdentifiesElement(model).build()
            </#if>
        )
        .addTag(astTag)
    .build()
);