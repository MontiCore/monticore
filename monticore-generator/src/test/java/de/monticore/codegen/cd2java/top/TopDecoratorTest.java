package de.monticore.codegen.cd2java.top;

import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.io.paths.IterablePath;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
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

public class TopDecoratorTest extends DecoratorTestCase {

  @Mock
  private IterablePath targetPath;

  private TopDecorator topDecorator;

  private ASTCDCompilationUnit topCD;

  @Before
  public void setup() throws IOException {
    LogStub.init();
    this.targetPath = Mockito.mock(IterablePath.class);
    this.topDecorator = new TopDecorator(this.targetPath);
    this.topCD = this.parse("de", "monticore", "codegen", "top", "Top");
  }

  @Test
  public void testHandWrittenClassFound() {
    Mockito.when(targetPath.exists(Mockito.any(Path.class))).thenReturn(true);
    ASTCDDefinition ast = this.topDecorator.decorate(this.topCD).getCDDefinition();

    assertEquals(1, ast.getCDClassList().size());
    ASTCDClass cdClass = ast.getCDClassList().get(0);
    assertEquals("CTOP", cdClass.getName());
    assertTrue(PUBLIC_ABSTRACT.build().deepEquals(cdClass.getModifier()));

    assertEquals(1, cdClass.getCDConstructorList().size());
    ASTCDConstructor constructor = cdClass.getCDConstructorList().get(0);
    assertEquals("CTOP", constructor.getName());
    assertTrue(PROTECTED_ABSTRACT.build().deepEquals(constructor.getModifier()));

    assertEquals(1, ast.getCDInterfaceList().size());
    ASTCDInterface cdInterface = ast.getCDInterfaceList().get(0);
    assertEquals("ITOP", cdInterface.getName());
    assertTrue(PUBLIC_ABSTRACT.build().deepEquals(cdInterface.getModifier()));

    assertEquals(1, ast.getCDEnumList().size());
    ASTCDEnum cdEnum = ast.getCDEnumList().get(0);
    assertEquals("ETOP", cdEnum.getName());
    assertTrue(PUBLIC_ABSTRACT.build().deepEquals(cdEnum.getModifier()));
  }

  @Test
  public void testHandWrittenClassNotFound() {
    Mockito.when(targetPath.exists(Mockito.any(Path.class))).thenReturn(false);
    ASTCDDefinition ast = this.topDecorator.decorate(this.topCD).getCDDefinition();

    assertEquals(1, ast.getCDClassList().size());
    ASTCDClass cdClass = ast.getCDClassList().get(0);
    assertEquals("C", cdClass.getName());
    assertTrue(PUBLIC.build().deepEquals(cdClass.getModifier()));

    assertEquals(1, cdClass.getCDConstructorList().size());
    ASTCDConstructor constructor = cdClass.getCDConstructorList().get(0);
    assertEquals("C", constructor.getName());
    assertTrue(PROTECTED.build().deepEquals(constructor.getModifier()));

    assertEquals(1, ast.getCDInterfaceList().size());
    ASTCDInterface cdInterface = ast.getCDInterfaceList().get(0);
    assertEquals("I", cdInterface.getName());
    assertTrue(PUBLIC.build().deepEquals(cdInterface.getModifier()));

    assertEquals(1, ast.getCDEnumList().size());
    ASTCDEnum cdEnum = ast.getCDEnumList().get(0);
    assertEquals("E", cdEnum.getName());
    assertTrue(PUBLIC.build().deepEquals(cdEnum.getModifier()));
  }
}
