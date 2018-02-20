<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName")}
    return <#if ast.isPresentModifier() && !ast.getModifier().isStatic()> this.</#if>${attributeName};
