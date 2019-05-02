package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.ast_new.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.factory.NodeFactoryService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ASTFullDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();


  private ASTCDClass astClass;


  @Before
  public void setup() {
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit  compilationUnit= this.parse("de", "monticore", "codegen", "ast", "AST");
    ASTService astService = new ASTService(compilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(compilationUnit);
    VisitorService visitorService = new VisitorService(compilationUnit);
    NodeFactoryService nodeFactoryService = new NodeFactoryService(compilationUnit);

    DataDecorator dataDecorator = new DataDecorator(glex, new MethodDecorator(glex), new ASTService(compilationUnit), new DataDecoratorUtil());
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService, nodeFactoryService);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, new MethodDecorator(glex), symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, new MethodDecorator(glex), symbolTableService);
    ASTReferenceDecorator astReferencedSymbolDecorator = new ASTReferenceDecorator(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astSymbolDecorator, astScopeDecorator, astReferencedSymbolDecorator);

    ASTCDClass clazz = getClassBy("A", compilationUnit);
    this.astClass = fullDecorator.decorate(clazz);
  }

  @Test
  public void testClassName() {
    assertEquals("A", astClass.getName());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(2, astClass.getCDAttributeList().size());
  }

  @Test
  public void testConstructorSize() {
    assertEquals(1, astClass.getCDConstructorList().size());
  }

  @Test
  public void testMethodSize() {
    assertFalse(astClass.getCDMethodList().isEmpty());
    assertEquals(23, astClass.getCDMethodList().size());
  }

}
