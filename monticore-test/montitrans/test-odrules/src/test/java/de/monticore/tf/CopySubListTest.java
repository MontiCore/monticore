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
public class CopySubListTest {

  ASTDef rootdef;
  ASTDef def1;
  ASTDef def2;
  ASTSub sub;

  @BeforeEach
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
  }
}
