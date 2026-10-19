import automata4counting.Automata4CountingMill;
import automata4counting._ast.ASTAutomata4CountingNode;
import automata4counting._ast.ASTAutomaton;
import automata4counting._ast.ASTState;
import automata4counting._ast.ASTTransition;
import automata4counting._parser.Automata4CountingParser;
import automata4counting._symboltable.*;
import automata4counting._visitor.Automata4CountingTraverser;
import automata4counting._visitor.Automata4CountingVisitor2;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ISymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AutomataSingleStepTest {

  @BeforeEach
  public void setup() {
    Automata4CountingMill.init();
    Automata4CountingMill.globalScope().clear();
  }

  @Test
  public void testSingleStepAutomaton() throws IOException {
    String model = "src/test/resources/automata/HierAut.aut";
    Automata4CountingParser parser = Automata4CountingMill.parser();

    // parse model
    Optional<ASTAutomaton> aut = parser.parse(model);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(aut.isPresent());

    // asser visited elements
    final int[] visits = new int[1];
    Automata4CountingTraverser traverser = Automata4CountingMill.singleStepTraverser();
    Automata4CountingVisitor2 dispatchVisitor = new Automata4CountingVisitor2() {

      @Override
      public void visit(ASTNode node) {
        assertEquals(0, visits[0]++, "Unexpected node visit order! Expected 0, but was " + visits[0]);
      }

      @Override
      public void visit(ASTAutomata4CountingNode node) {
        assertEquals(1, visits[0]++, "Unexpected node visit order! Expected 1, but was " + visits[0]);
      }

      @Override
      public void visit(ASTAutomaton node) {
        assertEquals(2, visits[0]++, "Unexpected node visit order! Expected 2, but was " + visits[0]);
      }

      @Override
      public void visit(ASTState node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ASTTransition node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(AutomatonSymbol node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(StateSymbol node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ICommonAutomata4CountingSymbol node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(IAutomata4CountingScope node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(IAutomata4CountingArtifactScope node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(IAutomata4CountingGlobalScope node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ISymbol symbol) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(IScope scope) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(IScopeSpanningSymbol symbol) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }
    };
    traverser.add4IVisitor(dispatchVisitor);
    traverser.add4Automata4Counting(dispatchVisitor);

    aut.get().accept(traverser);

    assertEquals(3, visits[0], "Unexpected number of visited elements!");
  }

  @Test
  public void testSingleStepAutomatonScope() throws IOException {
    String model = "src/test/resources/automata/HierAut.aut";
    Automata4CountingParser parser = Automata4CountingMill.parser();

    // parse model
    Optional<ASTAutomaton> aut = parser.parse(model);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(aut.isPresent());

    // create scopes
    Automata4CountingMill.scopesGenitorDelegator().createFromAST(aut.get());

    // asser visited elements
    final int[] visits = new int[1];
    Automata4CountingTraverser traverser = Automata4CountingMill.singleStepTraverser();
    Automata4CountingVisitor2 dispatchVisitor = new Automata4CountingVisitor2() {

      @Override
      public void visit(IScope scope) {
        assertEquals(0, visits[0]++, "Unexpected node visit order! Expected 0, but was " + visits[0]);
      }

      @Override
      public void visit(IAutomata4CountingScope node) {
        assertEquals(1, visits[0]++, "Unexpected node visit order! Expected 1, but was " + visits[0]);
      }

      @Override
      public void visit(IAutomata4CountingGlobalScope node) {
        assertEquals(2, visits[0]++, "Unexpected node visit order! Expected 2, but was " + visits[0]);
      }

      @Override
      public void visit(ASTNode node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ASTAutomata4CountingNode node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ASTAutomaton node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ASTState node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ASTTransition node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ISymbol symbol) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(AutomatonSymbol node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(StateSymbol node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ICommonAutomata4CountingSymbol node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(IAutomata4CountingArtifactScope node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(IScopeSpanningSymbol symbol) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }
    };
    traverser.add4IVisitor(dispatchVisitor);
    traverser.add4Automata4Counting(dispatchVisitor);

    Automata4CountingMill.globalScope().accept(traverser);

    assertEquals(3, visits[0], "Unexpected number of visited elements!");
  }

  @Test
  public void testSingleStepAutomatonSymbol() throws IOException {
    String model = "src/test/resources/automata/HierAut.aut";
    Automata4CountingParser parser = Automata4CountingMill.parser();

    // parse model
    Optional<ASTAutomaton> aut = parser.parse(model);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(aut.isPresent());

    // create scopes
    IAutomata4CountingArtifactScope artifactScope =
        Automata4CountingMill.scopesGenitorDelegator().createFromAST(aut.get());

    AutomatonSymbol automatonSymbol = artifactScope.getAutomatonSymbols().values().getFirst();

    // asser visited elements
    final int[] visits = new int[1];
    Automata4CountingTraverser traverser = Automata4CountingMill.singleStepTraverser();
    Automata4CountingVisitor2 dispatchVisitor = new Automata4CountingVisitor2() {

      @Override
      public void visit(ISymbol symbol) {
        assertEquals(0, visits[0]++, "Unexpected node visit order! Expected 0, but was " + visits[0]);
      }

      @Override
      public void visit(IScopeSpanningSymbol symbol) {
        assertEquals(1, visits[0]++, "Unexpected node visit order! Expected 1, but was " + visits[0]);
      }

      @Override
      public void visit(ICommonAutomata4CountingSymbol node) {
        assertEquals(2, visits[0]++, "Unexpected node visit order! Expected 2, but was " + visits[0]);
      }

      @Override
      public void visit(AutomatonSymbol node) {
        assertEquals(3, visits[0]++, "Unexpected node visit order! Expected 3, but was " + visits[0]);
      }

      @Override
      public void visit(IScope scope) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(IAutomata4CountingScope node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(IAutomata4CountingGlobalScope node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ASTNode node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ASTAutomata4CountingNode node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ASTAutomaton node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ASTState node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(ASTTransition node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(StateSymbol node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }

      @Override
      public void visit(IAutomata4CountingArtifactScope node) {
        fail("Unexpected node visit! Single step traverser should not traverse this node");
      }
    };
    traverser.add4IVisitor(dispatchVisitor);
    traverser.add4Automata4Counting(dispatchVisitor);

    automatonSymbol.accept(traverser);

    assertEquals(4, visits[0], "Unexpected number of visited elements!");
  }
}
