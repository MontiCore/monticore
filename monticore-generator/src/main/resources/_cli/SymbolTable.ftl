<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarname", "millFullName", "scopesGenitorDelegator", "artifactScope")}

${scopesGenitorDelegator} genitor = ${millFullName}.scopesGenitorDelegator();
${artifactScope} symTab = genitor.createFromAST(node);
return symTab;