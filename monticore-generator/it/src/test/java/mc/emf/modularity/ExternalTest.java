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

package mc.emf.modularity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import mc.feature.fautomaton.automatonwithaction.actionautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automatonwithaction.actionautomaton._ast.ASTCounter;
import mc.feature.fautomaton.automatonwithaction.actionautomaton._ast.ActionAutomatonNodeFactory;

public class ExternalTest extends GeneratorIntegrationsTest {
  
  private ASTAutomaton aut;
  
  @Before
  public void setUp() throws Exception {
    aut = ActionAutomatonNodeFactory.createASTAutomaton();
  }
  
  @Test
  public void testMetaObject() {
    EReference transition = FlatAutomatonPackage.eINSTANCE.getASTAutomaton_Transitions();
    
    EClass expectedExternalType = FlatAutomatonPackage.eINSTANCE.getTransition();
    
    assertTrue(transition.isMany());
    assertEquals(expectedExternalType, transition.getEReferenceType());
    assertEquals(FlatAutomatonPackage.ASTAutomaton_Transitions,
        transition.getFeatureID());
  }
  
  @Test
  public void testMethods() {
    ASTCounter counter = ActionAutomatonNodeFactory.createASTCounter();
    aut.setCounters(Lists.newArrayList(counter));
    
    assertTrue(aut.getCounters().contains(counter));
  }
  
}
