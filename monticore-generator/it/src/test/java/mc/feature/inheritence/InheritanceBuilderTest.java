/* (c) https://github.com/MontiCore/monticore */
package mc.feature.inheritence;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.inheritence.sup.sub.subgrammar.SubGrammarMill;
import mc.feature.inheritence.sup.sub.subgrammar._ast.ASTB;
import mc.feature.inheritence.sup.sub.subgrammar._ast.ASTBBuilder;
import mc.feature.inheritence.sup.sub.subgrammar._ast.ASTD;
import mc.feature.inheritence.sup.supergrammar.SuperGrammarMill;
import mc.feature.inheritence.sup.supergrammar._ast.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class InheritanceBuilderTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testBuildX() {
  
    ASTJ j = SuperGrammarMill.jBuilder().build();
    ASTY y = SuperGrammarMill.yBuilder().build();
    
    ASTXBuilder x = SuperGrammarMill.xBuilder();
    assertFalse(x.isValid());
  
    x.setJ(j).setY(y);
    assertTrue(x.isValid());
    
    try {
      SuperGrammarMill.xBuilder().build();
      fail("invalid ASTX could be build");
    } catch (IllegalStateException e) {
      assertEquals(2, Log.getFindings().size());
      Log.getFindings().clear();
    }
  }
  
  @Test
  public void testBuildB() {
    ASTD d = SubGrammarMill.dBuilder().build();
    ASTBBuilder b = SubGrammarMill.bBuilder();
    assertFalse(b.isValid());
    
    b.setD(d);
    assertTrue(b.isValid());
    
    try {
      SubGrammarMill.bBuilder().build();
      fail("invalid ASTB could be build");
    } catch (IllegalStateException e) {
      assertEquals(1, Log.getFindings().size());
      Log.getFindings().clear();
    }
  }
}
