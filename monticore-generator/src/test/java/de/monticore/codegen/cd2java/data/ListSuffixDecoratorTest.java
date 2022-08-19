/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.data;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertTrue;

public class ListSuffixDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass classWithS;

  private ASTCDClass originalClass;

  @Before
  public void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
    ASTCDCompilationUnit cd = this.parse("de", "monticore", "codegen", "data", "Data");

    originalClass = getClassBy("A", cd).deepClone();

    this.glex.setGlobalValue("service", new AbstractService(cd));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    ListSuffixDecorator listSuffixDecorator = new ListSuffixDecorator();
    cd = listSuffixDecorator.decorate(cd, cd);
    classWithS = getClassBy("A", cd);

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
  }

  @Test
  public void testNoSBefore() {
    ASTCDAttribute lists = getAttributeBy("list", originalClass);
    assertTrue(lists.getModifier().isProtected());
    assertDeepEquals("List<String>", lists.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test(expected = AssertionError.class)
  public void testWithSSBefore() {
    getAttributeBy("lists", originalClass);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testWithSAfter() {
    ASTCDAttribute lists = getAttributeBy("lists", classWithS);
    assertTrue(lists.getModifier().isProtected());
    assertDeepEquals("List<String>", lists.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test(expected = AssertionError.class)
  public void testNoSAfter() {
    getAttributeBy("list", classWithS);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNonListAttributesWithoutS() {
    getAttributeBy("i", classWithS);
    getAttributeBy("s", classWithS);
    getAttributeBy("opt", classWithS);
    getAttributeBy("b", classWithS);
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
