/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.io.paths.ModelPath;
import de.monticore.types2.SymObjectType;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;
import de.monticore.typescalculator.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.typescalculator.combineexpressionswithliterals._ast.CombineExpressionsWithLiteralsMill;
import de.monticore.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.typescalculator.combineexpressionswithliterals._symboltable.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.typescalculator.TypesCalculator.*;
import static org.junit.Assert.*;

public class TypesCalculatorTest {

  CombineExpressionsWithLiteralsGlobalScope globalScope;

  CombineExpressionsWithLiteralsArtifactScope artifactScope;

  @Before
  public void setup() throws IOException {
    LogStub.init();

    ASTExpression expression = new CombineExpressionsWithLiteralsParser().parse_StringExpression("A").get();
    ASTFoo ast = CombineExpressionsWithLiteralsMill.fooBuilder().setExpression(expression).build();
    CombineExpressionsWithLiteralsLanguage language = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsLanguageBuilder().build();
    globalScope = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsGlobalScopeBuilder().setLanguage(language).setModelPath(new ModelPath()).build();
    CombineExpressionsWithLiteralsSymbolTableCreatorDelegator stc = language.getSymbolTableCreator(globalScope);
    artifactScope = stc.createFromAST(ast);
    globalScope.addAdaptedEMethodSymbolResolvingDelegate(new DummyAdapter(artifactScope));
    globalScope.addAdaptedETypeSymbolResolvingDelegate(new DummyAdapter(artifactScope));
    globalScope.addAdaptedEVariableSymbolResolvingDelegate(new DummyAdapter(artifactScope));

    TypesCalculator.setExpressionAndLiteralsTypeCalculator(new CombineExpressionsWithLiteralsTypesCalculator(artifactScope));
  }

  @Test
  public void testIsBoolean() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("7<=4&&9>3");
    Optional<ASTExpression> b = p.parse_StringExpression("7+3-(23*9)");

    assertTrue(a.isPresent());
    assertTrue(isBoolean(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isBoolean(b.get()));
  }

  @Test
  public void testIsInt() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("7+3-(23*9)");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");

    assertTrue(a.isPresent());
    assertTrue(isInt(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isInt(b.get()));
  }

  @Test
  public void testIsDouble() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("7.3+3-(23*9)");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");

    assertTrue(a.isPresent());
    assertTrue(isDouble(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isDouble(b.get()));
  }

  @Test
  public void testIsFloat() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("7.3f+3-(23*9)");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");

    assertTrue(a.isPresent());
    assertTrue(isFloat(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isFloat(b.get()));
  }

  @Test
  public void testIsLong() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("7L+3-(23*9)");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");

    assertTrue(a.isPresent());
    assertTrue(isLong(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isLong(b.get()));
  }

  @Test
  public void testIsChar() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("\'3\'");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");

    assertTrue(a.isPresent());
    assertTrue(isChar(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isChar(b.get()));
  }

  @Test
  public void testIsPrimitive() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("\'3\'");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");
    Optional<ASTExpression> c = p.parse_StringExpression("\"Hello World\"");

    assertTrue(a.isPresent());
    assertTrue(isPrimitive(a.get()));

    assertTrue(b.isPresent());
    assertTrue(isPrimitive(b.get()));

    assertTrue(c.isPresent());
    assertFalse(isPrimitive(c.get()));
  }

  @Test
  public void testIsAssignable() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    TypesCalculator.setScope(artifactScope);
    Optional<ASTExpression> a = p.parse_StringExpression("vardouble");
    Optional<ASTExpression> b = p.parse_StringExpression("varint");
    Optional<ASTExpression> c = p.parse_StringExpression("varSuperTest");
    Optional<ASTExpression> d = p.parse_StringExpression("varTest");
    Optional<ASTExpression> e = p.parse_StringExpression("5");

    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    assertTrue(c.isPresent());
    assertTrue(d.isPresent());
    assertTrue(e.isPresent());
    assertTrue(isAssignableFrom(a.get(),b.get()));

    assertFalse(isAssignableFrom(b.get(),a.get()));

    //    assertTrue(isAssignableFrom(c.get(),d.get()));

    //    assertFalse(isAssignableFrom(d.get(),c.get()));

    assertTrue(isAssignableFrom(a.get(),e.get()));

    assertTrue(isAssignableFrom(a.get(),a.get()));
  }

  @Test
  public void testIsSubtype() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    TypesCalculator.setScope(artifactScope);
    Optional<ASTExpression> a = p.parse_StringExpression("vardouble");
    Optional<ASTExpression> b = p.parse_StringExpression("varint");
    Optional<ASTExpression> c = p.parse_StringExpression("varSuperTest");
    Optional<ASTExpression> d = p.parse_StringExpression("varTest");
    Optional<ASTExpression> e = p.parse_StringExpression("varLinkedList");
    Optional<ASTExpression> f = p.parse_StringExpression("varFoo");
    Optional<ASTExpression> g = p.parse_StringExpression("varCollection");
    Optional<ASTExpression> h = p.parse_StringExpression("varList");

    assertTrue(a.isPresent());
    assertTrue(b.isPresent());

    assertTrue(isSubtypeOf(b.get(),a.get()));

    assertFalse(isSubtypeOf(a.get(),b.get()));

    assertTrue(c.isPresent());
    assertTrue(d.isPresent());

    //    assertTrue(isSubtypeOf(d.get(),c.get()));

    //    assertFalse(isSubtypeOf(c.get(),d.get()));

    assertTrue(e.isPresent());
    assertTrue(f.isPresent());
    assertTrue(g.isPresent());
    assertTrue(h.isPresent());

    //    assertTrue(isSubtypeOf(e.get(),f.get()));
    //    assertTrue(isSubtypeOf(e.get(),g.get()));
    //    assertTrue(isSubtypeOf(e.get(),h.get()));
    //    assertTrue(isSubtypeOf(h.get(),g.get()));
    //    assertFalse(isSubtypeOf(f.get(),g.get()));
    //    assertFalse(isSubtypeOf(h.get(),f.get()));
  }

  @Test
  public void testGetType() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    TypesCalculator.setScope(artifactScope);
    Optional<ASTExpression> a = p.parse_StringExpression("13+12.5-9");
    Optional<ASTExpression> b = p.parse_StringExpression("varTest");

    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("double");
    assertTrue(a.isPresent());
    assertTrue(exp.deepEquals(getType(a.get())));

    SymTypeExpression exp2 = new SymObjectType();
    exp2.setName("Test");
    assertTrue(b.isPresent());
    assertTrue(exp2.deepEquals(getType(b.get())));
  }

  @Test
  public void testGetTypeString() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    TypesCalculator.setScope(artifactScope);
    Optional<ASTExpression> a = p.parse_StringExpression("13+12.5-9");
    Optional<ASTExpression> b = p.parse_StringExpression("varTest");
    Optional<ASTExpression> c = p.parse_StringExpression("varint");

    assertTrue(a.isPresent());
    assertEquals("double", getTypeString(a.get()));

    assertTrue(b.isPresent());
    assertEquals("Test", getTypeString(b.get()));

    assertTrue(c.isPresent());
    assertEquals("int",getTypeString(c.get()));
  }
}
