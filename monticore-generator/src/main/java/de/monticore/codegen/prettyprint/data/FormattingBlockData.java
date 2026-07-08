// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint.data;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Either the top-level body of a ClassProd (isClassProd == true), or a nested
 * (...)?/ (...)* / (...)+ group.
 */
public class FormattingBlockData {

  protected final List<FormattingAltData> altDataList = new ArrayList<>();
  protected final boolean isClassProd;
  protected final int iteration;
  protected final int inheritedIteration;
  protected final ASTNode node;
  protected boolean isListReady = false;

  protected final Set<String> optionalSet = new LinkedHashSet<>();
  protected final Set<String> requiredSet = new LinkedHashSet<>();

  public FormattingBlockData(boolean isClassProd, int iteration, int inheritedIteration, ASTNode node) {
    this.isClassProd = isClassProd;
    this.iteration = iteration;
    this.inheritedIteration = inheritedIteration;
    this.node = node;
  }

  public ASTNode getNode() {
    return node;
  }

  public List<FormattingAltData> getAltDataList() {
    return altDataList;
  }

  public boolean isClassProd() {
    return isClassProd;
  }

  public int getIteration() {
    return iteration;
  }

  public boolean isList() {
    return isListReady && (getIteration() == ASTConstantsGrammar.PLUS || getIteration() == ASTConstantsGrammar.STAR);
  }

  // (["fragment"]| ["comment"])* might be both, but we can't use a list/while due to no iterators used
  public boolean isNotListButNoElse() {
    return (getIteration() == ASTConstantsGrammar.PLUS || getIteration() == ASTConstantsGrammar.STAR);
  }

  public int getInheritedIteration() {
    return inheritedIteration;
  }

  public void markListReady() {
    this.isListReady = true;
  }

  public boolean isListReady() {
    return isListReady;
  }

  /**
   * @return set of optionally used AST-elements
   */
  public Set<String> getOptionalSet() {
    return optionalSet;
  }

  /**
   * @return set of required AST-elements to print this block
   */
  public Set<String> getRequiredSet() {
    return requiredSet;
  }

  @Override
  public String toString() {
    return "FormattingBlockData{" +
        "altDataList=" + altDataList +
        ", isClassProd=" + isClassProd +
        ", iteration=" + iteration +
        ", inheritedIteration=" + inheritedIteration +
        '}';
  }
}