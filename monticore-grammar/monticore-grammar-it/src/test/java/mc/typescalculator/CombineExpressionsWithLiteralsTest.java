/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.LogStub;
import mc.testcd4analysis._symboltable.TestCD4AnalysisGlobalScope;
import mc.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import mc.typescalculator.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsArtifactScope;
import mc.typescalculator.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsGlobalScope;
import mc.typescalculator.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsSymbolTableCreatorDelegator;
import mc.typescalculator.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
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
    TestCD4AnalysisGlobalScope globalScope =
            new TestCD4AnalysisGlobalScope(new ModelPath(Paths.get(MODEL_PATH)));


    CD2EAdapter adapter = new CD2EAdapter(globalScope);
    CombineExpressionsWithLiteralsGlobalScope globalScope1 =
        new CombineExpressionsWithLiteralsGlobalScope(new ModelPath());
    globalScope1.addAdaptedFieldSymbolResolvingDelegate(adapter);
    globalScope1.addAdaptedOOTypeSymbolResolvingDelegate(adapter);
    globalScope1.addAdaptedMethodSymbolResolvingDelegate(adapter);

    Optional<OOTypeSymbol> classD = globalScope1.resolveOOType("mc.typescalculator.TestCD.D");
    assertTrue(classD.isPresent());

    Optional<OOTypeSymbol> classB = globalScope1.resolveOOType("mc.typescalculator.TestCD.B");
    assertTrue(classB.isPresent());

    OOTypeSymbolSurrogate dSurrogate = new OOTypeSymbolSurrogate("D");
    dSurrogate.setEnclosingScope(classD.get().getEnclosingScope());

    OOTypeSymbolSurrogate bSurrogate = new OOTypeSymbolSurrogate("B");
    bSurrogate.setEnclosingScope(classB.get().getEnclosingScope());

    globalScope1.add(field("d", SymTypeExpressionFactory.createTypeObject(dSurrogate)));
    globalScope1.add(field("b",SymTypeExpressionFactory.createTypeObject(bSurrogate)));

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator();

    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

    Optional<ASTExpression> expr = p.parse_StringExpression("d.s+=d.s");
    CombineExpressionsWithLiteralsSymbolTableCreatorDelegator del = new CombineExpressionsWithLiteralsSymbolTableCreatorDelegator(globalScope1);

    assertTrue(expr.isPresent());
    ICombineExpressionsWithLiteralsArtifactScope art = del.createFromAST(expr.get());
    art.setImportsList(Lists.newArrayList(new ImportStatement("mc.typescalculator.TestCD.D", true)));
    Optional<SymTypeExpression> j = calc.calculateType(expr.get());
    assertTrue(j.isPresent());
    assertEquals("int", unbox(j.get().print()));

    Optional<ASTExpression> expr2 = p.parse_StringExpression("s+=s");
    assertTrue(expr2.isPresent());
    del.createFromAST(expr2.get());

    Optional<SymTypeExpression> j2 = calc.calculateType(expr2.get());
    assertTrue(j2.isPresent());
    assertEquals("int",j2.get().print());

    Optional<ASTExpression> exprC = p.parse_StringExpression("d.f = mc.typescalculator.TestCD.C.f");
    assertTrue(exprC.isPresent());
    del.createFromAST(exprC.get());
    j = calc.calculateType(exprC.get());
    assertTrue(j.isPresent());
    assertEquals("G",j.get().print());

    Optional<ASTExpression> exprD = p.parse_StringExpression("(b.a)++");
    assertTrue(exprD.isPresent());
    del.createFromAST(exprD.get());
    Optional<SymTypeExpression> j3 = calc.calculateType(exprD.get());
    assertTrue(j3.isPresent());
    assertEquals("double",j3.get().print());

    Optional<ASTExpression> exprB = p.parse_StringExpression("b.x = mc.typescalculator.TestCD.B.z");
    assertTrue(exprB.isPresent());
    del.createFromAST(exprB.get());

    ASTExpression b = exprB.get();

    Optional<SymTypeExpression> k = calc.calculateType(b);
    assertTrue(k.isPresent());
    assertEquals("C",k.get().print());
  }
}
