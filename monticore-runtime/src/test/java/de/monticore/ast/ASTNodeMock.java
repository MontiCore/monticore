/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;

import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.se_rwth.commons.SourcePosition;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Mock for ASTNode. DOES NOT IMPLEMENT ANY OF THE METHODS.
 *
 */
public class ASTNodeMock implements ASTNode {
  
  public static final ASTNode INSTANCE = new ASTNodeMock();
  
  /**
   * @see de.monticore.ast.ASTNode#deepClone()
   */
  @Override
  public ASTNode deepClone() {
    return null;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#get_SourcePositionEnd()
   */
  @Override
  public SourcePosition get_SourcePositionEnd() {
    return null;
  }
  
  @Override
  public Optional<SourcePosition> get_SourcePositionEndOpt() {
    return Optional.empty();
  }
  
  /**
   * @see de.monticore.ast.ASTNode#set_SourcePositionEnd(de.se_rwth.commons.SourcePosition)
   */
  @Override
  public void set_SourcePositionEnd(SourcePosition end) {
  }
  
  @Override
  public void set_SourcePositionEndOpt(Optional<SourcePosition> end) {
  
  }
  
  @Override
  public void set_SourcePositionEndAbsent() {
  
  }
  
  @Override
  public boolean isPresent_SourcePositionEnd() {
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#get_SourcePositionStart()
   */
  @Override
  public SourcePosition get_SourcePositionStart() {
    return null;
  }
  
  @Override
  public Optional<SourcePosition> get_SourcePositionStartOpt() {
    return Optional.empty();
  }
  
  /**
   * @see de.monticore.ast.ASTNode#set_SourcePositionStart(de.se_rwth.commons.SourcePosition)
   */
  @Override
  public void set_SourcePositionStart(SourcePosition start) {
  }
  
  @Override
  public void set_SourcePositionStartOpt(Optional<SourcePosition> start) {
  
  }
  
  @Override
  public void set_SourcePositionStartAbsent() {
  
  }
  
  @Override
  public boolean isPresent_SourcePositionStart() {
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#get_PreComments()
   */
  @Override
  public List<Comment> get_PreComments() {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#set_PreComments(java.util.List)
   */
  @Override
  public void set_PreComments(List<Comment> _precomments) {
  }

  /**
   * @see de.monticore.ast.ASTNode#get_PostComments()
   */
  @Override
  public List<Comment> get_PostComments() {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#set_PostComments(java.util.List)
   */
  @Override
  public void set_PostComments(List<Comment> _postcomments) {
  }
  
  /**
   * @see de.monticore.ast.ASTNode#equalAttributes(java.lang.Object)
   */
  @Override
  public boolean equalAttributes(Object o) {
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#equalsWithComments(java.lang.Object)
   */
  @Override
  public boolean equalsWithComments(Object o) {
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#deepEquals(java.lang.Object)
   */
  @Override
  public boolean deepEquals(Object o) {
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#deepEqualsWithComments(java.lang.Object)
   */
  @Override
  public boolean deepEqualsWithComments(Object o) {
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#deepEquals(java.lang.Object, boolean)
   */
  @Override
  public boolean deepEquals(Object o, boolean forceSameOrder) {
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#deepEqualsWithComments(java.lang.Object, boolean)
   */
  @Override
  public boolean deepEqualsWithComments(Object o, boolean forceSameOrder) {
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#get_Children()
   */
  @Override
  public Collection<ASTNode> get_Children() {
    return Lists.newArrayList();
  }
  
  /**
   * @see de.monticore.ast.ASTNode#remove_Child(de.monticore.ast.ASTNode)
   */
  @Override
  public void remove_Child(ASTNode child) {
    
  }
  
  @Override
  public void setEnclosingScope(Scope enclosingScope) {
    
  }

  @Override
  public void setEnclosingScopeOpt(Optional<? extends Scope> enclosingScopeOpt) {

  }

  @Override
  public void setEnclosingScopeAbsent() {

  }

  @Override
  public Optional<? extends Scope>  getEnclosingScope() {
    return null;
  }

  @Override
  public Optional<? extends Scope> getEnclosingScopeOpt() {
    return Optional.empty();
  }

  @Override
  public void setSymbol(Symbol symbol) {
    
  }

  @Override
  public void setSymbolOpt(Optional<? extends Symbol> spannedScopeOpt) {

  }

  @Override
  public void setSymbolAbsent() {

  }

  @Override
  public Optional<? extends Symbol>  getSymbol() {
    return null;
  }

  @Override
  public Optional<? extends Symbol> getSymbolOpt() {
    return Optional.empty();
  }

  @Override
  public boolean isPresentEnclosingScope() {
    return false;
  }

  @Override
  public boolean isPresentSymbol() {
    return false;
  }

  @Override
  public void setSpannedScope(Scope spannedScope) {

  }

  @Override
  public void setSpannedScopeOpt(Optional<? extends Scope> spannedScopeOpt) {

  }

  @Override
  public void setSpannedScopeAbsent() {

  }

  @Override
  public Optional<? extends Scope> getSpannedScope() {
    return null;
  }

  @Override
  public Optional<? extends Scope> getSpannedScopeOpt() {
    return Optional.empty();
  }

  @Override
  public boolean isPresentSpannedScope() {
    return false;
  }
  
  @Override
  public void clear_PreComments() { }
  
  @Override
  public boolean add_PreComment(Comment precomment) {
    return false;
  }
  
  @Override
  public boolean addAll_PreComments(Collection<Comment> precomments) {
    return false;
  }
  
  @Override
  public boolean contains_PreComment(Object element) {
    return false;
  }
  
  @Override
  public boolean containsAll_PreComments(Collection<?> element) {
    return false;
  }
  
  @Override
  public boolean isEmpty_PreComments() {
    return false;
  }
  
  @Override
  public Iterator<Comment> iterator_PreComments() {
    
    return null;
  }
  
  @Override
  public boolean remove_PreComment(Object element) {
    return false;
  }
  
  @Override
  public boolean removeAll_PreComments(Collection<?> element) {
    return false;
  }
  
  @Override
  public boolean retainAll_PreComments(Collection<?> element) {
    return false;
  }
  
  @Override
  public int size_PreComments() {
    return 0;
  }
  
  @Override
  public Comment[] toArray_PreComments(Comment[] array) {
    return null;
  }
  
  @Override
  public boolean removeIf_PreComment(Predicate<? super Comment> filter) {
    return false;
  }
  
  @Override
  public Spliterator<Comment> spliterator_PreComments() {
    return null;
  }
  
  @Override
  public Stream<Comment> stream_PreComments() {
    return null;
  }
  
  @Override
  public Stream<Comment> parallelStream_PreComments() {
    return null;
  }
  
  @Override
  public void forEach_PreComments(Consumer<? super Comment> action) {
  }
  
  @Override
  public void add_PreComment(int index, Comment precomment) {
  }
  
  @Override
  public boolean addAll_PreComments(int index, Collection<Comment> precomments) {
    return false;
  }
  
  @Override
  public Comment get_PreComment(int index) {
    return null;
  }
  
  @Override
  public int indexOf_PreComment(Object element) {
    return 0;
  }
  
  @Override
  public int lastIndexOf_PreComment(Object element) {
    return 0;
  }
  
  @Override
  public boolean equals_PreComments(Object element) {
    return false;
  }
  
  @Override
  public int hashCode_PreComments() {
    return 0;
  }
  
  @Override
  public ListIterator<Comment> listIterator_PreComments() {
    return null;
  }
  
  @Override
  public Comment remove_PreComment(int index) {
    return null;
  }
  
  @Override
  public List<Comment> subList_PreComments(int start, int end) {
    return null;
  }
  
  @Override
  public void replaceAll_PreComments(UnaryOperator<Comment> operator) {
  }
  
  @Override
  public void sort_PreComments(Comparator<? super Comment> comparator) {
  }
  
  @Override
  public void set_PreCommentList(List<Comment> preComments) {
  }
  
  @Override
  public List<Comment> get_PreCommentList() {
    return null;
  }
  
  @Override
  public void clear_PostComments() {
  }
  
  @Override
  public boolean add_PostComment(Comment postcomment) {
    return false;
  }
  
  @Override
  public boolean addAll_PostComments(Collection<Comment> postcomments) {
    return false;
  }
  
  @Override
  public boolean contains_PostComment(Object element) {
    return false;
  }
  
  @Override
  public boolean containsAll_PostComments(Collection<?> element) {
    return false;
  }
  
  @Override
  public boolean isEmpty_PostComments() {
    return false;
  }
  
  @Override
  public Iterator<Comment> iterator_PostComments() {
    return null;
  }
  
  @Override
  public boolean remove_PostComment(Object element) {
    return false;
  }
  
  @Override
  public boolean removeAll_PostComments(Collection<?> element) {
    return false;
  }
  
  @Override
  public boolean retainAll_PostComments(Collection<?> element) {
    return false;
  }
  
  @Override
  public int size_PostComments() {
    return 0;
  }
  
  @Override
  public Comment[] toArray_PostComments(Comment[] array) {
    return null;
  }
  
  @Override
  public boolean removeIf_PostComment(Predicate<? super Comment> filter) {
    return false;
  }
  
  @Override
  public Spliterator<Comment> spliterator_PostComments() {
    return null;
  }
  
  @Override
  public Stream<Comment> stream_PostComments() {
    return null;
  }
  
  @Override
  public Stream<Comment> parallelStream_PostComments() {
    return null;
  }
  
  @Override
  public void forEach_PostComments(Consumer<? super Comment> action) {
  }
  
  @Override
  public void add_PostComment(int index, Comment postcomment) {
  }
  
  @Override
  public boolean addAll_PostComments(int index, Collection<Comment> postcomments) {
    return false;
  }
  
  @Override
  public Comment get_PostComment(int index) {
    return null;
  }
  
  @Override
  public int indexOf_PostComment(Object element) {
    return 0;
  }
  
  @Override
  public int lastIndexOf_PostComment(Object element) {
    return 0;
  }
  
  @Override
  public boolean equals_PostComments(Object element) {
    return false;
  }
  
  @Override
  public int hashCode_PostComments() {
    return 0;
  }
  
  @Override
  public ListIterator<Comment> listIterator_PostComments() {
    return null;
  }
  
  @Override
  public Comment remove_PostComment(int index) {
    return null;
  }
  
  @Override
  public List<Comment> subList_PostComments(int start, int end) {
    return null;
  }
  
  @Override
  public void replaceAll_PostComments(UnaryOperator<Comment> operator) {
  }
  
  @Override
  public void sort_PostComments(Comparator<? super Comment> comparator) {
  }
  
  @Override
  public void set_PostCommentList(List<Comment> postComments) {
  }
  
  @Override
  public List<Comment> get_PostCommentList() {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#listIterator_PreComments(int)
   */
  @Override
  public ListIterator<Comment> listIterator_PreComments(int index) {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#set_PreComment(int, de.monticore.ast.Comment)
   */
  @Override
  public Comment set_PreComment(int index, Comment precomment) {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#toArray_PreComments()
   */
  @Override
  public Object[] toArray_PreComments() {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#listIterator_PostComments(int)
   */
  @Override
  public ListIterator<Comment> listIterator_PostComments(int index) {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#set_PostComment(int, de.monticore.ast.Comment)
   */
  @Override
  public Comment set_PostComment(int index, Comment postcomment) {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#toArray_PostComments()
   */
  @Override
  public Object[] toArray_PostComments() {
    return null;
  }
}
