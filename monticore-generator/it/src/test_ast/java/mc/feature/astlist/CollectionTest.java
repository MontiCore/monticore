/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package mc.feature.astlist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.monticore.ast.Comment;
import mc.feature.list.lists._ast.ASTParent;
import mc.feature.list.lists._ast.ASTSon;
import mc.feature.list.lists._ast.ListsNodeFactory;

public class CollectionTest {
      
  @Test
  public void testDeepEquals1() {
    ASTParent p1 = ListsNodeFactory.createASTParent();
    ASTParent p2 = ListsNodeFactory.createASTParent();

    ASTSon s1 = ListsNodeFactory.createASTSon();
    ASTSon s2 = ListsNodeFactory.createASTSon();
    ASTSon s3 = ListsNodeFactory.createASTSon();
    ASTSon s4 = ListsNodeFactory.createASTSon();
    
    p1.getSons().add(s1);
    p1.getSons().add(s2);
    p2.getSons().add(s3);
    p2.getSons().add(s4);
    
    assertTrue(p1.deepEquals(p1, true));
    assertTrue(p2.deepEquals(p2, true));
    assertTrue(p1.deepEquals(p2, true));
    assertTrue(p2.deepEquals(p1, true));
    
    p1.getSons().remove(s1);
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
    
    p1.getSons().add(s1);
    p1.getSons().add(s2);
    p2.getSons().add(s3);
    p2.getSons().add(s4);
    
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
    
    p1.getSons().add(s1);
    p1.getSons().add(s2);
    p2.getSons().add(s3);
    p2.getSons().add(s4);
    
    assertTrue(p1.deepEqualsWithComments(p1));
    assertTrue(p2.deepEqualsWithComments(p2));
    assertTrue(p1.deepEqualsWithComments(p2));
    assertTrue(p2.deepEqualsWithComments(p1));
    
    p1.getSons().remove(s1);
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
    
    p1.getSons().add(s1);
    p1.getSons().add(s2);
    p2.getSons().add(s3);
    p2.getSons().add(s4);
    
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
    
    p1.getSons().add(s1);
    p1.getSons().add(s2);
    p2.getSons().add(s3);
    p2.getSons().add(s4);
    
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
    
    p1.getSons().add(s1);
    p1.getSons().add(s2);
    
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
    
    p1.getSons().add(s1);
    p1.getSons().add(s2);
    
    ASTParent p2 = p1.deepClone();
    
    assertTrue(p1.deepEquals(p2));
  }
  
}
