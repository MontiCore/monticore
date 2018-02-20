/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.ast;

/**
 * Generic Methods from {@link de.monticore.ast.ASTNodeBuilder}
 */
public enum AstBuilderMethods {

  // ----------- SourcePosition -----------------------------

  set_SourcePositionEnd("public %s set_SourcePositionEnd(SourcePosition end);"),
  
  set_SourcePositionEndOpt("public %s set_SourcePositionEndOpt(Optional<SourcePosition> end);"),
  
  set_SourcePositionEndAbsent("public %s set_SourcePositionEndAbsent();"),

  set_SourcePositionStart("public %s set_SourcePositionStart(SourcePosition start);"),
  
  set_SourcePositionStartOpt("public %s set_SourcePositionStartOpt(Optional<SourcePosition> Start);"),
  
  set_SourcePositionStartAbsent("public %s set_SourcePositionStartAbsent();"),

  // ----------- Scope & Symbol -----------------------------

  setEnclosingScope("public %s setEnclosingScope(Scope enclosingScope);"),
  
  setEnclosingScopeOpt("public %s setEnclosingScopeOpt(Optional<? extends Scope> enclosingScopeOpt);"),

  setEnclosingScopeAbsent("public %s setEnclosingScopeAbsent();"),

  setSymbol("public %s setSymbol(Symbol symbol);"),

  setSymbolOpt("public %s setSymbolOpt(Optional<? extends Symbol> symbol);"),

  setSymbolAbsent("public %s setSymbolAbsent();"),

  setSpannedScope("public %s setSpannedScope(Scope spannedScope);"),

  setSpannedScopeOpt("public %s setSpannedScopeOpt(Optional<? extends Scope> enclosingScopeOpt);"),

  setSpannedScopeAbsent("public %s setSpannedScopeAbsent();"),

  // ----------- PreComments -----------------------------

  clear_PreComments("public %s clear_PreComments();"),

  add_PreComment("public %s add_PreComment(Comment precomment);"),

  add_PreComment$("public %s add_PreComment(int index, Comment precomment);"),

  addAll_PreComments("public %s addAll_PreComments(Collection<Comment> precomments);"),

  addAll_PreComments$("public %s addAll_PreComments(int index, Collection<Comment> precomments);"),

  remove_PreComment("public %s remove_PreComment(Object element);"),

  remove_PreComment$("public %s remove_PreComment(int index);"),

  removeAll_PreComments("public %s removeAll_PreComments(Collection<?> element);"),

  retainAll_PreComments("public %s retainAll_PreComments(Collection<?> element);"),

  removeIf_PreComment("public %s removeIf_PreComment(Predicate<? super Comment> filter);"),

  forEach_PreComments("public %s forEach_PreComments(Consumer<? super Comment> action);"),

  replaceAll_PreComments("public %s replaceAll_PreComments(UnaryOperator<Comment> operator);"),

  sort_PreComments("public %s sort_PreComments(Comparator<? super Comment> comparator);"),

  set_PreCommentList("public %s set_PreCommentList(List<Comment> preComments);"),

  set_PreComment("public %s set_PreComment(int index, Comment precomment);"),

  // ----------- PostComments -----------------------------

  clear_PostComments("public %s clear_PostComments();"),

  add_PostComment("public %s add_PostComment(Comment postcomment);"),

  add_PostComment$("public %s add_PostComment(int index, Comment postcomment);"),

  addAll_PostComments("public %s addAll_PostComments(Collection<Comment> postcomments);"),

  addAll_PostComments$("public %s addAll_PostComments(int index, Collection<Comment> postcomments);"),

  remove_PostComment("public %s remove_PostComment(Object element);"),

  remove_PostComment$("public %s remove_PostComment(int index);"),

  removeAll_PostComments("public %s removeAll_PostComments(Collection<?> element);"),

  retainAll_PostComments("public %s retainAll_PostComments(Collection<?> element);"),

  removeIf_PostComment("public %s removeIf_PostComment(Predicate<? super Comment> filter);"),

  forEach_PostComments("public %s forEach_PostComments(Consumer<? super Comment> action);"),

  replaceAll_PostComments("public %s replaceAll_PostComments(UnaryOperator<Comment> operator);"),

  sort_PostComments("public %s sort_PostComments(Comparator<? super Comment> comparator);"),

  set_PostCommentList("public %s set_PostCommentList(List<Comment> postComments);"),

  set_PostComment("public %s set_PostComment(int index, Comment postcomment);")
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
