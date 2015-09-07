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

package mc.feature.astlist.unordered;

import junit.framework.TestCase;
import de.monticore.ast.Comment;
import mc.feature.list.lists._ast.ASTParent;
import mc.feature.list.lists._ast.ASTSon;
import mc.feature.list.lists._ast.ASTSonList;
import mc.feature.list.lists._ast.ListsNodeFactory;

public class CollectionTest extends TestCase {
  
  public void testAddRemove() {
    ASTParent p = ListsNodeFactory.createASTParent();
    assertEquals(0, p.getSons().size());
    
    ASTSon s1 = ListsNodeFactory.createASTSon();
    ASTSon s2 = ListsNodeFactory.createASTSon();
    
    p.getSons().add(s1);
    assertEquals(1, p.getSons().size());
    assertTrue(p.getSons().contains(s1));
    
    p.getSons().add(s2);
    assertEquals(2, p.getSons().size());
    assertTrue(p.getSons().contains(s2));
    
    p.getSons().remove(s1);
    assertEquals(1, p.getSons().size());
    assertFalse(p.getSons().contains(s1));
    assertTrue(p.getSons().contains(s2));
    
    p.getSons().remove(s2);
    assertTrue(p.getSons().isEmpty());
  }
  
  public void testEquals() {
    ASTSonList l1 = ListsNodeFactory.createASTSonList();
    ASTSonList l2 = ListsNodeFactory.createASTSonList();
    assertTrue(l1.equals(l2));
    assertTrue(l2.equals(l1));
  }
  
  public void testDeepEquals1() {
    ASTSonList l1 = ListsNodeFactory.createASTSonList();
    ASTSonList l2 = ListsNodeFactory.createASTSonList();
    
    ASTSon s1 = ListsNodeFactory.createASTSon();
    ASTSon s2 = ListsNodeFactory.createASTSon();
    ASTSon s3 = ListsNodeFactory.createASTSon();
    ASTSon s4 = ListsNodeFactory.createASTSon();
    
    l1.add(s1);
    l1.add(s2);
    l2.add(s3);
    l2.add(s4);
    
    assertTrue(l1.deepEquals(l1, true));
    assertTrue(l2.deepEquals(l2, true));
    assertTrue(l1.deepEquals(l2, true));
    assertTrue(l2.deepEquals(l1, true));
    
    l1.remove(s1);
    assertFalse(l1.deepEquals(l2, true));
    assertFalse(l2.deepEquals(l1, true));
  }
  
  public void testDeepEquals2() {
    ASTSonList l1 = ListsNodeFactory.createASTSonList();
    ASTSonList l2 = ListsNodeFactory.createASTSonList();
    
    ASTSon s1 = ListsNodeFactory.createASTSon();
    s1.setName("foo");
    ASTSon s2 = ListsNodeFactory.createASTSon();
    s2.setName("bar");
    ASTSon s3 = ListsNodeFactory.createASTSon();
    s3.setName("bar");
    ASTSon s4 = ListsNodeFactory.createASTSon();
    s4.setName("foo");
    
    l1.add(s1);
    l1.add(s2);
    l2.add(s3);
    l2.add(s4);
    
    assertTrue(l1.deepEquals(l1));
    assertFalse(l1.deepEquals(l2));
    assertTrue(l1.deepEquals(l2, false));
    assertTrue(l2.deepEquals(l1, false));
    assertFalse(l1.deepEquals(l2, true));
    assertFalse(l2.deepEquals(l1, true));
  }
  
  public void testDeepEqualsWithComments1() {
    ASTSonList l1 = ListsNodeFactory.createASTSonList();
    ASTSonList l2 = ListsNodeFactory.createASTSonList();
    
    ASTSon s1 = ListsNodeFactory.createASTSon();
    ASTSon s2 = ListsNodeFactory.createASTSon();
    ASTSon s3 = ListsNodeFactory.createASTSon();
    ASTSon s4 = ListsNodeFactory.createASTSon();
    
    l1.add(s1);
    l1.add(s2);
    l2.add(s3);
    l2.add(s4);
    
    assertTrue(l1.deepEqualsWithComments(l1));
    assertTrue(l2.deepEqualsWithComments(l2));
    assertTrue(l1.deepEqualsWithComments(l2));
    assertTrue(l2.deepEqualsWithComments(l1));
    
    l1.remove(s1);
    assertFalse(l1.deepEqualsWithComments(l2));
    assertFalse(l2.deepEqualsWithComments(l1));
  }
  
  public void testDeepEqualsWithComments2() {
    ASTSonList l1 = ListsNodeFactory.createASTSonList(true);
    ASTSonList l2 = ListsNodeFactory.createASTSonList(true);
    
    ASTSon s1 = ListsNodeFactory.createASTSon();
    s1.setName("foo");
    ASTSon s2 = ListsNodeFactory.createASTSon();
    s2.setName("bar");
    ASTSon s3 = ListsNodeFactory.createASTSon();
    s3.setName("bar");
    ASTSon s4 = ListsNodeFactory.createASTSon();
    s4.setName("foo");
    
    l1.add(s1);
    l1.add(s2);
    l2.add(s3);
    l2.add(s4);
    
    assertFalse(l1.deepEqualsWithComments(l2));
    assertFalse(l2.deepEqualsWithComments(l1));
  }
  
  public void deepEqualsWithComments3() {
    ASTSonList l1 = ListsNodeFactory.createASTSonList();
    ASTSonList l2 = ListsNodeFactory.createASTSonList();
    
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
    
    l1.add(s1);
    l1.add(s2);
    l2.add(s3);
    l2.add(s4);
    
    assertTrue(l1.deepEqualsWithComments(l2));
    assertTrue(l2.deepEqualsWithComments(l1));
    
    c1.setText("different comment");
    
    assertFalse(l1.deepEqualsWithComments(l2));
    assertFalse(l2.deepEqualsWithComments(l1));
  }
  
  public void testDeepClone() {
    ASTSonList l1 = ListsNodeFactory.createASTSonList();
    
    ASTSon s1 = ListsNodeFactory.createASTSon();
    Comment c1 = new Comment();
    c1.setText("mycomment");
    s1.get_PreComments().add(c1);
    ASTSon s2 = ListsNodeFactory.createASTSon();
    
    l1.add(s1);
    l1.add(s2);
    
    ASTSonList l2 = l1.deepClone();
    
    assertTrue(l1.deepEqualsWithComments(l2));
  }
  
  public void testClone() {
    ASTSonList l1 = ListsNodeFactory.createASTSonList();
    
    ASTSon s1 = ListsNodeFactory.createASTSon();
    Comment c1 = new Comment();
    c1.setText("mycomment");
    s1.get_PreComments().add(c1);
    ASTSon s2 = ListsNodeFactory.createASTSon();
    
    l1.add(s1);
    l1.add(s2);
    
    ASTSonList l2 = l1.clone();
    
    assertTrue(l1.equals(l2));
  }
}
