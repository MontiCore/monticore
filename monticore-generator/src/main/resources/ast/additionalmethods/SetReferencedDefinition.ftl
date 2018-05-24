<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName")}
      set${attributeName?cap_first}DefinitionAbsent();
      set${attributeName?cap_first}(ast.getName());