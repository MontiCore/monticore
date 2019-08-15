package mc.typescalculator;

import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ETypeSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.typescalculator.TypeExpression;
import de.se_rwth.commons.logging.LogStub;
import mc.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import mc.typescalculator.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsGlobalScope;
import mc.typescalculator.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsLanguage;
import mc.typescalculator.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsSymTabMill;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class CombineExpressionsWithLiteralsTest {

  private static final String MODEL_PATH = "src/test/resources";

  @Test
  public void testCD() throws IOException {
    LogStub.init();
    CD4AnalysisLanguage cd4AnalysisLanguage = new CD4AnalysisLanguage();
    ModelPath modelPath = new ModelPath(Paths.get(MODEL_PATH));
    CD4AnalysisGlobalScope globalScope = new CD4AnalysisGlobalScope(modelPath, cd4AnalysisLanguage);


    CD2EAdapter adapter = new CD2EAdapter(globalScope);
    CombineExpressionsWithLiteralsLanguage language = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsLanguageBuilder().build();
    CombineExpressionsWithLiteralsGlobalScope globalScope1 = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsGlobalScopeBuilder().setLanguage(language).setModelPath(new ModelPath()).build();
    globalScope1.addAdaptedEVariableSymbolResolvingDelegate(adapter);
    globalScope1.addAdaptedETypeSymbolResolvingDelegate(adapter);
    globalScope1.addAdaptedEMethodSymbolResolvingDelegate(adapter);


    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(globalScope1);

    Optional<ETypeSymbol> classB = globalScope1.resolveEType("mc.typescalculator.TestCD.B");
    assertTrue(classB.isPresent());

    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

    TypeExpression exp = new TypeExpression();
    exp.setName("double");
    Optional<ASTExpression> expr = p.parse_StringExpression("mc.typescalculator.TestCD.B.a+=mc.typescalculator.TestCD.D.s");
    assertTrue(expr.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(expr.get())));

    Optional<ASTExpression> exprC = p.parse_StringExpression("mc.typescalculator.TestCD.D.f = mc.typescalculator.TestCD.C.f");
    exp.setName("String");
    assertTrue(exprC.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(exprC.get())));

    Optional<ASTExpression> exprD = p.parse_StringExpression("(mc.typescalculator.TestCD.B.a)++");
    exp.setName("double");
    assertTrue(exprD.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(exprD.get())));

    Optional<ASTExpression> exprB = p.parse_StringExpression("mc.typescalculator.TestCD.B.x = mc.typescalculator.TestCD.B.z");
    exp.setName("mc.typescalculator.TestCD.C");
    assertTrue(exprB.isPresent());
    assertTrue(exp.deepEquals(calc.calculateType(exprB.get())));

  }
}
