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

package de.monticore.codegen.cd2java.ast;

/**
 * Methods-profile for list-valued ast attributes.
 *
 * @author Galina Volkova
 */

public enum AstListMethods {
  
  // ----------- java.util.Collection -----------------------------
  
  clear("public void clear%s();"),
  
  add("public boolean add%s(%s element);"),
  
  addAll("public boolean addAll%s(Collection<? extends %s> collection);"),
  
  contains("public boolean contains%s(Object element);"),
  
  containsAll("public boolean containsAll%s(Collection<?> collection);"),
  
  isEmpty("public boolean isEmpty%s();"),
  
  iterator("public Iterator<%s> iterator%s();"),
  
  remove("public boolean remove%s(Object element);"),
  
  removeAll("public boolean removeAll%s(Collection<?> collection);"),
  
  retainAll("public boolean retainAll%s(Collection<?> collection);"),
  
  size("public int size%s();"),
  
  toArray("public %s[] toArray%s(%s[] array);"),
  
  // ----------- java.util.Collection since 1.8 --------------------
  
  removeIf("public boolean removeIf%s(Predicate<? super %s> filter);"),
  
  spliterator("public Spliterator<%s> spliterator%s();"),
  
  stream("public Stream<%s> stream%s();"),
  
  parallelStream("public Stream<%s> parallelStream%s();"),
  
  // ----------- java.util.Iterable since 1.8 -----------------------
  
  forEach("public void forEach%s(Consumer<? super %s> action);"),
  
  // ----------- java.util.List -------------------------------------
  
  add_(" public void add%s(int index, %s element);"),
  
  addAll_("public boolean addAll%s(int index, Collection<? extends %s> collection);"),
  
  get("public %s get%s(int index);"),
  
  indexOf("public int indexOf%s(Object element);"),
  
  lastIndexOf("public int lastIndexOf%s(Object element);"),
  
  equals("public boolean equals%s(Object o);"),
  
  hashCode("public int hashCode%s();"),
  
  listIterator("public ListIterator<%s> listIterator%s();"),
  
  listIterator_("public ListIterator<%s> listIterator%s(int index);"),
  
  remove_("public %s remove%s(int index);"),
  
  subList("public List<%s> subList%s(int start, int end);"),
  
  // ----------- java.util.List since 1.8 --------------------------
  
  replaceAll("public void replaceAll%s(UnaryOperator<%s> operator);"),
  
  sort("public void sort%s(Comparator<? super %s> comparator);");
  
  // ---------------------------------------------------------------
  
  private String methodDeclaration;
  
  private AstListMethods(String header) {
    this.methodDeclaration = header;
  }
  
  public String getMethodName() {
    int pos = this.toString().lastIndexOf("_");
    return pos > 0 ? this.toString().substring(0, pos) : this.toString();
  }
  
  public String getMethodDeclaration() {
    return methodDeclaration;
  }
  
}
