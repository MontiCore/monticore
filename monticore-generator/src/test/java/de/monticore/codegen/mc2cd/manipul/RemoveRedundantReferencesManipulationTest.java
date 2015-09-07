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

package de.monticore.codegen.mc2cd.manipul;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;

public class RemoveRedundantReferencesManipulationTest {
  
  @Test
  public void testFilterRedundantReferences() {
    ASTCDClass cdClass = setupCDClass("name",
        TransformationHelper.createSimpleReference("ASTReference"), "name",
        TransformationHelper.createSimpleReference("ASTReferenceList"));
    
    assertEquals(2, cdClass.getCDAttributes().size());
    
    new RemoveRedundantAttributesManipulation()
        .removeRedundantAttributes(cdClass.getCDAttributes());
    
    assertEquals(1, cdClass.getCDAttributes().size());
  }
  
  @Test
  public void testGenericList() {
    ASTCDClass cdClass = setupCDClass("name",
        TransformationHelper.createSimpleReference("ASTReference"), "name",
        TransformationHelper.createSimpleReference("java.util.List", "ASTReference"));
    
    assertEquals(2, cdClass.getCDAttributes().size());
    
    new RemoveRedundantAttributesManipulation()
        .removeRedundantAttributes(cdClass.getCDAttributes());
    
    assertEquals(1, cdClass.getCDAttributes().size());
  }
  
  private ASTCDClass setupCDClass(String firstReferenceName, ASTType firstReferenceType,
      String secondReferenceName, ASTType secondReferenceType) {
    ASTCDClass cdClass = CD4AnalysisNodeFactory.createASTCDClass();
    
    ASTCDAttribute singleAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    singleAttribute.setName(firstReferenceName);
    singleAttribute.setType(firstReferenceType);
    
    ASTCDAttribute listAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    listAttribute.setName(secondReferenceName);
    listAttribute.setType(secondReferenceType);
    
    cdClass.getCDAttributes().add(singleAttribute);
    cdClass.getCDAttributes().add(listAttribute);
    
    return cdClass;
  }
}
