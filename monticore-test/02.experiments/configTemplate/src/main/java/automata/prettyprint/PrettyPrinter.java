/* (c) https://github.com/MontiCore/monticore */
package automata.prettyprint;

import automata.AutomataMill;
import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._ast.ASTTransition;
import automata._visitor.AutomataHandler;
import automata._visitor.AutomataTraverser;
import de.monticore.prettyprint.IndentPrinter;

/**
 * Pretty prints automatons. Use {@link #handle(ASTAutomaton)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 */
public class PrettyPrinter implements AutomataHandler {
  private final IndentPrinter printer;
  private AutomataTraverser traverser;
  
  public PrettyPrinter() {
    this.printer = new IndentPrinter();
    this.traverser = AutomataMill.traverser();
    traverser.setAutomataHandler(this);
  }
  
  /**
   * Gets the printed result.
   *
   * @return the result of the pretty print.
   */
  public String getResult() {
    return this.printer.getContent();
  }
  
  /**
   * Gets the printer.
   *
   * @return the printer of this.
   */
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  @Override
  public void handle(ASTAutomaton node) {
    getPrinter().println("automaton " + node.getName() + " {");
    getPrinter().indent();
    getTraverser().traverse(node);
    getPrinter().unindent();
    getPrinter().println("}");
  }
  
  @Override
  public void traverse(ASTAutomaton node) {
    // guarantee ordering: states before transitions
    node.getStateList().forEach(s -> s.accept(getTraverser()));
    node.getTransitionList().forEach(t -> t.accept(getTraverser()));
  }
  
  @Override
  public void handle(ASTState node) {
    getPrinter().print("state " + node.getName());
    if (node.isInitial()) {
      getPrinter().print(" <<initial>>");
    }
    if (node.isFinal()) {
      getPrinter().print(" <<final>>");
    }
    getPrinter().println(";");
    getTraverser().traverse(node);
  }
  
  @Override
  public void handle(ASTTransition node) {
    getPrinter().print(node.getFrom());
    getPrinter().print(" - " + node.getInput() + " > ");
    getPrinter().print(node.getTo());
    getPrinter().println(";");
    getTraverser().traverse(node);
  }
  
  @Override
  public AutomataTraverser getTraverser() {
    return this.traverser;
  }
  
  @Override
  public void setTraverser(AutomataTraverser traverser) {
    this.traverser = traverser;
  }
  
}
