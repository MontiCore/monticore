/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemoveRedundantReferencesManipulationTest {
  
  @Test
  public void testGenericList() {
    ASTCDClass cdClass = setupCDClass("name",
        TransformationHelper.createType("ASTReference"), "name",
        TransformationHelper.createType("java.util.List", "ASTReference"));
    
    assertEquals(2, cdClass.getCDAttributesList().size());
    
    new RemoveRedundantAttributesManipulation()
        .removeRedundantAttributes(cdClass.getCDAttributesList());
    
    assertEquals(1, cdClass.getCDAttributesList().size());
  }
  
  private ASTCDClass setupCDClass(String firstReferenceName, ASTMCType firstReferenceType,
      String secondReferenceName, ASTMCType secondReferenceType) {
    ASTCDClass cdClass = CD4AnalysisNodeFactory.createASTCDClass();
    
    ASTCDAttribute singleAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    singleAttribute.setName(firstReferenceName);
    singleAttribute.setMCType(firstReferenceType);
    
    ASTCDAttribute listAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    listAttribute.setName(secondReferenceName);
    listAttribute.setMCType(secondReferenceType);
    
    cdClass.getCDAttributesList().add(singleAttribute);
    cdClass.getCDAttributesList().add(listAttribute);
    
    return cdClass;
  }
}
