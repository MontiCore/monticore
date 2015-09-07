package mc.feature.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import junit.framework.TestCase;
import mc.ast.ParentNotNullException;
import mc.feature._ast.A;
import mc.feature._ast.ASTA;
import mc.feature._ast.ASTAList;
import mc.feature._ast.ASTB;
import mc.feature._ast.FeatureDSLPackage;
import mc.feature._ast.featureDSLNodeFactory;
import mc.feature.automaton._ast.AutomatonNodeFactory;
import mc.feature.delete._ast.ASTChild;
import mc.feature.delete._ast.ASTParent;
import mc.feature.delete._ast.DeleteTestNodeFactory;

public class ASTEMFTest extends TestCase {
  
  public void testNpeDeepClone() {
    mc.feature.automaton._ast.ASTAutomaton a = AutomatonNodeFactory.createASTAutomaton();
    assertNull(a.deepClone().getName());
  }
  
  public void testGetParent1() {
    ASTA a = featureDSLNodeFactory.createASTA();
    
    // should have no parent
    assertNull(a.get_Parent());
  }
  
  public void testGetParent2() {
    ASTA a = featureDSLNodeFactory.createASTA();
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    
    assertNull(a.get_Parent());
    assertNull(aList.get_Parent());
    
    ASTB b = featureDSLNodeFactory.createASTB();
    
    b.setA(a);
    assertEquals(b, a.get_Parent());
    
    b.setB(aList);
    assertEquals(b, aList.get_Parent());
    
  }
  
  public void testGetParent2b() {
    ASTA a = featureDSLNodeFactory.createASTA();
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    
    assertNull(a.get_Parent());
    assertNull(aList.get_Parent());
    
    ASTB b = featureDSLNodeFactory.createASTB();
    
    b.setA(a);
    assertEquals(b, a.get_Parent());
    
    b.setB(aList);
    assertEquals(b, aList.get_Parent());
    
    ASTA a2 = featureDSLNodeFactory.createASTA();
    b.setA(a2);
    assertEquals(b, a2.get_Parent());
    assertNull(a.get_Parent());
    
  }
  
  public void testGetParent3() {
    ASTA a = featureDSLNodeFactory.createASTA();
    
    ASTB b = featureDSLNodeFactory.createASTB();
    ASTB b2 = featureDSLNodeFactory.createASTB();
    
    b.setA(a);
    assertEquals(b, a.get_Parent());
    try {
      a.set_Parent(b2);
      fail("expeced IllegalStateExcpetion");
    }
    catch (ParentNotNullException s) {
      
    }
    
  }
  
  public void testGetParent4() {
    // add() von Liste
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTA a = featureDSLNodeFactory.createASTA();
    aList.add(a);
    
    ASTA res = (ASTA) aList.get(0);
    assertEquals(a, res);
    
    assertEquals(aList, res.get_Parent());
  }
  
  public void testGetParent5() {
    // Konstruktor, leere Liste
    ASTB b = featureDSLNodeFactory.createASTB();
    
    assertNotNull(b.getB());
    
    assertEquals(b, b.getB().get_Parent());
  }
  
  public void testGetParent6() {
    // Konstruktor
    ASTA a = featureDSLNodeFactory.createASTA();
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTB b = featureDSLNodeFactory.createASTB(a, aList);
    
    assertEquals(b, a.get_Parent());
    assertEquals(b, aList.get_Parent());
  }
  
  public void testGetParent7() {
    ASTA a = featureDSLNodeFactory.createASTA();
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTB b = featureDSLNodeFactory.createASTB(a, aList);
    
    ASTB cloned = b.deepClone();
    assertEquals(cloned, cloned.getA().get_Parent());
    assertEquals(cloned, cloned.getB().get_Parent());
  }
  
  public void testGetParent8() {
    // list test: clear
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    
    assertNull(a1.get_Parent());
    assertNull(a2.get_Parent());
    
    assertEquals(0, aList.size());
    
    assertTrue(aList.add(a1));
    assertTrue(aList.add(a2));
    
    assertEquals(aList, a1.get_Parent());
    assertEquals(aList, a2.get_Parent());
    assertEquals(2, aList.size());
    
    aList.clear();
    assertEquals(0, aList.size());
    assertNull(a1.get_Parent());
    assertNull(a2.get_Parent());
  }
  
  public void testGetParent9() {
    // list test: add (i,o)
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    
    assertNull(a1.get_Parent());
    assertNull(a2.get_Parent());
    
    assertEquals(0, aList.size());
    
    aList.add(0, a1);
    aList.add(0, a2);
    
    assertEquals(aList, a1.get_Parent());
    assertEquals(aList, a2.get_Parent());
    assertEquals(2, aList.size());
    
    assertEquals(a1, aList.get(1));
    assertEquals(a2, aList.get(0));
  }
  
  public void testGetParent10() {
    // list test: remove (i)
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    
    assertNull(a1.get_Parent());
    assertNull(a2.get_Parent());
    
    assertEquals(0, aList.size());
    
    aList.add(a1);
    aList.add(a2);
    
    assertEquals(aList, a1.get_Parent());
    assertEquals(aList, a2.get_Parent());
    assertEquals(2, aList.size());
    
    assertEquals(a2, aList.remove(1));
    assertEquals(1, aList.size());
    assertNull(a2.get_Parent());
    
    assertEquals(a1, aList.remove(0));
    assertEquals(0, aList.size());
    assertNull(a1.get_Parent());
  }
  
  public void testGetParent11() {
    // list test: remove (o)
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    
    assertNull(a1.get_Parent());
    assertNull(a2.get_Parent());
    
    assertEquals(0, aList.size());
    
    aList.add(a1);
    aList.add(a2);
    
    assertEquals(aList, a1.get_Parent());
    assertEquals(aList, a2.get_Parent());
    assertEquals(2, aList.size());
    
    assertEquals(true, aList.remove(a1));
    assertEquals(1, aList.size());
    assertNull(a1.get_Parent());
    
    assertEquals(true, aList.remove(a2));
    assertEquals(0, aList.size());
    assertNull(a2.get_Parent());
    
    assertEquals(false, aList.remove(a2));
  }
  
  public void testGetParent12() {
    // list test: set (i,o)
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    ASTA a3 = featureDSLNodeFactory.createASTA();
    assertNull(a1.get_Parent());
    assertNull(a2.get_Parent());
    
    assertEquals(0, aList.size());
    
    aList.add(0, a1);
    aList.add(1, a2);
    
    assertEquals(2, aList.size());
    
    assertEquals(a2, aList.set(1, a3));
    assertNull(a2.get_Parent());
    assertEquals(aList, a3.get_Parent());
  }
  
  public void testGetParent13() {
    // list : addAll
    ASTAList aList = new mc.feature._ast.ASTAList(mc.feature._ast.ASTA.class, featureDSLNodeFactory.createASTB(), FeatureDSLPackage.B__B);
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    ASTA a3 = featureDSLNodeFactory.createASTA();
    aList.add(a1);
    aList.add(a2);
    aList.add(a3);
    
    ASTAList aList2 = featureDSLNodeFactory.createASTAList();
    try {
      aList2.addAll(aList);
      fail("expected illegal state exception");
    }
    catch (ParentNotNullException e) {
      
    }
  }
  
  public void testGetParent14() {
    // list : addAll
    List<ASTA> aList = new LinkedList<ASTA>();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    ASTA a3 = featureDSLNodeFactory.createASTA();
    aList.add(a1);
    aList.add(a2);
    aList.add(a3);
    
    ASTAList aList2 = featureDSLNodeFactory.createASTAList();
    try {
      aList2.addAll(aList);
      assertEquals(3, aList2.size());
      assertEquals(aList2, a1.get_Parent());
      assertEquals(aList2, a2.get_Parent());
      assertEquals(aList2, a3.get_Parent());
    }
    catch (IllegalStateException e) {
      fail("did not expect illegal state exception");
    }
  }
  
  public void testGetParent15() {
    // list : addAll(i,C)
    List<ASTA> aList = new LinkedList<ASTA>();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    ASTA a3 = featureDSLNodeFactory.createASTA();
    ASTA a4 = featureDSLNodeFactory.createASTA();
    aList.add(a1);
    aList.add(a2);
    aList.add(a3);
    
    ASTAList aList2 = featureDSLNodeFactory.createASTAList();
    aList2.add(a4);
    try {
      aList2.addAll(0, aList);
      assertEquals(4, aList2.size());
      assertEquals(aList2, a1.get_Parent());
      assertEquals(aList2, a2.get_Parent());
      assertEquals(aList2, a3.get_Parent());
      assertEquals(a1, aList2.get(0));
      assertEquals(a4, aList2.get(3));
    }
    catch (IllegalStateException e) {
      fail("did not expect illegal state exception");
    }
  }
  
  public void testGetParent16() {
    // list: remove (o)
    ASTAList aList1 = featureDSLNodeFactory.createASTAList();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    aList1.add(a1);
    Object o = a1;
    
    assertTrue(aList1.remove(o));
  }
  
  public void testGetParent17() {
    // list : removeAll
    List<ASTA> aList = new LinkedList<ASTA>();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    ASTA a3 = featureDSLNodeFactory.createASTA();
    ASTA a4 = featureDSLNodeFactory.createASTA();
    aList.add(a1);
    aList.add(a2);
    aList.add(a3);
    
    ASTAList aList2 = featureDSLNodeFactory.createASTAList();
    aList2.addAll(aList);
    aList2.add(a4);
    
    assertTrue(aList2.removeAll(aList));
    assertEquals(1, aList2.size());
    assertNull(a1.get_Parent());
    assertNull(a2.get_Parent());
    assertNull(a3.get_Parent());
    assertEquals(aList2, a4.get_Parent());
  }
  
  public void testGetParent18() {
    // list : retainAll
    List<ASTA> aList = new LinkedList<ASTA>();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    a2.setModifier(true);
    ASTA a3 = featureDSLNodeFactory.createASTA();
    a3.setModifier(true);
    ASTA a4 = featureDSLNodeFactory.createASTA();
    a4.setModifier(true);
    a4.setProtection(true);
    aList.add(a1);
    aList.add(a2);
    aList.add(a3);
    
    ASTAList aList2 = featureDSLNodeFactory.createASTAList();
    aList2.addAll(aList);
    aList2.add(a4);
    
    assertTrue(aList2.retainAll(aList));
    assertEquals(3, aList2.size());
    assertNull(a4.get_Parent());
    
    assertEquals(aList2, a1.get_Parent());
    assertEquals(aList2, a2.get_Parent());
    assertEquals(aList2, a3.get_Parent());
  }
  
  public void testGetParent19() {
    // list : iterator hasNext(), next()
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    ASTA a3 = featureDSLNodeFactory.createASTA();
    ASTA a4 = featureDSLNodeFactory.createASTA();
    ASTA[] array = { a1, a2, a3, a4 };
    aList.add(a1);
    aList.add(a2);
    aList.add(a3);
    aList.add(a4);
    
    Iterator<A> it = aList.iterator();
    
    int i = 0;
    while (it.hasNext()) {
      ASTA temp = (ASTA) it.next();
      assertEquals(array[i], temp);
      assertEquals(aList, temp.get_Parent());
      i++;
    }
  }
  
  public void testGetParent20() {
    // list : iterator remove()
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    ASTA a3 = featureDSLNodeFactory.createASTA();
    ASTA a4 = featureDSLNodeFactory.createASTA();
    ASTA[] array = new ASTA[4];
    array[0] = a1;
    array[1] = a2;
    array[2] = a3;
    array[3] = a4;
    aList.add(a1);
    aList.add(a2);
    aList.add(a3);
    aList.add(a4);
    
    for (int j = 0; j < array.length; j++) {
      assertEquals(aList, array[j].get_Parent());
    }
    
    Iterator it = aList.iterator();
    
    int i = 0;
    while (it.hasNext()) {
      assertEquals(array[i++], it.next());
      it.remove();
      assertEquals(4 - i, aList.size());
      
    }
    for (int j = 0; j < array.length; j++) {
      assertNull(array[j].get_Parent());
    }
  }
  
  public void testGetParent21() {
    // list: listIterator, previous, hasPrevious, nextIndex, previousIndex
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    ASTA a3 = featureDSLNodeFactory.createASTA();
    ASTA a4 = featureDSLNodeFactory.createASTA();
    ASTA[] array = new ASTA[4];
    array[0] = a1;
    array[1] = a2;
    array[2] = a3;
    array[3] = a4;
    aList.add(a1);
    aList.add(a2);
    aList.add(a3);
    aList.add(a4);
    
    ListIterator<A> it = aList.listIterator();
    int i = 0;
    while (it.hasNext()) {
      int idx = it.nextIndex();
      ASTA temp = (ASTA) it.next();
      assertEquals(i, idx);
      assertEquals(array[i], temp);
      assertEquals(aList, temp.get_Parent());
      i++;
    }
    i--;
    while (it.hasPrevious()) {
      int idx = it.previousIndex();
      ASTA temp = (ASTA) it.previous();
      assertEquals(i, idx);
      assertEquals(array[i], temp);
      assertEquals(aList, temp.get_Parent());
      i--;
    }
    
  }
  
  public void testGetParent22() {
    // list: listIterator add, set
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTA a1 = featureDSLNodeFactory.createASTA();
    ASTA a2 = featureDSLNodeFactory.createASTA();
    
    ListIterator<A> it = aList.listIterator();
    
    it.add(a1);
    
    assertEquals(aList, a1.get_Parent());
    ASTA temp = (ASTA) it.previous();
    assertEquals(a1, temp);
    
    it.set(a2);
    
    assertNull(a1.get_Parent());
    assertEquals(aList, a2.get_Parent());
    temp = (ASTA) it.next();
    assertEquals(a2, temp);
    
  }
  
  public void testGetParent23() {
    ASTA a = featureDSLNodeFactory.createASTA();
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTB b = featureDSLNodeFactory.createASTB(a, aList);
    // dummy parent != null
    ASTA dummy = featureDSLNodeFactory.createASTA();
    b.set_Parent(dummy);
    
    ASTB cloned = b.deepClone();
    assertEquals(cloned, cloned.getA().get_Parent());
    assertEquals(cloned, cloned.getB().get_Parent());
    assertEquals(dummy, b.get_Parent()); // Original hat parent dummy
    assertNull(cloned.get_Parent()); // clone hat parent null
    
    ASTAList lCloned = aList.deepClone();
    assertNull(lCloned.get_Parent());
  }
  
  public void testListExistent1() {
    ASTAList a = featureDSLNodeFactory.createASTAList();
    assertFalse(a.is_Existent());
  }
  
  public void testListExistent2() {
    ASTAList a = featureDSLNodeFactory.createASTAList();
    a.add(featureDSLNodeFactory.createASTA());
    assertTrue(a.is_Existent());
  }
  
  public void testListExistent3() {
    ASTAList a = featureDSLNodeFactory.createASTAList();
    a.add(featureDSLNodeFactory.createASTA());
    a.clear();
    assertTrue(a.isEmpty());
    assertTrue(a.is_Existent());
    
  }
  
  public void testListExistent4() {
    ASTAList a = featureDSLNodeFactory.createASTAList();
    a.add(0, featureDSLNodeFactory.createASTA());
    assertTrue(a.is_Existent());
  }
  
  public void testListExistent5() {
    ASTAList a = featureDSLNodeFactory.createASTAList();
    ListIterator<A> it = a.listIterator();
    assertFalse(a.is_Existent());
    it.add(featureDSLNodeFactory.createASTA());
    assertTrue(a.is_Existent());
  }
  
  public void testGet_ChildNodes1() {
    ASTAList aList = featureDSLNodeFactory.createASTAList();
    ASTA a = featureDSLNodeFactory.createASTA();
    assertEquals(0, aList.get_Children().size());
    aList.add(a);
    assertEquals(1, aList.get_Children().size());
  }
  
  public void testGet_ChildNodes2() {
    ASTParent p = DeleteTestNodeFactory.createASTParent();
    ASTChild s = DeleteTestNodeFactory.createASTChild();
    p.get_Children().add(s);
    p.setSon(s);
    assertEquals(2, p.get_Children().size());
    assertTrue(p.get_Children().contains(s));
    assertTrue(p.get_Children().contains(p.getChildList()));
  }
}
