/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import de.monticore.ast.ASTCNode;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;

/**
 * A minimal hand-written AST of automata used to test the Rete network.
 */
public final class AutomatonModel {

  private AutomatonModel() {
  }

  public interface ASTNamedElement extends ASTNode {
  }

  public abstract static class ASTBase extends ASTCNode {

    protected final String id;

    protected ASTBase(String id) {
      this.id = id;
    }

    @Override
    public IScope getEnclosingScope() {
      return null;
    }

    @Override
    public ASTNode deepClone() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return id;
    }
  }

  public static class ASTAutomaton extends ASTBase implements ASTNamedElement {

    public ASTAutomaton(String id) {
      super(id);
    }
  }

  public static class ASTState extends ASTBase implements ASTNamedElement {

    public ASTState(String id) {
      super(id);
    }
  }

  public static class ASTTransition extends ASTBase {

    public ASTTransition(String id) {
      super(id);
    }
  }

  public static final Feature<ASTAutomaton, ASTState> STATES =
      Feature.of("states", ASTAutomaton.class, ASTState.class);

  public static final Feature<ASTAutomaton, ASTTransition> TRANSITIONS =
      Feature.of("transitions", ASTAutomaton.class, ASTTransition.class);

  public static final Feature<ASTTransition, ASTState> SOURCE =
      Feature.of("source", ASTTransition.class, ASTState.class);

  public static final Feature<ASTTransition, ASTState> TARGET =
      Feature.of("target", ASTTransition.class, ASTState.class);

  public static final Feature<ASTNamedElement, String> NAME =
      Feature.of("name", ASTNamedElement.class, String.class);

  /**
   * Direct successor relation between states (used by randomized tests)
   */
  public static final Feature<ASTState, ASTState> NEXT =
      Feature.of("next", ASTState.class, ASTState.class);
}
