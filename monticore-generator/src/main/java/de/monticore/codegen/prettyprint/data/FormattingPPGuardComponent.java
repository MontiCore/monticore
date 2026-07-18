// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint.data;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;

import java.util.Map;
import java.util.Set;

public class FormattingPPGuardComponent {

  protected final FormattingPPGuardType type;
  protected final FormattingBlockData blockData;
  protected final String name;
  protected final String nameToUse;
  protected final String separator;
  protected final Set<Map.Entry<String, String>> constants;
  protected final int iteration;
  protected final boolean isMCCommonLiteralsSuper;
  protected boolean hasNoSpace = false;
  protected String tokenType = "placeholder";
  protected String nameOrIndex;
  protected ASTNode node;

  // NEW: Explicitly track if this is a Lexical token (String) or a Parser rule (ASTNode)
  protected final boolean isLexical;

  public FormattingPPGuardComponent(FormattingPPGuardType type, FormattingBlockData blockData, String name, String nameToUse,
                                    String separator, Set<Map.Entry<String, String>> constants, int iteration, boolean isMCCommonLiteralsSuper,
                                    boolean isLexical, ASTNode node, String tokenType, String nameOrIndex) {
    this.type = type;
    this.blockData = blockData;
    this.name = name;
    this.nameToUse = nameToUse;
    this.separator = separator;
    this.constants = constants;
    this.iteration = iteration;
    this.isMCCommonLiteralsSuper = isMCCommonLiteralsSuper;
    this.isLexical = isLexical;
    this.node = node;
    this.tokenType = tokenType;
    this.nameOrIndex = nameOrIndex;
  }

  public FormattingPPGuardType getType() { return type; }
  public FormattingBlockData getBlockData() {
    if (this.getType() != FormattingPPGuardType.BLOCK) throw new IllegalStateException("Not a block");
    return blockData;
  }
  public String getName() { return name; }
  public String getNameToUse() { return nameToUse; }
  public String getSeparator() { return separator; }
  public Set<Map.Entry<String, String>> getConstants() { return constants; }
  public int getIteration() { return iteration; }

  public boolean isList() {
    return getIteration() == ASTConstantsGrammar.PLUS || getIteration() == ASTConstantsGrammar.STAR;
  }

  // Strictly use the flag passed by the Visitor.
  public boolean isStringType() {
    return this.isLexical;
  }

  public boolean isOpt() { return getIteration() == ASTConstantsGrammar.QUESTION; }
  public boolean isHasNoSpace() { return this.hasNoSpace; }
  public void setHasNoSpace(boolean hasNoSpace) { this.hasNoSpace = hasNoSpace; }
  public String getTokenType() { return tokenType; }
  public void setTokenType(String tokenType) { this.tokenType = tokenType; }
  public String getNameOrIndex() { return nameOrIndex; }
  public void setNameOrIndex(String nameOrIndex) { this.nameOrIndex = nameOrIndex; }

  public enum FormattingPPGuardType { BLOCK, CG, NT, NT_ITERATED, NT_AST_DEF, T }

  public static FormattingPPGuardComponent forBlock(FormattingBlockData blockData, int iteration, ASTNode node, String tokenType) {
    return new FormattingPPGuardComponent(FormattingPPGuardType.BLOCK, blockData, null, null, null, null, iteration, false, false, node, tokenType, null);
  }

  public static FormattingPPGuardComponent forNT(String name, String nameToUse, int iteration, boolean iterated, boolean isMCCommonLiteralsSuper, boolean isLexical, ASTNode node, String tokenType) {
    return new FormattingPPGuardComponent(iterated ? FormattingPPGuardType.NT_ITERATED : FormattingPPGuardType.NT, null, name, nameToUse, null, null, iteration, isMCCommonLiteralsSuper, isLexical, node, tokenType, null);
  }

  public static FormattingPPGuardComponent forNTSingle(String name, String nameToUse, int iteration, boolean isMCCommonLiteralsSuper, boolean isLexical, ASTNode node, String tokenType) {
    return new FormattingPPGuardComponent(FormattingPPGuardType.NT_AST_DEF, null, name, nameToUse, null, null, iteration, isMCCommonLiteralsSuper, isLexical, node, tokenType, null);
  }

  public static FormattingPPGuardComponent forT(String name, ASTNode node, String tokenType) {
    return new FormattingPPGuardComponent(FormattingPPGuardType.T, null, name, name, null, null, ASTConstantsGrammar.DEFAULT, false, false, node, tokenType, null);
  }

  public static FormattingPPGuardComponent forT(String name, String usageName, int iteration, ASTNode node, String tokenType) {
    return new FormattingPPGuardComponent(FormattingPPGuardType.T, null, name, usageName, null, null, iteration, false, false, node, tokenType, null);
  }

  public static FormattingPPGuardComponent forCG(String usageName, Set<Map.Entry<String, String>> constants, ASTNode node, String tokenType) {
    return new FormattingPPGuardComponent(FormattingPPGuardType.CG, null, usageName, usageName, null, constants, 0, false, false, node, tokenType, null);
  }
}