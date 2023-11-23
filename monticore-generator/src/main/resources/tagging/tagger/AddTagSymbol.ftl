<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("prodname", "grammarname", "package")}
<#-- isSymbol: true for symbols -->
astTagUnit.addTags(
    TagsMill.targetElementBuilder()
        .addModelElementIdentifier(
            TagsMill.defaultIdentBuilder().setMCQualifiedName(
                TagsMill.mCQualifiedNameBuilder().setPartsList(de.se_rwth.commons.Splitters.QUALIFIED_NAME_DELIMITERS.splitToList(symbol.getFullName())).build()
            ).build()
        )
        .addTag(astTag)
    .build()
);