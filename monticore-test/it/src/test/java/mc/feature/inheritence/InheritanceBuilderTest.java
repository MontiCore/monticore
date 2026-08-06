/* (c) https://github.com/MontiCore/monticore */
package mc.feature.inheritence;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.inheritence.sup.sub.subgrammar.SubGrammarMill;
import mc.feature.inheritence.sup.sub.subgrammar._ast.ASTBBuilder;
import mc.feature.inheritence.sup.sub.subgrammar._ast.ASTD;
import mc.feature.inheritence.sup.supergrammar.SuperGrammarMill;
import mc.feature.inheritence.sup.supergrammar._ast.ASTJ;
import mc.feature.inheritence.sup.supergrammar._ast.ASTXBuilder;
import mc.feature.inheritence.sup.supergrammar._ast.ASTY;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(SuperGrammarMill.class)
public class InheritanceBuilderTest {

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
      MCAssertions.assertHasFindingStartingWith(
          "0xA4522 y of type mc.feature.inheritence.sup.supergrammar._ast.ASTY must not be null");
      MCAssertions.assertHasFindingStartingWith(
          "0xA4522 j of type mc.feature.inheritence.sup.supergrammar._ast.ASTJ must not be null");
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
      MCAssertions.assertHasFindingStartingWith(
          "0xA4522 d of type mc.feature.inheritence.sup.sub.subgrammar._ast.ASTD must not be null");
    }
  }
}
