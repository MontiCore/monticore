/* (c) https://github.com/MontiCore/monticore */

package mc.examples.automaton;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import mc.examples.automaton.automaton.AutomatonMill;
import mc.examples.automaton.automaton._visitor.AutomatonTraverser;
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
    AutomatonMill.globalScope().clear();
    AutomatonMill.automatonSymbolTableCreatorDelegator().createFromAST(optAutomaton.get());
    return optAutomaton.get();
  }


  private void printOD(ASTAutomaton ast, String symbolName) throws IOException {
    ReportingRepository reporting = new ReportingRepository(new ASTNodeIdentHelper());
    IndentPrinter printer = new IndentPrinter();
    Automaton2OD odCreator = new Automaton2OD(printer, reporting);
    AutomatonTraverser traverser = AutomatonMill.traverser();
    traverser.add4Automaton(odCreator);
    traverser.setAutomatonHandler(odCreator);
    odCreator.printObjectDiagram(symbolName, ast);
    assertTrue(printer.getContent().length()>0);
    assertTrue(readFile("src/test/resources/examples/automaton/Output.od", StandardCharsets.UTF_8).endsWith(printer.getContent()));
  }

  @Test
  public void test() throws IOException {
    ASTAutomaton ast = parse();
    printOD(ast, "Testautomat");
  }
  
  protected String readFile(String path, Charset encoding)
      throws IOException
  {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

}
