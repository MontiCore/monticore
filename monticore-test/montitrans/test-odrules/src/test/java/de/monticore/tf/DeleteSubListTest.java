/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.misc.MiscMill;
import mc.testcases.misc._ast.ASTDef;
import mc.testcases.misc._ast.ASTSub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteSubListTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  ASTDef def;
  ASTSub sub;

  @BeforeEach
  public void setUp()  {
    def = MiscMill.defBuilder().uncheckedBuild();
    sub = MiscMill.subBuilder().uncheckedBuild();
    def.setSub(sub);
  }

  @Test
  public void testDoReplacement() {
    DeleteSubList testee = new DeleteSubList(def);
    assertTrue(testee.doPatternMatching());
    testee.doReplacement();

    assertFalse(def.isPresentSub());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testUndoReplacement() {
    ASTDef def_before = def.deepClone();
    DeleteSubList testee = new DeleteSubList(def);
    testee.doAll();
    testee.undoReplacement();

    assertSame(sub, def.getSub());
    assertTrue(def_before.deepEquals(def));
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
