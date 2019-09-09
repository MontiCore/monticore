/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.io.paths.ModelPath;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;
import de.monticore.typescalculator.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.typescalculator.combineexpressionswithliterals._ast.CombineExpressionsWithLiteralsMill;
import de.monticore.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.typescalculator.combineexpressionswithliterals._symboltable.*;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;



public class CombineExpressionsWithLiteralsTest {

  private CombineExpressionsWithLiteralsArtifactScope artifactScope;

  private CombineExpressionsWithLiteralsGlobalScope globalScope;


  @Before
  public void setup() throws IOException{
    Log.enableFailQuick(false);

    ASTExpression expression = new CombineExpressionsWithLiteralsParser().parse_StringExpression("A").get();
    ASTFoo ast = CombineExpressionsWithLiteralsMill.fooBuilder().setExpression(expression).build();
    CombineExpressionsWithLiteralsLanguage language = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsLanguageBuilder().build();
    globalScope = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsGlobalScopeBuilder().setLanguage(language).setModelPath(new ModelPath()).build();
    CombineExpressionsWithLiteralsSymbolTableCreatorDelegator stc = language.getSymbolTableCreator(globalScope);
    artifactScope = stc.createFromAST(ast);
    globalScope.addAdaptedMethodSymbolResolvingDelegate(new DummyAdapter(artifactScope));
    globalScope.addAdaptedTypeSymbolResolvingDelegate(new DummyAdapter(artifactScope));
    globalScope.addAdaptedFieldSymbolResolvingDelegate(new DummyAdapter(artifactScope));
  }

  @Test
  public void testCommonExpressions() throws IOException {
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> p = parser.parse_StringExpression("3+4");

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    assertTrue(p.isPresent());

    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("int");
    assertTrue(exp.deepEquals(calc.calculateType(p.get())));

  }

  @Test
  public void testAssignmentExpressions() throws IOException{
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> p = parser.parse_StringExpression("varint-=4");

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    assertTrue(p.isPresent());

    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("int");
    assertTrue(exp.deepEquals(calc.calculateType(p.get())));
  }

  @Test
  public void testCombination() throws IOException{
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> p = parser.parse_StringExpression("vardouble+=3+4");

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(artifactScope);
    assertTrue(p.isPresent());

    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("double");
    assertTrue(exp.deepEquals(calc.calculateType(p.get())));
  }
}
