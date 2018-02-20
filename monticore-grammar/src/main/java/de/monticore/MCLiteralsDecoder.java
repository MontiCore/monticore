/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import java.io.CharConversionException;
import de.se_rwth.commons.logging.Log;

/**
 * This class provides methods for converting literals. The LiteralsHelper is a singleton.
 * 
 * @author Martin Schindler
 */
public class MCLiteralsDecoder {
  
  /**
   * Decodes a char literal into a char
   * 
   * @param s char literal as string including "'"
   * @return decoded char
   * @throws CharConversionException
   */
  public static char decodeChar(String s) {
    if (s.length() == 1) { // single char
      return s.charAt(0);
    }
    else if (s.length() == 2 && s.charAt(0) == '\\') { // escape sequence
      switch (s.charAt(1)) {
        case 'b':
          return '\b';
        case 't':
          return '\t';
        case 'n':
          return '\n';
        case 'f':
          return '\f';
        case 'r':
          return '\r';
        case '"':
          return '\"';
        case '\'':
          return '\'';
        case '\\':
          return '\\';
        default:
          break;
      }
    }
    else if (s.charAt(0) == '\\' && s.charAt(1) == 'u') { // unicode
      return (char) Integer.parseInt(s.substring(2), 16);
    }
    Log.error("0xA4080 Unable to convert String " + s + " to char.");
    // Noramlly this statement is not reachable
    throw new IllegalStateException();
  }
  
  /**
   * Decodes a string literal into a string
   * 
   * @param s string literal excluding '"'
   * @return decoded string
   * @throws CharConversionException
   */
  public static String decodeString(String s) {
    StringBuilder ret = new StringBuilder();
    String in = s;
    
    while (in.length() != 0) {
      if (in.charAt(0) == '\\') {
        if (in.charAt(1) == 'u') { // unicode
          ret.append(decodeChar(in.substring(0, 6)));
          in = in.substring(6);
        }
        else { // escape sequence
          ret.append(decodeChar(in.substring(0, 2)));
          in = in.substring(2);
        }
      }
      else { // single char
        ret.append(in.charAt(0));
        in = in.substring(1);
      }
    }
    return ret.toString();
  }
  
  /**
   * Decodes an int literal into an int
   * 
   * @param s int literal as string including '"'
   * @return decoded int
   */
  public static int decodeInt(String s) {
    int radix = 10;
    String in = removeUnderscores(s);
    if (in.startsWith("0") && in.length() > 1) {
      if (in.startsWith("0x") || in.startsWith("0X")) {
        return Integer.parseInt(in.substring(2), 16);
      }
      else if (in.startsWith("0b") || in.startsWith("0B")) {
        return Integer.parseInt(in.substring(2), 2);
      }
      else {
        radix = 8;
      }
    }
    return Integer.parseInt(in, radix);
  }
  
  /**
   * Decodes a long literal into a long
   * 
   * @param s long literal as string including '"'
   * @return decoded long
   */
  public static long decodeLong(String s) {
    int radix = 10;
    s = s.substring(0, s.length() - 1);
    String in = removeUnderscores(s);
    if (in.startsWith("0") && in.length() > 2) {
      if (in.startsWith("0x") || in.startsWith("0X")) {
        radix = 16;
        in = in.substring(2);
      }
      else if (in.startsWith("0b") || in.startsWith("0B")) {
        radix = 2;
        in = in.substring(2);
      }
      else {
        radix = 8;
      }
    }
    return Long.parseLong(in, radix);
  }
  
  /**
   * Decodes a float literal into a float
   * 
   * @param s float literal as string including '"'
   * @return decoded float
   */
  public static float decodeFloat(String s) {
    s = s.substring(0, s.length() - 1);
    s = removeUnderscores(s);
    if (s.startsWith("0x") || s.startsWith("0X")) {
      return Float.valueOf(s);
    }
    // workaround as parseFloat() does not parse 0xp1F correctly
    if (s.toLowerCase().startsWith("0xp")) {
      return Float.parseFloat("0x0p" + s.substring(3)); // 0xp1F == 0x0p1F == 0.0
    }
    return Float.parseFloat(s);
  }
  
  /**
   * Decodes a double literal into a double
   * 
   * @param s double literal as string including '"'
   * @return decoded double
   */
  public static double decodeDouble(String s) {
    if (s.endsWith("d") || s.endsWith("D")) {
      s = s.substring(0, s.length() - 1);
    }
    s = removeUnderscores(s);
    if (s.startsWith("0x") || s.startsWith("0X")) {
      return Double.valueOf(s);
    }
    // workaround as parseDouble() does not parse 0xp1 correctly
    if (s.toLowerCase().startsWith("0xp")) {
      return Double.parseDouble("0x0p" + s.substring(3)); // 0xp1 == 0x0p1 == 0.0
    }
    return Double.parseDouble(s);
  }
  
  private static String removeUnderscores(String s) {
    if (s.contains("_")) {
      
      if (s.indexOf("_") == 0) {
        Log.error("0xA4081 Do not put underscores at the beginning of the Number " + s);
      }
      
      if (s.contains("e")
          && (s.indexOf("_") == s.indexOf("e") - 1 || s.indexOf("_") == s.indexOf("e") + 1)) {
        Log.error("0xA4082 Do not put underscores before or after an 'e' in the Number" + s);
      }
      
      if (s.contains("p")
          && (s.indexOf("_") == s.indexOf("p") - 1 || s.indexOf("_") == s.indexOf("p") + 1)) {
        Log.error("0xA4083 Do not put underscores before or after an 'p' in the Number" + s);
      }
      
      if (s.contains(".")
          && (s.indexOf("_") == s.indexOf(".") - 1 || s.indexOf("_") == s.indexOf(".") + 1)) {
        Log.error("0xA4084 Do not put underscores before or after an '.' in the Number" + s);
      }
      
      if (s.startsWith("0x") || s.startsWith("0X") || s.startsWith("0b") || s.startsWith("0B")) {
        if (s.indexOf("_") == 2) {
          Log.error("0xA4081 Do not put underscores at the beginning of the Number " + s);
        }
      }
      if (s.endsWith("_")) {
        Log.error("0xA4081 Do not put underscores at the end of the Number " + s);
      }
      s = s.replaceAll("_", "");
    }
    
    return s;
  }
  
}
