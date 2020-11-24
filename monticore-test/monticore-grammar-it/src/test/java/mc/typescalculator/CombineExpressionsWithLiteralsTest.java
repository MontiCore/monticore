/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.LogStub;
import mc.testcd4analysis.TestCD4AnalysisMill;
import mc.testcd4analysis._symboltable.ITestCD4AnalysisGlobalScope;
import mc.typescalculator.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import mc.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import mc.typescalculator.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsSymbolTableCreatorDelegator;
import mc.typescalculator.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import mc.typescalculator.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import mc.typescalculator.combineexpressionswithliterals._symboltable.*;
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
    ITestCD4AnalysisGlobalScope globalScope = TestCD4AnalysisMill.globalScope();
    globalScope.getModelPath().addEntry(Paths.get(MODEL_PATH));
    globalScope.setFileExt("cd");

    CD2EAdapter adapter = new CD2EAdapter(globalScope);
    ICombineExpressionsWithLiteralsGlobalScope globalScope1 = CombineExpressionsWithLiteralsMill
        .globalScope();
    globalScope1.addAdaptedFieldSymbolResolver(adapter);
    globalScope1.addAdaptedOOTypeSymbolResolver(adapter);
    globalScope1.addAdaptedMethodSymbolResolver(adapter);
    globalScope1.addAdaptedFunctionSymbolResolver(adapter);
    globalScope1.addAdaptedVariableSymbolResolver(adapter);
    globalScope1.addAdaptedTypeSymbolResolver(adapter);

    Optional<OOTypeSymbol> classD = globalScope1.resolveOOType("mc.typescalculator.TestCD.D");
    assertTrue(classD.isPresent());

    Optional<OOTypeSymbol> classB = globalScope1.resolveOOType("mc.typescalculator.TestCD.B");
    assertTrue(classB.isPresent());

    OOTypeSymbol dSurrogate = new OOTypeSymbolSurrogate("D");
    dSurrogate.setEnclosingScope(classD.get().getEnclosingScope());

    OOTypeSymbol bSurrogate = new OOTypeSymbolSurrogate("B");
    bSurrogate.setEnclosingScope(classB.get().getEnclosingScope());

    FieldSymbol d = field("d", SymTypeExpressionFactory.createTypeObject(dSurrogate));
    globalScope1.add(d);
    globalScope1.add((VariableSymbol) d);

    FieldSymbol b = field("b", SymTypeExpressionFactory.createTypeObject(bSurrogate));
    globalScope1.add(b);
    globalScope1.add((VariableSymbol) b);

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator();

    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

    Optional<ASTExpression> expr = p.parse_StringExpression("d.s+=d.s");
    CombineExpressionsWithLiteralsPhasedSymbolTableCreatorDelegator del = new CombineExpressionsWithLiteralsPhasedSymbolTableCreatorDelegator(globalScope1);

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

    ASTExpression eb = exprB.get();

    Optional<SymTypeExpression> k = calc.calculateType(eb);
    assertTrue(k.isPresent());
    assertEquals("C",k.get().print());
  }
}
