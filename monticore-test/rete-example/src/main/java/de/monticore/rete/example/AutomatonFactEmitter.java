/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.example;

import de.monticore.ast.ASTNode;
import de.monticore.rete.Feature;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.example.simpleautomata.SimpleAutomataMill;
import de.monticore.rete.example.simpleautomata._ast.ASTAutomaton;
import de.monticore.rete.example.simpleautomata._ast.ASTSimpleAutomataNode;
import de.monticore.rete.example.simpleautomata._ast.ASTState;
import de.monticore.rete.example.simpleautomata._ast.ASTTransition;
import de.monticore.rete.example.simpleautomata._visitor.SimpleAutomataTraverser;
import de.monticore.rete.example.simpleautomata._visitor.SimpleAutomataVisitor2;

import static de.monticore.rete.example.simpleautomata._rete.SimpleAutomataReteFeatures.*;

/**
 * Reports the facts of SimpleAutomata ASTs (see the generated
 * {@link de.monticore.rete.example.simpleautomata._rete.SimpleAutomataReteFeatures}) to
 * a {@link ReteNetwork}, e.g. to initialize the network for a parsed model.
 * <p>
 * The facts of a subtree are the AST nodes it contains and the values of all
 * their features. The fact linking the subtree's root to its parent (e.g.
 * {@code AUTOMATON_TRANSITIONS(aut, t)} for a transition {@code t}) belongs
 * to the parent and has to be reported separately. Thus, adding a transition
 * to an automaton is reported as {@code addSubtree(t)} plus
 * {@code featureValueAdded(AUTOMATON_TRANSITIONS, aut, t)}.
 * <p>
 * Requires an initialized {@link SimpleAutomataMill}.
 */
public class AutomatonFactEmitter implements SimpleAutomataVisitor2 {

  protected final ReteNetwork network;

  protected final SimpleAutomataTraverser traverser;

  /**
   * Whether the current traversal reports insertions or deletions
   */
  protected boolean insert = true;

  public AutomatonFactEmitter(ReteNetwork network) {
    this.network = network;
    this.traverser = SimpleAutomataMill.traverser();
    traverser.add4SimpleAutomata(this);
  }

  /**
   * Reports all facts of the subtree as inserted, within one transaction.
   *
   * @throws IllegalStateException if an AST node of the subtree has already
   *                               been reported
   */
  public void addSubtree(ASTSimpleAutomataNode root) {
    emit(root, true);
  }

  /**
   * Reports all facts of the subtree as deleted, within one transaction. The
   * subtree must be unchanged since its facts were reported (or all changes
   * must have been reported), otherwise the reported facts do not match.
   *
   * @throws IllegalStateException if a fact of the subtree is not known to
   *                               the network
   */
  public void removeSubtree(ASTSimpleAutomataNode root) {
    emit(root, false);
  }

  /**
   * Traverses the subtree, reporting each visited node and its feature values
   * in the given direction.
   */
  protected void emit(ASTSimpleAutomataNode root, boolean insert) {
    network.transaction(() -> {
      this.insert = insert;
      root.accept(traverser);
    });
  }

  /**
   * Reports an AST node in the current direction.
   */
  protected void node(ASTNode node) {
    if (insert) {
      network.nodeAdded(node);
    } else {
      network.nodeRemoved(node);
    }
  }

  /**
   * Reports a feature value in the current direction.
   */
  protected <O extends ASTNode, V> void fact(Feature<O, V> feature, O owner, V value) {
    if (insert) {
      network.featureValueAdded(feature, owner, value);
    } else {
      network.featureValueRemoved(feature, owner, value);
    }
  }

  /**
   * Reports the automaton, its name and the containment of its states and
   * transitions (the states and transitions themselves are reported when
   * they are visited).
   */
  @Override
  public void visit(ASTAutomaton node) {
    node(node);
    fact(AUTOMATON_NAME, node, node.getName());
    for (ASTState state : node.getStateList()) {
      fact(AUTOMATON_STATES, node, state);
    }
    for (ASTTransition transition : node.getTransitionList()) {
      fact(AUTOMATON_TRANSITIONS, node, transition);
    }
  }

  /**
   * Reports the state, its name and both flags (always, even if {@code false}).
   */
  @Override
  public void visit(ASTState node) {
    node(node);
    fact(STATE_NAME, node, node.getName());
    fact(STATE_INITIAL, node, node.isInitial());
    fact(STATE_FINAL, node, node.isFinal());
  }

  /**
   * Reports the transition and the names of its source state, input and
   * target state.
   */
  @Override
  public void visit(ASTTransition node) {
    node(node);
    fact(TRANSITION_FROM, node, node.getFrom());
    fact(TRANSITION_INPUT, node, node.getInput());
    fact(TRANSITION_TO, node, node.getTo());
  }
}
