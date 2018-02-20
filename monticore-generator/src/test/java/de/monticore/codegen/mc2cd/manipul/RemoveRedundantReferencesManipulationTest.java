/* (c) https://github.com/MontiCore/monticore */

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
  public void testGenericList() {
    ASTCDClass cdClass = setupCDClass("name",
        TransformationHelper.createSimpleReference("ASTReference"), "name",
        TransformationHelper.createSimpleReference("java.util.List", "ASTReference"));
    
    assertEquals(2, cdClass.getCDAttributeList().size());
    
    new RemoveRedundantAttributesManipulation()
        .removeRedundantAttributes(cdClass.getCDAttributeList());
    
    assertEquals(1, cdClass.getCDAttributeList().size());
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
    
    cdClass.getCDAttributeList().add(singleAttribute);
    cdClass.getCDAttributeList().add(listAttribute);
    
    return cdClass;
  }
}
