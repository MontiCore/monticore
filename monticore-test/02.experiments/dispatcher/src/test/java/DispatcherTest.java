/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Test;
import simpleinterfaces._ast.*;
import simpleinterfaces._parser.SimpleInterfacesParser;
import simpleinterfaces._util.SimpleInterfacesTypeDispatcher;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class DispatcherTest {

  @Test
  public void testIsA() throws IOException {

    SimpleInterfacesParser parser = new SimpleInterfacesParser();
    SimpleInterfacesTypeDispatcher dispatcher = new SimpleInterfacesTypeDispatcher();

    final Optional<ASTA> optAST = parser.parse_StringA("c");
    assertTrue(optAST.isPresent());
    final ASTA ast = optAST.get();

    assertTrue(dispatcher.isASTA(ast));
    assertFalse(dispatcher.isASTB(ast));
    assertFalse(dispatcher.isASTC(ast));
    assertFalse(dispatcher.isASTD(ast));
    assertFalse(dispatcher.isASTE(ast));
    assertFalse(dispatcher.isASTF(ast));

  }

  @Test
  public void testIsC() throws IOException {

    SimpleInterfacesParser parser = new SimpleInterfacesParser();
    SimpleInterfacesTypeDispatcher dispatcher = new SimpleInterfacesTypeDispatcher();

    final Optional<ASTC> optAST = parser.parse_StringC("c");
    assertTrue(optAST.isPresent());
    final ASTC ast = optAST.get();

    assertTrue(dispatcher.isASTB(ast));
    assertTrue(dispatcher.isASTC(ast));
    assertFalse(dispatcher.isASTD(ast));
    assertFalse(dispatcher.isASTE(ast));
    assertFalse(dispatcher.isASTF(ast));

  }

  @Test
  public void testIsD() throws IOException {

    SimpleInterfacesParser parser = new SimpleInterfacesParser();
    SimpleInterfacesTypeDispatcher dispatcher = new SimpleInterfacesTypeDispatcher();

    final Optional<ASTD> optAST = parser.parse_StringD("d");
    assertTrue(optAST.isPresent());
    final ASTD ast = optAST.get();

    assertTrue(dispatcher.isASTB(ast));
    assertFalse(dispatcher.isASTC(ast));
    assertTrue(dispatcher.isASTD(ast));
    assertFalse(dispatcher.isASTE(ast));
    assertFalse(dispatcher.isASTF(ast));

  }

  @Test
  public void testIsE() throws IOException {

    SimpleInterfacesParser parser = new SimpleInterfacesParser();
    SimpleInterfacesTypeDispatcher dispatcher = new SimpleInterfacesTypeDispatcher();

    final Optional<ASTE> optAST = parser.parse_StringE("e");
    assertTrue(optAST.isPresent());
    final ASTE ast = optAST.get();

    assertFalse(dispatcher.isASTB(ast));
    assertFalse(dispatcher.isASTC(ast));
    assertFalse(dispatcher.isASTD(ast));
    assertTrue(dispatcher.isASTE(ast));
    assertFalse(dispatcher.isASTF(ast));

  }

  @Test
  public void testIsF() throws IOException {

    SimpleInterfacesParser parser = new SimpleInterfacesParser();
    SimpleInterfacesTypeDispatcher dispatcher = new SimpleInterfacesTypeDispatcher();

    final Optional<ASTF> optAST = parser.parse_StringF("f");
    assertTrue(optAST.isPresent());
    final ASTF ast = optAST.get();

    assertFalse(dispatcher.isASTB(ast));
    assertFalse(dispatcher.isASTC(ast));
    assertFalse(dispatcher.isASTD(ast));
    assertTrue(dispatcher.isASTE(ast));
    assertTrue(dispatcher.isASTF(ast));

  }

  @Test
  public void testAsA() throws IOException {
    SimpleInterfacesParser parser = new SimpleInterfacesParser();
    SimpleInterfacesTypeDispatcher dispatcher = new SimpleInterfacesTypeDispatcher();

    Log.enableFailQuick(false);

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

    ASTA castA = dispatcher.asASTA(astA);
    assertEquals(castA, astA);

    try {
      ASTA castB = dispatcher.asASTA(astB);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castC = dispatcher.asASTA(astC);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castD = dispatcher.asASTA(astD);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castE = dispatcher.asASTA(astE);
    } catch (IllegalStateException e) {
      assertEquals(8, Log.getFindings().size());
      assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castF = dispatcher.asASTA(astF);
    } catch (IllegalStateException e) {
      assertEquals(10, Log.getFindings().size());
      assertTrue(Log.getFindings().get(8).getMsg().startsWith("0x54987"));
    }
  }

  @Test
  public void testAsB() throws IOException {
    SimpleInterfacesParser parser = new SimpleInterfacesParser();
    SimpleInterfacesTypeDispatcher dispatcher = new SimpleInterfacesTypeDispatcher();

    Log.enableFailQuick(false);

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
      ASTB castA = dispatcher.asASTB(astA);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    ASTB castB = dispatcher.asASTB(astB);
    assertEquals(castB, astB);

    ASTB castC = dispatcher.asASTB(astC);
    assertEquals(castC, astC);

    ASTB castD = dispatcher.asASTB(astD);
    assertEquals(castD, astD);

    try {
      ASTB castE = dispatcher.asASTB(astE);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTB castF = dispatcher.asASTB(astF);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }
  }

  @Test
  public void testAsC() throws IOException {
    SimpleInterfacesParser parser = new SimpleInterfacesParser();
    SimpleInterfacesTypeDispatcher dispatcher = new SimpleInterfacesTypeDispatcher();

    Log.enableFailQuick(false);

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
      ASTC castA = dispatcher.asASTC(astA);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    ASTC castB = dispatcher.asASTC(astB);
    assertEquals(castB, astB);

    ASTC castC = dispatcher.asASTC(astC);
    assertEquals(castC, astC);

    try {
      ASTC castD = dispatcher.asASTC(astD);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTC castE = dispatcher.asASTC(astE);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTC castF = dispatcher.asASTC(astF);
    } catch (IllegalStateException e) {
      assertEquals(8, Log.getFindings().size());
      assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }
  }

  @Test
  public void testAsD() throws IOException {
    SimpleInterfacesParser parser = new SimpleInterfacesParser();
    SimpleInterfacesTypeDispatcher dispatcher = new SimpleInterfacesTypeDispatcher();

    Log.enableFailQuick(false);

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
      ASTD castA = dispatcher.asASTD(astA);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    ASTD castB = dispatcher.asASTD(astB);
    assertEquals(castB, astB);

    try {
      ASTD castC = dispatcher.asASTD(astC);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    ASTD castD = dispatcher.asASTD(astD);
    assertEquals(castD, astD);

    try {
      ASTD castE = dispatcher.asASTD(astE);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTD castF = dispatcher.asASTD(astF);
    } catch (IllegalStateException e) {
      assertEquals(8, Log.getFindings().size());
      assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }
  }

  @Test
  public void testAsE() throws IOException {
    SimpleInterfacesParser parser = new SimpleInterfacesParser();
    SimpleInterfacesTypeDispatcher dispatcher = new SimpleInterfacesTypeDispatcher();

    Log.enableFailQuick(false);

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
      ASTE castA = dispatcher.asASTE(astA);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    try {
      ASTE castB = dispatcher.asASTE(astB);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTE castC = dispatcher.asASTE(astC);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTE castD = dispatcher.asASTE(astD);
    } catch (IllegalStateException e) {
      assertEquals(8, Log.getFindings().size());
      assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }

    ASTE castE = dispatcher.asASTE(astE);
    assertEquals(castE, astE);

    ASTE castF = dispatcher.asASTE(astF);
    assertEquals(castF, astF);

    assertEquals(Log.getFindings().size(), 8);
  }

  @Test
  public void testAsF() throws IOException {
    SimpleInterfacesParser parser = new SimpleInterfacesParser();
    SimpleInterfacesTypeDispatcher dispatcher = new SimpleInterfacesTypeDispatcher();

    Log.enableFailQuick(false);

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
      ASTF castA = dispatcher.asASTF(astA);
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castB = dispatcher.asASTF(astB);
    } catch (IllegalStateException e) {
      assertEquals(4, Log.getFindings().size());
      assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castC = dispatcher.asASTF(astC);
    } catch (IllegalStateException e) {
      assertEquals(6, Log.getFindings().size());
      assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castD = dispatcher.asASTF(astD);
    } catch (IllegalStateException e) {
      assertEquals(8, Log.getFindings().size());
      assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castE = dispatcher.asASTF(astE);
    } catch (IllegalStateException e) {
      assertEquals(10, Log.getFindings().size());
      assertTrue(Log.getFindings().get(8).getMsg().startsWith("0x54987"));
    }

    ASTF castF = dispatcher.asASTF(astF);
    assertEquals(castF, astF);
  }

  @After
  public void after() {
    Log.getFindings().clear();
  }
}