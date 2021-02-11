/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class AttributeInASTTypeTest {

  private ASTCDClass astA;

  @BeforeClass
  public static void setup(){
    GrammarFamilyMill.init();
  }

  public AttributeInASTTypeTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AttributeInASTTypeGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
  }

  @Test
  public void testType() {
    astA.getCDAttributeList().stream()
        .map(ASTCDAttribute::getMCType)
        .map(Object::getClass)
        .forEach(type -> assertEquals(ASTMCPrimitiveType.class, type));
  }

}
