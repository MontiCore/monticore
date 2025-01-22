/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Optional;

public class TokenTypeTest extends TranslationTestCase {

  private ASTCDClass astTest;

  @BeforeEach
  public void setupTokenTypeTest() {
    Optional<ASTCDCompilationUnit> cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/LexerFormat.mc4"));
    astTest = TestHelper.getCDClass(cdCompilationUnit.get(), "ASTTest").get();
  }

  private Optional<ASTCDAttribute> getCDAttributeByName(String name) {
    return astTest.getCDAttributeList().stream()
        .filter(cdAttribute -> name.equals(cdAttribute.getName()))
        .findAny();
  }

  @Test
  public void testNumber() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("a").get();
    Assertions.assertEquals("int", CD4CodeMill.prettyPrint(cdAttribute.getMCType(), false));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBoolean() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("b").get();
    Assertions.assertEquals("boolean", CD4CodeMill.prettyPrint(cdAttribute.getMCType(), false));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testChar() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("c").get();
    Assertions.assertEquals("char", CD4CodeMill.prettyPrint(cdAttribute.getMCType(), false));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInt() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("d").get();
    Assertions.assertEquals("int", CD4CodeMill.prettyPrint(cdAttribute.getMCType(), false));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFloat() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("e").get();
    Assertions.assertEquals("float", CD4CodeMill.prettyPrint(cdAttribute.getMCType(), false));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDouble() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("f").get();
    Assertions.assertEquals("double", CD4CodeMill.prettyPrint(cdAttribute.getMCType(), false));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLong() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("g").get();
    Assertions.assertEquals("long", CD4CodeMill.prettyPrint(cdAttribute.getMCType(), false));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCard() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("h").get();
    Assertions.assertEquals("int", CD4CodeMill.prettyPrint(cdAttribute.getMCType(), false));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testShort() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("i").get();
    Assertions.assertEquals("short", CD4CodeMill.prettyPrint(cdAttribute.getMCType(), false));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testByte() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("j").get();
    Assertions.assertEquals("byte", CD4CodeMill.prettyPrint(cdAttribute.getMCType(), false));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testByte2() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("k").get();
    Assertions.assertEquals("byte", CD4CodeMill.prettyPrint(cdAttribute.getMCType(), false));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
