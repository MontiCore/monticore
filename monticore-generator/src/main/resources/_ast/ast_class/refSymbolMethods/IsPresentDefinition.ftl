<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName")}
    return isPresent${attributeName?cap_first}Symbol() && get${attributeName?cap_first}Symbol().isPresentAstNode();
