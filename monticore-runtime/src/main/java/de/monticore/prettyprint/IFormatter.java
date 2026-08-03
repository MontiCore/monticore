/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

public interface IFormatter {
  
  /**
   * A linebreak should be done before this token
   */
  public static int LINEBREAK_PRE = 0b00001;
  /**
   * A linebreak should be done after this token
   */
  public static int LINEBREAK_POST = 0b00010;
  /**
   * The following content should be indented
   */
  public static int INDENT = 0b00100;
  /**
   * The following content should be un-indented
   */
  public static int UNINDENT = 0b01000;
  /**
   * The following content should be followed by a space
   */
  public static int SPACE_FOLLOWING = 0b10000;
  
  int getFormatOptions(String token, String tokenType, String position, String productionName,
      String lastTokenType, String lastTokenString, String nextTokenType, String nextTokenString,
      int depth);
  
  /**
   * Default implementation, based on curly brackets and semicolons
   * TODO: This default formatter currently does not support the noSpace directive
   */
  public class DefaultIFormatter implements IFormatter {
    
    @Override
    public int getFormatOptions(String token, String tokenType, String position,
        String productionName, String lastTokenType, String lastTokenString, String nextTokenType,
        String nextTokenString, int depth) {
      if ("}".equals(token)) {
        return LINEBREAK_PRE | UNINDENT;
      }
      int withSpace = isTerminalNoSpace(token) ? 0 : SPACE_FOLLOWING;
      if (token.equals(";")) {
        return LINEBREAK_POST | withSpace;
      }
      else if ("{".equals(token)) {
        return withSpace | LINEBREAK_POST | INDENT;
      }
      return withSpace;
    }
    
    public boolean isTerminalNoSpace(String value) {
      for (int i = 0, l = value.length(); i < l; i++) {
        if (value.charAt(i) == '?' || Character.isAlphabetic(value.charAt(i)) || Character.isDigit(
            value.charAt(i))) {
          return false;
        }
      }
      return true;
    }
  }
}
