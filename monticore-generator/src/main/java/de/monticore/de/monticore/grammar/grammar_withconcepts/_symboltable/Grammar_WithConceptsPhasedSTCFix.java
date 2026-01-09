/* (c) https://github.com/MontiCore/monticore */
package de.monticore.de.monticore.grammar.grammar_withconcepts._symboltable;

import de.monticore.grammar.concepts.antlr.antlr._ast.ASTConceptAntlr;
import de.monticore.grammar.concepts.antlr.antlr._visitor.AntlrHandler;
import de.monticore.grammar.concepts.antlr.antlr._visitor.AntlrTraverser;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsPhasedSTC;

/**
 * Backport of Grammar_WithConceptsPhasedSTC fix
 * <a href="https://git.rwth-aachen.de/monticore/monticore/-/issues/4842">...</a>
 * Remove after 7.9.0 release
 */
public class Grammar_WithConceptsPhasedSTCFix extends Grammar_WithConceptsPhasedSTC {
  public Grammar_WithConceptsPhasedSTCFix() {
    super();
    this.priorityList.get(0).setAntlrHandler(new DoNotTCJavaCode());
  }


  // Explicitly do not check the JavaCode of ConceptAntlr (as we can't TC it without knowing the java classpath)
  // required since the 7.7.0 -> 7.8.0 update
  protected static class DoNotTCJavaCode implements AntlrHandler {
    protected AntlrTraverser realThis;

    @Override
    public AntlrTraverser getTraverser() {
      return realThis;
    }

    @Override
    public void setTraverser(AntlrTraverser traverser) {
      this.realThis = traverser;
    }

    @Override
    public void traverse(ASTConceptAntlr node) {
      // do nothing!
    }
  }
}
