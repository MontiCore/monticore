/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import mc.testcases.misc.MiscMill;
import mc.testcases.misc._ast.ASTDef;
import mc.testcases.misc._ast.ASTSub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

public class MoveSubTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  ASTDef oldParent, newParent;
  ASTSub child;

  @Before
  public void setUp() {
    oldParent = MiscMill.defBuilder().uncheckedBuild();
    newParent = MiscMill.defBuilder().uncheckedBuild();
    child = MiscMill.subBuilder().uncheckedBuild();
    oldParent.setSub(child);
  }

  @Test
  public void testDoReplacment() {
    List<ASTNode> parents = new ArrayList<ASTNode>();
    parents.add(oldParent);
    parents.add(newParent);
    MoveSub testee = new MoveSub(parents);
    testee.doPatternMatching();
    testee.doReplacement();

    assertFalse(oldParent.isPresentSub());
    assertSame(child, newParent.getSub());
  }



}
