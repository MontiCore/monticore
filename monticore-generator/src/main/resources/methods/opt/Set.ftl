<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "nativeAttributeName")}
set${nativeAttributeName?cap_first}Opt(Optional.ofNullable(${attribute.getName()}));
