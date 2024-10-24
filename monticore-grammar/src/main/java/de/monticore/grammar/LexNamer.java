/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;

/**
 * Class generates human readable names for Lexersymbols
 * 
 */
public class LexNamer {
  
  protected int constantCounter = 0;

  protected int lexCounter = 0;
  
  protected Map<String, String> usedLex = new HashMap<String, String>();
  
  protected Map<String, String> usedConstants = new HashMap<String, String>();
  
  protected static Map<String, String> goodNames = null;
  
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
      goodNames.put("-", "MINUS");
      goodNames.put("*", "STAR");
      goodNames.put("%", "PERCENT");
      goodNames.put("/", "SLASH");
      goodNames.put("&", "AND_");
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
   * Returns a good name for the lex symbol or ""
   */
  public static String createGoodName(String x) {

    if (x.matches("[a-zA-Z][a-zA-Z_0-9]*")) {
      return x.toUpperCase()+Integer.toUnsignedString(x.hashCode());
    }

    if (x.matches("[^a-zA-Z0-9]+")) {
      StringBuilder ret = new StringBuilder();
      for (int i = 0; i < x.length(); i++) {

        String substring = x.substring(i, i + 1);
        if (getGoodNames().containsKey(substring)) {
          ret.append(getGoodNames().get(substring));
        } else {
          return "";
        }
      }
      return ret.toString();
    }
    return "";

  }

  /**
   * Returns a good name for the lex symbol or ""
   */
  public static String createSimpleGoodName(String x) {

    if (x.matches("[a-zA-Z][a-zA-Z_0-9]*")) {
      return x.toUpperCase();
    }

    if (x.matches("[^a-zA-Z0-9]+")) {
      StringBuilder ret = new StringBuilder();
      for (int i = 0; i < x.length(); i++) {

        String substring = x.substring(i, i + 1);
        if (getGoodNames().containsKey(substring)) {
          ret.append(getGoodNames().get(substring));
        } else {
          return "";
        }
      }
      return ret.toString();
    }
    return "";

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
    if (goodName.isEmpty() || grammarSymbol.getProd(goodName).isPresent()) {
      goodName = "LEXNAME" + lexCounter++;
    }
    usedLex.put(sym, goodName);
    Log.debug("Using lexer symbol " + goodName + " for symbol '" + sym + "'", "LexNamer");
    return goodName;
  }
  
  public String getConstantName(String sym) {
    String s = sym.intern();
    
    if (!usedConstants.containsKey(s)) {
      String goodName = createSimpleGoodName(s);
      if (!goodName.isEmpty()) {
        usedConstants.put(s, goodName);
      }
      else {
        usedConstants.put(s, ("CONSTANT" + constantCounter++).intern());
      }
    }
    
    String name = usedConstants.get(sym.intern());
    Log.debug("Using lexer constant " + name + " for symbol '" + s + "'", "LexNamer");
    
    return name;
  }

  protected String convertKeyword(String key)  {
    key = StringUtils.replace(key, "\\\"", "\"");
    key = StringUtils.replace(key, "'", "\\'");
    return key;
  }


  public Set<String> getLexnames() {
    return usedLex.keySet();
  }
}
