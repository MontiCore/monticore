/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simpleinterfaces.SimpleInterfacesMill;
import simpleinterfaces._ast.*;
import simpleinterfaces._parser.SimpleInterfacesParser;
import simpleinterfaces._symboltable.ISimpleInterfacesArtifactScope;
import simpleinterfaces._symboltable.ISimpleInterfacesGlobalScope;
import simpleinterfaces._symboltable.ISimpleInterfacesScope;
import simpleinterfaces._util.ISimpleInterfacesTypeDispatcher;

import java.io.IOException;
import java.util.Optional;

public class DispatcherTest {

  protected static final SimpleInterfacesParser parser = new SimpleInterfacesParser();
  protected static final ISimpleInterfacesTypeDispatcher dispatcher = SimpleInterfacesMill.typeDispatcher();

  @BeforeEach
  public void before() {
    SimpleInterfacesMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testAsGlobalScope() {
    ISimpleInterfacesGlobalScope scope = SimpleInterfacesMill.globalScope();
    Assertions.assertTrue(dispatcher.isSimpleInterfacesISimpleInterfacesGlobalScope(scope));
  }

  @Test
  public void testAsArtifactScope() {
    ISimpleInterfacesArtifactScope scope = SimpleInterfacesMill.artifactScope();
    Assertions.assertTrue(dispatcher.isSimpleInterfacesISimpleInterfacesArtifactScope(scope));
  }

  @Test
  public void testAsScope() {
    ISimpleInterfacesScope scope = SimpleInterfacesMill.scope();
    Assertions.assertTrue(dispatcher.isSimpleInterfacesISimpleInterfacesScope(scope));
  }

  @Test
  public void testIsA() throws IOException {
    final Optional<ASTA> optAST = parser.parse_StringA("c");
    Assertions.assertTrue(optAST.isPresent());
    final ASTA ast = optAST.get();

    Assertions.assertTrue(dispatcher.isSimpleInterfacesASTA(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTB(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTC(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTD(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTE(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTF(ast));
  }

  @Test
  public void testIsC() throws IOException {
    final Optional<ASTC> optAST = parser.parse_StringC("c");
    Assertions.assertTrue(optAST.isPresent());
    final ASTC ast = optAST.get();

    Assertions.assertTrue(dispatcher.isSimpleInterfacesASTB(ast));
    Assertions.assertTrue(dispatcher.isSimpleInterfacesASTC(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTD(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTE(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTF(ast));
  }

  @Test
  public void testIsD() throws IOException {
    final Optional<ASTD> optAST = parser.parse_StringD("d");
    Assertions.assertTrue(optAST.isPresent());
    final ASTD ast = optAST.get();

    Assertions.assertTrue(dispatcher.isSimpleInterfacesASTB(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTC(ast));
    Assertions.assertTrue(dispatcher.isSimpleInterfacesASTD(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTE(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTF(ast));
  }

  @Test
  public void testIsE() throws IOException {
    final Optional<ASTE> optAST = parser.parse_StringE("e");
    Assertions.assertTrue(optAST.isPresent());
    final ASTE ast = optAST.get();

    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTB(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTC(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTD(ast));
    Assertions.assertTrue(dispatcher.isSimpleInterfacesASTE(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTF(ast));
  }

  @Test
  public void testIsF() throws IOException {
    final Optional<ASTF> optAST = parser.parse_StringF("f");
    Assertions.assertTrue(optAST.isPresent());
    final ASTF ast = optAST.get();

    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTB(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTC(ast));
    Assertions.assertFalse(dispatcher.isSimpleInterfacesASTD(ast));
    Assertions.assertTrue(dispatcher.isSimpleInterfacesASTE(ast));
    Assertions.assertTrue(dispatcher.isSimpleInterfacesASTF(ast));
  }

  @Test
  public void testAsA() throws IOException {
    final Optional<ASTA> optASTA = parser.parse_StringA("c");
    final Optional<ASTB> optASTB = parser.parse_StringB("c");
    final Optional<ASTC> optASTC = parser.parse_StringC("c");
    final Optional<ASTD> optASTD = parser.parse_StringD("d");
    final Optional<ASTE> optASTE = parser.parse_StringE("e");
    final Optional<ASTF> optASTF = parser.parse_StringF("f");

    Assertions.assertTrue(optASTA.isPresent());
    Assertions.assertTrue(optASTB.isPresent());
    Assertions.assertTrue(optASTC.isPresent());
    Assertions.assertTrue(optASTD.isPresent());
    Assertions.assertTrue(optASTE.isPresent());
    Assertions.assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    ASTA castA = dispatcher.asSimpleInterfacesASTA(astA);
    Assertions.assertEquals(castA, astA);

    try {
      ASTA castB = dispatcher.asSimpleInterfacesASTA(astB);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(2, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castC = dispatcher.asSimpleInterfacesASTA(astC);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(4, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castD = dispatcher.asSimpleInterfacesASTA(astD);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(6, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castE = dispatcher.asSimpleInterfacesASTA(astE);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(8, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }

    try {
      ASTA castF = dispatcher.asSimpleInterfacesASTA(astF);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(10, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(8).getMsg().startsWith("0x54987"));
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

    Assertions.assertTrue(optASTA.isPresent());
    Assertions.assertTrue(optASTB.isPresent());
    Assertions.assertTrue(optASTC.isPresent());
    Assertions.assertTrue(optASTD.isPresent());
    Assertions.assertTrue(optASTE.isPresent());
    Assertions.assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    try {
      ASTB castA = dispatcher.asSimpleInterfacesASTB(astA);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(2, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    ASTB castB = dispatcher.asSimpleInterfacesASTB(astB);
    Assertions.assertEquals(castB, astB);

    ASTB castC = dispatcher.asSimpleInterfacesASTB(astC);
    Assertions.assertEquals(castC, astC);

    ASTB castD = dispatcher.asSimpleInterfacesASTB(astD);
    Assertions.assertEquals(castD, astD);

    try {
      ASTB castE = dispatcher.asSimpleInterfacesASTB(astE);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(4, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTB castF = dispatcher.asSimpleInterfacesASTB(astF);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(6, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
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

    Assertions.assertTrue(optASTA.isPresent());
    Assertions.assertTrue(optASTB.isPresent());
    Assertions.assertTrue(optASTC.isPresent());
    Assertions.assertTrue(optASTD.isPresent());
    Assertions.assertTrue(optASTE.isPresent());
    Assertions.assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    try {
      ASTC castA = dispatcher.asSimpleInterfacesASTC(astA);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(2, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    ASTC castB = dispatcher.asSimpleInterfacesASTC(astB);
    Assertions.assertEquals(castB, astB);

    ASTC castC = dispatcher.asSimpleInterfacesASTC(astC);
    Assertions.assertEquals(castC, astC);

    try {
      ASTC castD = dispatcher.asSimpleInterfacesASTC(astD);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(4, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTC castE = dispatcher.asSimpleInterfacesASTC(astE);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(6, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTC castF = dispatcher.asSimpleInterfacesASTC(astF);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(8, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
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

    Assertions.assertTrue(optASTA.isPresent());
    Assertions.assertTrue(optASTB.isPresent());
    Assertions.assertTrue(optASTC.isPresent());
    Assertions.assertTrue(optASTD.isPresent());
    Assertions.assertTrue(optASTE.isPresent());
    Assertions.assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    try {
      ASTD castA = dispatcher.asSimpleInterfacesASTD(astA);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(2, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    ASTD castB = dispatcher.asSimpleInterfacesASTD(astB);
    Assertions.assertEquals(castB, astB);

    try {
      ASTD castC = dispatcher.asSimpleInterfacesASTD(astC);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(4, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    ASTD castD = dispatcher.asSimpleInterfacesASTD(astD);
    Assertions.assertEquals(castD, astD);

    try {
      ASTD castE = dispatcher.asSimpleInterfacesASTD(astE);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(6, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTD castF = dispatcher.asSimpleInterfacesASTD(astF);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(8, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
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

    Assertions.assertTrue(optASTA.isPresent());
    Assertions.assertTrue(optASTB.isPresent());
    Assertions.assertTrue(optASTC.isPresent());
    Assertions.assertTrue(optASTD.isPresent());
    Assertions.assertTrue(optASTE.isPresent());
    Assertions.assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    try {
      ASTE castA = dispatcher.asSimpleInterfacesASTE(astA);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(2, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    try {
      ASTE castB = dispatcher.asSimpleInterfacesASTE(astB);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(4, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTE castC = dispatcher.asSimpleInterfacesASTE(astC);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(6, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTE castD = dispatcher.asSimpleInterfacesASTE(astD);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(8, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }

    ASTE castE = dispatcher.asSimpleInterfacesASTE(astE);
    Assertions.assertEquals(castE, astE);

    ASTE castF = dispatcher.asSimpleInterfacesASTE(astF);
    Assertions.assertEquals(castF, astF);

    Assertions.assertEquals(Log.getFindings().size(), 8);
  }

  @Test
  public void testAsF() throws IOException {
    final Optional<ASTA> optASTA = parser.parse_StringA("c");
    final Optional<ASTB> optASTB = parser.parse_StringB("c");
    final Optional<ASTC> optASTC = parser.parse_StringC("c");
    final Optional<ASTD> optASTD = parser.parse_StringD("d");
    final Optional<ASTE> optASTE = parser.parse_StringE("e");
    final Optional<ASTF> optASTF = parser.parse_StringF("f");

    Assertions.assertTrue(optASTA.isPresent());
    Assertions.assertTrue(optASTB.isPresent());
    Assertions.assertTrue(optASTC.isPresent());
    Assertions.assertTrue(optASTD.isPresent());
    Assertions.assertTrue(optASTE.isPresent());
    Assertions.assertTrue(optASTF.isPresent());

    final ASTA astA = optASTA.get();
    final ASTB astB = optASTB.get();
    final ASTC astC = optASTC.get();
    final ASTD astD = optASTD.get();
    final ASTE astE = optASTE.get();
    final ASTF astF = optASTF.get();

    try {
      ASTF castA = dispatcher.asSimpleInterfacesASTF(astA);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(2, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castB = dispatcher.asSimpleInterfacesASTF(astB);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(4, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(2).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castC = dispatcher.asSimpleInterfacesASTF(astC);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(6, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(4).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castD = dispatcher.asSimpleInterfacesASTF(astD);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(8, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(6).getMsg().startsWith("0x54987"));
    }

    try {
      ASTF castE = dispatcher.asSimpleInterfacesASTF(astE);
    } catch (IllegalStateException e) {
      Assertions.assertEquals(10, Log.getFindings().size());
      Assertions.assertTrue(Log.getFindings().get(8).getMsg().startsWith("0x54987"));
    }

    ASTF castF = dispatcher.asSimpleInterfacesASTF(astF);
    Assertions.assertEquals(castF, astF);
  }

  @AfterEach
  public void after() {
    Log.getFindings().clear();
  }
}