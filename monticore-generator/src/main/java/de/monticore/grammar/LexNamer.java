/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.grammar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.se_rwth.commons.logging.Log;

/**
 * Class generates human readable names for Lexersymbols
 * 
 * @author krahn
 */
public class LexNamer {
  
  private int j = 0;
  
  private Map<String, String> usedLex = new HashMap<String, String>();
  
  private Map<String, String> usedConstants = new HashMap<String, String>();
  
  private static Map<String, String> goodNames = null;
  
  public static Map<String, String> getGoodNames() {
    if (goodNames == null) {
      goodNames = new HashMap<String, String>();
      // Put all common names here, one character only, since all others are
      // concatanation of these
      goodNames.put(";", "SEMI");
      goodNames.put("@", "AT");
      goodNames.put("#", "HASH");
      goodNames.put(".", "POINT");
      goodNames.put(",", "COMMA");
      goodNames.put("?", "QUESTION");
      goodNames.put("§", "LEX");
      goodNames.put("\"", "QUOTE");
      goodNames.put("'", "APOSTROPHE");
      goodNames.put("$", "DOLLAR");
      goodNames.put("~", "TILDE");
      goodNames.put(">", "GT");
      goodNames.put("<", "LT");
      goodNames.put("=", "EQUALS");
      goodNames.put("+", "PLUS");
      goodNames.put("++", "PLUSPLUS");
      goodNames.put("-", "MINUS");
      goodNames.put("*", "STAR");
      goodNames.put("%", "PERCENT");
      goodNames.put("/", "SLASH");
      goodNames.put("&", "AND");
      goodNames.put("|", "PIPE");
      goodNames.put(":", "COLON");
      goodNames.put("!", "EXCLAMATIONMARK");
      goodNames.put("^", "ROOF");
      
      // Don't change the following, unless you change Grammar2Antlr too
      goodNames.put("(", "LPAREN");
      goodNames.put(")", "RPAREN");
      goodNames.put("[", "LBRACK");
      goodNames.put("]", "RBRACK");
      goodNames.put("{", "LCURLY");
      goodNames.put("}", "RCURLY");
    }
    return goodNames;
  }
  
  /**
   * Returns a good name for the lex symbol, or null it is not posssible
   */
  
  public static String createGoodName(String x) {
    StringBuilder ret = new StringBuilder();
    
    if (x.matches("[a-zA-Z]+")) {
      return x.toUpperCase();
    }
    
    for (int i = 0; i < x.length(); i++) {
      
      String substring = x.substring(i, i + 1);
      if (getGoodNames().containsKey(substring)) {
        ret.append(getGoodNames().get(substring));
      }
      else {
        ret = null;
        break;
      }
    }
    
    if (ret != null) {
      return ret.toString();
    }
    else
      return null;
      
  }
  
  /**
   * Returns Human-Readable, antlr conformed name for a lexsymbols nice names for common tokens
   * (change constructor to add tokenes) LEXi where i is number for unknown ones
   * 
   * @param sym lexer symbol
   * @return Human-Readable, antlr conformed name for a lexsymbols
   */
  public String getLexName(Collection<String> ruleNames, String sym) {
    if (usedLex.containsKey(sym)) {
      return usedLex.get(sym);
    }
    
    String goodName = createGoodName(sym);
    if (goodName != null && !ruleNames.contains(goodName)) {
      usedLex.put(sym, goodName);
      Log.debug("Using lexer symbol " + goodName + " for symbol '" + sym + "'", "LexNamer");
      return goodName;
    }
    
    return "'" + sym + "'";
  }
  
  public String getConstantName(String sym) {
    String s = sym.intern();
    
    if (!usedConstants.containsKey(s)) {
      String goodName = createGoodName(s);
      if (goodName != null) {
        usedConstants.put(s, goodName);
      }
      else {
        usedConstants.put(s, ("CONSTANT" + j++).intern());
      }
    }
    
    String name = usedConstants.get(sym.intern());
    Log.debug("Using lexer constant " + name + " for symbol '" + s + "'", "LexNamer");
    
    return name;
  }
  
  public Set<String> getLexnames() {
    return usedLex.keySet();
  }
}
