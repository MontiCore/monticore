// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint.data;

import de.monticore.grammar.grammar._ast.ASTBlock;
import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BlockData {
  protected final List<AltData> altDataList = new ArrayList<>();
  protected final boolean isClassProd;
  protected final int iteration;

  protected final int inheritedIteration;

  // For pretty printing debug
  protected final ASTBlock block;

  // If Iterators were used for NonTerminals in this block
  protected boolean isListReady = false;

  protected final Set<String> optionalSet = new LinkedHashSet<>(),
          requiredSet = new LinkedHashSet<>();

  public BlockData(boolean isClassProd, int iteration, int inheritedIteration, ASTBlock block) {
    this.isClassProd = isClassProd;
    this.iteration = iteration;
    this.inheritedIteration = inheritedIteration;
    this.block = block;
  }

  public List<AltData> getAltDataList() {
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

  public ASTBlock getBlock() {
    return block;
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
    return "BlockData{" +
            "altDataList=" + altDataList +
            ", isClassProd=" + isClassProd +
            ", iteration=" + iteration +
            ", inheritedIteration=" + inheritedIteration +
            '}';
  }
}
