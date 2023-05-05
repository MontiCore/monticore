/* (c) https://github.com/MontiCore/monticore */

import bluea._ast.ASTPlace;
import blueb._ast.ASTBluePlace;
import blueb._ast.ASTRedPlace;
import bluec.BlueCMill;
import bluec._ast.ASTLightBluePlace;
import bluec._parser.BlueCParser;
import bluec._util.BlueCTypeDispatcher;
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

    BlueCTypeDispatcher dispatcher = new BlueCTypeDispatcher();

    assertFalse(dispatcher.isASTLightBluePlace(ast));
    assertFalse(dispatcher.isASTBluePlace(ast));
    assertFalse(dispatcher.isASTRedPlace(ast));
    assertTrue(dispatcher.isASTPlace(ast));

    assertFalse(dispatcher.isASTLightBluePlace(ast.getPlace(0)));
    assertTrue(dispatcher.isASTBluePlace(ast.getPlace(0)));
    assertFalse(dispatcher.isASTRedPlace(ast.getPlace(0)));
    assertTrue(dispatcher.isASTPlace(ast.getPlace(0)));

    assertFalse(dispatcher.isASTLightBluePlace(ast.getPlace(1)));
    assertFalse(dispatcher.isASTBluePlace(ast.getPlace(1)));
    assertTrue(dispatcher.isASTRedPlace(ast.getPlace(1)));
    assertTrue(dispatcher.isASTPlace(ast.getPlace(1)));

    assertTrue(dispatcher.isASTLightBluePlace(ast.getPlace(0).getPlace(0)));
    assertTrue(dispatcher.isASTBluePlace(ast.getPlace(0).getPlace(0)));
    assertFalse(dispatcher.isASTRedPlace(ast.getPlace(0).getPlace(0)));
    assertTrue(dispatcher.isASTPlace(ast.getPlace(0).getPlace(0)));

    assertFalse(dispatcher.isASTLightBluePlace(ast.getPlace(0).getPlace(0).getPlace(0)));
    assertFalse(dispatcher.isASTBluePlace(ast.getPlace(0).getPlace(0).getPlace(0)));
    assertFalse(dispatcher.isASTRedPlace(ast.getPlace(0).getPlace(0).getPlace(0)));
    assertTrue(dispatcher.isASTPlace(ast.getPlace(0).getPlace(0).getPlace(0)));
  }

  @Test
  public void testAsMethods() throws IOException {
    BlueCParser parser = BlueCMill.parser();
    final Optional<ASTPlace> optAST = parser.parse_StringPlace("place p1{bluePlace p2{lightBluePlace p3{place p4{}}}redPlace p5{}}");
    assertTrue(optAST.isPresent());
    final ASTPlace ast = optAST.get();
    BlueCTypeDispatcher dispatcher = new BlueCTypeDispatcher();

    assertEquals("place", printType(dispatcher.asASTPlace(ast)));

    assertEquals("place", printType(dispatcher.asASTPlace(ast.getPlace(0))));
    assertEquals("bluePlace", printType(dispatcher.asASTBluePlace(ast.getPlace(0))));

    assertEquals("place", printType(dispatcher.asASTPlace(ast.getPlace(1))));
    assertEquals("redPlace", printType(dispatcher.asASTRedPlace(ast.getPlace(1))));

    assertEquals("place", printType(dispatcher.asASTPlace(ast.getPlace(0).getPlace(0))));
    assertEquals("bluePlace", printType(dispatcher.asASTBluePlace(ast.getPlace(0).getPlace(0))));
    assertEquals("lightBluePlace", printType(dispatcher.asASTLightBluePlace(ast.getPlace(0).getPlace(0))));

    assertEquals("place", printType(dispatcher.asASTPlace(ast.getPlace(0).getPlace(0).getPlace(0))));
  }

  @After
  public void after() {
    if (!Log.getFindings().isEmpty()) {
      Log.getFindings().stream().map(Finding::getMsg).forEach(System.out::println);
    }
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
