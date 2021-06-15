/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.top;

import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.*;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.CDModifier.*;
import static org.junit.Assert.assertEquals;

public class TopDecoratorTest extends DecoratorTestCase {

  @Mock
  private MCPath targetPath;

  private TopDecorator topDecorator;

  private ASTCDCompilationUnit topCD;

  @Before
  public void setup() {
    LogStub.init();
    this.targetPath = Mockito.mock(MCPath.class);
    this.topDecorator = new TopDecorator(this.targetPath);
    this.topCD = this.parse("de", "monticore", "codegen", "top", "Top");
  }

  @Test
  public void testHandWrittenClassFound() {
    Mockito.when(targetPath.getResolvedPath(Mockito.any(MCPath.class))).thenReturn(Optional.of(Mockito.mock(MCPath.class)));
    ASTCDDefinition ast = this.topDecorator.decorate(this.topCD).getCDDefinition();

    assertEquals(1, ast.getCDClassesList().size());
    ASTCDClass cdClass = ast.getCDClassesList().get(0);
    assertEquals("CTOP", cdClass.getName());
    assertDeepEquals(PUBLIC_ABSTRACT, cdClass.getModifier());

    assertEquals(1, cdClass.getCDConstructorList().size());
    ASTCDConstructor constructor = cdClass.getCDConstructorList().get(0);
    assertEquals("CTOP", constructor.getName());
    assertDeepEquals(PROTECTED, constructor.getModifier());

    assertEquals(1, ast.getCDInterfacesList().size());
    ASTCDInterface cdInterface = ast.getCDInterfacesList().get(0);
    assertEquals("ITOP", cdInterface.getName());
    assertDeepEquals(PUBLIC, cdInterface.getModifier());

    assertEquals(1, ast.getCDEnumsList().size());
    ASTCDEnum cdEnum = ast.getCDEnumsList().get(0);
    assertEquals("ETOP", cdEnum.getName());
    assertDeepEquals(PUBLIC, cdEnum.getModifier());
  }

  @Test
  public void testHandWrittenClassNotFound() {
    Mockito.when(targetPath.exists(Mockito.any(MCPath.class))).thenReturn(false);
    ASTCDDefinition ast = this.topDecorator.decorate(this.topCD).getCDDefinition();

    assertEquals(1, ast.getCDClassesList().size());
    ASTCDClass cdClass = ast.getCDClassesList().get(0);
    assertEquals("C", cdClass.getName());
    assertDeepEquals(PUBLIC, cdClass.getModifier());

    assertEquals(1, cdClass.getCDConstructorList().size());
    ASTCDConstructor constructor = cdClass.getCDConstructorList().get(0);
    assertEquals("C", constructor.getName());
    assertDeepEquals(PROTECTED, constructor.getModifier());

    assertEquals(1, ast.getCDInterfacesList().size());
    ASTCDInterface cdInterface = ast.getCDInterfacesList().get(0);
    assertEquals("I", cdInterface.getName());
    assertDeepEquals(PUBLIC, cdInterface.getModifier());

    assertEquals(1, ast.getCDEnumsList().size());
    ASTCDEnum cdEnum = ast.getCDEnumsList().get(0);
    assertEquals("E", cdEnum.getName());
    assertDeepEquals(PUBLIC, cdEnum.getModifier());
  }
}
