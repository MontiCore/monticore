<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
return ast.getTagsList().stream()
.filter(t -> t.getModelElementIdentifierList().stream()
.anyMatch(i -> isIdentified(i, element)));
