package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.builder.BuilderGeneratorConstants.BUILD_METHOD;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class BuilderDecoratorTest {

  private static final String BUILDER_CD = Paths.get("src/test/resources/de/monticore/codegen/builder/Builder.cd").toAbsolutePath().toString();

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

    BuilderGenerator builderGenerator = new BuilderGenerator(glex);
    this.builderClass = builderGenerator.generate(cdClass);
  }

  @Test
  public void testClassName() {
    assertEquals("ABuilder", builderClass.getName());
  }

  @Test
  public void testConstructor() {
    List<ASTCDConstructor> constructors = builderClass.getCDConstructorList();
    assertEquals(1, constructors.size());
    ASTCDConstructor constructor = constructors.get(0);
    assertEquals("protected", constructor.printModifier().trim());
    assertEquals(0, constructor.getCDParameterList().size());
  }

  @Test
  public void testAttributes() {
    List<ASTCDAttribute> attributes = builderClass.getCDAttributeList();
    assertEquals(4, attributes.size());

    Optional<ASTCDAttribute> iOpt = attributes.stream().filter(a -> "i".equals(a.getName())).findFirst();
    assertTrue(iOpt.isPresent());
    ASTCDAttribute i = iOpt.get();
    assertEquals("protected", i.printModifier().trim());
    assertEquals("int", i.printType());

    Optional<ASTCDAttribute> sOpt = attributes.stream().filter(a -> "s".equals(a.getName())).findFirst();
    assertTrue(sOpt.isPresent());
    ASTCDAttribute s = sOpt.get();
    assertEquals("protected", s.printModifier().trim());
    assertEquals("String", s.printType());

    Optional<ASTCDAttribute> optOpt = attributes.stream().filter(a -> "opt".equals(a.getName())).findFirst();
    assertTrue(optOpt.isPresent());
    ASTCDAttribute opt = optOpt.get();
    assertEquals("protected", opt.printModifier().trim());
    assertEquals("Optional<String>", opt.printType());

    Optional<ASTCDAttribute> realThisOpt = attributes.stream().filter(a -> "realThis".equals(a.getName())).findFirst();
    assertTrue(realThisOpt.isPresent());
    ASTCDAttribute realThis = realThisOpt.get();
    assertEquals("protected", realThis.printModifier().trim());
    assertEquals("ABuilder", realThis.printType());
  }

  @Test
  public void testBuildMethod() {
    Optional<ASTCDMethod> buildOpt = builderClass.getCDMethodList().stream().filter(m -> BUILD_METHOD.equals(m.getName())).findFirst();
    assertTrue(buildOpt.isPresent());
    ASTCDMethod build = buildOpt.get();
    assertEquals("A", build.printReturnType());
    assertEquals("public", build.printModifier().trim());
    assertEquals(0, build.getCDParameterList().size());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, builderClass, builderClass);
    System.out.println(sb.toString());
  }
}
