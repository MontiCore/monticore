// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint.data;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;

import java.util.Map;
import java.util.Set;

/**
 * Class used for constructing pretty printers for rule-components.
 */
@SuppressWarnings("unused") // Used in templates
public class PPGuardComponent {

  protected final PPGuardType type;

  protected final BlockData blockData;

  protected final String name;
  protected final String nameToUse;

  protected final String separator;

  protected final Set<Map.Entry<String, String>> constants;
  protected final int iteration;

  protected final boolean isMCCommonLiteralsSuper;

  protected String tokenType; // the corresponding token type (e.g., Name, AUT1234)
  protected String nameOrIndex; // the position in the parsetree
  protected boolean hasNoSpace = false;

  // NEW: Explicitly track if this is a Lexical token (String) or a Parser rule (ASTNode)
  protected final boolean isLexical;

  // For tracing
  protected ASTNode node;

  public PPGuardComponent(PPGuardType type, BlockData blockData, String name, String nameToUse,
                          String separator, Set<Map.Entry<String, String>> constants, int iteration, boolean isMCCommonLiteralsSuper,
                          ASTNode node,
                          boolean isLexical, String tokenType, String nameOrIndex) {
    this.type = type;
    this.blockData = blockData;
    this.name = name;
    this.nameToUse = nameToUse;
    this.separator = separator;
    this.constants = constants;
    this.iteration = iteration;
    this.isMCCommonLiteralsSuper = isMCCommonLiteralsSuper;
    this.node = node;
    this.isLexical = isLexical;
    this.tokenType = tokenType;
    this.nameOrIndex = nameOrIndex;
  }

  public PPGuardType getType() {
    return type;
  }

  public BlockData getBlockData() {
    if (this.getType() != PPGuardType.BLOCK) throw new IllegalStateException("Not a block");

    return blockData;
  }

  public String getName() {
    return name;
  }

  public String getNameToUse() {
    return nameToUse;
  }

  public String getSeparator() {
    return separator;
  }

  public Set<Map.Entry<String, String>> getConstants() {
    return constants;
  }

  public int getIteration() {
    return iteration;
  }

  public boolean isList() {
    return getIteration() == ASTConstantsGrammar.PLUS || getIteration() == ASTConstantsGrammar.STAR;
  }

  /**
   * Anything which is represented by a String in the AST (such as lexed tokens)
   */
  public boolean isStringType() {
    return this.isLexical;
  }

  public boolean isCommonTokenString() {
    // CommonLiterals uses subString on the "String" LexProd
    return "String".equals(getName());
  }

  public boolean isCommonTokenChar() {
    // CommonLiterals uses subString on the "Char" LexProd
    return "Char".equals(getName());
  }

  public boolean isOpt() {
    return getIteration() == ASTConstantsGrammar.QUESTION;
  }

  public boolean isHasNoSpace() {
    return this.hasNoSpace;
  }

  public String getTokenType() {
    return this.tokenType;
  }

  public String getNameOrIndex() {
    return this.nameOrIndex;
  }

  /**
   * Guess if no Space before or after this terminal/constant is a possibility
   * If the terminal/constant is only consisting of non alpha-numeric or question mark characters
   * @param value the string/terminal to be printed
   */
  public boolean isTerminalNoSpace(String value) {
    if (this.type == PPGuardType.T || this.type == PPGuardType.CG) {
      for (int i = 0, l = value.length(); i < l; i++) {
        if (value.charAt(i) == '?' || Character.isAlphabetic(value.charAt(i)) || Character.isDigit(value.charAt(i)))
          return false;
      }
      return true;
    }
    return false;
  }

  public void setHasNoSpace(boolean hasNoSpace) {
    this.hasNoSpace = hasNoSpace;
  }

  public void setNameOrIndex(String nameOrIndex) {
    this.nameOrIndex = nameOrIndex;
  }

  public enum PPGuardType {
    /**
     * A block
     */
    BLOCK,
    /**
     * A ConstantGroup
     */
    CG,
    /**
     * A NonTerminalReference
     */
    NT,
    /**
     * A NonTerminalReference using an Iterator (e.g. due to multiple occurrences)
     */
    NT_ITERATED,
    /**
     * A NonTerminalReference where the attribute is a List but the iteration indicates a default repetition
     * Might occur due to ASTRules
     */
    NT_AST_DEF,
    /**
     * A Terminal
     */
    T
  }

  public static PPGuardComponent forBlock(BlockData blockData,
                                          int iteration, ASTNode node, String tokenType) {
    return new PPGuardComponent(PPGuardType.BLOCK, blockData, null, null, null, null, iteration, false, node, false, tokenType, null);
  }

  public static PPGuardComponent forNT(String name, String nameToUse,
                                       int iteration, boolean iterated, boolean isMCCommonLiteralsSuper, ASTNode node, boolean isLexical, String tokenType) {
    return new PPGuardComponent(iterated ? PPGuardType.NT_ITERATED : PPGuardType.NT, null, name, nameToUse, null, null, iteration, isMCCommonLiteralsSuper, node, isLexical, tokenType, null);
  }

  public static PPGuardComponent forNTSingle(String name, String nameToUse,
                                             int iteration, boolean isMCCommonLiteralsSuper, ASTNode node, boolean isLexical, String tokenType) {
    return new PPGuardComponent(PPGuardType.NT_AST_DEF, null, name, nameToUse, null, null, iteration, isMCCommonLiteralsSuper, node, isLexical, tokenType, null);
  }

  public static PPGuardComponent forT(String name, ASTNode node, String tokenType) {
    return new PPGuardComponent(PPGuardType.T, null, name, name, null, null, ASTConstantsGrammar.DEFAULT, false, node, false, tokenType, null);
  }

  public static PPGuardComponent forT(String name, String usageName, int iteration, ASTNode node, String tokenType) {
    return new PPGuardComponent(PPGuardType.T, null, name, usageName, null, null, iteration, false, node, false, tokenType, null);
  }

  public static PPGuardComponent forCG(String usageName, Set<Map.Entry<String, String>> constants, ASTNode node, String tokenType) {
    return new PPGuardComponent(PPGuardType.CG, null, usageName, usageName, null, constants, 0, false, node, false, tokenType, null);
  }

}
