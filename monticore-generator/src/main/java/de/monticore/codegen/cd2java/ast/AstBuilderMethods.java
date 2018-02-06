/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.cd2java.ast;

/**
 * Generic Methods from {@link de.monticore.ast.ASTNodeBuilder}
 */
public enum AstBuilderMethods {

  // ----------- SourcePosition -----------------------------

  set_SourcePositionEnd("public %s set_SourcePositionEnd(SourcePosition end);"),

  set_SourcePositionStart("public %s set_SourcePositionStart(SourcePosition start);"),

  // ----------- Scope & Symbol -----------------------------

  setEnclosingScope("public %s setEnclosingScope(Scope enclosingScope);"),

  setSymbol("public %s setSymbol(Symbol symbol);"),

  setSpannedScope("public %s setSpannedScope(Scope spannedScope);"),

  // ----------- PreComments -----------------------------

  clear_PreComments("public %s clear_PreComments();"),

  add_PreComments("public %s add_PreComments(Comment precomment);"),

  add_PreComments$("public %s add_PreComments(int index, Comment precomment);"),

  addAll_PreComments("public %s addAll_PreComments(Collection<Comment> precomments);"),

  addAll_PreComments$("public %s addAll_PreComments(int index, Collection<Comment> precomments);"),

  remove_PreComments("public %s remove_PreComments(Object element);"),

  remove_PreComments$("public %s remove_PreComments(int index);"),

  removeAll_PreComments("public %s removeAll_PreComments(Collection<?> element);"),

  retainAll_PreComments("public %s retainAll_PreComments(Collection<?> element);"),

  removeIf_PreComments("public %s removeIf_PreComments(Predicate<? super Comment> filter);"),

  forEach_PreComments("public %s forEach_PreComments(Consumer<? super Comment> action);"),

  replaceAll_PreComments("public %s replaceAll_PreComments(UnaryOperator<Comment> operator);"),

  sort_PreComments("public %s sort_PreComments(Comparator<? super Comment> comparator);"),

  set_PreCommentList("public %s set_PreCommentList(List<Comment> preComments);"),

  set_PreComments("public %s set_PreComments(int index, Comment precomment);"),

  //@Deprecated
  set_PreComments$("public %s set_PreComments(List<Comment> precomments);"),

  // ----------- PostComments -----------------------------

  clear_PostComments("public %s clear_PostComments();"),

  add_PostComments("public %s add_PostComments(Comment postcomment);"),

  add_PostComments$("public %s add_PostComments(int index, Comment postcomment);"),

  addAll_PostComments("public %s addAll_PostComments(Collection<Comment> postcomments);"),

  addAll_PostComments$("public %s addAll_PostComments(int index, Collection<Comment> postcomments);"),

  remove_PostComments("public %s remove_PostComments(Object element);"),

  remove_PostComments$("public %s remove_PostComments(int index);"),

  removeAll_PostComments("public %s removeAll_PostComments(Collection<?> element);"),

  retainAll_PostComments("public %s retainAll_PostComments(Collection<?> element);"),

  removeIf_PostComments("public %s removeIf_PostComments(Predicate<? super Comment> filter);"),

  forEach_PostComments("public %s forEach_PostComments(Consumer<? super Comment> action);"),

  replaceAll_PostComments("public %s replaceAll_PostComments(UnaryOperator<Comment> operator);"),

  sort_PostComments("public %s sort_PostComments(Comparator<? super Comment> comparator);"),

  set_PostCommentList("public %s set_PostCommentList(List<Comment> postComments);"),

  set_PostComments("public %s set_PostComments(int index, Comment postcomment);"),

  //@Deprecated
  set_PostComments$("public %s set_PostComments(List<Comment> postcomments);")
  ;

  private String methodDeclaration;

  private AstBuilderMethods(String header) {
    this.methodDeclaration = header;
  }

  public String getMethodName() {
    int pos = this.toString().lastIndexOf("$");
    return pos > 0 ? this.toString().substring(0, pos) : this.toString();
  }

  public String getMethodDeclaration() {
    return methodDeclaration;
  }
}
