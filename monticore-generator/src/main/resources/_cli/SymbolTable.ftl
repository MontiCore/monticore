<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millFullName", "scopesGenitorDelegator", "artifactScope")}

${scopesGenitorDelegator} genitor = ${millFullName}.scopesGenitorDelegator();
${artifactScope} symTab = genitor.createFromAST(node);
return symTab;