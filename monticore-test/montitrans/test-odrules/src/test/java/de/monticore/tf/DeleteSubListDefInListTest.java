/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.misc.MiscMill;
import mc.testcases.misc._ast.ASTDef;
import mc.testcases.misc._ast.ASTSub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(MiscMill.class)
public class DeleteSubListDefInListTest {

  ASTDef def;
  ASTDef def2;
  ASTSub sub;
  ASTSub sub2;

  @BeforeEach
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
  }
}
