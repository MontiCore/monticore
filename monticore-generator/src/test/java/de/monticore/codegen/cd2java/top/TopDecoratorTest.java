package de.monticore.codegen.cd2java.top;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.assertEquals;

public class TopDecoratorTest extends DecoratorTestCase {

  @Mock
  private IterablePath targetPath;

  private TopDecorator topDecorator;

  private ASTCDCompilationUnit topCD;

  @Before
  public void setup() {
    LogStub.init();
    this.targetPath = Mockito.mock(IterablePath.class);
    this.topDecorator = new TopDecorator(this.targetPath);
    this.topCD = this.parse("de", "monticore", "codegen", "top", "Top");
  }

  @Test
  public void testHandWrittenClassFound() {
    Mockito.when(targetPath.getResolvedPath(Mockito.any(Path.class))).thenReturn(Optional.of(Mockito.mock(Path.class)));
    ASTCDDefinition ast = this.topDecorator.decorate(this.topCD, this.topCD.deepClone()).getCDDefinition();

    assertEquals(1, ast.getCDClassList().size());
    ASTCDClass cdClass = ast.getCDClassList().get(0);
    assertEquals("CTOP", cdClass.getName());
    assertDeepEquals(PUBLIC_ABSTRACT, cdClass.getModifier());

    assertEquals(1, cdClass.getCDConstructorList().size());
    ASTCDConstructor constructor = cdClass.getCDConstructorList().get(0);
    assertEquals("CTOP", constructor.getName());
    assertDeepEquals(PROTECTED, constructor.getModifier());

    assertEquals(1, ast.getCDInterfaceList().size());
    ASTCDInterface cdInterface = ast.getCDInterfaceList().get(0);
    assertEquals("ITOP", cdInterface.getName());
    assertDeepEquals(PUBLIC, cdInterface.getModifier());

    assertEquals(1, ast.getCDEnumList().size());
    ASTCDEnum cdEnum = ast.getCDEnumList().get(0);
    assertEquals("ETOP", cdEnum.getName());
    assertDeepEquals(PUBLIC, cdEnum.getModifier());
  }

  @Test
  public void testHandWrittenClassNotFound() {
    Mockito.when(targetPath.exists(Mockito.any(Path.class))).thenReturn(false);
    ASTCDDefinition ast = this.topDecorator.decorate(this.topCD, this.topCD.deepClone()).getCDDefinition();

    assertEquals(1, ast.getCDClassList().size());
    ASTCDClass cdClass = ast.getCDClassList().get(0);
    assertEquals("C", cdClass.getName());
    assertDeepEquals(PUBLIC, cdClass.getModifier());

    assertEquals(1, cdClass.getCDConstructorList().size());
    ASTCDConstructor constructor = cdClass.getCDConstructorList().get(0);
    assertEquals("C", constructor.getName());
    assertDeepEquals(PROTECTED, constructor.getModifier());

    assertEquals(1, ast.getCDInterfaceList().size());
    ASTCDInterface cdInterface = ast.getCDInterfaceList().get(0);
    assertEquals("I", cdInterface.getName());
    assertDeepEquals(PUBLIC, cdInterface.getModifier());

    assertEquals(1, ast.getCDEnumList().size());
    ASTCDEnum cdEnum = ast.getCDEnumList().get(0);
    assertEquals("E", cdEnum.getName());
    assertDeepEquals(PUBLIC, cdEnum.getModifier());
  }
}
