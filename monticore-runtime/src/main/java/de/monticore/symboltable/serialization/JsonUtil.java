/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serialization;

import java.util.ArrayList;
import java.util.List;

import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonUtil {
  
  /**
   * Adds escape sequences for all characters that are escaped in Java Strings according to
   * https://docs.oracle.com/javase/tutorial/java/data/characters.html
   */
  public static String escapeSpecialChars(String input) {
    return input
    .replace("\\", "\\\\")  // Insert a backslash character in the text at this point.
    .replace("\t", "\\t")   // Insert a tab in the text at this point.
    .replace("\b", "\\b")   // Insert a backspace in the text at this point.
    .replace("\n", "\\n")   // Insert a newline in the text at this point.
    .replace("\r", "\\r")   // Insert a carriage return in the text at this point.
    .replace("\f", "\\f")   // Insert a formfeed in the text at this point.
    .replace("\'", "\\\'")  // Insert a single quote character in the text at this point.
    .replace("\"", "\\\""); // Insert a double quote character in the text at this point.
  }
  
  public static List<ImportStatement> deserializeImports(JsonObject scope) {
    List<ImportStatement> result = new ArrayList<>();
    if(scope.containsKey(JsonConstants.IMPORTS)) {
      for (JsonElement e : scope.get(JsonConstants.IMPORTS).getAsJsonArray().getElements()) {
        String i = e.getAsJsonString().getValue();
        result.add(new ImportStatement(i, i.endsWith("*")));
      }
    }
    return result;
  }
  
}
