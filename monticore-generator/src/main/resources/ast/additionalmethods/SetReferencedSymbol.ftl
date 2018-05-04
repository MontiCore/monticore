<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "symbolClass")}

    return (${symbolClass}) enclosingScope.get().resolve(${attributeName}, ${symbolClass}.KIND).get();
