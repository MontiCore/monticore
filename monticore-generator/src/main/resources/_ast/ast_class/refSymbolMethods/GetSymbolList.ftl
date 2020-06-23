<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbol", "simpleSymbolName")}
    update${attributeName?cap_first}Surrogate();
    java.util.List<Optional<${referencedSymbol}>> temp = new java.util.ArrayList<>();
        if (getEnclosingScope() != null) {
            for (String element : this.names) {
                //create the returned list, because the names list has not changed
                try{
                    temp.add(Optional.ofNullable(${attributeName}Surrogate.get(element).lazyLoadDelegate()));
                }catch(Exception e){
                    temp.add(Optional.empty());
                }
            }
    }
    return temp;
