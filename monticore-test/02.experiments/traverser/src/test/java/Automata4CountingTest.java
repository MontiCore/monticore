/* (c) https://github.com/MontiCore/monticore */

import automata.visitor.VisitedElementsCounter;
import automata4counting.Automata4CountingMill;
import automata4counting._ast.ASTAutomaton;
import automata4counting._parser.Automata4CountingParser;
import automata4counting._symboltable.IAutomata4CountingArtifactScope;
import automata4counting._visitor.Automata4CountingTraverser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class Automata4CountingTest {

  @Before
  public void setup() {
    Automata4CountingMill.init();
    Automata4CountingMill.globalScope().clear();
  }
  
  @Test
  public void testCountVisitedElementsWithoutSymTab() throws IOException {
    String model = "src/test/resources/automata/HierAut.aut";
    Automata4CountingParser parser = Automata4CountingMill.parser();

    // parse model
    Optional<ASTAutomaton> aut = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(aut.isPresent());

    // count visited elements
    Automata4CountingTraverser traverser = Automata4CountingMill.inheritanceTraverser();
    VisitedElementsCounter counter = new VisitedElementsCounter();
    traverser.add4IVisitor(counter);

    aut.get().accept(traverser);
    assertEquals(5, counter.getCount());
  }

  @Test
  public void testCountVisitedElementsWithSymTab() throws IOException {
    String model = "src/test/resources/automata/HierAut.aut";
    Automata4CountingParser parser = Automata4CountingMill.parser();

    // parse model
    Optional<ASTAutomaton> aut = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(aut.isPresent());

    // build symbol table
    Automata4CountingMill.scopesGenitorDelegator().createFromAST(aut.get());

    // count visited elements
    Automata4CountingTraverser traverser = Automata4CountingMill.inheritanceTraverser();
    VisitedElementsCounter counter = new VisitedElementsCounter();
    traverser.add4IVisitor(counter);

    aut.get().accept(traverser);
    assertEquals(12, counter.getCount());
  }

  @Test
  public void testCountVisitedElementsOnlySymTab() throws IOException {
    String model = "src/test/resources/automata/HierAut.aut";
    Automata4CountingParser parser = Automata4CountingMill.parser();

    // parse model
    Optional<ASTAutomaton> aut = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(aut.isPresent());

    // build symbol table
    IAutomata4CountingArtifactScope as = Automata4CountingMill.scopesGenitorDelegator().createFromAST(aut.get());

    // count visited elements of symbol table
    Automata4CountingTraverser traverser = Automata4CountingMill.inheritanceTraverser();
    VisitedElementsCounter counter = new VisitedElementsCounter();
    traverser.add4IVisitor(counter);

    as.accept(traverser);
    assertEquals(2, counter.getCount());  // should be 9 after introducing new traversal strategy
  }
  


}
