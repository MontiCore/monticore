/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.monticore.grammar.symboltable.MCGrammarSymbol;
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
  
  public LexNamer() {}
  
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
  public String getLexName(MCGrammarSymbol grammarSymbol, String sym) {
    if (usedLex.containsKey(sym)) {
      return usedLex.get(sym);
    }
    
    String goodName = createGoodName(sym);
    if (goodName != null && !grammarSymbol.getProd(goodName).isPresent()) {
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
