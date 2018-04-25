<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName", "symbolClass")}

    return (${symbolClass}) enclosingScope.get().resolve(${attributeName}, ${symbolClass}.KIND).get();
