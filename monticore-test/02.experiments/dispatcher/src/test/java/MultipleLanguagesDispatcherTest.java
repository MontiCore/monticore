/* (c) https://github.com/MontiCore/monticore */

import bluea._ast.ASTPlace;
import blueb._ast.ASTBluePlace;
import blueb._ast.ASTRedPlace;
import bluec.BlueCMill;
import bluec._ast.ASTLightBluePlace;
import bluec._parser.BlueCParser;
import bluec._util.BlueCTypeDispatcher;
import bluec._util.IBlueCTypeDispatcher;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MultipleLanguagesDispatcherTest {

  @Before
  public void before() {
    BlueCMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testIsMethods() throws IOException {
    BlueCParser parser = BlueCMill.parser();
    final Optional<ASTPlace> optAST = parser.parse_StringPlace("place p1{bluePlace p2{lightBluePlace p3{place p4{}}}redPlace p5{}}");
    assertTrue(optAST.isPresent());
    final ASTPlace ast = optAST.get();

    IBlueCTypeDispatcher dispatcher = BlueCMill.typeDispatcher();

    assertFalse(dispatcher.isBlueCASTLightBluePlace(ast));
    assertFalse(dispatcher.isBlueBASTBluePlace(ast));
    assertFalse(dispatcher.isBlueBASTRedPlace(ast));
    assertTrue(dispatcher.isBlueAASTPlace(ast));

    assertFalse(dispatcher.isBlueCASTLightBluePlace(ast.getPlace(0)));
    assertTrue(dispatcher.isBlueBASTBluePlace(ast.getPlace(0)));
    assertFalse(dispatcher.isBlueBASTRedPlace(ast.getPlace(0)));
    assertTrue(dispatcher.isBlueAASTPlace(ast.getPlace(0)));

    assertFalse(dispatcher.isBlueCASTLightBluePlace(ast.getPlace(1)));
    assertFalse(dispatcher.isBlueBASTBluePlace(ast.getPlace(1)));
    assertTrue(dispatcher.isBlueBASTRedPlace(ast.getPlace(1)));
    assertTrue(dispatcher.isBlueAASTPlace(ast.getPlace(1)));

    assertTrue(dispatcher.isBlueCASTLightBluePlace(ast.getPlace(0).getPlace(0)));
    assertTrue(dispatcher.isBlueBASTBluePlace(ast.getPlace(0).getPlace(0)));
    assertFalse(dispatcher.isBlueBASTRedPlace(ast.getPlace(0).getPlace(0)));
    assertTrue(dispatcher.isBlueAASTPlace(ast.getPlace(0).getPlace(0)));

    assertFalse(dispatcher.isBlueCASTLightBluePlace(ast.getPlace(0).getPlace(0).getPlace(0)));
    assertFalse(dispatcher.isBlueBASTBluePlace(ast.getPlace(0).getPlace(0).getPlace(0)));
    assertFalse(dispatcher.isBlueBASTRedPlace(ast.getPlace(0).getPlace(0).getPlace(0)));
    assertTrue(dispatcher.isBlueAASTPlace(ast.getPlace(0).getPlace(0).getPlace(0)));
  }

  @Test
  public void testAsMethods() throws IOException {
    BlueCParser parser = BlueCMill.parser();
    final Optional<ASTPlace> optAST = parser.parse_StringPlace("place p1{bluePlace p2{lightBluePlace p3{place p4{}}}redPlace p5{}}");
    assertTrue(optAST.isPresent());
    final ASTPlace ast = optAST.get();

    IBlueCTypeDispatcher dispatcher = BlueCMill.typeDispatcher();

    assertEquals("place", printType(dispatcher.asBlueAASTPlace(ast)));

    assertEquals("place", printType(dispatcher.asBlueAASTPlace(ast.getPlace(0))));
    assertEquals("bluePlace", printType(dispatcher.asBlueBASTBluePlace(ast.getPlace(0))));

    assertEquals("place", printType(dispatcher.asBlueAASTPlace(ast.getPlace(1))));
    assertEquals("redPlace", printType(dispatcher.asBlueBASTRedPlace(ast.getPlace(1))));

    assertEquals("place", printType(dispatcher.asBlueAASTPlace(ast.getPlace(0).getPlace(0))));
    assertEquals("bluePlace", printType(dispatcher.asBlueBASTBluePlace(ast.getPlace(0).getPlace(0))));
    assertEquals("lightBluePlace", printType(dispatcher.asBlueCASTLightBluePlace(ast.getPlace(0).getPlace(0))));

    assertEquals("place", printType(dispatcher.asBlueAASTPlace(ast.getPlace(0).getPlace(0).getPlace(0))));
  }

  @After
  public void after() {
    assertTrue(Log.getFindings().isEmpty());
    Log.getFindings().clear();
  }

  public String printType(ASTPlace ast) {
    return "place";
  }

  public String printType(ASTBluePlace ast) {
    return "bluePlace";
  }


  public String printType(ASTRedPlace ast) {
    return "redPlace";
  }

  public String printType(ASTLightBluePlace ast) {
    return "lightBluePlace";
  }

}
