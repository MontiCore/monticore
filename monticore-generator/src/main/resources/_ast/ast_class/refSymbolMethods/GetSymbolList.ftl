${tc.signature("attributeName", "referencedSymbol", "simpleSymbolName")}
    java.util.List<Optional<${referencedSymbol}>> temp = new java.util.ArrayList<>();
    if (this.${attributeName}.isEmpty() && getEnclosingScope2() != null) {
      ${attributeName}.clear();
      for (String element : this.${attributeName?remove_ending("Symbol")}) {
        //add the string to Symbol match to the map
        ${attributeName}.put(element, getEnclosingScope2().resolve${simpleSymbolName}(element));
        //create the returned list
        temp.add(${attributeName}.get(element));
      }
    }else if(getEnclosingScope2() != null){
      for (String element : this.${attributeName?remove_ending("Symbol")}) {
        //create the returned list, because the names list has not changed
        temp.add(${attributeName}.get(element));
      }
    }
    return temp;
