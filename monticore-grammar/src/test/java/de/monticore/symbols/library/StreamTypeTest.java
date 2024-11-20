// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.library;

import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfTuple;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamTypeTest {

  @BeforeEach
  public void init() throws URISyntaxException {
    LogStub.init();
    Log.enableFailQuick(false);

    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
    // workaround to get library path working in emf
    Path symbolsPath = new File(FileReaderWriter.getResource(getClass().getClassLoader(), "de/monticore/symbols/library/Stream.sym").get().toURI()).toPath().getParent();
    OOSymbolsMill.globalScope().setSymbolPath(new MCPath(symbolsPath));
  }

  @Test
  public void testStreamTypeResolution() {
    Optional<TypeSymbol> streamOpt = OOSymbolsMill.globalScope()
        .resolveType("Stream");
    Assertions.assertTrue(streamOpt.isPresent());
    TypeSymbol stream = streamOpt.get();
    Assertions.assertNotNull(stream.getSpannedScope());
    Assertions.assertEquals(1, stream.getSpannedScope().getTypeVarSymbols().size());
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testStaticRepeat() {
    MethodSymbol method = resolveMethod("Stream.repeat");
    Assertions.assertEquals(method.getParameterList().size(), 2);
    assertIsStreamWithTypeVar(method.getType());
    Assertions.assertTrue(method.isIsStatic());
    List<TypeVarSymbol> typeVars = method.getSpannedScope().getLocalTypeVarSymbols();
    Assertions.assertEquals(1, typeVars.size());
    Assertions.assertNotEquals(typeVars.get(0).getName(), "S");
  }

  @Test
  public void testStaticProjection() {
    MethodSymbol method = resolveMethod("Stream.projFst");
    Assertions.assertTrue(method.isIsStatic());
    Assertions.assertEquals(1, method.getParameterList().size());
    assertIsStreamWithTypeVar(method.getType());
    assertIsStreamWithTupleOfTypeVars(method.getParameterList().get(0).getType());
  }

  @Test
  public void testStreamMethodLen() {
    MethodSymbol method = resolveMethod("Stream.len");
    Assertions.assertEquals(0, method.getParameterList().size());
    Assertions.assertInstanceOf(TypeSymbol.class, method.getEnclosingScope().getSpanningSymbol());
    Assertions.assertEquals("Stream", method.getEnclosingScope().getSpanningSymbol().getName());
    Assertions.assertEquals(BasicSymbolsMill.LONG, method.getType().getTypeInfo().getName());
  }

  @Test
  public void resolveStreamMethodsTest() {
    resolveCommonMemberMethods("UntimedStream");
    resolveCommonMemberMethods("EventStream");
    resolveCommonMemberMethods("SyncStream");
    resolveCommonMemberMethods("ToptStream");
  }

  @Test
  public void testInterfaceMethodResolution() {
    resolveStaticMethods("Stream");
    // methods such as "zip", "first", "nth" are not in the interface
    // as the signatures do not match between implementations
    resolveMethod("Stream.len");
    resolveMethod("Stream.hasInfiniteLen");
    resolveMethod("Stream.take");
    resolveMethod("Stream.dropFirst");
    resolveMethod("Stream.dropMultiple");
    resolveMethod("Stream.times");
    resolveMethod("Stream.infTimes");
    resolveMethod("Stream.map");
    resolveMethod("Stream.filter");
    resolveMethod("Stream.takeWhile");
    resolveMethod("Stream.dropWhile");
    resolveMethod("Stream.rcDups");
    resolveMethod("Stream.values");
    resolveMethod("Stream.scanl");
  }

  protected void resolveStaticMethods(String streamType) {
    resolveMethod(streamType + ".repeat");
    resolveMethod(streamType + ".iterate");
  }

  protected void resolveCommonMemberMethods(String streamType) {
    resolveMethod(streamType + ".len");
    resolveMethod(streamType + ".hasInfiniteLen");
    resolveMethod(streamType + ".first");
    resolveMethod(streamType + ".nth");
    resolveMethod(streamType + ".take");
    resolveMethod(streamType + ".dropFirst");
    resolveMethod(streamType + ".dropMultiple");
    resolveMethod(streamType + ".times");
    resolveMethod(streamType + ".infTimes");
    resolveMethod(streamType + ".map");
    resolveMethod(streamType + ".filter");
    resolveMethod(streamType + ".takeWhile");
    resolveMethod(streamType + ".dropWhile");
    resolveMethod(streamType + ".rcDups");
    resolveMethod(streamType + ".zip");
    resolveMethod(streamType + ".values");
    resolveMethod(streamType + "scanl");
  }

  @Test
  public void testTimedStreamMethodsResolution() {
    resolveMethod("EventStream.delay");
    resolveMethod("EventStream.mapSlice");
    resolveMethod("EventStream.rougherTime");
    resolveMethod("EventStream.finerTime");
    resolveMethod("EventStream.sync");
    resolveMethod("EventStream.topt");
    resolveMethod("EventStream.untimed");
    resolveMethod("ToptStream.sizeEmptyTimeslices");
    resolveMethod("SyncStream.event");
  }

  @Test
  public void isTypeVarResolvableFromReturn() {

  }

  protected void assertIsStreamWithTypeVar(SymTypeExpression type) {
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isGenericType());
    Assertions.assertEquals("Stream", type.getTypeInfo().getName());
    Assertions.assertEquals(1, ((SymTypeOfGenerics) type).getArgumentList().size());
    Assertions.assertTrue(((SymTypeOfGenerics) type).getArgument(0).isTypeVariable());
  }

  protected void assertIsStreamWithTupleOfTypeVars(SymTypeExpression type) {
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isGenericType());
    Assertions.assertEquals("Stream", type.getTypeInfo().getName());
    Assertions.assertEquals(1, ((SymTypeOfGenerics) type).getArgumentList().size());
    Assertions.assertTrue(((SymTypeOfGenerics) type).getArgument(0).isTupleType());
    Assertions.assertEquals(2, ((SymTypeOfTuple)((SymTypeOfGenerics) type).getArgument(0)).getTypeList().size());
    List<SymTypeExpression> tuple = ((SymTypeOfTuple)((SymTypeOfGenerics) type).getArgument(0)).getTypeList();
    Assertions.assertTrue(tuple.get(0).isTypeVariable());
    Assertions.assertTrue(tuple.get(1).isTypeVariable());
  }

  protected MethodSymbol resolveMethod(String name) {
    Optional<MethodSymbol> emptyOpt = OOSymbolsMill.globalScope().resolveMethod(name);
    Assertions.assertTrue(emptyOpt.isPresent());
    Assertions.assertTrue(emptyOpt.get().isIsMethod());
    return emptyOpt.get();
  }
}
