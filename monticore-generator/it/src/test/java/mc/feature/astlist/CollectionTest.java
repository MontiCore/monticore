/* (c) https://github.com/MontiCore/monticore */

package mc.feature.astlist;

import de.monticore.ast.Comment;
import mc.feature.list.lists._ast.ASTParent;
import mc.feature.list.lists._ast.ASTSon;
import mc.feature.list.lists._ast.ListsNodeFactory;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollectionTest {
      
  @Test
  public void testDeepEquals1() {
    ASTParent p1 = ListsNodeFactory.createASTParent();
    ASTParent p2 = ListsNodeFactory.createASTParent();

    ASTSon s1 = ListsNodeFactory.createASTSon();
    ASTSon s2 = ListsNodeFactory.createASTSon();
    ASTSon s3 = ListsNodeFactory.createASTSon();
    ASTSon s4 = ListsNodeFactory.createASTSon();
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    p2.getSonsList().add(s3);
    p2.getSonsList().add(s4);
    
    assertTrue(p1.deepEquals(p1, true));
    assertTrue(p2.deepEquals(p2, true));
    assertTrue(p1.deepEquals(p2, true));
    assertTrue(p2.deepEquals(p1, true));
    
    p1.getSonsList().remove(s1);
    assertFalse(p1.deepEquals(p2, true));
    assertFalse(p2.deepEquals(p1, true));
  }
  
  @Test
  public void testDeepEquals2() {
    ASTParent p1 = ListsNodeFactory.createASTParent();
    ASTParent p2 = ListsNodeFactory.createASTParent();
    
    ASTSon s1 = ListsNodeFactory.createASTSon();
    s1.setName("foo");
    ASTSon s2 = ListsNodeFactory.createASTSon();
    s2.setName("bar");
    ASTSon s3 = ListsNodeFactory.createASTSon();
    s3.setName("bar");
    ASTSon s4 = ListsNodeFactory.createASTSon();
    s4.setName("foo");
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    p2.getSonsList().add(s3);
    p2.getSonsList().add(s4);
    
    assertTrue(p1.deepEquals(p1));
    assertFalse(p1.deepEquals(p2));
    assertTrue(p1.deepEquals(p2, false));
    assertTrue(p2.deepEquals(p1, false));
    assertFalse(p1.deepEquals(p2, true));
    assertFalse(p2.deepEquals(p1, true));
  }
  
  @Test
  public void testDeepEqualsWithComments1() {
    ASTParent p1 = ListsNodeFactory.createASTParent();
    ASTParent p2 = ListsNodeFactory.createASTParent();
    
    ASTSon s1 = ListsNodeFactory.createASTSon();
    ASTSon s2 = ListsNodeFactory.createASTSon();
    ASTSon s3 = ListsNodeFactory.createASTSon();
    ASTSon s4 = ListsNodeFactory.createASTSon();
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    p2.getSonsList().add(s3);
    p2.getSonsList().add(s4);
    
    assertTrue(p1.deepEqualsWithComments(p1));
    assertTrue(p2.deepEqualsWithComments(p2));
    assertTrue(p1.deepEqualsWithComments(p2));
    assertTrue(p2.deepEqualsWithComments(p1));
    
    p1.getSonsList().remove(s1);
    assertFalse(p1.deepEqualsWithComments(p2));
    assertFalse(p2.deepEqualsWithComments(p1));
  }
  
  @Test
  public void testDeepEqualsWithComments2() {
    ASTParent p1 = ListsNodeFactory.createASTParent();
    ASTParent p2 = ListsNodeFactory.createASTParent();
    
    ASTSon s1 = ListsNodeFactory.createASTSon();
    s1.setName("foo");
    ASTSon s2 = ListsNodeFactory.createASTSon();
    s2.setName("bar");
    ASTSon s3 = ListsNodeFactory.createASTSon();
    s3.setName("bar");
    ASTSon s4 = ListsNodeFactory.createASTSon();
    s4.setName("foo");
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    p2.getSonsList().add(s3);
    p2.getSonsList().add(s4);
    
    assertFalse(p1.deepEqualsWithComments(p2));
    assertFalse(p2.deepEqualsWithComments(p1));
  }
  
  @Test
  public void deepEqualsWithComments3() {
    ASTParent p1 = ListsNodeFactory.createASTParent();
    ASTParent p2 = ListsNodeFactory.createASTParent();
    
    ASTSon s1 = ListsNodeFactory.createASTSon();
    Comment c1 = new Comment();
    c1.setText("mycomment");
    s1.get_PreComments().add(c1);
    ASTSon s2 = ListsNodeFactory.createASTSon();
    ASTSon s3 = ListsNodeFactory.createASTSon();
    Comment c2 = new Comment();
    c2.setText("mycomment");
    s3.get_PreComments().add(c2);
    ASTSon s4 = ListsNodeFactory.createASTSon();
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    p2.getSonsList().add(s3);
    p2.getSonsList().add(s4);
    
    assertTrue(p1.deepEqualsWithComments(p2));
    assertTrue(p2.deepEqualsWithComments(p1));
    
    c1.setText("different comment");
    
    assertFalse(p1.deepEqualsWithComments(p2));
    assertFalse(p2.deepEqualsWithComments(p1));
  }
  
  @Test
  public void testDeepClone() {
    ASTParent p1 = ListsNodeFactory.createASTParent();
    
    ASTSon s1 = ListsNodeFactory.createASTSon("myname1");
    Comment c1 = new Comment();
    c1.setText("mycomment");
    s1.get_PreComments().add(c1);
    ASTSon s2 = ListsNodeFactory.createASTSon("myname2");
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    
    ASTParent p2 = p1.deepClone();
    
    assertTrue(p1.deepEqualsWithComments(p2));
  }
  
  @Test
  public void testClone() {
    ASTParent p1 = ListsNodeFactory.createASTParent();
    
    ASTSon s1 = ListsNodeFactory.createASTSon("myname1");
    Comment c1 = new Comment();
    c1.setText("mycomment");
    s1.get_PreComments().add(c1);
    ASTSon s2 = ListsNodeFactory.createASTSon("myname1");
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    
    ASTParent p2 = p1.deepClone();
    
    assertTrue(p1.deepEquals(p2));
  }
  
}
