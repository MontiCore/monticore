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

import org.junit.Before;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.action.expression._ast.ASTComplexAssigment;
import mc.feature.fautomaton.action.expression._ast.ASTValue;
import mc.feature.fautomaton.action.expression._ast.ExpressionNodeFactory;
import mc.feature.fautomaton.action.expression._ast.ExpressionPackage;


public class FeatureIDConversionTest extends GeneratorIntegrationsTest {
  
  ASTComplexAssigment ast;
  
  @Before
  public void setUp() throws Exception {
    ast = ExpressionNodeFactory.createASTComplexAssigment();
  }
  
  // TODO GV
  public void testDerivedFeatureID() {
    int derivedID = ast.eDerivedStructuralFeatureID(ExpressionPackage.ASTValue, ASTValue.class);
    
    int expectedDerivedID = ExpressionPackage.ASTComplexAssigment_A;
    
    assertEquals(expectedDerivedID, derivedID);
  }
  
  public void testBaseFeatureID() {
    int baseID = ast.eBaseStructuralFeatureID(
        ExpressionPackage.ASTComplexAssigment_A, ASTValue.class);
    
    int expectedBaseID = ExpressionPackage.ASTValue;
    
    assertEquals(expectedBaseID, baseID);
  }
}
