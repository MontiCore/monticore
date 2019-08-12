import automaton3._ast.ASTInvariant;
import automaton3._visitor.Automaton3Visitor;
import de.monticore.prettyprint.IndentPrinter;

/**
 * Pretty prints automatons. Use {@link #print(ASTAutomaton3)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *

 */
public class Automaton3SublangPP implements Automaton3Visitor {

  // ----------------------------------------------------------
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  Automaton3Visitor realThis = this;

  @Override
  public void setRealThis(Automaton3Visitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public Automaton3Visitor getRealThis() {
    return realThis;
  }

  // ----------------------------------------------------------
  protected IndentPrinter out;

  public Automaton3SublangPP(IndentPrinter o) {
    out = o;
  }

  // ----------------------------------------------------------
  // Typical visit/endvist methods:

  @Override
  public void visit(ASTInvariant node) {
    out.print("/*[*/ ");
  }

  @Override
  public void endVisit(ASTInvariant node) {
    out.print("/*]*/ ");
  }



}
