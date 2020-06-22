<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbol", "simpleSymbolName")}
    update${attributeName?cap_first}Surrogate();
    java.util.List<Optional<${referencedSymbol}>> temp = new java.util.ArrayList<>();
        if (getEnclosingScope() != null) {
            for (String element : this.names) {
                //create the returned list, because the names list has not changed
                if (${attributeName}Surrogate.get(element).lazyLoadDelegate()!=null) {
                    temp.add(Optional.ofNullable(${attributeName}Surrogate.get(element).lazyLoadDelegate()));
                } else {
                    temp.add(Optional.empty());
                }
            }
    }
    return temp;
