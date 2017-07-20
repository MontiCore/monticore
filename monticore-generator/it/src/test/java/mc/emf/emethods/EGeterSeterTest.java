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

package mc.emf.emethods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.action.expression._ast.ASTAssignment;
import mc.feature.fautomaton.action.expression._ast.ExpressionNodeFactory;
import mc.feature.fautomaton.action.expression._ast.ExpressionPackage;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTState;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTTransition;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonNodeFactory;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;

public class EGeterSeterTest extends GeneratorIntegrationsTest {
  
  private ASTAutomaton aut;
  private ASTTransition transition;
  private ASTAssignment assign;
  
  @Before
  public void setUp() throws Exception {
    aut = FlatAutomatonNodeFactory.createASTAutomaton();
    aut.setName("aut1");
    ASTState state1 = FlatAutomatonNodeFactory.createASTState();
    ASTState state2 = FlatAutomatonNodeFactory.createASTState();
    
    transition = FlatAutomatonNodeFactory.createASTTransition();
    
    aut.getStates().add(state1);
    aut.getStates().add(state2);
    aut.getTransitions().add(transition);
    
    state1.setName("state1");
    state2.setName("state2");
    
    assign = ExpressionNodeFactory.createASTAssignment();
    assign.setValue("value");
  }
  
  @Test
  public void testEGet() {
    String expectedName = "aut1";
    List<ASTState> expectedState = aut.getStates();
    
    // Get name of automaton with reflective methods
    String nameFromID = (String) aut.eGet(
        FlatAutomatonPackage.ASTAutomaton_Name, false, false);
    String nameFromMetaObject = (String) aut
        .eGet(FlatAutomatonPackage.eINSTANCE.getASTAutomaton_Name());
        
    List<ASTState> stateFromID = (List<ASTState>) aut.eGet(
        FlatAutomatonPackage.ASTAutomaton_States, false, false);
    List<ASTState> stateFromMetaObject = (List<ASTState>) aut
        .eGet(FlatAutomatonPackage.eINSTANCE.getASTAutomaton_States());
        
    assertEquals(expectedName, nameFromID);
    assertEquals(expectedName, nameFromMetaObject);
    assertEquals(expectedState, stateFromID);
    assertEquals(expectedState, stateFromMetaObject);
  }
  
  @Test
  public void testESet() {
    String expectedFrom = "from";
    transition.eSet(FlatAutomatonPackage.ASTTransition_From, expectedFrom);
    assertEquals(expectedFrom, transition.getFrom());
    
    String expectedTo = "to";
    transition.eSet(FlatAutomatonPackage.ASTTransition_To, expectedTo);
    assertEquals(expectedTo, transition.getTo());
  }
  
  @Test
  public void testEUnSet() {
    aut.eUnset(FlatAutomatonPackage.ASTAutomaton_States);
    assertTrue(aut.getStates().isEmpty());
    
    assign.eUnset(ExpressionPackage.ASTAssignment_Value);
    assertFalse(assign.getValue().isPresent());
  }
  
  @Test
  public void testEIsSet() {
   // assertFalse(aut.eIsSet(FlatAutomatonPackage.ASTAutomaton_States));
  //  assertFalse(assign.eIsSet(ExpressionPackage.ASTAssignment_Value));
    assertTrue(aut.eIsSet(FlatAutomatonPackage.ASTAutomaton_Transitions));
  }
  
}
