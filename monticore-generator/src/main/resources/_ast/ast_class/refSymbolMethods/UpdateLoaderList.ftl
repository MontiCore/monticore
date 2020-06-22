<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("loaderAttributeName", "attributeName", "attributeType")}
    for (String name : ${attributeName}) {
        if (!${loaderAttributeName}.containsKey(name)) {
            ${attributeType} attr = new ${attributeType}(name);
            attr.setEnclosingScope(this.getEnclosingScope());
            ${loaderAttributeName}.put(name, attr);
        }
        ${attributeType} loader = namesSymbolSurrogate.get(name);
        if (loader == null) {
            ${attributeType} attr = new ${attributeType}(name);
            attr.setEnclosingScope(this.getEnclosingScope());
            ${loaderAttributeName}.put(name, attr);
        } else if (!loader.getName().equals(name)) {
            loader.setName(name);
        } else if (getEnclosingScope() != null && !getEnclosingScope().equals(loader.getEnclosingScope())) {
            loader.setEnclosingScope(getEnclosingScope());
        }
    }