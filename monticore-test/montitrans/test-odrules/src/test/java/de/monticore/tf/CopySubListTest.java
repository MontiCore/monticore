/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.misc.MiscMill;
import mc.testcases.misc._ast.ASTDef;
import mc.testcases.misc._ast.ASTSub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CopySubListTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  ASTDef rootdef;
  ASTDef def1;
  ASTDef def2;
  ASTSub sub;

  @Before
  public void setUp()  {
    rootdef = MiscMill.defBuilder().uncheckedBuild();
    def1 = MiscMill.defBuilder().uncheckedBuild();
    def2 = MiscMill.defBuilder().uncheckedBuild();
    sub = MiscMill.subBuilder().uncheckedBuild();
    rootdef.addDef(def1);
    rootdef.addDef(def2);
    def1.setSub(sub);
    def1.setName("a");
    def2.setName("b");
  }

  @Test
  public void testDoReplacement() {
    CopySubList testee = new CopySubList(rootdef);
    assertTrue(testee.doPatternMatching());
    testee.doReplacement();

    assertTrue(def1.isPresentSub());
    assertTrue(def2.isPresentSub());

    testee.undoReplacement();

    assertTrue(def1.isPresentSub());
    assertFalse(def2.isPresentSub());
  
    assertTrue(Log.getFindings().isEmpty());
  }


}
