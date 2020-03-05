/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.io.paths.ModelPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import de.se_rwth.commons.logging.LogStub;
import mc.testcd4analysis._symboltable.TestCD4AnalysisLanguage;
import mc.testcd4analysis._symboltable.TestCD4AnalysisGlobalScope;
import mc.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import mc.typescalculator.combineexpressionswithliterals._symboltable.*;
import mc.typescalculator.combineexpressionswithliterals.prettyprint.CombineExpressionsWithLiteralsPrettyPrinter;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.types.check.DefsTypeBasic.field;
import static de.monticore.types.check.SymTypeConstant.unbox;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CombineExpressionsWithLiteralsTest {

  private static final String MODEL_PATH = "src/test/resources";

  @Test
  public void testCD() throws IOException {
    LogStub.init();
    TestCD4AnalysisLanguage cd4AnalysisLanguage = new TestCD4AnalysisLanguage();
    ModelPath modelPath = new ModelPath(Paths.get(MODEL_PATH));
    TestCD4AnalysisGlobalScope globalScope =
            new TestCD4AnalysisGlobalScope(modelPath, cd4AnalysisLanguage);


    CD2EAdapter adapter = new CD2EAdapter(globalScope);
    CombineExpressionsWithLiteralsLanguage language = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsLanguageBuilder().build();
    CombineExpressionsWithLiteralsGlobalScope globalScope1 = CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsGlobalScopeBuilder()
        .setCombineExpressionsWithLiteralsLanguage(language).setModelPath(new ModelPath()).build();
    globalScope1.addAdaptedFieldSymbolResolvingDelegate(adapter);
    globalScope1.addAdaptedTypeSymbolResolvingDelegate(adapter);
    globalScope1.addAdaptedMethodSymbolResolvingDelegate(adapter);

    Optional<TypeSymbol> classD = globalScope1.resolveType("mc.typescalculator.TestCD.D");
    assertTrue(classD.isPresent());

    Optional<TypeSymbol> classB = globalScope1.resolveType("mc.typescalculator.TestCD.B");
    assertTrue(classB.isPresent());

    globalScope1.add(field("d", SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("D",classD.get().getEnclosingScope()))));
    globalScope1.add(field("b",SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("B",classB.get().getEnclosingScope()))));

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(globalScope1);
    calc.setPrettyPrinter(new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));

    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

    Optional<ASTExpression> expr = p.parse_StringExpression("d.s+=d.s");
    CombineExpressionsWithLiteralsSymbolTableCreatorDelegator del = new CombineExpressionsWithLiteralsSymbolTableCreatorDelegator(globalScope1);

    assertTrue(expr.isPresent());
    CombineExpressionsWithLiteralsArtifactScope art = del.createFromAST(expr.get());
    art.setImportList(Lists.newArrayList(new ImportStatement("mc.typescalculator.TestCD.D", true)));
    Optional<SymTypeExpression> j = calc.calculateType(expr.get());
    assertTrue(j.isPresent());
    assertEquals("int", unbox(j.get().print()));




    CombineExpressionsWithLiteralsTypesCalculator calc2 = new CombineExpressionsWithLiteralsTypesCalculator(art);
    calc2.setPrettyPrinter(new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));
    Optional<ASTExpression> expr2 = p.parse_StringExpression("s+=s");
    assertTrue(expr2.isPresent());
    Optional<SymTypeExpression> j2 = calc2.calculateType(expr2.get());
    assertTrue(j2.isPresent());
    assertEquals("int",j2.get().print());


    Optional<ASTExpression> exprC = p.parse_StringExpression("d.f = mc.typescalculator.TestCD.C.f");
    assertTrue(exprC.isPresent());
    j = calc.calculateType(exprC.get());
    assertTrue(j.isPresent());
    assertEquals("G",j.get().print());

    Optional<ASTExpression> exprD = p.parse_StringExpression("(b.a)++");
    assertTrue(exprD.isPresent());
    Optional<SymTypeExpression> j3 = calc.calculateType(exprD.get());
    assertTrue(j3.isPresent());
    assertEquals("double",j3.get().print());

    Optional<ASTExpression> exprB = p.parse_StringExpression("b.x = mc.typescalculator.TestCD.B.z");

    assertTrue(exprB.isPresent());

    ASTExpression b = exprB.get();

    Optional<SymTypeExpression> k = calc.calculateType(b);
    assertTrue(k.isPresent());
    assertEquals("C",k.get().print());
  }
}
