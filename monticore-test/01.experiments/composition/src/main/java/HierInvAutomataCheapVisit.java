/* (c) https://github.com/MontiCore/monticore */
import automata3._ast.ASTInvariant;
import automata3._visitor.Automata3Visitor2;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;
import expression._ast.ASTAnd;
import expression._ast.ASTNot;
import expression._ast.ASTTruth;
import expression._ast.ASTVariable;
import expression._visitor.ExpressionVisitor2;
import hierinvautomata._ast.ASTStateBody;
import hierinvautomata._visitor.HierInvAutomataVisitor2;
import invautomata._ast.ASTAutomaton;
import invautomata._ast.ASTTransition;
import invautomata._visitor.InvAutomataVisitor2;

/**
 * Visitor for the composed language  HierInvAutomata
 *
 * but the vistor has been defined in a monolithic form
 * by copy pasting the content of two visitors from the sub languages.
 * (actually this is a bad an cheap form of reuse)
 */
public class HierInvAutomataCheapVisit
                        implements HierInvAutomataVisitor2, Automata3Visitor2, 
                                   InvAutomataVisitor2, ExpressionVisitor2 {


  // ----------------------------------------------------------
  // Flag that controls verbosity of output
  boolean verbose = false;

  public void setVerbosity() { 
    verbose = true; 
  }


  // ----------------------------------------------------------
  // Typical visit/endvisit methods:

  @Override
  public void visit(ASTInvariant node) {
    if(verbose)
      Log.println(" HA-ASTInvariant ");
    else
      Log.print("  [[ "); 
  }
  
  @Override
  public void endVisit(ASTInvariant node) {
    if(verbose)
      Log.println("   HA-/Invariant ");
    else 
      Log.println(" ]]");
  }

  // ----------------------------------------------------------
  // ----------------------------------------------------------
  @Override
  public void visit(ASTTruth node) {
    if(verbose)
      Log.println(" HA-ASTTruth " + node.isTt()+ "," + node.isFf()); 
    else 
      Log.print(node.isTt() ? " true" : " false "); 
  }
  
  @Override
  public void visit(ASTNot node) {
    if(verbose)
      Log.println(" HA-ASTNot ");
    else 
      Log.print(" !"); 
  }
  
  @Override
  public void visit(ASTVariable node) {
    if(verbose)
      Log.println(" HA-ASTVariable " + node.getName());
    else 
      Log.print(" " + node.getName()); 
  }
  
  @Override
  public void visit(ASTAnd node) {
    if(verbose)
      Log.println(" HA-ASTAnd ");
    else 
      Log.print(" &&[ " ); 
  }
  
  // ----------------------------------------------------------
  @Override
  public void endVisit(ASTTruth node) {
    if(verbose)
      Log.println("   HA-/Truth");
  }

  @Override
  public void endVisit(ASTNot node) {
    if(verbose)
      Log.println("   HA-/Not ");
  }

  @Override
  public void endVisit(ASTVariable node) {
    if(verbose)
      Log.println("   HA-/Variable ");
  }

  @Override
  public void endVisit(ASTAnd node) {
    if(verbose)
      Log.println("   HA-/And ");
    else 
      Log.print(" ] " ); 
  }
  

  // ----------------------------------------------------------
  @Override
  public void visit(ASTAutomaton node) {
    if(verbose)
      Log.println("HA-/* printed with " + this.getClass()+ " */");
    Log.println("automaton " + node.getName() + " {");
  }
  
  // Because of the hierarchic decomposition, the order
  // of printing is not correct. We need to adapt the
  // handle(ASTState) method to get thet correct
  @Override
  public void visit(hierinvautomata._ast.ASTState node) {
    if(verbose)
      Log.print(" HA-state " + node.getName() +" ");
    else
      Log.print(" state " + node.getName() +" ");

    if (node.isInitial()) {
      Log.print(" <<initial>>");
    }
    if (node.isFinal()) {
      Log.print(" <<final>>");
    }
    Log.println("");
  }

  @Override
  public void visit(ASTStateBody node) {
    if(verbose)
      Log.println(" HA-stateBody {  ");
    else
      Log.println(" { ");
  }

  @Override
  public void visit(invautomata._ast.ASTState node) {
    // This one should not occur: Objects of that class are not
    // instatiated; subclasss has its own visit
    Log.print(" HA-illegal ASTState object detected: "
                            + node.getName() +" ");
  }

  @Override
  public void visit(ASTTransition node) {
    if(verbose)
      Log.print(" HA-");
    Log.print(" "+ node.getFrom());
    Log.print(" - " + node.getInput() + " > ");
    Log.print(node.getTo());
    Log.println(";");
  }
  
  // ----------------------------------------------------------
  @Override
  public void endVisit(ASTAutomaton node) {
    if(verbose)
      Log.println("    HA-/Automaton }");
    else
      Log.println("}");
  }

  @Override
  public void endVisit(hierinvautomata._ast.ASTState node) {
    if(verbose)
      Log.println("    HA-/State ");
  }

  @Override
  public void endVisit(ASTStateBody node) {
    if(verbose)
      Log.println(" HA-/StateBody }  ");
    else
      Log.println(" } ");
  }

  @Override
  public void endVisit(ASTTransition node) {
    if(verbose)
      Log.println("    HA-/Transition ");
  }

  // Overriding shared methods of multiple interfaces.
  
  @Override
  public void endVisit(ASTNode node) {
  }
  
  @Override
  public void endVisit(ISymbol node) {
  }
  
  @Override
  public void endVisit(IScope node) {
  }
  
  @Override
  public void visit(ASTNode node) {
  }
  
  @Override
  public void visit(ISymbol node) {
  }
  
  @Override
  public void visit(IScope node) {
  }
  
}

