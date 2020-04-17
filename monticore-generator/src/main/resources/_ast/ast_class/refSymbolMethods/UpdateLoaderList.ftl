<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("loaderAttributeName", "attributeName", "attributeType")}
    for (String name : ${attributeName}) {
        if (!${loaderAttributeName}.containsKey(name)) {
            ${loaderAttributeName}.put(name, new ${attributeType}(name, this.getEnclosingScope()));
        }
        ${attributeType} loader = namesSymbolLoader.get(name);
        if (loader == null) {
            ${loaderAttributeName}.put(name, new ${attributeType}(name, this.getEnclosingScope()));
        } else if (!loader.getName().equals(name)) {
            loader.setName(name);
        } else if (getEnclosingScope() != null && !getEnclosingScope().equals(loader.getEnclosingScope())) {
            loader.setEnclosingScope(getEnclosingScope());
        }
    }