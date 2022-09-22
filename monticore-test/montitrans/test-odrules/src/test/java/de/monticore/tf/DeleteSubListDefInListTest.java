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

import static org.junit.Assert.*;

public class DeleteSubListDefInListTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  ASTDef def;
  ASTDef def2;
  ASTSub sub;
  ASTSub sub2;

  @Before
  public void setUp()  {
    def = MiscMill.defBuilder().uncheckedBuild();
    def2 = MiscMill.defBuilder().uncheckedBuild();
    sub = MiscMill.subBuilder().uncheckedBuild();
    sub2 = MiscMill.subBuilder().uncheckedBuild();
    def.setSub(sub);
    def.addDef(def2);
    def2.setSub(sub2);
  }

  @Test
  public void testDoAll() {
    DeleteSubListDefInList testee = new DeleteSubListDefInList(def);
    assertTrue(testee.doPatternMatching());
    testee.doReplacement();

    assertFalse(def.isPresentSub());
    assertFalse(def2.isPresentSub());
    testee.undoReplacement();
    assertTrue(def.isPresentSub());
    assertTrue(def2.isPresentSub());
  
    assertTrue(Log.getFindings().isEmpty());
  }


}
