<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "attributeName")}
    return <#if ast.isPresentModifier() && !ast.getModifier().isStatic()> this.</#if>${attributeName};
