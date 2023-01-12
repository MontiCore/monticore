// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.library;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StreamTypeTest {

  @Before
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);

    BasicSymbolsMill.reset();
    BasicSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
    new StreamType().addStreamType();
  }

  @Test
  public void resolveStreamType() {
    Optional<TypeSymbol> streamOpt = BasicSymbolsMill.globalScope()
        .resolveType(StreamType.STREAM_TYPE_NAME);
    assertTrue(streamOpt.isPresent());
    TypeSymbol stream = streamOpt.get();
    assertNotNull(stream.getSpannedScope());
    assertEquals(1, stream.getSpannedScope().getTypeVarSymbols().size());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void resolveStreamFunctionEmpty() {
    FunctionSymbol function = resolveFunction("emptyStream");
    assertTrue(function.getParameterList().isEmpty());
    assertIsStreamWithTypeVar(function.getType());
  }

  @Test
  public void resolveStreamFunctionAppendFirst() {
    FunctionSymbol function = resolveFunction("appendFirst");
    assertEquals(2, function.getParameterList().size());
    assertTrue(function.getParameterList().get(0).getType().isTypeVariable());
    assertIsStreamWithTypeVar(function.getParameterList().get(1).getType());
    assertIsStreamWithTypeVar(function.getType());
  }

  @Test
  public void resolveStreamFunctionLen() {
    FunctionSymbol function = resolveFunction("len");
    assertEquals(1, function.getParameterList().size());
    assertTrue(function.getParameterList().get(0).getType().isGenericType());
    assertEquals(StreamType.STREAM_TYPE_NAME,
        function.getParameterList().get(0).getType().getTypeInfo().getName());
    assertEquals(0,
        ((SymTypeOfGenerics) function.getParameterList().get(0).getType())
            .getArgumentList().size());
    assertEquals(BasicSymbolsMill.LONG, function.getType().getTypeInfo().getName());
  }

  @Test
  public void resolveStreamFunctions() {
    resolveFunction("emptyStream");
    resolveFunction("appendFirst");
    resolveFunction("conc");
    resolveFunction("len");
    resolveFunction("first");
    resolveFunction("dropFirst");
    resolveFunction("nth");
    resolveFunction("take");
    resolveFunction("drop");
    assertEquals(2, BasicSymbolsMill.globalScope().resolveFunctionMany("times").size());
    resolveFunction("map");
    resolveFunction("iterate");
    resolveFunction("filter");
    resolveFunction("takeWhile");
    resolveFunction("dropWhile");
    resolveFunction("rcDups");
  }

  protected void assertIsStreamWithTypeVar(SymTypeExpression type) {
    assertNotNull(type);
    assertTrue(type.isGenericType());
    assertEquals(StreamType.STREAM_TYPE_NAME, type.getTypeInfo().getName());
    assertEquals(1, ((SymTypeOfGenerics) type).getArgumentList().size());
    assertTrue(((SymTypeOfGenerics) type).getArgument(0).isTypeVariable());
  }

  protected FunctionSymbol resolveFunction(String name) {
    Optional<FunctionSymbol> emptyOpt = BasicSymbolsMill.globalScope().resolveFunction(name);
    assertTrue(emptyOpt.isPresent());
    return emptyOpt.get();
  }

}
