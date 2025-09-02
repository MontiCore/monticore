<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("fullSymbolName", "fullSymbolType", "allSymbolSubKinds")}
    ${fullSymbolType} symbols = com.google.common.collect.LinkedListMultimap.create();
    symbols.putAll(get${fullSymbolName}());
<#list allSymbolSubKinds as subKind>
    symbols.putAll(get${subKind}Symbols());
</#list>
    return symbols;