package mc.feature.delete._ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import mc.DSLRoot;
import mc.StandardInfrastructureProvider;
import mc.feature.delete.DeleteTestTool;
import mc.feature.delete._tool.DeleteTestRootFactory;

import org.junit.Before;
import org.junit.Test;

import mc.antlr.RecognitionException;
import mc.antlr.TokenStreamException;

public class ASTNodeDeletionEMFTest {
  
  DSLRoot<ASTB> root;
  
  @Before
  public void setUp() throws FileNotFoundException, RecognitionException, TokenStreamException, IOException {
    DeleteTestRootFactory factory = new DeleteTestRootFactory(new StandardInfrastructureProvider(new DeleteTestTool()), null, null);
    root = factory.create(new StringReader(""), "reader without proper input for test purposes");
  }
  
  @Test
  public void testRemove_Child() {
    // setup test data
    ASTParent p = new ASTParent();
    ASTChild c1 = new ASTChild();
    ASTChild c2 = new ASTChild();
    p.setSon(c1);
    p.getChildList().add(c2);
    
    // run testee
    p.remove_Child(c1);
    p.getChildList().remove_Child(c2);
    
    // check result
    assertNull(p.getSon());
    assertTrue(p.getChildList().isEmpty());
    
  }
  
  @Test
  public void testDelete_NonNavigableSingle() {
    // setup test data
    ASTB b = new ASTB();
    b.set_root(root);
    ASTC c = new ASTC();
    c.set_root(root);
    b.setC(c);
    mc.ast.MCAssociation bc = b.get_root().getAssociation("mc.feature.delete.DeleteTest.BC");
    
    // run testee
    b.delete();
    
    // check result
    assertFalse(bc.contains(b, c));
  }
  
  @Test
  public void testDelete_NonNavigableMulti() {
    // setup test data
    ASTA a = new ASTA();
    a.set_root(root);
    ASTB b = new ASTB();
    b.set_root(root);
    a.addB(b);
    mc.ast.MCAssociation ab = b.get_root().getAssociation("mc.feature.delete.DeleteTest.AB");
    
    // run testee
    a.delete();
    
    // check result
    assertFalse(ab.contains(a, b));
  }
  
  /**
   * tests whether the invocation of the delete method removes links to ancestor
   * nodes
   */
  @Test
  public void testDelete_Recursion() {
    // setup test data
    ASTParent p = new ASTParent();
    p.set_root(root);
    ASTA a = new ASTA();
    a.set_root(root);
    p.setA(a);
    ASTB b = new ASTB();
    b.set_root(root);
    a.addB(b);
    
    // run testee
    p.delete();
    
    // check result
    assertTrue(a.getB().isEmpty());
  }
  
}
