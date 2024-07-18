/* (c) https://github.com/MontiCore/monticore */

import bluea._ast.ASTPlace;
import blueb._ast.ASTBluePlace;
import blueb._ast.ASTRedPlace;
import bluec.BlueCMill;
import bluec._ast.ASTLightBluePlace;
import bluec._parser.BlueCParser;
import bluec._util.IBlueCTypeDispatcher;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MultipleLanguagesDispatcherTest {

  @BeforeEach
  public void before() {
    BlueCMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testIsMethods() throws IOException {
    BlueCParser parser = BlueCMill.parser();
    final Optional<ASTPlace> optAST = parser.parse_StringPlace("place p1{bluePlace p2{lightBluePlace p3{place p4{}}}redPlace p5{}}");
    Assertions.assertTrue(optAST.isPresent());
    final ASTPlace ast = optAST.get();

    IBlueCTypeDispatcher dispatcher = BlueCMill.typeDispatcher();

    Assertions.assertFalse(dispatcher.isBlueCASTLightBluePlace(ast));
    Assertions.assertFalse(dispatcher.isBlueBASTBluePlace(ast));
    Assertions.assertFalse(dispatcher.isBlueBASTRedPlace(ast));
    Assertions.assertTrue(dispatcher.isBlueAASTPlace(ast));

    Assertions.assertFalse(dispatcher.isBlueCASTLightBluePlace(ast.getPlace(0)));
    Assertions.assertTrue(dispatcher.isBlueBASTBluePlace(ast.getPlace(0)));
    Assertions.assertFalse(dispatcher.isBlueBASTRedPlace(ast.getPlace(0)));
    Assertions.assertTrue(dispatcher.isBlueAASTPlace(ast.getPlace(0)));

    Assertions.assertFalse(dispatcher.isBlueCASTLightBluePlace(ast.getPlace(1)));
    Assertions.assertFalse(dispatcher.isBlueBASTBluePlace(ast.getPlace(1)));
    Assertions.assertTrue(dispatcher.isBlueBASTRedPlace(ast.getPlace(1)));
    Assertions.assertTrue(dispatcher.isBlueAASTPlace(ast.getPlace(1)));

    Assertions.assertTrue(dispatcher.isBlueCASTLightBluePlace(ast.getPlace(0).getPlace(0)));
    Assertions.assertTrue(dispatcher.isBlueBASTBluePlace(ast.getPlace(0).getPlace(0)));
    Assertions.assertFalse(dispatcher.isBlueBASTRedPlace(ast.getPlace(0).getPlace(0)));
    Assertions.assertTrue(dispatcher.isBlueAASTPlace(ast.getPlace(0).getPlace(0)));

    Assertions.assertFalse(dispatcher.isBlueCASTLightBluePlace(ast.getPlace(0).getPlace(0).getPlace(0)));
    Assertions.assertFalse(dispatcher.isBlueBASTBluePlace(ast.getPlace(0).getPlace(0).getPlace(0)));
    Assertions.assertFalse(dispatcher.isBlueBASTRedPlace(ast.getPlace(0).getPlace(0).getPlace(0)));
    Assertions.assertTrue(dispatcher.isBlueAASTPlace(ast.getPlace(0).getPlace(0).getPlace(0)));
  }

  @Test
  public void testAsMethods() throws IOException {
    BlueCParser parser = BlueCMill.parser();
    final Optional<ASTPlace> optAST = parser.parse_StringPlace("place p1{bluePlace p2{lightBluePlace p3{place p4{}}}redPlace p5{}}");
    Assertions.assertTrue(optAST.isPresent());
    final ASTPlace ast = optAST.get();

    IBlueCTypeDispatcher dispatcher = BlueCMill.typeDispatcher();

    Assertions.assertEquals("place", printType(dispatcher.asBlueAASTPlace(ast)));

    Assertions.assertEquals("place", printType(dispatcher.asBlueAASTPlace(ast.getPlace(0))));
    Assertions.assertEquals("bluePlace", printType(dispatcher.asBlueBASTBluePlace(ast.getPlace(0))));

    Assertions.assertEquals("place", printType(dispatcher.asBlueAASTPlace(ast.getPlace(1))));
    Assertions.assertEquals("redPlace", printType(dispatcher.asBlueBASTRedPlace(ast.getPlace(1))));

    Assertions.assertEquals("place", printType(dispatcher.asBlueAASTPlace(ast.getPlace(0).getPlace(0))));
    Assertions.assertEquals("bluePlace", printType(dispatcher.asBlueBASTBluePlace(ast.getPlace(0).getPlace(0))));
    Assertions.assertEquals("lightBluePlace", printType(dispatcher.asBlueCASTLightBluePlace(ast.getPlace(0).getPlace(0))));

    Assertions.assertEquals("place", printType(dispatcher.asBlueAASTPlace(ast.getPlace(0).getPlace(0).getPlace(0))));
  }

  @AfterEach
  public void after() {
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
