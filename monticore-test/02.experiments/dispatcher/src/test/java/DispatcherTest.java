/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import simpleinterfaces.SimpleInterfacesMill;
import simpleinterfaces._ast.*;
import simpleinterfaces._parser.SimpleInterfacesParser;
import simpleinterfaces._symboltable.ISimpleInterfacesArtifactScope;
import simpleinterfaces._symboltable.ISimpleInterfacesGlobalScope;
import simpleinterfaces._symboltable.ISimpleInterfacesScope;
import simpleinterfaces._util.ISimpleInterfacesTypeDispatcher;
import simpleinterfaces._util.SimpleInterfacesTypeDispatcher;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class DispatcherTest {

  protected static final SimpleInterfacesParser parser = new SimpleInterfacesParser();
  protected static final ISimpleInterfacesTypeDispatcher dispatcher = SimpleInterfacesMill.typeDispatcher();

  @Before
  public void before() {
    SimpleInterfacesMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testAsGlobalScope() {
    ISimpleInterfacesGlobalScope scope = SimpleInterfacesMill.globalScope();
    assertTrue(dispatcher.isSimpleInterfacesISimpleInterfacesGlobalScope(scope));
  }

  @Test
  public void testAsArtifactScope() {
    ISimpleInterfacesArtifactScope scope = SimpleInterfacesMill.artifactScope();
    assertTrue(dispatcher.isSimpleInterfacesISimpleInterfacesArtifactScope(scope));
  }

  @Test
  public void testAsScope() {
    ISimpleInterfacesScope scope = SimpleInterfacesMill.scope();
    assertTrue(dispatcher.isSimpleInterfacesISimpleInterfacesScope(scope));
  }

  @Test
  public void testIsA() throws IOException {
    final Optional<ASTA> optAST = parser.parse_StringA("c");
    assertTrue(optAST.isPresent());
    final ASTA ast = optAST.get();

    assertTrue(dispatcher.isSimpleInterfacesASTA(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTB(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTC(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTD(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTE(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTF(ast));
  }

  @Test
  public void testIsC() throws IOException {
    final Optional<ASTC> optAST = parser.parse_StringC("c");
    assertTrue(optAST.isPresent());
    final ASTC ast = optAST.get();

    assertTrue(dispatcher.isSimpleInterfacesASTB(ast));
    assertTrue(dispatcher.isSimpleInterfacesASTC(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTD(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTE(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTF(ast));
  }

  @Test
  public void testIsD() throws IOException {
    final Optional<ASTD> optAST = parser.parse_StringD("d");
    assertTrue(optAST.isPresent());
    final ASTD ast = optAST.get();

    assertTrue(dispatcher.isSimpleInterfacesASTB(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTC(ast));
    assertTrue(dispatcher.isSimpleInterfacesASTD(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTE(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTF(ast));
  }

  @Test
  public void testIsE() throws IOException {
    final Optional<ASTE> optAST = parser.parse_StringE("e");
    assertTrue(optAST.isPresent());
    final ASTE ast = optAST.get();

    assertFalse(dispatcher.isSimpleInterfacesASTB(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTC(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTD(ast));
    assertTrue(dispatcher.isSimpleInterfacesASTE(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTF(ast));
  }

  @Test
  public void testIsF() throws IOException {
    final Optional<ASTF> optAST = parser.parse_StringF("f");
    assertTrue(optAST.isPresent());
    final ASTF ast = optAST.get();

    assertFalse(dispatcher.isSimpleInterfacesASTB(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTC(ast));
    assertFalse(dispatcher.isSimpleInterfacesASTD(ast));
    assertTrue(dispatcher.isSimpleInterfacesASTE(ast));
    assertTrue(dispatcher.isSimpleInterfacesASTF(ast));
  }

  @Test
  public void testAsA() throws IOException {
    final Optional<ASTA> optASTA = parser.parse_StringA("c");
    final Optional<ASTB> optASTB = parser.parse_StringB("c");
    final Optional<ASTC> optASTC = parser.parse_StringC("c");
    final Optional<ASTD> optASTD = parser.parse_StringD("d");
    final Optional<ASTE> optASTE = parser.parse_StringE("e");
    final Optional<ASTF> optASTF = parser.parse_StringF("f");

    assertTrue(optASTA.isPresent());
    assertTrue(optASTB.isPresent());
    assertTrue(optASTC.isPresent());
    assertTrue(optASTD.isPresent());
    assertTrue(optASTE.isPresent());
    assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    ASTA castA = dispatcher.asSimpleInterfacesASTA(astA);
    assertEquals(castA, astA);

    try {
      ASTA castB = dispatcher.asSimpleInterfacesASTA(astB);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castC = dispatcher.asSimpleInterfacesASTA(astC);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castD = dispatcher.asSimpleInterfacesASTA(astD);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castE = dispatcher.asSimpleInterfacesASTA(astE);
    } catch (IllegalStateException e) {
      assertEquals(8, Log.getFindings().size());
      assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castF = dispatcher.asSimpleInterfacesASTA(astF);
    } catch (IllegalStateException e) {
      assertEquals(10, Log.getFindings().size());
      assertTrue(Log.getFindings().get(8).getMsg().startsWith("0x54987"));
    }
  }

  @Test
  public void testAsB() throws IOException {
    final Optional<ASTA> optASTA = parser.parse_StringA("c");
    final Optional<ASTB> optASTB = parser.parse_StringB("c");
    final Optional<ASTC> optASTC = parser.parse_StringC("c");
    final Optional<ASTD> optASTD = parser.parse_StringD("d");
    final Optional<ASTE> optASTE = parser.parse_StringE("e");
    final Optional<ASTF> optASTF = parser.parse_StringF("f");

    assertTrue(optASTA.isPresent());
    assertTrue(optASTB.isPresent());
    assertTrue(optASTC.isPresent());
    assertTrue(optASTD.isPresent());
    assertTrue(optASTE.isPresent());
    assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    try {
      ASTB castA = dispatcher.asSimpleInterfacesASTB(astA);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    ASTB castB = dispatcher.asSimpleInterfacesASTB(astB);
    assertEquals(castB, astB);

    ASTB castC = dispatcher.asSimpleInterfacesASTB(astC);
    assertEquals(castC, astC);

    ASTB castD = dispatcher.asSimpleInterfacesASTB(astD);
    assertEquals(castD, astD);

    try {
      ASTB castE = dispatcher.asSimpleInterfacesASTB(astE);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTB castF = dispatcher.asSimpleInterfacesASTB(astF);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }
  }

  @Test
  public void testAsC() throws IOException {
    final Optional<ASTA> optASTA = parser.parse_StringA("c");
    final Optional<ASTB> optASTB = parser.parse_StringB("c");
    final Optional<ASTC> optASTC = parser.parse_StringC("c");
    final Optional<ASTD> optASTD = parser.parse_StringD("d");
    final Optional<ASTE> optASTE = parser.parse_StringE("e");
    final Optional<ASTF> optASTF = parser.parse_StringF("f");

    assertTrue(optASTA.isPresent());
    assertTrue(optASTB.isPresent());
    assertTrue(optASTC.isPresent());
    assertTrue(optASTD.isPresent());
    assertTrue(optASTE.isPresent());
    assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    try {
      ASTC castA = dispatcher.asSimpleInterfacesASTC(astA);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    ASTC castB = dispatcher.asSimpleInterfacesASTC(astB);
    assertEquals(castB, astB);

    ASTC castC = dispatcher.asSimpleInterfacesASTC(astC);
    assertEquals(castC, astC);

    try {
      ASTC castD = dispatcher.asSimpleInterfacesASTC(astD);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTC castE = dispatcher.asSimpleInterfacesASTC(astE);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTC castF = dispatcher.asSimpleInterfacesASTC(astF);
    } catch (IllegalStateException e) {
      assertEquals(8, Log.getFindings().size());
      assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }
  }

  @Test
  public void testAsD() throws IOException {
    final Optional<ASTA> optASTA = parser.parse_StringA("c");
    final Optional<ASTB> optASTB = parser.parse_StringB("d");
    final Optional<ASTC> optASTC = parser.parse_StringC("c");
    final Optional<ASTD> optASTD = parser.parse_StringD("d");
    final Optional<ASTE> optASTE = parser.parse_StringE("e");
    final Optional<ASTF> optASTF = parser.parse_StringF("f");

    assertTrue(optASTA.isPresent());
    assertTrue(optASTB.isPresent());
    assertTrue(optASTC.isPresent());
    assertTrue(optASTD.isPresent());
    assertTrue(optASTE.isPresent());
    assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    try {
      ASTD castA = dispatcher.asSimpleInterfacesASTD(astA);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    ASTD castB = dispatcher.asSimpleInterfacesASTD(astB);
    assertEquals(castB, astB);

    try {
      ASTD castC = dispatcher.asSimpleInterfacesASTD(astC);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    ASTD castD = dispatcher.asSimpleInterfacesASTD(astD);
    assertEquals(castD, astD);

    try {
      ASTD castE = dispatcher.asSimpleInterfacesASTD(astE);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTD castF = dispatcher.asSimpleInterfacesASTD(astF);
    } catch (IllegalStateException e) {
      assertEquals(8, Log.getFindings().size());
      assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }
  }

  @Test
  public void testAsE() throws IOException {
    final Optional<ASTA> optASTA = parser.parse_StringA("c");
    final Optional<ASTB> optASTB = parser.parse_StringB("c");
    final Optional<ASTC> optASTC = parser.parse_StringC("c");
    final Optional<ASTD> optASTD = parser.parse_StringD("d");
    final Optional<ASTE> optASTE = parser.parse_StringE("e");
    final Optional<ASTF> optASTF = parser.parse_StringF("f");

    assertTrue(optASTA.isPresent());
    assertTrue(optASTB.isPresent());
    assertTrue(optASTC.isPresent());
    assertTrue(optASTD.isPresent());
    assertTrue(optASTE.isPresent());
    assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    try {
      ASTE castA = dispatcher.asSimpleInterfacesASTE(astA);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    try {
      ASTE castB = dispatcher.asSimpleInterfacesASTE(astB);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTE castC = dispatcher.asSimpleInterfacesASTE(astC);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTE castD = dispatcher.asSimpleInterfacesASTE(astD);
    } catch (IllegalStateException e) {
      assertEquals(8, Log.getFindings().size());
      assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }

    ASTE castE = dispatcher.asSimpleInterfacesASTE(astE);
    assertEquals(castE, astE);

    ASTE castF = dispatcher.asSimpleInterfacesASTE(astF);
    assertEquals(castF, astF);

    assertEquals(Log.getFindings().size(), 8);
  }

  @Test
  public void testAsF() throws IOException {
    final Optional<ASTA> optASTA = parser.parse_StringA("c");
    final Optional<ASTB> optASTB = parser.parse_StringB("c");
    final Optional<ASTC> optASTC = parser.parse_StringC("c");
    final Optional<ASTD> optASTD = parser.parse_StringD("d");
    final Optional<ASTE> optASTE = parser.parse_StringE("e");
    final Optional<ASTF> optASTF = parser.parse_StringF("f");

    assertTrue(optASTA.isPresent());
    assertTrue(optASTB.isPresent());
    assertTrue(optASTC.isPresent());
    assertTrue(optASTD.isPresent());
    assertTrue(optASTE.isPresent());
    assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    try {
      ASTF castA = dispatcher.asSimpleInterfacesASTF(astA);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castB = dispatcher.asSimpleInterfacesASTF(astB);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castC = dispatcher.asSimpleInterfacesASTF(astC);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castD = dispatcher.asSimpleInterfacesASTF(astD);
    } catch (IllegalStateException e) {
      assertEquals(8, Log.getFindings().size());
      assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castE = dispatcher.asSimpleInterfacesASTF(astE);
    } catch (IllegalStateException e) {
      assertEquals(10, Log.getFindings().size());
      assertTrue(Log.getFindings().get(8).getMsg().startsWith("0x54987"));
    }

    ASTF castF = dispatcher.asSimpleInterfacesASTF(astF);
    assertEquals(castF, astF);
  }

  @After
  public void after() {
    Log.getFindings().clear();
  }
}