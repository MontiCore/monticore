/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_new;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static org.junit.Assert.*;

public class ASTSymbolDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDAttribute> attributes;

  @Before
  public void setup() {
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "AST");

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("service", new AbstractService(ast));

    ASTSymbolDecorator decorator = new ASTSymbolDecorator(this.glex, new SymbolTableService(ast));
    ASTCDClass clazz = getClassBy("A", ast);
    this.attributes = decorator.decorate(clazz);
  }

  @Test
  public void testAttributes() {
    assertFalse(attributes.isEmpty());
    assertEquals(2, attributes.size());
  }

  @Test
  public void testSymbolAttribute() {
    Optional<ASTCDAttribute> symbolAttribute = attributes.stream().filter(x -> x.getName().equals("aSymbol")).findFirst();
    assertTrue(symbolAttribute.isPresent());
    assertDeepEquals(PROTECTED, symbolAttribute.get().getModifier());
    assertOptionalOf("de.monticore.codegen.ast.ast._symboltable.ASymbol", symbolAttribute.get().getMCType());
  }

  @Test
  public void testSymbolAttribute2() {
    Optional<ASTCDAttribute> symbolAttribute = attributes.stream().filter(x -> x.getName().equals("symbol")).findFirst();
    assertTrue(symbolAttribute.isPresent());
    assertDeepEquals(PROTECTED, symbolAttribute.get().getModifier());
    assertOptionalOf("de.monticore.codegen.ast.ast._symboltable.ASymbol", symbolAttribute.get().getMCType());
  }
}
