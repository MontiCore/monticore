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
    MapBasedTypeCheck3 tc3 = CombineExpressionsWithLiteralsTypeTraverserFactory
            .initTypeCheck3();
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
    VariableSymbol symbol = BasicSymbolsMill.globalScope()
            .resolveVariable(name).get();
    return interpreter.loadVariable(symbol);
  }
  
  public MIValue loadFunction(String name) {
    FunctionSymbol symbol = BasicSymbolsMill.globalScope()
            .resolveFunction(name).get();
    return interpreter.loadFunction(symbol);
  }
  
  public void assertValue(MIValue expected, MIValue actual) {
    if (expected.isVoid()) {
      if (actual.isVoid()) return;
    } else if (expected.isError()) {
      if (actual.isError() && expected.asError().equals(actual.asError())) return;
    } else if (expected.isBreak()) {
      if (actual.isBreak()) return;
    } else if (expected.isContinue()) {
      if (actual.isContinue()) return;
    } else if (expected.isReturn()) {
      if (actual.isReturn()) {
        assertValue(expected.asReturnValue(), actual.asReturnValue());
        return;
      }
    } else if (expected.isBoolean()) {
      if (actual.isBoolean() && expected.asBoolean() == actual.asBoolean()) return;
    } else if (expected.isByte()) {
      if (actual.isByte() && expected.asByte() == actual.asByte()) return;
    } else if (expected.isShort()) {
      if (actual.isShort() && expected.asShort() == actual.asShort()) return;
    } else if (expected.isChar()) {
      if (actual.isChar() && expected.asChar() == actual.asChar()) return;
    } else if (expected.isInt()) {
      if (actual.isInt() && expected.asInt() == actual.asInt()) return;
    } else if (expected.isLong()) {
      if (actual.isLong() && expected.asLong() == actual.asLong()) return;
    } else if (expected.isFloat()) {
      if (actual.isFloat() && expected.asFloat()  + delta > actual.asFloat()
              && expected.asFloat() - delta < actual.asFloat()) return;
    } else if (expected.isDouble()) {
      if (actual.isDouble() && expected.asDouble() + delta > actual.asDouble()
              && expected.asDouble() - delta < actual.asDouble()) return;
    } else if (expected.isFunction()) {
      if (actual.isFunction() && expected.asFunction().equals(actual.asFunction())) return;
    } else if (expected.isObject()) {
      if (actual.isObject() && expected.asObject().equals(actual.asObject())) return;
    } else {
      Log.error("Trying to compare unsupported MIValue type '"
              + expected.printType() + "'.");
      fail();
    }

    fail("Expected " + expected.printType() + " (" + expected.printValue()
            + ") but got " + actual.printType() + " (" + actual.printValue()
            + ").");
  }

}
