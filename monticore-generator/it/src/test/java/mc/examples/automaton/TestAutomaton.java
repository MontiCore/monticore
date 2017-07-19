/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
