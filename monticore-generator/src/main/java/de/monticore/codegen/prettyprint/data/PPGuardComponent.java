// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint.data;

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

  protected boolean hasNoSpace = false;

  public PPGuardComponent(PPGuardType type, BlockData blockData, String name, String nameToUse,
                          String separator, Set<Map.Entry<String, String>> constants, int iteration, boolean isMCCommonLiteralsSuper) {
    this.type = type;
    this.blockData = blockData;
    this.name = name;
    this.nameToUse = nameToUse;
    this.separator = separator;
    this.constants = constants;
    this.iteration = iteration;
    this.isMCCommonLiteralsSuper = isMCCommonLiteralsSuper;
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
    // Note: We catch LexProds earlier and substitute them with a Name
    return "String".equals(getName()) || "Name".equals(getName()) || "Char".equals(getName()) || "Digits".equals(getName());
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
                                          int iteration) {
    return new PPGuardComponent(PPGuardType.BLOCK, blockData, null, null, null, null, iteration, false);
  }

  public static PPGuardComponent forNT(String name, String nameToUse,
                                       int iteration, boolean iterated, boolean isMCCommonLiteralsSuper) {
    return new PPGuardComponent(iterated ? PPGuardType.NT_ITERATED : PPGuardType.NT, null, name, nameToUse, null, null, iteration, isMCCommonLiteralsSuper);
  }

  public static PPGuardComponent forNTSingle(String name, String nameToUse,
                                             int iteration, boolean isMCCommonLiteralsSuper) {
    return new PPGuardComponent(PPGuardType.NT_AST_DEF, null, name, nameToUse, null, null, iteration, isMCCommonLiteralsSuper);
  }

  public static PPGuardComponent forT(String name) {
    return new PPGuardComponent(PPGuardType.T, null, name, name, null, null, ASTConstantsGrammar.DEFAULT, false);
  }

  public static PPGuardComponent forT(String name, String usageName, int iteration) {
    return new PPGuardComponent(PPGuardType.T, null, name, usageName, null, null, iteration, false);
  }

  public static PPGuardComponent forCG(String usageName, Set<Map.Entry<String, String>> constants) {
    return new PPGuardComponent(PPGuardType.CG, null, usageName, usageName, null, constants, 0, false);
  }

}
