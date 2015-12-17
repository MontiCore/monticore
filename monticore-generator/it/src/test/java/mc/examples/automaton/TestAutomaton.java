/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import mc.GeneratorIntegrationsTest;
import mc.examples.automaton.automaton._ast.ASTAutomaton;
import mc.examples.automaton.automaton._od.Automaton2OD;
import mc.examples.automaton.automaton._parser.AutomatonParser;
import mc.examples.automaton.automaton._symboltable.AutomatonLanguage;
import mc.examples.automaton.automaton._symboltable.AutomatonSymbol;
import mc.examples.automaton.automaton._symboltable.AutomatonSymbolTableCreator;

public class TestAutomaton extends GeneratorIntegrationsTest {
  
  private Scope createSymTab(ASTAutomaton node) {
    final AutomatonLanguage language = new AutomatonLanguage();
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(language.getResolvers());
    
    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/"));
    
    GlobalScope globalScope = new GlobalScope(modelPath, language,
        resolverConfiguration);
    assertTrue(language.getSymbolTableCreator(resolverConfiguration, globalScope).isPresent());
    AutomatonSymbolTableCreator creator = language
        .getSymbolTableCreator(resolverConfiguration, globalScope).get();
    return creator.createFromAST(node);
  }
  
  
  private ASTAutomaton parse() throws IOException {
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> optAutomaton;
    optAutomaton = parser.parseAutomaton("src/test/resources/examples/automaton/Testautomat.aut");
    assertFalse(parser.hasErrors());
    assertTrue(optAutomaton.isPresent());
    return optAutomaton.get();
  }
  

  private void printOD(ASTAutomaton ast, Scope scope, String symbolName) {
    Optional<Symbol> symbol = scope.resolve(symbolName, AutomatonSymbol.KIND);
    Automaton2OD odCreator = new Automaton2OD(symbol.get(), new ASTNodeIdentHelper());
    String actual = odCreator.printObjectDiagram(ast);
    // TODO Check the output?
  }
  
  @Test
  public void test() throws IOException {
    ASTAutomaton ast = parse();
    Scope scope = createSymTab(ast);
    printOD(ast, scope, "Testautomat");
  }
  
}
