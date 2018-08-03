/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class SymbolTableSerializationHelper {
  
  
  public static Collection<Symbol> getLocalSymbols(Scope scope){
    Collection<Symbol> symbols = new ArrayList<>();
    for(Collection<Symbol> s : scope.getLocalSymbols().values()) {
      symbols.addAll(s);
    }
    return symbols;
  }
  
  public static List<ImportStatement> deserializeImports(JsonElement i) {
    List<ImportStatement> imports = new ArrayList<>();
    // context.deserialize(jsonObject.get("imports"), List.class);
    JsonArray list = i.getAsJsonArray();
    for (JsonElement e : list) {
      String importStatement = e.getAsString();
      imports.add(new ImportStatement(importStatement, importStatement.endsWith("*")));
    }
    return imports;
  }
  
}
