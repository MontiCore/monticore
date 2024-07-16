/* (c) https://github.com/MontiCore/monticore */

package mc.feature.astlist;

import de.monticore.ast.Comment;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.list.lists.ListsMill;
import mc.feature.list.lists._ast.ASTParent;
import mc.feature.list.lists._ast.ASTSon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollectionTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDeepEquals1() {
    ASTParent p1 = ListsMill.parentBuilder().uncheckedBuild();
    ASTParent p2 = ListsMill.parentBuilder().uncheckedBuild();

    ASTSon s1 = ListsMill.sonBuilder().uncheckedBuild();
    ASTSon s2 = ListsMill.sonBuilder().uncheckedBuild();
    ASTSon s3 = ListsMill.sonBuilder().uncheckedBuild();
    ASTSon s4 = ListsMill.sonBuilder().uncheckedBuild();
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    p2.getSonsList().add(s3);
    p2.getSonsList().add(s4);
    
    Assertions.assertTrue(p1.deepEquals(p1, true));
    Assertions.assertTrue(p2.deepEquals(p2, true));
    Assertions.assertTrue(p1.deepEquals(p2, true));
    Assertions.assertTrue(p2.deepEquals(p1, true));
    
    p1.getSonsList().remove(s1);
    Assertions.assertFalse(p1.deepEquals(p2, true));
    Assertions.assertFalse(p2.deepEquals(p1, true));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testDeepEquals2() {
    ASTParent p1 = ListsMill.parentBuilder().uncheckedBuild();
    ASTParent p2 = ListsMill.parentBuilder().uncheckedBuild();
    
    ASTSon s1 = ListsMill.sonBuilder().uncheckedBuild();
    s1.setName("foo");
    ASTSon s2 = ListsMill.sonBuilder().uncheckedBuild();
    s2.setName("bar");
    ASTSon s3 = ListsMill.sonBuilder().uncheckedBuild();
    s3.setName("bar");
    ASTSon s4 = ListsMill.sonBuilder().uncheckedBuild();
    s4.setName("foo");
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    p2.getSonsList().add(s3);
    p2.getSonsList().add(s4);
    
    Assertions.assertTrue(p1.deepEquals(p1));
    Assertions.assertFalse(p1.deepEquals(p2));
    Assertions.assertTrue(p1.deepEquals(p2, false));
    Assertions.assertTrue(p2.deepEquals(p1, false));
    Assertions.assertFalse(p1.deepEquals(p2, true));
    Assertions.assertFalse(p2.deepEquals(p1, true));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testDeepEqualsWithComments1() {
    ASTParent p1 = ListsMill.parentBuilder().uncheckedBuild();
    ASTParent p2 = ListsMill.parentBuilder().uncheckedBuild();
    
    ASTSon s1 = ListsMill.sonBuilder().uncheckedBuild();
    ASTSon s2 = ListsMill.sonBuilder().uncheckedBuild();
    ASTSon s3 = ListsMill.sonBuilder().uncheckedBuild();
    ASTSon s4 = ListsMill.sonBuilder().uncheckedBuild();
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    p2.getSonsList().add(s3);
    p2.getSonsList().add(s4);
    
    Assertions.assertTrue(p1.deepEqualsWithComments(p1));
    Assertions.assertTrue(p2.deepEqualsWithComments(p2));
    Assertions.assertTrue(p1.deepEqualsWithComments(p2));
    Assertions.assertTrue(p2.deepEqualsWithComments(p1));
    
    p1.getSonsList().remove(s1);
    Assertions.assertFalse(p1.deepEqualsWithComments(p2));
    Assertions.assertFalse(p2.deepEqualsWithComments(p1));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testDeepEqualsWithComments2() {
    ASTParent p1 = ListsMill.parentBuilder().uncheckedBuild();
    ASTParent p2 = ListsMill.parentBuilder().uncheckedBuild();
    
    ASTSon s1 = ListsMill.sonBuilder().uncheckedBuild();
    s1.setName("foo");
    ASTSon s2 = ListsMill.sonBuilder().uncheckedBuild();
    s2.setName("bar");
    ASTSon s3 = ListsMill.sonBuilder().uncheckedBuild();
    s3.setName("bar");
    ASTSon s4 = ListsMill.sonBuilder().uncheckedBuild();
    s4.setName("foo");
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    p2.getSonsList().add(s3);
    p2.getSonsList().add(s4);
    
    Assertions.assertFalse(p1.deepEqualsWithComments(p2));
    Assertions.assertFalse(p2.deepEqualsWithComments(p1));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void deepEqualsWithComments3() {
    ASTParent p1 = ListsMill.parentBuilder().uncheckedBuild();
    ASTParent p2 = ListsMill.parentBuilder().uncheckedBuild();
    
    ASTSon s1 = ListsMill.sonBuilder().uncheckedBuild();
    Comment c1 = new Comment();
    c1.setText("mycomment");
    s1.get_PreCommentList().add(c1);
    ASTSon s2 = ListsMill.sonBuilder().uncheckedBuild();
    ASTSon s3 = ListsMill.sonBuilder().uncheckedBuild();
    Comment c2 = new Comment();
    c2.setText("mycomment");
    s3.get_PreCommentList().add(c2);
    ASTSon s4 = ListsMill.sonBuilder().uncheckedBuild();
    
    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    p2.getSonsList().add(s3);
    p2.getSonsList().add(s4);
    
    Assertions.assertTrue(p1.deepEqualsWithComments(p2));
    Assertions.assertTrue(p2.deepEqualsWithComments(p1));
    
    c1.setText("different comment");
    
    Assertions.assertFalse(p1.deepEqualsWithComments(p2));
    Assertions.assertFalse(p2.deepEqualsWithComments(p1));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testDeepClone() {
    ASTParent p1 = ListsMill.parentBuilder().uncheckedBuild();
    
    ASTSon s1 = ListsMill.sonBuilder().uncheckedBuild();
    s1.setName("myname1");
    Comment c1 = new Comment();
    c1.setText("mycomment");
    s1.get_PreCommentList().add(c1);
    ASTSon s2 = ListsMill.sonBuilder().uncheckedBuild();
    s1.setName("myname2");

    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    
    ASTParent p2 = p1.deepClone();
    
    Assertions.assertTrue(p1.deepEqualsWithComments(p2));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testClone() {
    ASTParent p1 = ListsMill.parentBuilder().uncheckedBuild();
    
    ASTSon s1 = ListsMill.sonBuilder().uncheckedBuild();
    s1.setName("myname1");

    Comment c1 = new Comment();
    c1.setText("mycomment");
    s1.get_PreCommentList().add(c1);
    ASTSon s2 = ListsMill.sonBuilder().uncheckedBuild();
    s1.setName("myname1");

    p1.getSonsList().add(s1);
    p1.getSonsList().add(s2);
    
    ASTParent p2 = p1.deepClone();
    
    Assertions.assertTrue(p1.deepEquals(p2));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
