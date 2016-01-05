package de.monticore.emf._ast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.NotifyingList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.se_rwth.commons.SourcePosition;

public class ASTENodeList implements
    java.lang.Iterable<ASTENode>, EList<ASTENode>, NotifyingList<ASTENode>, RandomAccess,
    Cloneable, Serializable, InternalEList<ASTENode>, InternalEList.Unsettable<ASTENode>,
    EStructuralFeature.Setting {
  
  private EObjectContainmentEList<ASTENode> list;
  
  public ASTENodeList(Class<?> dataClass, InternalEObject owner, int featureID) {
    list = new EObjectContainmentEList<ASTENode>(dataClass, owner, featureID);
  }
  
  public ASTENodeList() {
    list = new EObjectContainmentEList<ASTENode>(ASTENode.class,
        new de.monticore.emf.ASTDummyEObjectImplNode(), 0);
  }
  
  /* @deprecated dont gets actual list but creates a new arraylist and makes
   * addAll */
  @Deprecated
  protected ArrayList<ASTENode> getList() {
    ArrayList<ASTENode> arrayList = new ArrayList<ASTENode>();
    arrayList.addAll(list);
    return arrayList;
  }
  
  /* @deprecated dont sets actual list but clears the list and makes addAll */
  @Deprecated
  protected void setList(ArrayList<ASTENode> list) {
    this.list.clear();
    this.list.addAll(list);
  }
  
  // added for EMF generation
  protected void setList(EObjectContainmentEList<ASTENode> list) {
    this.list.clear();
    this.list.addAll(list);
  }
  
  public void add(int index, ASTENode o) {
    list.add(index, o);
  }
  
  public void add(int index, ASTNode o) {
    if (o instanceof ASTENode) {
      add(index, (ASTENode) o);
    }
    else {
      throw new IllegalArgumentException("Parameter must be instance of ASTENode");
    }
  }
  
  public boolean add(ASTENode o) {
    list.add(o);
    return true;
  }
  
  public boolean add(ASTNode o) {
    if (o instanceof ASTENode) {
      return add((ASTENode) o);
    }
    else {
      throw new IllegalArgumentException("Parameter must be instance of ASTENode");
    }
  }
  
  public void clear() {
    list.clear();
  }
  
  public boolean contains(ASTENode o) {
    return list.contains(o);
  }
  
  public boolean equals(ASTENodeList o) {
    return list.equals(o.getList());
  }
  
  public ASTENode get(int index) {
    return list.get(index);
  }
  
  public int hashCode() {
    return list.hashCode();
  }
  
  public int indexOf(ASTENode o) {
    return list.indexOf(o);
  }
  
  public boolean isEmpty() {
    return list.isEmpty();
  }
  
  public Iterator<ASTENode> iterator() {
    return new TemplateListIterator((ASTENodeList) this, 0);
  }
  
  public int lastIndexOf(ASTENode o) {
    return list.lastIndexOf(o);
  }
  
  public ASTENode remove(int index) {
    ASTENode r = list.remove(index);
    return r;
  }
  
  public boolean remove(ASTENode o) {
    boolean r = list.remove(o);
    return r;
  }
  
  public ASTENode set(int index, ASTENode o) {
    ASTENode old = list.set(index, o);
    return old;
  }
  
  public int size() {
    return list.size();
  }
  
  public ASTENode[] toArray() {
    ASTENode[] result = new ASTENode[list.size()];
    list.toArray(result);
    return result;
  }
  
  public SourcePosition getStart() {
    if (list.size() != 0)
      return get(0).get_SourcePositionStart();
    return null;
  }
  
  public SourcePosition getEnd() {
    if (list.size() != 0)
      return get(list.size() - 1).get_SourcePositionEnd();
    return null;
  }
  
  public ASTENodeList deepClone() {
    ASTENodeList result = new ASTENodeList();
    if (list.size() != 0) {
      Iterator<ASTENode> iter = iterator();
      while (iter.hasNext()) {
        result.add((ASTENode) iter.next().deepClone());
      }
    }
    // TODO GV:
    /*
    for (Comment x : get_PreComments()) {
      result.get_PreComments().add(new Comment(x.getText()));
    }
    
    for (Comment x : get_PostComments()) {
      result.get_PostComments().add(new Comment(x.getText()));
    }
    
    */
    
    return result;
  }
  
  public boolean addAll(Collection<? extends ASTENode> c) {
    boolean r = false;
    for (ASTENode n : c) {
      this.add(n);
      r = true;
    }
    return r;
  }
  
  public boolean addAll(int index, Collection<? extends ASTENode> c) {
    boolean r = false;
    int idx = index;
    for (ASTENode n : c) {
      this.add(idx++, n);
      r = true;
    }
    return r;
  }
  
  public boolean contains(Object o) {
    return list.contains(o);
  }
  
  public boolean containsAll(Collection<?> c) {
    return list.containsAll(c);
  }
  
  public int indexOf(Object o) {
    
    return list.indexOf(o);
  }
  
  public int lastIndexOf(Object o) {
    
    return list.lastIndexOf(o);
  }
  
  public ListIterator<ASTENode> listIterator() {
    
    return new TemplateListIterator((ASTENodeList) this, 0);
  }
  
  public ListIterator<ASTENode> listIterator(int index) {
    return new TemplateListIterator((ASTENodeList) this, 0);
  }
  
  public boolean remove(Object o) {
    boolean r = list.remove(o);
    return r;
  }
  
  public void remove_Child(ASTNode child) {
    remove(child);
  }
  
  public boolean removeAll(Collection<?> c) {
    boolean r = false;
    for (Object o : c) {
      r = r | this.remove(o);
      
    }
    return r;
  }
  
  public boolean retainAll(Collection<?> c) {
    boolean r = false;
    Iterator<?> i = this.iterator();
    while (i.hasNext()) {
      Object o = i.next();
      if (!c.contains(o)) {
        i.remove();
        r = true;
      }
    }
    return r;
  }
  
  public List<ASTENode> subList(int fromIndex, int toIndex) {
    return list.subList(fromIndex, toIndex);
  }
  
  public <T> T[] toArray(T[] a) {
    return list.toArray(a);
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public ASTENodeList clone() {
    ASTENodeList ret = new ASTENodeList();
    ret.setList((EObjectContainmentEList<ASTENode>) list.clone());
    return ret;
  }
  
  public boolean equalAttributes(Object o) {
    ASTENodeList comp;
    if ((o instanceof ASTENodeList)) {
      comp = (ASTENodeList) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
      java.util.Iterator<ASTENode> one = this.iterator();
      java.util.Iterator<ASTENode> two = comp.iterator();
      while (one.hasNext()) {
        if (!one.next().equalAttributes(two.next())) {
          return false;
        }
      }
    }
    else {
      return false;
    }
    return true;
  }
  
  public boolean equalsWithComments(Object o) {
    ASTENodeList comp;
    if ((o instanceof ASTENodeList)) {
      comp = (ASTENodeList) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
      java.util.Iterator<ASTENode> one = this.iterator();
      java.util.Iterator<ASTENode> two = comp.iterator();
      while (one.hasNext()) {
        if (!one.next().equalsWithComments(two.next())) {
          return false;
        }
      }
    }
    else {
      return false;
    }
    return true;
  }
  
  public boolean deepEquals(Object o) {
    ASTENodeList comp;
    if ((o instanceof ASTENodeList)) {
      comp = (ASTENodeList) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
        java.util.Iterator<ASTENode> one = this.iterator();
        while (one.hasNext()) {
          ASTENode oneNext = one.next();
          boolean matchFound = false;
          java.util.Iterator<ASTENode> two = comp.iterator();
          while (two.hasNext()) {
            if (oneNext.deepEquals(two.next())) {
              matchFound = true;
              break;
            }
          }
          if (!matchFound) {
            return false;
          }
        }
    }
    else {
      return false;
    }
    return true;
  }
  
  public boolean deepEqualsWithComments(Object o) {
    ASTENodeList comp;
    if ((o instanceof ASTENodeList)) {
      comp = (ASTENodeList) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
        java.util.Iterator<ASTENode> one = this.iterator();
        while (one.hasNext()) {
          ASTENode oneNext = one.next();
          boolean matchFound = false;
          java.util.Iterator<ASTENode> two = comp.iterator();
          while (two.hasNext()) {
            if (oneNext.deepEqualsWithComments(two.next())) {
              matchFound = true;
              break;
            }
          }
          if (!matchFound) {
            return false;
          }
      }
    }
    else {
      return false;
    }
    return true;
  }
  
  public boolean deepEquals(Object o, boolean forceSameOrder) {
    ASTENodeList comp;
    if ((o instanceof ASTENodeList)) {
      comp = (ASTENodeList) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
      if (forceSameOrder) {
        java.util.Iterator<ASTENode> one = this.iterator();
        java.util.Iterator<ASTENode> two = comp.iterator();
        while (one.hasNext()) {
          if (!one.next().deepEquals(two.next(), forceSameOrder)) {
            return false;
          }
        }
      }
      else {
        java.util.Iterator<ASTENode> one = this.iterator();
        while (one.hasNext()) {
          ASTENode oneNext = one.next();
          boolean matchFound = false;
          java.util.Iterator<ASTENode> two = comp.iterator();
          while (two.hasNext()) {
            if (oneNext.deepEquals(two.next(), forceSameOrder)) {
              matchFound = true;
              break;
            }
          }
          if (!matchFound) {
            return false;
          }
        }
      }
    }
    else {
      return false;
    }
    return true;
  }
  
  public boolean deepEqualsWithComments(Object o, boolean forceSameOrder) {
    ASTENodeList comp;
    if ((o instanceof ASTENodeList)) {
      comp = (ASTENodeList) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
      if (forceSameOrder) {
        java.util.Iterator<ASTENode> one = this.iterator();
        while (one.hasNext()) {
          ASTENode oneNext = one.next();
          boolean matchFound = false;
          java.util.Iterator<ASTENode> two = comp.iterator();
          while (two.hasNext()) {
            if (oneNext.deepEqualsWithComments(two.next(), forceSameOrder)) {
              matchFound = true;
              break;
            }
          }
          if (!matchFound) {
            return false;
          }
        }
      }
      else {
        java.util.Iterator<ASTENode> one = this.iterator();
        java.util.Iterator<ASTENode> two = comp.iterator();
        while (one.hasNext()) {
          if (!one.next().deepEqualsWithComments(two.next(), forceSameOrder)) {
            return false;
          }
        }
      }
    }
    else {
      return false;
    }
    return true;
  }
  
  private class TemplateListIterator implements ListIterator<ASTENode> {
    
    private ListIterator<ASTENode> it;
    
    private ASTENodeList t;
    
    private ASTENode lastRet = null;
    
    TemplateListIterator(ASTENodeList t, int index) {
      this.t = t;
      this.it = list.listIterator(index);
    }
    
    public boolean hasNext() {
      return it.hasNext();
    }
    
    public ASTENode next() {
      lastRet = it.next();
      return lastRet;
    }
    
    public boolean hasPrevious() {
      return it.hasPrevious();
    }
    
    public ASTENode previous() {
      lastRet = it.previous();
      return lastRet;
    }
    
    public int nextIndex() {
      return it.nextIndex();
    }
    
    public int previousIndex() {
      return it.previousIndex();
    }
    
    public void remove() {
      if (lastRet != null) {
        lastRet = null;
      }
      it.remove();
    }
    
    public void set(ASTENode o) {
      if (lastRet != null) {
        lastRet = null;
      }
      it.set(o);
    }
    
    public void add(ASTENode o) {
      it.add(o);
  }
  
  // Methods added for EMF
  
  public void move(int newPosition, ASTENode object) {
    list.move(newPosition, object);
  }
  
  public ASTENode move(int newPosition, int oldPosition) {
    return list.move(newPosition, oldPosition);
  }
  
  public boolean addAllUnique(Collection<? extends ASTENode> collection) {
    return list.addAllUnique(collection);
  }
  
  public boolean addAllUnique(int index, Collection<? extends ASTENode> collection) {
    return list.addAllUnique(index, collection);
  }
  
  public void addUnique(ASTENode object) {
    list.addUnique(object);
  }
  
  public void addUnique(int index, ASTENode object) {
    list.addUnique(index, object);
  }
  
  public NotificationChain basicAdd(ASTENode object, NotificationChain notifications) {
    return list.basicAdd(object, notifications);
  }
  
  public boolean basicContains(Object object) {
    return list.basicContains(object);
  }
  
  public boolean basicContainsAll(Collection<?> collection) {
    return list.basicContainsAll(collection);
  }
  
  public ASTENode basicGet(int index) {
    return list.basicGet(index);
  }
  
  public int basicIndexOf(Object object) {
    return list.basicIndexOf(object);
  }
  
  
  public int basicLastIndexOf(Object object) {
    return list.lastIndexOf(object);
  }
  
  
  public NotificationChain basicRemove(Object object, NotificationChain notifications) {
    return list.basicRemove(object, notifications);
  }
  
  public Object[] basicToArray() {
    return list.basicToArray();
  }
  
  public <T> T[] basicToArray(T[] array) {
    return list.basicToArray(array);
  }
  
  public ASTENode setUnique(int index, ASTENode object) {
    return list.setUnique(index, object);
  }
  
  public boolean isSet() {
    return list.isSet();
  }
  
  public void unset() {
    list.unset();
  }
  
  public Object get(boolean resolve) {
    return list.get(resolve);
  }
  
  public EObject getEObject() {
    return list.getEObject();
  }
  
  public EStructuralFeature getEStructuralFeature() {
    return list.getEStructuralFeature();
  }
  
  public Object getFeature() {
    return list.getFeature();
  }
  
  public int getFeatureID() {
    return list.getFeatureID();
  }
  
  public Object getNotifier() {
    return list.getNotifier();
  }
}

  /**
   * @see org.eclipse.emf.ecore.EStructuralFeature.Setting#getEObject()
   */
  @Override
  public EObject getEObject() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.EStructuralFeature.Setting#getEStructuralFeature()
   */
  @Override
  public EStructuralFeature getEStructuralFeature() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.EStructuralFeature.Setting#get(boolean)
   */
  @Override
  public Object get(boolean resolve) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.EStructuralFeature.Setting#set(java.lang.Object)
   */
  @Override
  public void set(Object newValue) {
    // TODO Auto-generated method stub
    
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList.Unsettable#isSet()
   */
  @Override
  public boolean isSet() {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList.Unsettable#unset()
   */
  @Override
  public void unset() {
    // TODO Auto-generated method stub
    
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicGet(int)
   */
  @Override
  public ASTENode basicGet(int index) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicList()
   */
  @Override
  public List<ASTENode> basicList() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicIterator()
   */
  @Override
  public Iterator<ASTENode> basicIterator() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicListIterator()
   */
  @Override
  public ListIterator<ASTENode> basicListIterator() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicListIterator(int)
   */
  @Override
  public ListIterator<ASTENode> basicListIterator(int index) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicToArray()
   */
  @Override
  public Object[] basicToArray() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicToArray(java.lang.Object[])
   */
  @Override
  public <T> T[] basicToArray(T[] array) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicIndexOf(java.lang.Object)
   */
  @Override
  public int basicIndexOf(Object object) {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicLastIndexOf(java.lang.Object)
   */
  @Override
  public int basicLastIndexOf(Object object) {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicContains(java.lang.Object)
   */
  @Override
  public boolean basicContains(Object object) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicContainsAll(java.util.Collection)
   */
  @Override
  public boolean basicContainsAll(Collection<?> collection) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicRemove(java.lang.Object, org.eclipse.emf.common.notify.NotificationChain)
   */
  @Override
  public NotificationChain basicRemove(Object object, NotificationChain notifications) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#basicAdd(java.lang.Object, org.eclipse.emf.common.notify.NotificationChain)
   */
  @Override
  public NotificationChain basicAdd(ASTENode object, NotificationChain notifications) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#addUnique(java.lang.Object)
   */
  @Override
  public void addUnique(ASTENode object) {
    // TODO Auto-generated method stub
    
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#addUnique(int, java.lang.Object)
   */
  @Override
  public void addUnique(int index, ASTENode object) {
    // TODO Auto-generated method stub
    
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#addAllUnique(java.util.Collection)
   */
  @Override
  public boolean addAllUnique(Collection<? extends ASTENode> collection) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#addAllUnique(int, java.util.Collection)
   */
  @Override
  public boolean addAllUnique(int index, Collection<? extends ASTENode> collection) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * @see org.eclipse.emf.ecore.util.InternalEList#setUnique(int, java.lang.Object)
   */
  @Override
  public ASTENode setUnique(int index, ASTENode object) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.common.notify.NotifyingList#getNotifier()
   */
  @Override
  public Object getNotifier() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.common.notify.NotifyingList#getFeature()
   */
  @Override
  public Object getFeature() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.eclipse.emf.common.notify.NotifyingList#getFeatureID()
   */
  @Override
  public int getFeatureID() {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * @see org.eclipse.emf.common.util.EList#move(int, java.lang.Object)
   */
  @Override
  public void move(int newPosition, ASTENode object) {
    // TODO Auto-generated method stub
    
  }

  /**
   * @see org.eclipse.emf.common.util.EList#move(int, int)
   */
  @Override
  public ASTENode move(int newPosition, int oldPosition) {
    // TODO Auto-generated method stub
    return null;
  }
}