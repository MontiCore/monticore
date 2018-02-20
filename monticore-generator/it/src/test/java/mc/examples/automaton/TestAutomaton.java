/* (c) https://github.com/MontiCore/monticore */

package mc.examples.automaton;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.prettyprint.IndentPrinter;
import mc.GeneratorIntegrationsTest;
import mc.examples.automaton.automaton._ast.ASTAutomaton;
import mc.examples.automaton.automaton._od.Automaton2OD;
import mc.examples.automaton.automaton._parser.AutomatonParser;

public class TestAutomaton extends GeneratorIntegrationsTest {
  
  private ASTAutomaton parse() throws IOException {
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> optAutomaton;
    optAutomaton = parser.parseAutomaton("src/test/resources/examples/automaton/Testautomat.aut");
    assertFalse(parser.hasErrors());
    assertTrue(optAutomaton.isPresent());
    return optAutomaton.get();
  }
  

  private void printOD(ASTAutomaton ast, String symbolName) {
    ReportingRepository reporting = new ReportingRepository(new ASTNodeIdentHelper());
    IndentPrinter printer = new IndentPrinter();
    Automaton2OD odCreator = new Automaton2OD(printer, reporting);
    odCreator.printObjectDiagram(symbolName, ast);
    // TODO Check the output?
    assertTrue(printer.getContent().length()>0);
  }
  
  @Test
  public void test() throws IOException {
    ASTAutomaton ast = parse();
    printOD(ast, "Testautomat");
  }
  
}
