/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.util.*;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractInterpreterTest {

  protected static final double delta = 0.00001;
  
  protected Type4Ast type4Ast;
  
  protected ITraverser typeMapTraverser;
  
  @Deprecated
  protected ITraverser scopeGenitor;
  
  protected ITraverser symbolTableCompleter;
  
  protected MCConcreteParser parser;
  protected ModelInterpreter interpreter;

  protected Supplier<MCConcreteParser> parserSupplier;
  protected Runnable resetMill;
  protected Runnable initMill;
  
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.clearFindings();
    Log.enableFailQuick(false);
    
    resetMill.run();
    initMill.run();
    BasicSymbolsMill.initializePrimitives();
    SymTypeRelations.init();
    WithinScopeBasicSymbolsResolver.init();
    WithinTypeBasicSymbolsResolver.init();
    TypeVisitorOperatorCalculator.init();
    DefsTypesForTests.setup();
    parser = parserSupplier.get();
    MapBasedTypeCheck3 tc3 = CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    type4Ast = tc3.getType4Ast();
    typeMapTraverser = tc3.getTypeTraverser();
    setupSymbolTableCompleter(typeMapTraverser, type4Ast);
  }
  
  protected abstract void setupSymbolTableCompleter(ITraverser typeMapTraverser, Type4Ast type4Ast);
  
  protected Type4Ast getType4Ast() {
    return type4Ast;
  }
  
  protected ITraverser getSymbolTableCompleter() {
    return symbolTableCompleter;
  }
  
  /**
   * @return all findings as one String
   */
  protected static String getAllFindingsAsString() {
    return Log.getFindings().stream()
        .map(Finding::buildMsg)
        .collect(Collectors.joining(System.lineSeparator()))
        ;
  }
  
  public MIValue loadVariable(String name) {
    VariableSymbol symbol = BasicSymbolsMill.globalScope().resolveVariable(name).get();
    return interpreter.loadVariable(symbol);
  }
  
  public MIValue loadFunction(String name) {
    FunctionSymbol symbol = BasicSymbolsMill.globalScope().resolveFunction(name).get();
    return interpreter.loadFunction(symbol);
  }
  
  public void assertValue(MIValue expected, MIValue actual) {
    if (expected.isVoid()) {
      assertTrue(actual.isVoid());
    } else if (expected.isError()) {
      assertTrue(actual.isError());
      assertEquals(expected.asError(), actual.asError());
    } else if (expected.isBreak()) {
      assertTrue(actual.isBreak());
    } else if (expected.isContinue()) {
      assertTrue(actual.isContinue());
    } else if (expected.isReturn()) {
      assertTrue(actual.isReturn());
      assertValue(expected.asReturnValue(), actual.asReturnValue());
    } else if (expected.isBoolean()) {
      assertTrue(actual.isBoolean());
      assertEquals(expected.asBoolean(), actual.asBoolean());
    } else if (expected.isByte()) {
      assertTrue(actual.isByte());
      Assertions.assertEquals(expected.asByte(), actual.asByte());
    } else if (expected.isShort()) {
      assertTrue(actual.isShort());
      assertEquals(expected.asShort(), actual.asShort());
    } else if (expected.isChar()) {
      assertTrue(actual.isChar());
      assertEquals(expected.asChar(), actual.asChar());
    } else if (expected.isInt()) {
      assertTrue(actual.isInt());
      assertEquals(expected.asInt(), actual.asInt());
    } else if (expected.isLong()) {
      assertTrue(actual.isLong());
      assertEquals(expected.asLong(), actual.asLong());
    } else if (expected.isFloat()) {
      assertTrue(actual.isFloat());
      assertEquals(expected.asFloat(), actual.asFloat());
    } else if (expected.isDouble()) {
      assertTrue(actual.isDouble());
      assertEquals(expected.asDouble(), actual.asDouble());
    } else if (expected.isFunction()) {
      assertTrue(actual.isFunction());
    } else if (expected.isObject()) {
      assertTrue(actual.isObject());
      assertEquals(expected.asObject(), actual.asObject());
    } else {
      Log.error("Trying to compare unsupported MIValue type '" + expected.printType() + "'.");
      fail();
    }
  }

}
