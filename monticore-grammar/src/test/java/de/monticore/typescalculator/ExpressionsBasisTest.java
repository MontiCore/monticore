package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.io.paths.ModelPath;
import de.monticore.typescalculator.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.typescalculator.combineexpressionswithliterals._ast.CombineExpressionsWithLiteralsMill;
import de.monticore.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.typescalculator.combineexpressionswithliterals._symboltable.*;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExpressionsBasisTest {

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
  public void nameTest() throws IOException {
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("int");
    Optional<ASTExpression> r = p.parse_StringExpression("vardouble");

    TypeExpression exp = new TypeExpression();
    exp.setName("int");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    exp.setName("double");
    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void qualifiedNameTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("A.B.C.QName");

    TypeExpression exp = new TypeExpression();
    exp.setName("A.B.C.QName");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));
    assertEquals("A.B.C.QName", calc.calculateType(o.get()).getName());
  }

  @Test
  public void literalTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("3");
    Optional<ASTExpression> q = p.parse_StringExpression("true");
    Optional<ASTExpression> r = p.parse_StringExpression("4.5");
    Optional<ASTExpression> s = p.parse_StringExpression("\"Hallo\"");
    Optional<ASTExpression> t = p.parse_StringExpression("\'a\'");
    Optional<ASTExpression> u = p.parse_StringExpression("3.0f");
    Optional<ASTExpression> v = p.parse_StringExpression("3L");

    TypeExpression exp = new TypeExpression();
    exp.setName("int");
    assertTrue(o.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(o.get())));

    exp.setName("boolean");
    assertTrue(q.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(q.get())));

    exp.setName("double");
    assertTrue(r.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(r.get())));

    exp.setName("String");
    assertTrue(s.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(s.get())));

    exp.setName("char");
    assertTrue(t.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(t.get())));

    exp.setName("float");
    assertTrue(u.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(u.get())));

    exp.setName("long");
    assertTrue(v.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(v.get())));
  }

}
