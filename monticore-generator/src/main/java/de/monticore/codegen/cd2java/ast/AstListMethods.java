/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.ast;

/**
 * Methods-profile for list-valued ast attributes.
 *
 * @author Galina Volkova
 */

public enum AstListMethods {
  
  // ----------- java.util.Collection -----------------------------
  
  clear("public %s clear%s();"),//void
  
  add("public %s add%s(%s element);"),//boolean
  
  addAll("public %s addAll%s(Collection<? extends %s> collection);"),//boolean
  
  contains("public boolean contains%s(Object element);"),
  
  containsAll("public boolean containsAll%s(Collection<?> collection);"),
  
  isEmpty("public boolean isEmpty%s();"),
  
  iterator("public Iterator<%s> iterator%s();"),
  
  remove("public %s remove%s(Object element);"),//boolean
  
  removeAll("public %s removeAll%s(Collection<?> collection);"),//boolean
  
  retainAll("public %s retainAll%s(Collection<?> collection);"),//boolean
  
  size("public int size%s();"),
  
  toArray("public %s[] toArray%s(%s[] array);"),
  
  toArray_("public Object[] toArray%s();"),
  
  // ----------- java.util.Collection since 1.8 --------------------
  
  removeIf("public %s removeIf%s(Predicate<? super %s> filter);"),//boolean
  
  spliterator("public Spliterator<%s> spliterator%s();"),
  
  stream("public Stream<%s> stream%s();"),
  
  parallelStream("public Stream<%s> parallelStream%s();"),
  
  // ----------- java.util.Iterable since 1.8 -----------------------
  
  forEach("public %s forEach%s(Consumer<? super %s> action);"),//void
  
  // ----------- java.util.List -------------------------------------
  
  add_(" public %s add%s(int index, %s element);"),//void
  
  addAll_("public %s addAll%s(int index, Collection<? extends %s> collection);"),//boolean
  
  get("public %s get%s(int index);"),
  
  indexOf("public int indexOf%s(Object element);"),
  
  lastIndexOf("public int lastIndexOf%s(Object element);"),
  
  equals("public boolean equals%s(Object o);"),
  
  hashCode("public int hashCode%s();"),
  
  listIterator("public ListIterator<%s> listIterator%s();"),
  
  listIterator_("public ListIterator<%s> listIterator%s(int index);"),
  
  remove_("public %s remove%s(int index);"),
  
  set_("public %s set%s(int index, %s element);"),
  
  subList("public List<%s> subList%s(int start, int end);"),
  
  // ----------- java.util.List since 1.8 --------------------------
  
  replaceAll("public %s replaceAll%s(UnaryOperator<%s> operator);"),//void
  
  sort("public %s sort%s(Comparator<? super %s> comparator);");//void
  
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
