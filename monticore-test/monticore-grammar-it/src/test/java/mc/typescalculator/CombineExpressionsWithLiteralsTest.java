/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcd4analysis.TestCD4AnalysisMill;
import mc.testcd4analysis._symboltable.ITestCD4AnalysisGlobalScope;
import mc.typescalculator.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import mc.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import mc.typescalculator.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsScopesGenitorDelegator;
import mc.typescalculator.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import mc.typescalculator.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.types.check.SymTypePrimitive.unbox;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CombineExpressionsWithLiteralsTest {

  private static final String SYMBOL_PATH = "src/test/resources";
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setup() {
    TestCD4AnalysisMill.reset();
    CombineExpressionsWithLiteralsMill.reset();

    TestCD4AnalysisMill.init();
    CombineExpressionsWithLiteralsMill.init();

    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void testCD() throws IOException {
    ITestCD4AnalysisGlobalScope globalScope = TestCD4AnalysisMill.globalScope();
    globalScope.getSymbolPath().addEntry(Paths.get(SYMBOL_PATH));

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
    Assertions.assertTrue(classD.isPresent());

    Optional<OOTypeSymbol> classB = globalScope1.resolveOOType("mc.typescalculator.TestCD.B");
    Assertions.assertTrue(classB.isPresent());

    OOTypeSymbol dSurrogate = new OOTypeSymbolSurrogate("D");
    dSurrogate.setEnclosingScope(classD.get().getEnclosingScope());

    OOTypeSymbol bSurrogate = new OOTypeSymbolSurrogate("B");
    bSurrogate.setEnclosingScope(classB.get().getEnclosingScope());

    FieldSymbol d = OOSymbolsMill.fieldSymbolBuilder().
            setName("d").setType(SymTypeExpressionFactory.
            createTypeObject(dSurrogate)).build();

    globalScope1.add(d);
    globalScope1.add((VariableSymbol) d);

    FieldSymbol b = OOSymbolsMill.fieldSymbolBuilder().
            setName("b").setType(SymTypeExpressionFactory.
            createTypeObject(bSurrogate)).build();
    globalScope1.add(b);
    globalScope1.add((VariableSymbol) b);

    FullDeriveFromCombineExpressionsWithLiterals calc = new FullDeriveFromCombineExpressionsWithLiterals();

    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

    Optional<ASTExpression> expr = p.parse_StringExpression("d.s+=d.s");
    CombineExpressionsWithLiteralsScopesGenitorDelegator del = new CombineExpressionsWithLiteralsScopesGenitorDelegator();

    Assertions.assertTrue(expr.isPresent());
    ICombineExpressionsWithLiteralsArtifactScope art = del.createFromAST(expr.get());
    art.setName("");
    art.setImportsList(Lists.newArrayList(new ImportStatement("mc.typescalculator.TestCD.D", true)));
    TypeCheckResult j = calc.deriveType(expr.get());
    Assertions.assertTrue(j.isPresentResult());
    Assertions.assertEquals("int", unbox(j.getResult().print()));

    Optional<ASTExpression> exprC = p.parse_StringExpression("d.f = mc.typescalculator.TestCD.C.f");
    Assertions.assertTrue(exprC.isPresent());
    ICombineExpressionsWithLiteralsArtifactScope artifactScope = del.createFromAST(exprC.get());
    artifactScope.setName("");
    j = calc.deriveType(exprC.get());
    Assertions.assertTrue(j.isPresentResult());
    Assertions.assertEquals("G", j.getResult().print());

    Optional<ASTExpression> exprD = p.parse_StringExpression("(b.a)++");
    Assertions.assertTrue(exprD.isPresent());
    artifactScope = del.createFromAST(exprD.get());
    artifactScope.setName("");
    TypeCheckResult j3 = calc.deriveType(exprD.get());
    Assertions.assertTrue(j3.isPresentResult());
    Assertions.assertEquals("double", j3.getResult().print());

    Optional<ASTExpression> exprB = p.parse_StringExpression("b.x = mc.typescalculator.TestCD.B.z");
    Assertions.assertTrue(exprB.isPresent());
    artifactScope = del.createFromAST(exprB.get());
    artifactScope.setName("");
    ASTExpression eb = exprB.get();

    TypeCheckResult k = calc.deriveType(eb);
    Assertions.assertTrue(k.isPresentResult());
    Assertions.assertEquals("C", k.getResult().print());

    Optional<ASTExpression> complicated = p.parse_StringExpression("b.z.f.toString()");
    Assertions.assertTrue(complicated.isPresent());
    artifactScope = del.createFromAST(complicated.get());
    artifactScope.setName("");
    TypeCheckResult sym = calc.deriveType(complicated.get());
    Assertions.assertTrue(sym.isPresentResult());
    Assertions.assertEquals("String", sym.getResult().print());
  }
}
