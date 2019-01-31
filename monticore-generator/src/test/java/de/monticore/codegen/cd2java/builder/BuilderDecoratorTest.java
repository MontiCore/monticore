package de.monticore.codegen.cd2java.builder;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuilderDecoratorTest {

  private static final String BUILDER_CD = Paths.get("src/test/resources/de/monticore/codegen/builder/Builder.cd").toAbsolutePath().toString();

  private ASTCDClass builderClass;

  @Before
  public void setup() throws IOException {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> ast = parser.parse(BUILDER_CD);
    if (!ast.isPresent()) {
      Log.error(BUILDER_CD + " is not present");
    }
    ASTCDClass cdClass = ast.get().getCDDefinition().getCDClass(0);

    BuilderGenerator builderGenerator = new BuilderGenerator(new GlobalExtensionManagement());
    this.builderClass = builderGenerator.generate(cdClass);
  }

  @Test
  public void testBuilderClassName() {
    assertEquals("ABuilder", builderClass.getName());
  }

  @Test
  public void testBuilderConstructor() {
    List<ASTCDConstructor> constructors = builderClass.getCDConstructorList();
    assertEquals(1, constructors.size());
    ASTCDConstructor constructor = constructors.get(0);
    assertEquals("protected", constructor.printModifier().trim());
    assertEquals(0, constructor.getCDParameterList().size());
  }
}
