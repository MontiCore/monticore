// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SerializationCDDecoratorTest extends DecoratorTestCase {

  private ASTCDCompilationUnit decoratedASTCompilationUnit;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDCompilationUnit originalASTCompilationUnit;

  private ASTCDCompilationUnit originalSymbolCompilationUnit;

  private ASTCDCompilationUnit originalScopeCompilationUnit;

  private ASTCDCompilationUnit serializationCD;

  @Before
  public void setUp() {
    Log.init();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();

    glex.setGlobalValue("astHelper", new DecorationHelper());
    glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedASTCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    originalASTCompilationUnit = decoratedASTCompilationUnit.deepClone();
    originalSymbolCompilationUnit = decoratedSymbolCompilationUnit.deepClone();
    originalScopeCompilationUnit = decoratedScopeCompilationUnit.deepClone();

    SymbolTableService symbolTableService = new SymbolTableService(decoratedASTCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedASTCompilationUnit);

    SymbolDeSerDecorator symbolDeSerDecorator = new SymbolDeSerDecorator(glex, symbolTableService);
    ScopeDeSerDecorator scopeDeSerDecorator = new ScopeDeSerDecorator(glex, symbolTableService);
    SymbolTablePrinterDecorator symbolTablePrinterDecorator = new SymbolTablePrinterDecorator(glex, symbolTableService, visitorService);

    SerializationCDDecorator serializationCDDecorator = new SerializationCDDecorator(glex, symbolTableService, symbolDeSerDecorator,
        scopeDeSerDecorator, symbolTablePrinterDecorator);
    serializationCD = serializationCDDecorator.decorate(decoratedASTCompilationUnit, decoratedSymbolCompilationUnit
        , decoratedScopeCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalASTCompilationUnit, decoratedASTCompilationUnit);
    assertDeepEquals(originalSymbolCompilationUnit, originalSymbolCompilationUnit);
    assertDeepEquals(originalScopeCompilationUnit, originalScopeCompilationUnit);
  }

  @Test
  public void testCDName() {
    assertEquals("AutomatonSymbolCD", serializationCD.getCDDefinition().getName());
  }

  @Test
  public void testClassCount() {
    assertEquals(5, serializationCD.getCDDefinition().getCDClassList().size());
  }

  @Test
  public void testClassNames() {
    ASTCDClass automatonSymbol = getClassBy("AutomatonSymbolDeSer", serializationCD);
    ASTCDClass stateSymbol = getClassBy("StateSymbolDeSer", serializationCD);
    ASTCDClass fooSymbol = getClassBy("FooSymbolDeSer", serializationCD);
    ASTCDClass automatonSymbolBuilder = getClassBy("AutomatonScopeDeSer", serializationCD);
    ASTCDClass stateSymbolBuilder = getClassBy("AutomatonSymbolTablePrinter", serializationCD);
  }

  @Test
  public void testNoInterfaces() {
    assertTrue(serializationCD.getCDDefinition().isEmptyCDInterfaces());
  }

  @Test
  public void testNoEnum() {
    assertTrue(serializationCD.getCDDefinition().isEmptyCDEnums());
  }

  @Test
  public void testPackage() {
    assertEquals(7, serializationCD.getPackageList().size());
    assertEquals("de", serializationCD.getPackageList().get(0));
    assertEquals("monticore", serializationCD.getPackageList().get(1));
    assertEquals("codegen", serializationCD.getPackageList().get(2));
    assertEquals("symboltable", serializationCD.getPackageList().get(3));
    assertEquals("automatonsymbolcd", serializationCD.getPackageList().get(4));
    assertEquals("_symboltable", serializationCD.getPackageList().get(5));
    assertEquals("serialization", serializationCD.getPackageList().get(6));
  }

  @Test
  public void testImports() {
    assertEquals(0, serializationCD.getMCImportStatementList().size());
  }

}
