package de.monticore.codegen.cd2java.top;

import de.monticore.io.paths.IterablePath;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TopDecoratorTest {

  private static final String CD = Paths.get("src/test/resources/de/monticore/codegen/top/Top.cd").toAbsolutePath().toString();

  @Mock
  private IterablePath targetPath;

  private TopDecorator topDecorator;

  private ASTCDCompilationUnit topCD;

  @Before
  public void setup() throws IOException {
    LogStub.init();
    this.targetPath = Mockito.mock(IterablePath.class);
    this.topDecorator = new TopDecorator(this.targetPath);
    CD4AnalysisParser parser = new CD4AnalysisParser();
    this.topCD = parser.parse(CD).get();
  }

  @Test
  public void testHandWrittenClassFound() {
    Mockito.when(targetPath.exists(Mockito.any(Path.class))).thenReturn(true);
    ASTCDDefinition ast = this.topDecorator.decorate(this.topCD).getCDDefinition();

    assertEquals(1, ast.getCDClassList().size());
    ASTCDClass cdClass = ast.getCDClassList().get(0);
    assertEquals("CTOP", cdClass.getName());
    assertTrue(PUBLIC_ABSTRACT.deepEquals(cdClass.getModifier()));

    assertEquals(1, cdClass.getCDConstructorList().size());
    ASTCDConstructor constructor = cdClass.getCDConstructorList().get(0);
    assertEquals("CTOP", constructor.getName());
    assertTrue(PROTECTED_ABSTRACT.deepEquals(constructor.getModifier()));

    assertEquals(1, ast.getCDInterfaceList().size());
    ASTCDInterface cdInterface = ast.getCDInterfaceList().get(0);
    assertEquals("ITOP", cdInterface.getName());
    assertTrue(PUBLIC_ABSTRACT.deepEquals(cdInterface.getModifier()));

    assertEquals(1, ast.getCDEnumList().size());
    ASTCDEnum cdEnum = ast.getCDEnumList().get(0);
    assertEquals("ETOP", cdEnum.getName());
    assertTrue(PUBLIC_ABSTRACT.deepEquals(cdEnum.getModifier()));
  }

  @Test
  public void testHandWrittenClassNotFound() {
    Mockito.when(targetPath.exists(Mockito.any(Path.class))).thenReturn(false);
    ASTCDDefinition ast = this.topDecorator.decorate(this.topCD).getCDDefinition();

    assertEquals(1, ast.getCDClassList().size());
    ASTCDClass cdClass = ast.getCDClassList().get(0);
    assertEquals("C", cdClass.getName());
    assertTrue(PUBLIC.deepEquals(cdClass.getModifier()));

    assertEquals(1, cdClass.getCDConstructorList().size());
    ASTCDConstructor constructor = cdClass.getCDConstructorList().get(0);
    assertEquals("C", constructor.getName());
    assertTrue(PROTECTED.deepEquals(constructor.getModifier()));

    assertEquals(1, ast.getCDInterfaceList().size());
    ASTCDInterface cdInterface = ast.getCDInterfaceList().get(0);
    assertEquals("I", cdInterface.getName());
    assertTrue(PUBLIC.deepEquals(cdInterface.getModifier()));

    assertEquals(1, ast.getCDEnumList().size());
    ASTCDEnum cdEnum = ast.getCDEnumList().get(0);
    assertEquals("E", cdEnum.getName());
    assertTrue(PUBLIC.deepEquals(cdEnum.getModifier()));
  }
}
