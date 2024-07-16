/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RemoveRedundantReferencesManipulationTest extends TranslationTestCase {

  @Test
  public void testGenericList() {
    ASTCDClass cdClass = setupCDClass("name",
        TransformationHelper.createType("ASTReference"), "name",
        TransformationHelper.createType("java.util.List", "ASTReference"));
    
    Assertions.assertEquals(2, cdClass.getCDAttributeList().size());
    
    cdClass.setCDAttributeList(new RemoveRedundantAttributesManipulation()
        .removeRedundantAttributes(cdClass.getCDAttributeList()));
    
    Assertions.assertEquals(1, cdClass.getCDAttributeList().size());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  private ASTCDClass setupCDClass(String firstReferenceName, ASTMCType firstReferenceType,
      String secondReferenceName, ASTMCType secondReferenceType) {
    ASTCDClass cdClass = CD4AnalysisMill.cDClassBuilder().uncheckedBuild();
    
    ASTCDAttribute singleAttribute = CD4AnalysisMill.cDAttributeBuilder().uncheckedBuild();
    singleAttribute.setName(firstReferenceName);
    singleAttribute.setMCType(firstReferenceType);
    
    ASTCDAttribute listAttribute = CD4AnalysisMill.cDAttributeBuilder().uncheckedBuild();
    listAttribute.setName(secondReferenceName);
    listAttribute.setMCType(secondReferenceType);
    
    cdClass.addCDMember(singleAttribute);
    cdClass.addCDMember(listAttribute);
    
    return cdClass;
  }
}
