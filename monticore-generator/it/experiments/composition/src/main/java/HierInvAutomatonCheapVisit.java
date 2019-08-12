import automaton3._ast.ASTInvariant;
import expression._ast.ASTAnd;
import expression._ast.ASTNot;
import expression._ast.ASTTruth;
import expression._ast.ASTVariable;
import hierinvautomaton._ast.ASTStateBody;
import hierinvautomaton._visitor.HierInvAutomatonVisitor;
import invautomaton._ast.ASTAutomaton;
import invautomaton._ast.ASTTransition;

/**
 * Visitor for the composed language  HierInvAutomaton
 *
 * but the vistor has been defined in a monolithic form
 * by copy pasting the content of two visitors from the sub languages.
 * (actually this is a bad an cheap form of reuse)
 */
public class HierInvAutomatonCheapVisit
				implements HierInvAutomatonVisitor {


  // ----------------------------------------------------------
  // Flag that controls verbosity of output
  boolean verbose = false;

  public void setVerbosity() { 
    verbose = true; 
  }

  // ----------------------------------------------------------
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  HierInvAutomatonVisitor realThis = this;

  @Override
  public void setRealThis(HierInvAutomatonVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public HierInvAutomatonVisitor getRealThis() {
    return realThis;
  }

  // ----------------------------------------------------------
  // Typical visit/endvisit methods:

  @Override
  public void visit(ASTInvariant node) {
    if(verbose)
      System.out.println(" HA-ASTInvariant "); 
    else
      System.out.print("  [[ "); 
  }
  
  @Override
  public void endVisit(ASTInvariant node) {
    if(verbose)
      System.out.println("   HA-/Invariant ");
    else 
      System.out.println(" ]]");
  }

  // ----------------------------------------------------------
  // ----------------------------------------------------------
  @Override
  public void visit(ASTTruth node) {
    if(verbose)
      System.out.println(" HA-ASTTruth " + node.isTt()+ "," + node.isFf()); 
    else 
      System.out.print(node.isTt() ? " true" : " false "); 
  }
  
  @Override
  public void visit(ASTNot node) {
    if(verbose)
      System.out.println(" HA-ASTNot ");
    else 
      System.out.print(" !"); 
  }
  
  @Override
  public void visit(ASTVariable node) {
    if(verbose)
      System.out.println(" HA-ASTVariable " + node.getName());
    else 
      System.out.print(" " + node.getName()); 
  }
  
  @Override
  public void visit(ASTAnd node) {
    if(verbose)
      System.out.println(" HA-ASTAnd ");
    else 
      System.out.print(" &&[ " ); 
  }
  
  // ----------------------------------------------------------
  @Override
  public void endVisit(ASTTruth node) {
    if(verbose)
      System.out.println("   HA-/Truth");
  }

  @Override
  public void endVisit(ASTNot node) {
    if(verbose)
      System.out.println("   HA-/Not ");
  }

  @Override
  public void endVisit(ASTVariable node) {
    if(verbose)
      System.out.println("   HA-/Variable ");
  }

  @Override
  public void endVisit(ASTAnd node) {
    if(verbose)
      System.out.println("   HA-/And ");
    else 
      System.out.print(" ] " ); 
  }
  

  // ----------------------------------------------------------
  @Override
  public void visit(ASTAutomaton node) {
    if(verbose)
      System.out.println("HA-/* printed with " + this.getClass()+ " */");
    System.out.println("automaton " + node.getName() + " {");
  }
  
  // Because of the hierarchic decomposition, the order
  // of printing is not correct. We need to adapt the
  // handle(ASTState) method to get thet correct
  @Override
  public void visit(hierinvautomaton._ast.ASTState node) {
    if(verbose)
      System.out.print(" HA-state " + node.getName() +" ");
    else
      System.out.print(" state " + node.getName() +" ");

    if (node.isInitial()) {
      System.out.print(" <<initial>>");
    }
    if (node.isFinal()) {
      System.out.print(" <<final>>");
    }
    System.out.println("");
  }

  @Override
  public void visit(ASTStateBody node) {
    if(verbose)
      System.out.println(" HA-stateBody {  ");
    else
      System.out.println(" { ");
  }

  @Override
  public void visit(invautomaton._ast.ASTState node) {
    // This one should not occur: Objects of that class are not
    // instatiated; subclasss has its own visit
    System.out.print(" HA-illegal ASTState object detected: "
    						+ node.getName() +" ");
  }

  @Override
  public void visit(ASTTransition node) {
    if(verbose)
      System.out.print(" HA-");
    System.out.print(" "+ node.getFrom());
    System.out.print(" - " + node.getInput() + " > ");
    System.out.print(node.getTo());
    System.out.println(";");
  }
  
  // ----------------------------------------------------------
  @Override
  public void endVisit(ASTAutomaton node) {
    if(verbose)
      System.out.println("    HA-/Automaton }");
    else
      System.out.println("}");
  }

  @Override
  public void endVisit(hierinvautomaton._ast.ASTState node) {
    if(verbose)
      System.out.println("    HA-/State ");
  }

  @Override
  public void endVisit(ASTStateBody node) {
    if(verbose)
      System.out.println(" HA-/StateBody }  ");
    else
      System.out.println(" } ");
  }

  @Override
  public void endVisit(ASTTransition node) {
    if(verbose)
      System.out.println("    HA-/Transition ");
  }
  
}

