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

package mc.emf.epackage;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.action.expression._ast.ExpressionPackage;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackageImpl;

public class IDTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testFeatureIDs() {
    assertEquals(3, FlatAutomatonPackage.ASTTransition);
    assertEquals(2, FlatAutomatonPackage.ASTTransition_To);
  }
  
  public void testInheritanceFeatureIDs() {
    // test feature ids for inheritance
    assertEquals(ExpressionPackage.ASTAssignment_Varname,
        ExpressionPackage.ASTComplexAssigment_Varname);
    assertEquals(ExpressionPackage.ASTAssignment_RHS, ExpressionPackage.ASTComplexAssigment_RHS);
  }
  
  @Test
  public void testClassIDs() {
    // test EDatatype
    assertEquals(ExpressionPackage.eINSTANCE.getEVector().getClassifierID(),
        ExpressionPackage.EVector);
    // test Classes
    assertEquals(ExpressionPackage.eINSTANCE.getDecreaseExpression().getClassifierID(),
        ExpressionPackage.ASTDecreaseExpression);
    assertEquals(FlatAutomatonPackage.eINSTANCE.getTransition(),
        FlatAutomatonPackageImpl.eINSTANCE.getTransition());
        
  }
  
  @Test
  public void testFeatureIDMetaObjectRelation() {
    assertEquals(FlatAutomatonPackage.eINSTANCE.getASTAutomaton_Name(), FlatAutomatonPackage.eINSTANCE
        .getAutomaton().getEAllStructuralFeatures().get(FlatAutomatonPackage.ASTAutomaton_Name));
        
    assertEquals(FlatAutomatonPackage.eINSTANCE.getASTAutomaton_StateList(),
        FlatAutomatonPackage.eINSTANCE.getAutomaton().getEAllStructuralFeatures()
            .get(FlatAutomatonPackage.ASTAutomaton_StateList));
  }
}
