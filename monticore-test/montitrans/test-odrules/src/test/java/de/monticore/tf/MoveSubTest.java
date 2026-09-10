/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.misc.MiscMill;
import mc.testcases.misc._ast.ASTDef;
import mc.testcases.misc._ast.ASTSub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(MiscMill.class)
public class MoveSubTest {

  ASTDef oldParent, newParent;
  ASTSub child;

  @BeforeEach
  public void setUp() {
    oldParent = MiscMill.defBuilder().uncheckedBuild();
    newParent = MiscMill.defBuilder().uncheckedBuild();
    child = MiscMill.subBuilder().uncheckedBuild();
    oldParent.setSub(child);
  }

  @Test
  public void testDoReplacement() {
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
