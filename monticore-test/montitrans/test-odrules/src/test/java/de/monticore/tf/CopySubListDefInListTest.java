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
public class CopySubListDefInListTest {
  
  ASTDef def, def2, def3, def4;
  ASTSub sub, sub2;

  @BeforeEach
  public void setUp()  {
    def = MiscMill.defBuilder().uncheckedBuild();
    def2 = MiscMill.defBuilder().uncheckedBuild();
    def3 = MiscMill.defBuilder().uncheckedBuild();
    def4 = MiscMill.defBuilder().uncheckedBuild();
    sub = MiscMill.subBuilder().uncheckedBuild();
    sub2 = MiscMill.subBuilder().uncheckedBuild();
    def.setName("a");
    def.setSub(sub);
    def.addDef(def2);
    def.addDef(def3);
    def.addDef(def4);
    def2.setName("a");
    def2.setSub(sub2);
    def3.setName("b");
    def4.setName("b");
  }

  @Test
  public void testDoAll() {
    CopySubListDefInList testee = new CopySubListDefInList(def);
    assertTrue(testee.doPatternMatching());
    testee.doReplacement();

    assertTrue(def.isPresentSub());
    assertTrue(def2.isPresentSub());
    assertTrue(def3.isPresentSub());
    assertTrue(def4.isPresentSub());
    testee.undoReplacement();
    assertTrue(def.isPresentSub());
    assertTrue(def2.isPresentSub());
    assertFalse(def3.isPresentSub());
    assertFalse(def4.isPresentSub());
  }
}
