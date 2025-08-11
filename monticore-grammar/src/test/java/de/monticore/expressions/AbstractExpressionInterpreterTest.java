package de.monticore.expressions;

import de.monticore.AbstractInterpreterTest;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsInterpreter;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.lambdaexpressions._symboltable.LambdaExpressionsSTCompleteTypes2;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.ocl.oclexpressions.symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.ocl.setexpressions.symboltable.SetExpressionsSymbolTableCompleter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.types3wrapper.TypeCheck3AsIDerive;
import de.monticore.types.check.types3wrapper.TypeCheck3AsISynthesize;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.interpreter.MIValueFactory.createValue;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.variable;
import static org.junit.jupiter.api.Assertions.*;

public class AbstractExpressionInterpreterTest extends AbstractInterpreterTest {

  @Override
  @BeforeEach
  public void init() {
    parserSupplier = CombineExpressionsWithLiteralsMill::parser;
    resetMill = CombineExpressionsWithLiteralsMill::reset;
    initMill = CombineExpressionsWithLiteralsMill::init;
    
    super.init();
    
    interpreter = new CombineExpressionsWithLiteralsInterpreter();
    
    try {
      initBool();
      initChar();
      initByte();
      initShort();
      initInt();
      initLong();
      initFloat();
      initDouble();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
  
  protected void initBool() throws IOException {
    VariableSymbol varSymbol = variable("b", SymTypeExpressionFactory.createPrimitive("boolean"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, Optional.of(createValue(true)));
  }
  
  protected void initChar() throws IOException {
    VariableSymbol varSymbol = variable("c", SymTypeExpressionFactory.createPrimitive("char"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, Optional.of(createValue('a')));
  }
  
  protected void initByte() throws IOException {
    VariableSymbol varSymbol = variable("by", SymTypeExpressionFactory.createPrimitive("byte"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, Optional.of(createValue((byte)3)));
  }
  
  protected void initShort() throws IOException {
    VariableSymbol varSymbol = variable("s", SymTypeExpressionFactory.createPrimitive("short"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, Optional.of(createValue((short)256)));
  }
  
  protected void initInt() throws IOException {
    VariableSymbol varSymbol = variable("i", SymTypeExpressionFactory.createPrimitive("int"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, Optional.of(createValue(1)));
  }
  
  protected void initLong() throws IOException {
    VariableSymbol varSymbol = variable("l", SymTypeExpressionFactory.createPrimitive("long"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, Optional.of(createValue(5L)));
  }
  
  protected void initFloat() throws IOException {
    VariableSymbol varSymbol = variable("f", SymTypeExpressionFactory.createPrimitive("float"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, Optional.of(createValue(1.5f)));
  }
  
  protected void initDouble() throws IOException {
    VariableSymbol varSymbol = variable("d", SymTypeExpressionFactory.createPrimitive("double"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, Optional.of(createValue(3.14)));
  }
  
  protected void testValidExpression(String expr, MIValue expected) {
    Log.clearFindings();
    MIValue interpretationResult = null;
    try {
      interpretationResult = parseExpressionAndInterpret(expr);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    assertNotNull(interpretationResult);
    if (!Log.getFindings().isEmpty()) {
      Log.printFindings();
      fail();
    }
    assertValue(expected, interpretationResult);
    assertTrue(Log.getFindings().isEmpty());
  }
  
  protected void testInvalidExpression(String expr) {
    Log.clearFindings();
    MIValue interpretationResult;
    
    try {
      interpretationResult = parseExpressionAndInterpret(expr);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    assertNotNull(interpretationResult);

    if (Log.getFindings().isEmpty() && !interpretationResult.isError()) {
      fail("Expected an error but interpretation succeeded with result of " + interpretationResult.printType()
              + " (" + interpretationResult.printValue() + ").");
    }

    assertFalse(Log.getFindings().isEmpty());
    assertTrue(interpretationResult.isError());
  }
  
  protected MIValue parseExpressionAndInterpret(String expr) throws IOException {
    final ASTExpression ast = parseExpr(expr);
    generateScopes(ast);
    SymTypeExpression type = TypeCheck3.typeOf(ast);
    if (type.isObscureType()) {
      String errorMsg = "Invalid Model: " + expr;
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    return ast.evaluate(interpreter);
  }
  
  protected ASTExpression parseExpr(String exprStr) throws IOException {
    Optional<ASTExpression> astExpression = parseStringExpr(exprStr);
    Assertions.assertTrue(astExpression.isPresent(), getAllFindingsAsString());
    return astExpression.get();
  }
  
  // Parse a String expression of the according language
  protected Optional<ASTExpression> parseStringExpr(String exprStr)
      throws IOException {
    return ((CombineExpressionsWithLiteralsParser)parser).parse_StringExpression(exprStr);
  }
  
  protected void generateScopes(ASTExpression expr) {
    // create a root
    ASTFoo rootNode = CombineExpressionsWithLiteralsMill.fooBuilder()
        .setExpression(expr)
        .build();
    ICombineExpressionsWithLiteralsArtifactScope rootScope =
        CombineExpressionsWithLiteralsMill.scopesGenitorDelegator()
            .createFromAST(rootNode);
    
    rootScope.setName("fooRoot");
    // complete the symbol table
    expr.accept(getSymbolTableCompleter());
  }
  
  protected void setupSymbolTableCompleter(
      ITraverser typeMapTraverser, Type4Ast type4Ast) {
    CombineExpressionsWithLiteralsTraverser combinedScopesCompleter =
        CombineExpressionsWithLiteralsMill.traverser();
    IDerive deriver = new TypeCheck3AsIDerive();
    ISynthesize synthesizer = new TypeCheck3AsISynthesize();
    combinedScopesCompleter.add4LambdaExpressions(
        new LambdaExpressionsSTCompleteTypes2(
            typeMapTraverser,
            getType4Ast()
        )
    );
    OCLExpressionsSymbolTableCompleter oclExprCompleter =
        new OCLExpressionsSymbolTableCompleter();
    oclExprCompleter.setDeriver(deriver);
    oclExprCompleter.setSynthesizer(synthesizer);
    combinedScopesCompleter.add4OCLExpressions(oclExprCompleter);
    combinedScopesCompleter.setOCLExpressionsHandler(oclExprCompleter);
    
    SetExpressionsSymbolTableCompleter setExprCompleter =
        new SetExpressionsSymbolTableCompleter();
    setExprCompleter.setDeriver(deriver);
    setExprCompleter.setSynthesizer(synthesizer);
    combinedScopesCompleter.add4SetExpressions(setExprCompleter);
    combinedScopesCompleter.setSetExpressionsHandler(setExprCompleter);
    
    symbolTableCompleter = combinedScopesCompleter;
    scopeGenitor = combinedScopesCompleter;
  }

}
