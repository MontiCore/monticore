/* (c) https://github.com/MontiCore/monticore */

package mc.feature.cocochecker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.cocochecker.a._ast.ASTANode;
import mc.feature.cocochecker.a._ast.ASTX;
import mc.feature.cocochecker.a._cocos.AASTXCoCo;
import mc.feature.cocochecker.a._cocos.ACoCoChecker;
import mc.feature.cocochecker.a._parser.AParser;
import mc.feature.cocochecker.b._cocos.BASTXCoCo;
import mc.feature.cocochecker.b._cocos.BASTYCoCo;
import mc.feature.cocochecker.b._cocos.BCoCoChecker;
import mc.feature.cocochecker.c._cocos.CASTXCoCo;
import mc.feature.cocochecker.c._cocos.CASTZCoCo;
import mc.feature.cocochecker.c._cocos.CCoCoChecker;

/**
 * Tests adding cocos of super languages to a checker of a sublanguage.<br/>
 * <br/>
 * X is defined in C and B. A extends C and B and overrides X, so CoCos defined
 * for X are only checked if they are defined for the X from language A and its
 * supertypes (hence, also the cocos added for X from C are checked).<br/>
 * In contrast, the cocos defined for Y (from B) and Z (from C) must be checked!<br/>
 * <br/>
 * The tests in this test suite ensure that this happens for composing all the
 * cocos within the checker of A as well as for composing the cocos in their
 * language's checkers and then composing the checkers in A's checker.
 * 
 * @author Robert Heim
 */
public class CoCoCheckerTest extends GeneratorIntegrationsTest {
  final StringBuilder checked = new StringBuilder();
  
  final private AASTXCoCo cocoA = new AASTXCoCo() {
    @Override
    public void check(ASTX node) {
      checked.append("A");
    }
  };
  
  final private BASTXCoCo cocoB = new BASTXCoCo() {
    @Override
    public void check(mc.feature.cocochecker.b._ast.ASTX node) {
      checked.append("B");
    }
  };
  
  final private BASTYCoCo cocoY = new BASTYCoCo() {
    @Override
    public void check(mc.feature.cocochecker.b._ast.ASTY node) {
      checked.append("Y");
    }
  };
  
  final private CASTXCoCo cocoC = new CASTXCoCo() {
    @Override
    public void check(mc.feature.cocochecker.c._ast.ASTX node) {
      checked.append("C");
    }
  };
  
  final private CASTZCoCo cocoZ = new CASTZCoCo() {
    @Override
    public void check(mc.feature.cocochecker.c._ast.ASTZ node) {
      checked.append("Z");
    }
  };
  
  // the ast used for testing.
  private ASTANode ast;
  
  @Before
  public void setUp() {
    Optional<ASTX> astOpt = Optional.empty();
    try {
      astOpt = new AParser().parseX(new StringReader("xyz"));
    }
    catch (IOException e) {
      e.printStackTrace();
      fail("Parser Error.");
    }
    assertTrue(astOpt.isPresent());
    ast = astOpt.get();
    checked.setLength(0);
  }
  
  @Test
  public void testCoCoComposition() {
    ACoCoChecker checker = new ACoCoChecker();
    checker.addCoCo(cocoA);
    checker.addCoCo(cocoB);
    checker.addCoCo(cocoY);
    checker.addCoCo(cocoC);
    checker.addCoCo(cocoZ);
    
    checker.checkAll(ast);
    assertEquals("BAYZ", checked.toString());
  }
  
  @Test
  public void testCheckerComposition() {
    
    ACoCoChecker checkerA = new ACoCoChecker();
    checkerA.addCoCo(cocoA);
    
    BCoCoChecker checkerB = new BCoCoChecker();
    checkerB.addCoCo(cocoB);
    checkerB.addCoCo(cocoY);
    
    CCoCoChecker checkerC = new CCoCoChecker();
    checkerC.addCoCo(cocoC);
    checkerC.addCoCo(cocoZ);
    
    // compose existing checkers
    checkerA.addChecker(checkerB);
    checkerA.addChecker(checkerC);
    
    checkerA.checkAll(ast);
    assertEquals("BAYZ", checked.toString());
    
  }
}
