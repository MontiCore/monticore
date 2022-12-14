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

  public PPGuardComponent(PPGuardType type, BlockData blockData, String name, String nameToUse,
                          String separator, Set<Map.Entry<String, String>> constants, int iteration) {
    this.type = type;
    this.blockData = blockData;
    this.name = name;
    this.nameToUse = nameToUse;
    this.separator = separator;
    this.constants = constants;
    this.iteration = iteration;
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

  public boolean isString() {
    // Note: We catch LexProds earlier and substitute them with a String
    return "String".equals(getName()) || "Name".equals(getName()) || "Char".equals(getName()) || "Digits".equals(getName());
  }

  public boolean isOpt() {
    return getIteration() == ASTConstantsGrammar.QUESTION;
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
    return new PPGuardComponent(PPGuardType.BLOCK, blockData, null, null, null, null, iteration);
  }

  public static PPGuardComponent forNT(String name, String nameToUse,
                                       int iteration, boolean iterated) {
    return new PPGuardComponent(iterated ? PPGuardType.NT_ITERATED : PPGuardType.NT, null, name, nameToUse, null, null, iteration);
  }

  public static PPGuardComponent forNTSingle(String name, String nameToUse,
                                             int iteration) {
    return new PPGuardComponent(PPGuardType.NT_AST_DEF, null, name, nameToUse, null, null, iteration);
  }

  public static PPGuardComponent forT(String name,
                                      int iteration) {
    return new PPGuardComponent(PPGuardType.T, null, name, name, null, null, iteration);
  }

  public static PPGuardComponent forCG(String usageName, Set<Map.Entry<String, String>> constants) {
    return new PPGuardComponent(PPGuardType.CG, null, usageName, usageName, null, constants, 0);
  }

}
