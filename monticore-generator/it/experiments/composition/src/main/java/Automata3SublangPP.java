/* (c) https://github.com/MontiCore/monticore */
import automata3._ast.ASTInvariant;
import automata3._visitor.Automata3Visitor;
import de.monticore.prettyprint.IndentPrinter;

/**
 * Pretty prints automatas. Use {@link #print(ASTAutomata3)} to start a pretty
 * print and get the result by using {@link #getResult()}.
 *

 */
public class Automata3SublangPP implements Automata3Visitor {

  // ----------------------------------------------------------
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  Automata3Visitor realThis = this;

  @Override
  public void setRealThis(Automata3Visitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public Automata3Visitor getRealThis() {
    return realThis;
  }

  // ----------------------------------------------------------
  protected IndentPrinter out;

  public Automata3SublangPP(IndentPrinter o) {
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
