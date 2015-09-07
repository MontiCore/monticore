<#--
***************************************************************************************
Copyright (c) 2015, MontiCore
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
***************************************************************************************
-->
 <#assign genHelper = glex.getGlobalValue("astHelper")>
 <#assign astName = genHelper.getAstClassNameForASTLists(ast.getName())>
 
  protected ${ast.getName()} () {
    list = newArrayList();
    set_Existent(false);
  }
  
  protected ${ast.getName()} (boolean strictlyOrdered) {
    this();
    this._strictlyOrdered = strictlyOrdered;
  }
  
  protected ArrayList<${astName}> getList() {
    return list;
  }
  
  protected void setList(ArrayList<${astName}> list) {
    this.list = list;
    set_Existent(false);
  }
  
  public void add(int index, ${astName} o) {
    if (!is_Existent()) {
      set_Existent(true);
    }
    list.add(index, o);
  }
  
  public boolean add(${astName} o) {
    if (!is_Existent()) {
      set_Existent(true);
    }
    list.add(o);
    return true;
  }
  
  public void clear() {
    list.clear();
  }
  
  public boolean contains(${astName} o) {
    return list.contains(o);
  }
  
  public boolean equals(${astName}List o) {
    return list.equals(o.getList());
  }
  
  public ${astName} get(int index) {
    return list.get(index);
  }
  
  public int hashCode() {
    return list.hashCode();
  }
  
  public int indexOf(${astName} o) {
    return list.indexOf(o);
  }
  
  public boolean isEmpty() {
    return list.isEmpty();
  }
  
  public Iterator<${astName}> iterator() {
    return new TemplateListIterator((${astName}List) this, 0);
  }
  
  public int lastIndexOf(${astName} o) {
    return list.lastIndexOf(o);
  }
  
  public ${astName} remove(int index) {
    return list.remove(index);
  }
  
  public boolean remove(${astName} o) {
    return list.remove(o);
  }
  
  public ${astName} set(int index, ${astName} o) {
    return list.set(index, o);
  }
  
  public int size() {
    return list.size();
  }
  
  public ${astName}[] toArray() {
    ${astName}[] result = new ${astName}[list.size()];
    list.toArray(result);
    return result;
  }
  
  public SourcePosition getStart() {
    if (list.size() != 0) {
      return get(0).get_SourcePositionStart();
    }
    return null;
  }
  
  public SourcePosition getEnd() {
    if (list.size() != 0) {
      return get(list.size() - 1).get_SourcePositionEnd();
    }
    return null;
  }
  
  public ${astName}List deepClone() {
    ${astName}List result = new ${astName}List();
    if (list.size() != 0) {
      Iterator< ${astName}> iter = iterator();
      while (iter.hasNext()) {
        result.add((${astName}) iter.next().deepClone());
      }
    }
    for (de.monticore.ast.Comment x : get_PreComments()) {
      result.get_PreComments().add(new de.monticore.ast.Comment(x.getText()));
    }
    for (de.monticore.ast.Comment x : get_PostComments()) {
      result.get_PostComments().add(new de.monticore.ast.Comment(x.getText()));
    }
    return result;
  }
    
  public boolean addAll(Collection<? extends ${astName}> c) {
    boolean r = false;
    for (${astName} n : c) {
      this.add(n);
      r = true;
     }
     return r;
  }
  
  public boolean addAll(int index, Collection<? extends ${astName}> c) {
    boolean r = false;
    int idx = index;
    for (${astName} n : c) {
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
  
  public ListIterator<${astName}> listIterator() {
    return new TemplateListIterator((${astName}List) this, 0);
  }
  
  public ListIterator<${astName}> listIterator(int index) {
    return new TemplateListIterator((${astName}List) this, 0);
  }
  
  public boolean remove(Object o) {
    return list.remove(o);
  }
  
  public void remove_Child(de.monticore.ast.ASTNode child) {
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
  
  public List<${astName}> subList(int fromIndex, int toIndex) {
    return list.subList(fromIndex, toIndex);
  }
  
  public <T> T[] toArray(T[] a) {
    return list.toArray(a);
  }
  
  @Override
  @SuppressWarnings("unchecked")    
  public ${astName}List clone() {
    ${astName}List ret = new ${astName}List();
    ret.setList((ArrayList<${astName}>) list.clone());
    return ret;
  }
  
  public boolean equalAttributes(Object o) {
    ${astName}List comp;
    if ((o instanceof ${astName}List)) {
      comp = (${astName}List) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
      java.util.Iterator<${astName}> one = this.iterator();
      java.util.Iterator<${astName}> two = comp.iterator();
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
    ${astName}List comp;
    if ((o instanceof ${astName}List)) {
      comp = (${astName}List) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
      java.util.Iterator<${astName}> one = this.iterator();
      java.util.Iterator<${astName}> two = comp.iterator();
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
    ${astName}List comp;
    if ((o instanceof ${astName}List)) {
      comp = (${astName}List) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
      if (isStrictlyOrdered()) {
        java.util.Iterator<${astName}> one = this.iterator();
        java.util.Iterator<${astName}> two = comp.iterator();
        while (one.hasNext()) {
          if (!one.next().deepEquals(two.next())) {
            return false;
          }
        }
      }
      else {
        java.util.Iterator<${astName}> one = this.iterator();
        while (one.hasNext()) {
          ${astName} oneNext = one.next();
          boolean matchFound = false;
          java.util.Iterator<${astName}> two = comp.iterator();
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
    ${astName}List comp;
    if ((o instanceof ${astName}List)) {
      comp = (${astName}List) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
      if (isStrictlyOrdered()) {
        java.util.Iterator<${astName}> one = this.iterator();
        java.util.Iterator<${astName}> two = comp.iterator();
        while (one.hasNext()) {
          if (!one.next().deepEqualsWithComments(two.next())) {
            return false;
          }
        }
      }
      else {
        java.util.Iterator<${astName}> one = this.iterator();
        while (one.hasNext()) {
          ${astName} oneNext = one.next();
          boolean matchFound = false;
          java.util.Iterator<${astName}> two = comp.iterator();
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
    ${astName}List comp;
    if ((o instanceof ${astName}List)) {
      comp = (${astName}List) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
      if (forceSameOrder) {
        java.util.Iterator<${astName}> one = this.iterator();
        java.util.Iterator<${astName}> two = comp.iterator();
        while (one.hasNext()) {
          if (!one.next().deepEquals(two.next(), forceSameOrder)) {
            return false;
          }
        }
      }
      else {
        java.util.Iterator<${astName}> one = this.iterator();
        while (one.hasNext()) {
          ${astName} oneNext = one.next();
          boolean matchFound = false;
          java.util.Iterator<${astName}> two = comp.iterator();
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
    ${astName}List comp;
    if ((o instanceof ${astName}List)) {
      comp = (${astName}List) o;
    }
    else {
      return false;
    }
    if (this.size() == comp.size()) {
      if (forceSameOrder) {
        java.util.Iterator<${astName}> one = this.iterator();
        while (one.hasNext()) {
          ${astName} oneNext = one.next();
          boolean matchFound = false;
          java.util.Iterator<${astName}> two = comp.iterator();
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
        java.util.Iterator<${astName}> one = this.iterator();
        java.util.Iterator<${astName}> two = comp.iterator();
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
  
  private class TemplateListIterator implements ListIterator<${astName}> {
    private ListIterator<${astName}> it;
    private ${astName}List t;
    private ${astName} lastRet = null;
    
    TemplateListIterator(${astName}List t, int index) {
      this.t = t;
      this.it = list.listIterator(index);
    }
    
    public boolean hasNext() {
      return it.hasNext();
    }
    
    public ${astName} next() {
      lastRet = it.next();
      return lastRet;
    }
    
    public boolean hasPrevious() {
      return it.hasPrevious();
    }
    
    public ${astName} previous() {
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
    
    public void set(${astName} o) {
      if (lastRet != null) {
        lastRet = null;
      }
      it.set(o);
    }
    
    public void add(${astName} o) {
      it.add(o);
      if (!t.is_Existent())
        t.set_Existent(true);
      }        
    }

