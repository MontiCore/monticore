// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.library;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.*;

public class StreamTypeTest {

  @BeforeEach
  public void init() throws IOException {
    LogStub.init();
    Log.enableFailQuick(false);

    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();

    // workaround to get library path working (in emf)
    URL streamURL = StreamTypeTest.class.getClassLoader().getResource("Stream.symtabdefinitionsym");
    assertNotNull(streamURL);
    // need to be expanded if ever false (could be a folder?)
    assertEquals("jar", streamURL.getProtocol());
    JarURLConnection urlConnection = (JarURLConnection) streamURL.openConnection();
    JarFile jar = urlConnection.getJarFile();
    Path jarPath = Path.of(jar.getName());
    BasicSymbolsMill.globalScope().getSymbolPath().addEntry(jarPath);

    // workaround as CDs are not known
    BasicSymbolsMill.globalScope().putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new OOTypeSymbolDeSer());
    BasicSymbolsMill.globalScope().putSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol", new MethodSymbolDeSer());
  }

  @Test
  public void resolveStreamType() {
    Optional<TypeSymbol> streamOpt = OOSymbolsMill.globalScope()
        .resolveType("Stream");
    assertTrue(streamOpt.isPresent());
    TypeSymbol stream = streamOpt.get();
    assertNotNull(stream.getSpannedScope());
    assertEquals(1, stream.getSpannedScope().getTypeVarSymbols().size());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void resolveStaticRepeat() {
    MethodSymbol method = getMethodSymbol("Stream.repeat");
    assertEquals(2, method.getParameterList().size());
    assertIsStreamWithTypeVar(method.getType());
    assertTrue(method.isIsStatic());
    List<TypeVarSymbol> typeVars = method.getSpannedScope().getLocalTypeVarSymbols();
    assertEquals(1, typeVars.size());
    assertNotEquals("T", typeVars.get(0).getName());
  }

  @Test
  public void resolveStreamMethodLen() {
    MethodSymbol method = getMethodSymbol("Stream.len");
    assertEquals(0, method.getParameterList().size());
    assertInstanceOf(TypeSymbol.class, method.getEnclosingScope().getSpanningSymbol());
    assertEquals("Stream", method.getEnclosingScope().getSpanningSymbol().getName());
    assertEquals(BasicSymbolsMill.LONG, method.getType().getTypeInfo().getName());
  }

  @Test
  public void resolveStreamMethodIsEmpty() {
    MethodSymbol method = getMethodSymbol("Stream.isEmpty");
    assertEquals(0, method.getParameterList().size());
    assertInstanceOf(TypeSymbol.class, method.getEnclosingScope().getSpanningSymbol());
    assertEquals("Stream", method.getEnclosingScope().getSpanningSymbol().getName());
    assertEquals(BasicSymbolsMill.BOOLEAN, method.getType().getTypeInfo().getName());
  }

  @Test
  public void resolveStreamFunctions() {
    resolveCommonMemberMethods("EventStream");
    resolveCommonMemberMethods("SyncStream");
    resolveCommonMemberMethods("ToptStream");
    resolveCommonMemberMethods("UntimedStream");

    resolveStaticMethods("Stream");
    resolveStaticMethods("EventStream");
    resolveStaticMethods("SyncStream");
    resolveStaticMethods("ToptStream");
  }

  protected void resolveStaticMethods(String streamType) {
    testResolveMethod(streamType + ".repeat");
    testResolveMethod(streamType + ".iterate");
    testResolveMethod(streamType + ".projFst");
    testResolveMethod(streamType + ".projSnd");
  }

  protected void resolveCommonMemberMethods(String streamType) {
    testResolveMethod(streamType + ".isEmpty");
    testResolveMethod(streamType + ".len");
    testResolveMethod(streamType + ".first");
    testResolveMethod(streamType + ".dropFirst");
    testResolveMethod(streamType + ".nth");
    testResolveMethod(streamType + ".take");
    testResolveMethod(streamType + ".times");
    testResolveMethod(streamType + ".map");
    testResolveMethod(streamType + ".filter");
    testResolveMethod(streamType + ".takeWhile");
    testResolveMethod(streamType + ".dropWhile");
    testResolveMethod(streamType + ".rcDups");
  }

  @Test
  public void resolveTimedStreamMethods() {
    testResolveMethod("EventStream.delay");
    testResolveMethod("EventStream.mapSlice");
    testResolveMethod("EventStream.rougherTime");
    testResolveMethod("EventStream.finerTime");
    testResolveMethod("EventStream.sync");
    testResolveMethod("EventStream.topt");
    testResolveMethod("EventStream.untimed");
    testResolveMethod("ToptStream.sizeEmptyTimeslices");
    testResolveMethod("SyncStream.event");
  }

  @Test
  public void isTypeVarResolvableFromReturn() {

  }

  protected void assertIsStreamWithTypeVar(SymTypeExpression type) {
    assertNotNull(type);
    assertTrue(type.isGenericType());
    assertEquals("Stream", type.getTypeInfo().getName());
    assertEquals(1, ((SymTypeOfGenerics) type).getArgumentList().size());
    assertTrue(((SymTypeOfGenerics) type).getArgument(0).isTypeVariable());
  }

  protected void testResolveMethod(String name) {
    Optional<SymTypeExpression> methodType = WithinScopeBasicSymbolsResolver.resolveNameAsExpr(BasicSymbolsMill.globalScope(), name);
    assertTrue(methodType.isPresent(), name);
    assertTrue(methodType.get().isFunctionType()
        || methodType.get().isIntersectionType()
    );
  }

  protected MethodSymbol getMethodSymbol(String name) {
    testResolveMethod(name);
    Optional<SymTypeExpression> methodType = WithinScopeBasicSymbolsResolver.resolveNameAsExpr(BasicSymbolsMill.globalScope(), name);
    return (MethodSymbol) methodType.get().asFunctionType().getSymbol();
  }

}
