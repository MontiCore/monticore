package de.monticore.codegen.cd2java.data;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static org.junit.Assert.assertTrue;

public class ListSuffixDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass classWithS;

  private ASTCDClass originalClass;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    ASTCDCompilationUnit cd = this.parse("de", "monticore", "codegen", "data", "Data");

    originalClass = getClassBy("A", cd).deepClone();

    this.glex.setGlobalValue("service", new AbstractService(cd));
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    ListSuffixDecorator listSuffixDecorator = new ListSuffixDecorator();
    cd = listSuffixDecorator.decorate(cd, cd);

    classWithS = getClassBy("A", cd);

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
  }

  @Test
  public void testNoSBefore() {
    ASTCDAttribute lists = getAttributeBy("list", originalClass);
    assertTrue(lists.isPresentModifier());
    assertDeepEquals(PROTECTED, lists.getModifier());
    assertDeepEquals("List<String>", lists.getMCType());
  }

  @Test(expected = AssertionError.class)
  public void testWithSSBefore() {
    getAttributeBy("lists", originalClass);
  }

  @Test
  public void testWithSAfter() {
    ASTCDAttribute lists = getAttributeBy("lists", classWithS);
    assertTrue(lists.isPresentModifier());
    assertDeepEquals(PROTECTED, lists.getModifier());
    assertDeepEquals("List<String>", lists.getMCType());
  }

  @Test(expected = AssertionError.class)
  public void testNoSAfter() {
    getAttributeBy("list", classWithS);
  }

  @Test
  public void testNonListAttributesWithoutS(){
    getAttributeBy("i", classWithS);
    getAttributeBy("s", classWithS);
    getAttributeBy("opt", classWithS);
    getAttributeBy("b", classWithS);

  }
}
