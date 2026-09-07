/* (c) https://github.com/MontiCore/monticore */
package automata3;
import automata3._ast.ASTLogicInv;
import automata3._visitor.Automata3Visitor2;
import de.monticore.prettyprint.IndentPrinter;

/**
 * Pretty prints automatas. Use {@link #print(ASTAutomata3)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *

 */
public class Automata3SublangPP implements Automata3Visitor2 {

  protected IndentPrinter out;

  public Automata3SublangPP(IndentPrinter o) {
    out = o;
  }

  // ----------------------------------------------------------
  // Typical visit/endvist methods:

  @Override
  public void visit(ASTLogicInv node) {
    out.print("/*[*/ ");
  }

  @Override
  public void endVisit(ASTLogicInv node) {
    out.print("/*]*/ ");
  }



}
