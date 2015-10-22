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

public class ASTENodeList extends de.monticore.ast.ASTCList implements
    java.lang.Iterable<ASTENode>, EList<ASTENode>, NotifyingList<ASTENode>, RandomAccess,
    Cloneable, Serializable, InternalEList<ASTENode>, InternalEList.Unsettable<ASTENode>,
    EStructuralFeature.Setting {
  
  private EObjectContainmentEList<ASTENode> list;
  
  public ASTENodeList(Class<?> dataClass, InternalEObject owner, int featureID) {
    list = new EObjectContainmentEList<ASTENode>(dataClass, owner, featureID);
    set_Existent(false);
  }
  
  public ASTENodeList() {
    list = new EObjectContainmentEList<ASTENode>(ASTENode.class,
        new de.monticore.emf.ASTDummyEObjectImplNode(), 0);
    set_Existent(false);
  }
  
  public ASTENodeList(boolean strictlyOrdered) {
    this();
    this._strictlyOrdered = strictlyOrdered;
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
    set_Existent(false);
  }
  
  // added for EMF generation
  protected void setList(EObjectContainmentEList<ASTENode> list) {
    this.list.clear();
    this.list.addAll(list);
    set_Existent(false);
  }
  
  public void add(int index, ASTENode o) {
    if (!is_Existent()) {
      set_Existent(true);
    }
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
    if (!is_Existent()) {
      set_Existent(true);
    }
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
    for (Comment x : get_PreComments()) {
      result.get_PreComments().add(new Comment(x.getText()));
    }
    
    for (Comment x : get_PostComments()) {
      result.get_PostComments().add(new Comment(x.getText()));
    }
    
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
      if (isStrictlyOrdered()) {
        java.util.Iterator<ASTENode> one = this.iterator();
        java.util.Iterator<ASTENode> two = comp.iterator();
        while (one.hasNext()) {
          if (!one.next().deepEquals(two.next())) {
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
      if (isStrictlyOrdered()) {
        java.util.Iterator<ASTENode> one = this.iterator();
        java.util.Iterator<ASTENode> two = comp.iterator();
        while (one.hasNext()) {
          if (!one.next().deepEqualsWithComments(two.next())) {
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
      if (!t.is_Existent())
        t.set_Existent(true);
    }
  }
  
  // Methods added for EMF
  
  public void move(int newPosition, ASTENode object) {
    list.move(newPosition, object);
  }
  
  public ASTENode move(int newPosition, int oldPosition) {
    return list.move(newPosition, oldPosition);
  }
  
  @Override
  public boolean addAllUnique(Collection<? extends ASTENode> collection) {
    return list.addAllUnique(collection);
  }
  
  @Override
  public boolean addAllUnique(int index, Collection<? extends ASTENode> collection) {
    return list.addAllUnique(index, collection);
  }
  
  @Override
  public void addUnique(ASTENode object) {
    list.addUnique(object);
  }
  
  @Override
  public void addUnique(int index, ASTENode object) {
    list.addUnique(index, object);
  }
  
  @Override
  public NotificationChain basicAdd(ASTENode object, NotificationChain notifications) {
    return list.basicAdd(object, notifications);
  }
  
  @Override
  public boolean basicContains(Object object) {
    return list.basicContains(object);
  }
  
  @Override
  public boolean basicContainsAll(Collection<?> collection) {
    return list.basicContainsAll(collection);
  }
  
  @Override
  public ASTENode basicGet(int index) {
    return list.basicGet(index);
  }
  
  @Override
  public int basicIndexOf(Object object) {
    return list.basicIndexOf(object);
  }
  
  @Override
  public Iterator<ASTENode> basicIterator() {
    return this.iterator();
  }
  
  @Override
  public int basicLastIndexOf(Object object) {
    return list.lastIndexOf(object);
  }
  
  @Override
  public List<ASTENode> basicList() {
    return this;
  }
  
  @Override
  public ListIterator<ASTENode> basicListIterator() {
    return this.listIterator();
  }
  
  @Override
  public ListIterator<ASTENode> basicListIterator(int index) {
    return this.listIterator(index);
  }
  
  @Override
  public NotificationChain basicRemove(Object object, NotificationChain notifications) {
    return list.basicRemove(object, notifications);
  }
  
  @Override
  public Object[] basicToArray() {
    return list.basicToArray();
  }
  
  @Override
  public <T> T[] basicToArray(T[] array) {
    return list.basicToArray(array);
  }
  
  @Override
  public ASTENode setUnique(int index, ASTENode object) {
    return list.setUnique(index, object);
  }
  
  @Override
  public boolean isSet() {
    return list.isSet();
  }
  
  @Override
  public void unset() {
    list.unset();
  }
  
  @Override
  public Object get(boolean resolve) {
    return list.get(resolve);
  }
  
  @Override
  public EObject getEObject() {
    return list.getEObject();
  }
  
  @Override
  public EStructuralFeature getEStructuralFeature() {
    return list.getEStructuralFeature();
  }
  
  @Override
  public void set(Object newValue) {
    list.set(newValue);
  }
  
  @Override
  public Object getFeature() {
    return list.getFeature();
  }
  
  @Override
  public int getFeatureID() {
    return list.getFeatureID();
  }
  
  @Override
  public Object getNotifier() {
    return list.getNotifier();
  }
}
