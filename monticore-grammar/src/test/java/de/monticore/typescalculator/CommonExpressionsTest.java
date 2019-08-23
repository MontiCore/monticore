/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.io.paths.ModelPath;
import de.monticore.typescalculator.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.typescalculator.combineexpressionswithliterals._ast.CombineExpressionsWithLiteralsMill;
import de.monticore.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.typescalculator.combineexpressionswithliterals._symboltable.*;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class CommonExpressionsTest {

  private CombineExpressionsWithLiteralsGlobalScope globalScope;

  private CombineExpressionsWithLiteralsArtifactScope artifactScope;


  @Before
  public void setup() throws IOException{
    Log.enableFailQuick(false);
    ASTExpression expression = new CombineExpressionsWithLiteralsParser().parse_StringExpression("A").get();
    ASTFoo ast = CombineExpressionsWithLiteralsMill.fooBuilder().setExpression(expression).build();
    CombineExpressionsWithLiteralsLanguage language = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsLanguageBuilder().build();
    globalScope = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsGlobalScopeBuilder().setLanguage(language).setModelPath(new ModelPath()).build();
    CombineExpressionsWithLiteralsSymbolTableCreatorDelegator stc = language.getSymbolTableCreator(globalScope);
    artifactScope = stc.createFromAST(ast);
    globalScope.addAdaptedEMethodSymbolResolvingDelegate(new DummyAdapter(artifactScope));
    globalScope.addAdaptedETypeSymbolResolvingDelegate(new DummyAdapter(artifactScope));
    globalScope.addAdaptedEVariableSymbolResolvingDelegate(new DummyAdapter(artifactScope));
  }


  @Test
  public void plusTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4+3");
    Optional<ASTExpression> r = p.parse_StringExpression("9.13+7.73");
    Optional<ASTExpression> s = p.parse_StringExpression("9+7");
    Optional<ASTExpression> t = p.parse_StringExpression("9l+10.0f");
    Optional<ASTExpression> u = p.parse_StringExpression("\'a\'+13.4");
    Optional<ASTExpression> v = p.parse_StringExpression("\'a\'+\'b\'");
    Optional<ASTExpression> w = p.parse_StringExpression("\"Hallo\"+\" Welt\"");
    Optional<ASTExpression> x = p.parse_StringExpression("\"Hallo\"+4.3f");

    TypeExpression exp = new TypeExpression();
    exp.setName("double");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(q.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    exp.setName("int");
    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));

    exp.setName("float");
    assertTrue(t.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(t.get())));

    exp.setName("double");
    assertTrue(u.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(u.get())));

    exp.setName("int");
    assertTrue(v.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(v.get())));

    exp.setName("String");
    assertTrue(w.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(w.get())));

    assertTrue(x.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(x.get())));
  }

  @Test
  public void moduloTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9%7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4%3");
    Optional<ASTExpression> r = p.parse_StringExpression("9.13%7.73");
    Optional<ASTExpression> s = p.parse_StringExpression("9%7");
    Optional<ASTExpression> t = p.parse_StringExpression("9l%10.0f");
    Optional<ASTExpression> u = p.parse_StringExpression("\'a\'%13.4");
    Optional<ASTExpression> v = p.parse_StringExpression("\'a\'%\'b\'");

    TypeExpression exp = new TypeExpression();
    exp.setName("double");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(q.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    exp.setName("int");
    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));

    exp.setName("float");
    assertTrue(t.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(t.get())));

    exp.setName("double");
    assertTrue(u.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(u.get())));

    exp.setName("int");
    assertTrue(v.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(v.get())));
  }

  @Test
  public void multTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4*3");
    Optional<ASTExpression> r = p.parse_StringExpression("9.13*7.73");
    Optional<ASTExpression> s = p.parse_StringExpression("9*7");
    Optional<ASTExpression> t = p.parse_StringExpression("9l*10.0f");
    Optional<ASTExpression> u = p.parse_StringExpression("\'a\'*13.4");
    Optional<ASTExpression> v = p.parse_StringExpression("\'a\'*\'b\'");

    TypeExpression exp = new TypeExpression();
    exp.setName("double");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(q.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    exp.setName("int");
    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));

    exp.setName("float");
    assertTrue(t.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(t.get())));

    exp.setName("double");
    assertTrue(u.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(u.get())));

    exp.setName("int");
    assertTrue(v.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(v.get())));
  }

  @Test
  public void divideTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9/7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4/3");
    Optional<ASTExpression> r = p.parse_StringExpression("9.13/7.73");
    Optional<ASTExpression> s = p.parse_StringExpression("9/7");
    Optional<ASTExpression> t = p.parse_StringExpression("9l/10.0f");
    Optional<ASTExpression> u = p.parse_StringExpression("\'a\'/13.4");
    Optional<ASTExpression> v = p.parse_StringExpression("\'a\'/\'b\'");

    TypeExpression exp = new TypeExpression();
    exp.setName("double");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(q.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    exp.setName("int");
    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));

    exp.setName("float");
    assertTrue(t.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(t.get())));

    exp.setName("double");
    assertTrue(u.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(u.get())));

    exp.setName("int");
    assertTrue(v.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(v.get())));
  }

  @Test
  public void minusTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9-7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4-3");
    Optional<ASTExpression> r = p.parse_StringExpression("9.13-7.73");
    Optional<ASTExpression> s = p.parse_StringExpression("9-7");
    Optional<ASTExpression> t = p.parse_StringExpression("9l-10.0f");
    Optional<ASTExpression> u = p.parse_StringExpression("\'a\'-13.4");
    Optional<ASTExpression> v = p.parse_StringExpression("\'a\'-\'b\'");

    TypeExpression exp = new TypeExpression();
    exp.setName("double");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(q.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    exp.setName("int");
    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));

    exp.setName("float");
    assertTrue(t.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(t.get())));

    exp.setName("double");
    assertTrue(u.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(u.get())));

    exp.setName("int");
    assertTrue(v.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(v.get())));
  }

  @Test
  public void lessThanTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4<7");
    Optional<ASTExpression> r = p.parse_StringExpression("4.5f<7l");
    Optional<ASTExpression> s = p.parse_StringExpression("4.5<\'a\'");

    TypeExpression exp = new TypeExpression();
    exp.setName("boolean");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void greaterThanTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("15>9.2");
    Optional<ASTExpression> r = p.parse_StringExpression("4.5f>7l");
    Optional<ASTExpression> s = p.parse_StringExpression("4.5>\'a\'");

    TypeExpression exp = new TypeExpression();
    exp.setName("boolean");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void lessEqualTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4<=7");
    Optional<ASTExpression> r = p.parse_StringExpression("4.5f<=7l");
    Optional<ASTExpression> s = p.parse_StringExpression("4.5<=\'a\'");

    TypeExpression exp = new TypeExpression();
    exp.setName("boolean");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void greaterEqualTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4>=2.7");
    Optional<ASTExpression> r = p.parse_StringExpression("4.5f>=7l");
    Optional<ASTExpression> s = p.parse_StringExpression("4.5>=\'a\'");

    TypeExpression exp = new TypeExpression();
    exp.setName("boolean");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void logicalNotTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("!true");
    Optional<ASTExpression> r = p.parse_StringExpression("!(3<=7)");

    TypeExpression exp = new TypeExpression();
    exp.setName("boolean");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void booleanOrOpTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("true||false");
    Optional<ASTExpression> r = p.parse_StringExpression("(4>3)||varboolean");

    TypeExpression exp = new TypeExpression();
    exp.setName("boolean");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void booleanAndOpTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("true&&true");
    Optional<ASTExpression> r = p.parse_StringExpression("(4>3)&&varboolean");

    TypeExpression exp = new TypeExpression();
    exp.setName("boolean");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void notEqualsTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("3!=4");
    Optional<ASTExpression> r = p.parse_StringExpression("\"Hallo\"!=\"Welt\"");
    Optional<ASTExpression> q = p.parse_StringExpression("varList!=varList");
    Optional<ASTExpression> s = p.parse_StringExpression("7.9f!=8l");

    TypeExpression exp = new TypeExpression();
    exp.setName("boolean");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    assertTrue(q.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(q.get())));

    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void equalsTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7==7");
    Optional<ASTExpression> r = p.parse_StringExpression("\"Hallo\"==\"Welt\"");
    Optional<ASTExpression> q = p.parse_StringExpression("varList==varList");
    Optional<ASTExpression> s = p.parse_StringExpression("7.9f==8l");

    TypeExpression exp = new TypeExpression();
    exp.setName("boolean");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    assertTrue(q.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(q.get())));

    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void bracketTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("(7+8)");
    Optional<ASTExpression> r = p.parse_StringExpression("(7-2.5)");
    Optional<ASTExpression> s = p.parse_StringExpression("(false==(4<3))");
    Optional<ASTExpression> t = p.parse_StringExpression("(7l-2.5f)");

    TypeExpression exp = new TypeExpression();
    exp.setName("int");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    exp.setName("double");
    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    exp.setName("boolean");
    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));

    exp.setName("float");
    assertTrue(t.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void conditionalTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7<3? 7 : 3");
    Optional<ASTExpression> r = p.parse_StringExpression("7.2<3? 7.2 : 3");
    Optional<ASTExpression> s = p.parse_StringExpression("7.2f<3l? 7.2f : 3l");
    Optional<ASTExpression> t = p.parse_StringExpression("(\"Hallo\"==\"Welt\")? \"Gleich\" : \"Ungleich\"");

    TypeExpression exp = new TypeExpression();
    exp.setName("int");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    exp.setName("double");
    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    exp.setName("float");
    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));

    exp.setName("String");
    assertTrue(t.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void booleanNotTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("~7");
    Optional<ASTExpression> r = p.parse_StringExpression("~7l");
    Optional<ASTExpression> s = p.parse_StringExpression("~\'a\'");

    TypeExpression exp = new TypeExpression();
    exp.setName("int");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    exp.setName("long");
    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    exp.setName("int");
    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void combineOperationsTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7.4-3/2+4");
    Optional<ASTExpression> q = p.parse_StringExpression("9l*4+3-5/2");
    Optional<ASTExpression> r = p.parse_StringExpression("3<4+\'a\'");
    Optional<ASTExpression> s = p.parse_StringExpression("(4<6)&&true");
    Optional<ASTExpression> t = p.parse_StringExpression("~3<4? ~3 : 4");
    Optional<ASTExpression> u = p.parse_StringExpression("9.2f*(7.2+8)");
    Optional<ASTExpression> v = p.parse_StringExpression("false&&true");

    TypeExpression exp = new TypeExpression();
    exp.setName("double");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    exp.setName("long");
    assertTrue(q.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(q.get())));

    exp.setName("boolean");
    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));

    exp.setName("int");
    assertTrue(t.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(t.get())));

    exp.setName("double");
    assertTrue(u.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(u.get())));

    exp.setName("boolean");
    assertTrue(v.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(v.get())));
  }

  @Test
  public void callTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("call()");
    Optional<ASTExpression> q = p.parse_StringExpression("A.B.C.call()");

    TypeExpression exp = new TypeExpression();
    exp.setName("int");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(q.get())));
  }

  @Ignore
  @Test
  public void callStringTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("(\"3\"+\"7\").toString()");

    assertTrue(o.isPresent());
    ASTExpression expr = o.get();

    expr.accept(calc);
  }
}
