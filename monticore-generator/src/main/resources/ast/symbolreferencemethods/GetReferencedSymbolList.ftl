${tc.signature("attributeName", "symbolName", "referencedSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
    java.util.List<Optional<${referencedSymbol}>> temp = new java.util.ArrayList<>();
    if (this.${attributeName}Map.isEmpty() && this.enclosingScope.isPresent()) {
      ${attributeName}Map.clear();
      for (String element : this.${attributeName}) {
        //add the string to Symbol match to the map
        ${attributeName}Map.put(element, ((${genHelper.getQualifiedScopeInterfaceType(genHelper.getCdSymbol())}) this.enclosingScope.get()).resolve${symbolName}(element));
        //create the returned list
        temp.add(${attributeName}Map.get(element));
      }
    }else if(enclosingScope.isPresent()){
      for (String element : this.${attributeName}) {
        //create the returned list, because the names list has not changed
        temp.add(${attributeName}Map.get(element));
      }
    }
    return temp;