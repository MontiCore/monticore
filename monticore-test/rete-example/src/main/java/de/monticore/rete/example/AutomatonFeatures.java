/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.example;

import de.monticore.rete.Feature;
import de.monticore.rete.example.simpleautomata._ast.ASTAutomaton;
import de.monticore.rete.example.simpleautomata._ast.ASTState;
import de.monticore.rete.example.simpleautomata._ast.ASTTransition;

import java.util.List;

/**
 * The {@link Feature}s of the SimpleAutomata language, one per attribute of
 * each AST class. They define how ASTs are represented as facts of a
 * {@link de.monticore.rete.ReteNetwork}:
 * <ul>
 *   <li>mandatory attributes (including booleans) yield exactly one fact,</li>
 *   <li>list-valued attributes yield one fact per element,</li>
 *   <li>optional attributes yield a fact only if present.</li>
 * </ul>
 * Name references (e.g. the source of a transition) are kept as Strings and
 * resolved by joins in the Rete network.
 */
public final class AutomatonFeatures {

  private AutomatonFeatures() {
  }

  // Automaton

  public static final Feature<ASTAutomaton, String> AUTOMATON_NAME =
      Feature.of("name", ASTAutomaton.class, String.class);

  public static final Feature<ASTAutomaton, ASTState> AUTOMATON_STATES =
      Feature.of("states", ASTAutomaton.class, ASTState.class);

  public static final Feature<ASTAutomaton, ASTTransition> AUTOMATON_TRANSITIONS =
      Feature.of("transitions", ASTAutomaton.class, ASTTransition.class);

  // State

  public static final Feature<ASTState, String> STATE_NAME =
      Feature.of("name", ASTState.class, String.class);

  public static final Feature<ASTState, Boolean> STATE_INITIAL =
      Feature.of("initial", ASTState.class, Boolean.class);

  public static final Feature<ASTState, Boolean> STATE_FINAL =
      Feature.of("final", ASTState.class, Boolean.class);

  // Transition

  public static final Feature<ASTTransition, String> TRANSITION_FROM =
      Feature.of("from", ASTTransition.class, String.class);

  public static final Feature<ASTTransition, String> TRANSITION_INPUT =
      Feature.of("input", ASTTransition.class, String.class);

  public static final Feature<ASTTransition, String> TRANSITION_TO =
      Feature.of("to", ASTTransition.class, String.class);

  /**
   * All features of the language
   */
  public static final List<Feature<?, ?>> ALL = List.of(
      AUTOMATON_NAME, AUTOMATON_STATES, AUTOMATON_TRANSITIONS,
      STATE_NAME, STATE_INITIAL, STATE_FINAL,
      TRANSITION_FROM, TRANSITION_INPUT, TRANSITION_TO);
}
