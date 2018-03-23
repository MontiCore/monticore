/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package mc.feature.inheritence;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.inheritence.sup.sub.subgrammar._ast.ASTB;
import mc.feature.inheritence.sup.sub.subgrammar._ast.SubGrammarMill;
import mc.feature.inheritence.sup.supergrammar._ast.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class InheritanceBuilderTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testBuildX() {
  
    ASTJ j = SuperGrammarMill.jBuilder().build();
    ASTY y = SuperGrammarMill.yBuilder().build();
    
    ASTX x = SuperGrammarMill.xBuilder().setJ(j).setY(y).build();
    
    try {
      SuperGrammarMill.xBuilder().build();
      fail("invalid ASTX could be build");
    } catch (IllegalStateException e) {
      assertEquals(1, Log.getFindings().size());
      Log.getFindings().clear();
    }
  }
  
  @Test
  public void testBuildB() {
    ASTB b = SubGrammarMill.bBuilder().setD(SubGrammarMill.dBuilder().build()).build();
    
    try {
      SubGrammarMill.bBuilder().build();
      fail("invalid ASTB could be build");
    } catch (IllegalStateException e) {
      assertEquals(1, Log.getFindings().size());
      Log.getFindings().clear();
    }
  }
}
