package de.monticore.codegen.cd2java.builder;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.TypesHelper;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class BuilderMandatoryMethodDecoratorStrategyTest {

  private static final String BUILDER_CD = Paths.get("src/test/resources/de/monticore/codegen/builder/Mandatory.cd").toAbsolutePath().toString();

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass builderClass;

  @Before
  public void setup() throws IOException {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> ast = parser.parse(BUILDER_CD);
    if (!ast.isPresent()) {
      Log.error(BUILDER_CD + " is not present");
    }
    ASTCDClass cdClass = ast.get().getCDDefinition().getCDClass(0);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex);
    this.builderClass = builderDecorator.decorate(cdClass);
  }

  @Test
  public void testAttributes() {
    List<ASTCDAttribute> attributes = builderClass.getCDAttributeList();
    assertEquals(2, attributes.size());

    Optional<ASTCDAttribute> iOpt = attributes.stream().filter(a -> "i".equals(a.getName())).findFirst();
    assertTrue(iOpt.isPresent());
    ASTCDAttribute i = iOpt.get();
    assertEquals("protected", i.printModifier().trim());
    assertEquals("int", i.printType());

    Optional<ASTCDAttribute> realThisOpt = attributes.stream().filter(a -> "realThis".equals(a.getName())).findFirst();
    assertTrue(realThisOpt.isPresent());
    ASTCDAttribute realThis = realThisOpt.get();
    assertEquals("protected", realThis.printModifier().trim());
    assertEquals("ABuilder", realThis.printType());
  }

  @Test
  public void testMethods() {
    List<ASTCDMethod> methods = builderClass.getCDMethodList();
    assertEquals(4, methods.size());
  }

  @Test
  public void testGetMethod() {
    Optional<ASTCDMethod> getMethod = builderClass.getCDMethodList().stream().filter(m -> "getI".equals(m.getName())).findFirst();
    assertTrue(getMethod.isPresent());
    assertTrue(getMethod.get().getCDParameterList().isEmpty());
    assertEquals("public", getMethod.get().printModifier().trim());
    assertEquals("int", getMethod.get().printReturnType());
  }

  @Test
  public void testSetMethod() {
    Optional<ASTCDMethod> getMethod = builderClass.getCDMethodList().stream().filter(m -> "setI".equals(m.getName())).findFirst();
    assertTrue(getMethod.isPresent());

    assertEquals(1, getMethod.get().getCDParameterList().size());
    ASTCDParameter parameter = getMethod.get().getCDParameter(0);
    assertEquals("int", TypesHelper.printType(parameter.getType()));
    assertEquals("i", parameter.getName());

    assertEquals("public", getMethod.get().printModifier().trim());
    assertEquals("ABuilder", getMethod.get().printReturnType());
  }
}
