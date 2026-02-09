/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.MIValue;
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
import org.junit.jupiter.api.BeforeEach;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Abstract class for interpreter tests.
 * Supplies utils for comparing MIValues and loading functions/variables.
 * Implementations need to initialize the parserSupplier, resetMill and
 * initMill-attributes and implement the setupSymbolTableCompleter method.
 */
public abstract class AbstractInterpreterTest {

  protected static final double delta = 0.00001;
  
  protected Type4Ast type4Ast;
  
  protected ITraverser typeMapTraverser;
  
  @Deprecated
  protected ITraverser scopeGenitor;
  
  protected ITraverser symbolTableCompleter;
  
  protected MCConcreteParser parser;
  protected IModelInterpreter interpreter;

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


  /**
   * Loads variable by full qualified name.
   * @param name full qualified name
   * @return stored MIValue or ErrorMIValue if not declared/initialized
   */
  public MIValue loadVariable(String name) {
    VariableSymbol symbol = BasicSymbolsMill.globalScope()
            .resolveVariable(name).get();
    return interpreter.loadVariable(symbol);
  }

  /**
   * Loads function by full qualified name.
   * @param name full qualified name
   * @return stored FunctionMIValue or ErrorMIValue if not declared
   */
  public MIValue loadFunction(String name) {
    FunctionSymbol symbol = BasicSymbolsMill.globalScope()
            .resolveFunction(name).get();
    return interpreter.loadFunction(symbol);
  }

  /**
   * Compares two MIValues based on type and value.
   * @param expected
   * @param actual
   */
  public void assertValueEquals(MIValue expected, MIValue actual) {
    // if you join the ifs with && you can't tell missing comparison implementations for a new value
    // from value mismatches apart
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
        assertValueEquals(expected.asReturnValue(), actual.asReturnValue());
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
