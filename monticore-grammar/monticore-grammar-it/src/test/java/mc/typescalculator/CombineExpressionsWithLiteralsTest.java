/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;
import de.monticore.types2.SymTypeOfObject;
import de.se_rwth.commons.logging.LogStub;
import mc.typescalculator.combineexpressionswithliterals._ast.CombineExpressionsWithLiteralsMill;
import mc.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import mc.typescalculator.combineexpressionswithliterals._symboltable.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class CombineExpressionsWithLiteralsTest {

  private static final String MODEL_PATH = "src/test/resources";

  @Test
  public void testCD() throws IOException {
    LogStub.init();
    CD4AnalysisLanguage cd4AnalysisLanguage = new CD4AnalysisLanguage();
    ModelPath modelPath = new ModelPath(Paths.get(MODEL_PATH));
    CD4AnalysisGlobalScope globalScope =
            new CD4AnalysisGlobalScope(modelPath, cd4AnalysisLanguage);


    CD2EAdapter adapter = new CD2EAdapter(globalScope);
    CombineExpressionsWithLiteralsLanguage language = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsLanguageBuilder().build();
    CombineExpressionsWithLiteralsGlobalScope globalScope1 = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsGlobalScopeBuilder().setLanguage(language).setModelPath(new ModelPath()).build();
    globalScope1.addAdaptedFieldSymbolResolvingDelegate(adapter);
    globalScope1.addAdaptedTypeSymbolResolvingDelegate(adapter);
    globalScope1.addAdaptedMethodSymbolResolvingDelegate(adapter);

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(globalScope1);

    Optional<TypeSymbol> classB = globalScope1.resolveType("mc.typescalculator.TestCD.B");
    assertTrue(classB.isPresent());

    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("double");
    Optional<ASTExpression> expr = p.parse_StringExpression("mc.typescalculator.TestCD.D.s+=mc.typescalculator.TestCD.D.s");
    CombineExpressionsWithLiteralsSymbolTableCreatorDelegator del = new CombineExpressionsWithLiteralsSymbolTableCreatorDelegator(globalScope1);

    CombineExpressionsWithLiteralsArtifactScope art = del.createFromAST(expr.get());
    art.setImports(Lists.newArrayList(new ImportStatement("mc.typescalculator.TestCD.D", true)));

    assertTrue(expr.isPresent());
//    Optional<SymTypeExpression> j = calc.calculateType(expr.get());
    // TODO: j isnull
    //assertTrue(exp.deepEquals(j));




    CombineExpressionsWithLiteralsTypesCalculator calc2 = new CombineExpressionsWithLiteralsTypesCalculator(art);
    Optional<ASTExpression> expr2 = p.parse_StringExpression("s+=s");
    assertTrue(expr2.isPresent());
//    Optional<SymTypeExpression> j2 = calc2.calculateType(expr2.get());
    //TODO RE resolve local variable via import
    //assertTrue(exp.deepEquals(j2));


    SymTypeExpression exp2 = new SymTypeOfObject();
    Optional<ASTExpression> exprC = p.parse_StringExpression("mc.typescalculator.TestCD.D.f = mc.typescalculator.TestCD.C.f");
    exp2.setName("String");
    assertTrue(exprC.isPresent());
//    j = calc.calculateType(exprC.get());
    //TODO String vs mc.typescalculator.TestCD.String
    //assertTrue(exp2.deepEquals(j));

    Optional<ASTExpression> exprD = p.parse_StringExpression("(mc.typescalculator.TestCD.B.a)++");
    exp.setName("double");
    assertTrue(exprD.isPresent());
    //TODO
    //assertTrue(exp.deepEquals(calc.calculateType(exprD.get())));

    Optional<ASTExpression> exprB = p.parse_StringExpression("mc.typescalculator.TestCD.B.x = mc.typescalculator.TestCD.B.z");

    exp2.setName("C");

    assertTrue(exprB.isPresent());

    ASTExpression b = exprB.get();

//    Optional<SymTypeExpression> k = calc.calculateType(b);
    //TODO RE
    //assertTrue(exp2.deepEquals(k));
  }
}
