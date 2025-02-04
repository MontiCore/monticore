/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionType;
import de.monticore.types.mcfunctiontypestest._parser.MCFunctionTypesTestParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SynthesizeSymTypeFromMCcFunctionTypesTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void symTypeFromAST_TestRunnable() throws IOException {
    testSynthesizePrintCompare("() -> void");
  }

  @Test
  public void symTypeFromAST_TestSimpleFunction1() throws IOException {
    testSynthesizePrintCompare("(int) -> int", "int -> int");
  }

  @Test
  public void symTypeFromAST_TestSimpleFunction2() throws IOException {
    testSynthesizePrintCompare("(long, int) -> int");
  }

  @Test
  public void symTypeFromAST_TestEllipticFunction1() throws IOException {
    testSynthesizePrintCompare("(int...) -> int");
  }

  @Test
  public void symTypeFromAST_TestEllipticFunction2() throws IOException {
    testSynthesizePrintCompare("(long, int...) -> void");
  }

  @Test
  public void symTypeFromAST_TestHigherOrderFunction1() throws IOException {
    testSynthesizePrintCompare("(int -> void) -> () -> int");
  }

  @Test
  public void symTypeFromAST_TestHigherOrderEllipticFunction() throws IOException {
    testSynthesizePrintCompare(
        "(int) -> (() -> (int, long...) -> int...) -> void",
        "int -> ((() -> (int, long...) -> int)...) -> void"
    );
  }

  protected ASTMCFunctionType parse(String mcTypeStr) throws IOException {
    MCFunctionTypesTestParser parser = new MCFunctionTypesTestParser();
    Optional<ASTMCFunctionType> typeOpt = parser.parse_StringMCFunctionType(mcTypeStr);
    Assertions.assertNotNull(typeOpt);
    Assertions.assertTrue(typeOpt.isPresent(), Log.getFindings().stream()
            .map(Finding::toString)
            .collect(Collectors.joining("\n")));
    Assertions.assertEquals(0, Log.getFindingsCount());
    return typeOpt.get();
  }

  protected SymTypeOfFunction synthesizeType(ASTMCFunctionType mcType) {
    ISynthesize synthesize = new FullSynthesizeFromMCFunctionTypes();
    TypeCalculator tc = new TypeCalculator(synthesize, null);

    CombineExpressionsWithLiteralsTraverser traverser = CombineExpressionsWithLiteralsMill.traverser();
    FlatExpressionScopeSetter scopeSetter = new FlatExpressionScopeSetter(
        CombineExpressionsWithLiteralsMill.globalScope());
    traverser.add4MCFunctionTypes(scopeSetter);
    traverser.add4MCBasicTypes(scopeSetter);
    mcType.accept(traverser);

    SymTypeExpression symType = tc.symTypeFromAST(mcType);
    Assertions.assertTrue(symType.isFunctionType());
    SymTypeOfFunction funcType = (SymTypeOfFunction) symType;
    Assertions.assertNotNull(funcType.getTypeInfo());
    Assertions.assertEquals(SymTypeOfFunction.TYPESYMBOL_NAME, funcType.getTypeInfo().getName());
    Assertions.assertNotNull(funcType.getType());
    Assertions.assertFalse(funcType.getType().isObscureType());
    Assertions.assertNotNull(funcType.getArgumentTypeList());
    for (SymTypeExpression argType : funcType.getArgumentTypeList()) {
      Assertions.assertNotNull(argType);
      Assertions.assertFalse(argType.isObscureType());
    }
    Assertions.assertTrue(mcType.getDefiningSymbol().isPresent());
    Assertions.assertEquals(SymTypeOfFunction.TYPESYMBOL_NAME, mcType.getDefiningSymbol().get().getName());

    return funcType;
  }

  protected void testSynthesizePrintCompare(String mcTypeStr) throws IOException {
    // The symTypeExpression is to be printed the same way as the MCType
    testSynthesizePrintCompare(mcTypeStr, mcTypeStr);
  }

  protected void testSynthesizePrintCompare(String mcTypeStr, String expected)
      throws IOException {
    ASTMCFunctionType mcType = parse(mcTypeStr);
    SymTypeOfFunction symType = synthesizeType(mcType);
    Assertions.assertEquals(expected, symType.printFullName());
  }

}
