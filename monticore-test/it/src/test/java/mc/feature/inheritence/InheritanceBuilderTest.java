/* (c) https://github.com/MontiCore/monticore */
package mc.feature.inheritence;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.inheritence.sup.sub.subgrammar.SubGrammarMill;
import mc.feature.inheritence.sup.sub.subgrammar._ast.ASTBBuilder;
import mc.feature.inheritence.sup.sub.subgrammar._ast.ASTD;
import mc.feature.inheritence.sup.supergrammar.SuperGrammarMill;
import mc.feature.inheritence.sup.supergrammar._ast.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InheritanceBuilderTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testBuildX() {
  
    ASTJ j = SuperGrammarMill.jBuilder().build();
    ASTY y = SuperGrammarMill.yBuilder().build();
    
    ASTXBuilder x = SuperGrammarMill.xBuilder();
    Assertions.assertFalse(x.isValid());
  
    x.setJ(j).setY(y);
    Assertions.assertTrue(x.isValid());
    
    try {
      SuperGrammarMill.xBuilder().build();
      Assertions.fail("invalid ASTX could be build");
    } catch (IllegalStateException e) {
      Assertions.assertEquals(2, Log.getFindings().size());
      Log.getFindings().clear();
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBuildB() {
    ASTD d = SubGrammarMill.dBuilder().build();
    ASTBBuilder b = SubGrammarMill.bBuilder();
    Assertions.assertFalse(b.isValid());
    
    b.setD(d);
    Assertions.assertTrue(b.isValid());
    
    try {
      SubGrammarMill.bBuilder().build();
      Assertions.fail("invalid ASTB could be build");
    } catch (IllegalStateException e) {
      Assertions.assertEquals(1, Log.getFindings().size());
      Log.getFindings().clear();
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
