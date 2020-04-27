<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbol", "simpleSymbolName")}
    update${attributeName?cap_first}Loader();
    java.util.List<Optional<${referencedSymbol}>> temp = new java.util.ArrayList<>();
        if (getEnclosingScope() != null) {
            for (String element : this.names) {
                //create the returned list, because the names list has not changed
                if (${attributeName}Loader.get(element).isSymbolLoaded()) {
                    temp.add(Optional.ofNullable(${attributeName}Loader.get(element).getLoadedSymbol()));
                } else {
                    temp.add(Optional.empty());
                }
            }
    }
    return temp;
