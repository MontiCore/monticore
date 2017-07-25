/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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

package mc.emf.eobjects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.monticore.emf._ast.ASTENode;
import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTState;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTTransition;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonNodeFactory;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;

public class CreateEObjectsTest extends GeneratorIntegrationsTest {
  
  @Test
  public void factoryTest() {
    ASTENode ast = (ASTENode) FlatAutomatonNodeFactory.getFactory()
        .create(FlatAutomatonPackage.Literals.ASTAutomaton);
    assertNotNull(ast);
    assertTrue(ast instanceof ASTAutomaton);
   
    ast = (ASTENode) FlatAutomatonNodeFactory.getFactory()
        .create(FlatAutomatonPackage.Literals.ASTState);
    assertNotNull(ast);
    assertTrue(ast instanceof ASTState);
    
    ast = (ASTENode) FlatAutomatonNodeFactory.getFactory()
        .create(FlatAutomatonPackage.Literals.ASTTransition);
    assertNotNull(ast);
    assertTrue(ast instanceof ASTTransition);
  }
  
  @Test
  public void testCreate() {
    ASTState state = FlatAutomatonNodeFactory.createASTState();
    assertNotNull(state);
    // TODO GV
  }
  
}
