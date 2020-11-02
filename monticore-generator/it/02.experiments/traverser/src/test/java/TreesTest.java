/* (c) https://github.com/MontiCore/monticore */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import subtrees.SubTreesMill;
import subtrees._parser.SubTreesParser;
import tree.visitor.LeafCounter;
import trees.TreesMill;
import trees._ast.ASTRoot;
import trees._parser.TreesParser;
import trees._visitor.ITreesTraverser;

public class TreesTest {
  
  @Test
  public void testTrees() throws IOException {
    TreesMill.init();
    String model = "src/test/resources/trees/Leafs.tree";
    TreesParser parser = new TreesParser();
    
    Optional<ASTRoot> tree = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(tree.isPresent());
    
    // compute and check result
    assertEquals(3, leafCount(tree.get()));
  }
  
  @Test
  public void testBranches() throws IOException {
    SubTreesMill.init();
    String model = "src/test/resources/trees/Branches.tree";
    SubTreesParser parser = new SubTreesParser();
    
    Optional<ASTRoot> tree = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(tree.isPresent());
    
    // compute and check result
    assertEquals(6, leafCount(tree.get()));
  }
  
  /**
   * Computes the number of leaf nodes in the given tree.
   * 
   * @param tree The root node of the tree
   * @return The number of leaf nodes
   */
  protected int leafCount(ASTRoot tree) {
    // create traverser using mill
    ITreesTraverser traverser = TreesMill.traverser();
    LeafCounter counter = new LeafCounter();
    traverser.setTreesVisitor(counter);
    
    // compute
    tree.accept(traverser);
    return counter.getCount();
  }
  
}
