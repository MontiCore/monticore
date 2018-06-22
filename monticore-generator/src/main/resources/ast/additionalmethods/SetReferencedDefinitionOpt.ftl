<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName")}
        set${attributeName?cap_first}DefinitionAbsent();
        set${attributeName?cap_first}(astOpt.get().getName());