${tc.signature("attributeName", "referencedSymbol")}
    java.util.List<Optional<${referencedSymbol}>> temp = new java.util.ArrayList<>();
    if (this.${attributeName}Map.isEmpty() && this.enclosingScope.isPresent()) {
      ${attributeName}Map.clear();
      for (String element : this.${attributeName}) {
        //add the string to Symbol match to the map
        ${attributeName}Map.put(element, this.enclosingScope.get().resolve(element, ${referencedSymbol}.KIND));
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