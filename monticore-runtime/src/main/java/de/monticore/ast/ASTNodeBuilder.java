/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;

import com.google.common.collect.Lists;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Foundation class for all ASTBuilder.
 */
public abstract class ASTNodeBuilder<T extends ASTNodeBuilder> {
  
  protected Optional<SourcePosition> sourcePositionStart = Optional.empty();
  
  protected Optional<SourcePosition> sourcePositionEnd = Optional.empty();
  
  protected List<Comment> precomments = Lists.newArrayList();
  
  protected List<Comment> postcomments = Lists.newArrayList();
  
  protected Optional<? extends Symbol> symbol = Optional.empty();
  
  protected Optional<? extends Scope> enclosingScope = Optional.empty();
  
  protected Optional<? extends Scope> spannedScope = Optional.empty();
  
  protected T realBuilder;
  
  protected ASTNodeBuilder() {
    this.realBuilder = (T) this;
  }
  
  // ----------------------------------------------------------------------
  // Handle the Optional SourcePosition end
  // ----------------------------------------------------------------------
  
  public T set_SourcePositionEnd(SourcePosition end) {
    this.sourcePositionEnd = Optional.ofNullable(end);
    return this.realBuilder;
  }
  
  public Optional<SourcePosition> get_SourcePositionEndOpt() {
    return sourcePositionEnd;
  }
  
  public SourcePosition get_SourcePositionEnd() {
    if (get_SourcePositionEndOpt().isPresent()) {
      return get_SourcePositionEndOpt().get();
    }
    Log.error("0xB9266 get_SourcePositionEndOpt can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }
  
  public boolean isPresent_SourcePositionEnd() {
    return get_SourcePositionEndOpt().isPresent();
  }
  
  public T set_SourcePositionEndAbsent() {
    sourcePositionEnd = Optional.empty();
    return this.realBuilder;
  }
  
  public T set_SourcePositionEndOpt(Optional<SourcePosition> value) {
    this.sourcePositionEnd = value;
    return this.realBuilder;
  }
  
  // ----------------------------------------------------------------------
  // Handle the Optional SourcePosition start
  // ----------------------------------------------------------------------
  
  public T set_SourcePositionStart(SourcePosition start) {
    this.sourcePositionStart = Optional.ofNullable(start);
    return this.realBuilder;
  }
  
  public Optional<SourcePosition> get_SourcePositionStartOpt() {
    return sourcePositionStart;
  }
  
  public SourcePosition get_SourcePositionStart() {
    if (get_SourcePositionStartOpt().isPresent()) {
      return get_SourcePositionStartOpt().get();
    }
    Log.error("0xB9267 get_SourcePositionStartOpt can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }
  
  public boolean isPresent_SourcePositionStart() {
    return get_SourcePositionStartOpt().isPresent();
  }
  
  public T set_SourcePositionStartAbsent() {
    sourcePositionStart = Optional.empty();
    return this.realBuilder;
  }
  
  public T set_SourcePositionStartOpt(Optional<SourcePosition> value) {
    this.sourcePositionStart = value;
    return this.realBuilder;
  }
  
  // ----------------------------------------------------------------------
  // Handle the Optional Enclosing Scope
  // ----------------------------------------------------------------------
  
  /**
   * set Enclosing Scope
   */
  public T setEnclosingScope(Scope enclosingScope) {
    this.enclosingScope = Optional.ofNullable(enclosingScope);
    return this.realBuilder;
  }
  
  public Optional<? extends Scope> getEnclosingScopeOpt() {
    return enclosingScope;
  }
  
  public Scope getEnclosingScope() {
    if (getEnclosingScopeOpt().isPresent()) {
      return getEnclosingScopeOpt().get();
    }
    Log.error("0xB9262 getEnclosingScopeOpt can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }
  
  public boolean isPresentEnclosingScope() {
    return getEnclosingScopeOpt().isPresent();
  }
  
  public T setEnclosingScopeAbsent() {
    enclosingScope = Optional.empty();
    return this.realBuilder;
  }
  
  public T setEnclosingScopeOpt(Optional<? extends Scope> value) {
    this.enclosingScope = value;
    return this.realBuilder;
  }
  
  // ----------------------------------------------------------------------
  // Handle the optional Symbol
  // ----------------------------------------------------------------------
  
  public T setSymbol(Symbol symbol) {
    this.symbol = Optional.ofNullable(symbol);
    return this.realBuilder;
  }
  
  public Optional<? extends Symbol> getSymbolOpt() {
    return symbol;
  }
  
  public Symbol getSymbol() {
    if (getSymbolOpt().isPresent()) {
      return getSymbolOpt().get();
    }
    Log.error("0xB9263 getSymbolOpt can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }
  
  public boolean isPresentSymbol() {
    return getSymbolOpt().isPresent();
  }
  
  public T setSymbolAbsent() {
    symbol = Optional.empty();
    return this.realBuilder;
  }
  
  public T setSymbolOpt(Optional<? extends Symbol> value) {
    this.symbol = value;
    return this.realBuilder;
  }
  
  // ----------------------------------------------------------------------
  // Handle the optional Spanned Scope
  // ----------------------------------------------------------------------
  
  public T setSpannedScope(Scope spannedScope) {
    this.spannedScope = Optional.ofNullable(spannedScope);
    return this.realBuilder;
  }
  
  public Optional<? extends Scope> getSpannedScopeOpt() {
    return spannedScope;
  }
  
  public Scope getSpannedScope() {
    if (getSpannedScopeOpt().isPresent()) {
      return getSpannedScopeOpt().get();
    }
    Log.error("0xB9264 getSpannedScopeOpt can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }
  
  public boolean isPresentSpannedScope() {
    return getSpannedScopeOpt().isPresent();
  }
  
  public T setSpannedScopeAbsent() {
    spannedScope = Optional.empty();
    return this.realBuilder;
  }
  
  public T setSpannedScopeOpt(Optional<? extends Scope> value) {
    this.spannedScope = value;
    return this.realBuilder;
  }
  
  // ----------------------------------------------------------------------
  // Handle Pre Comments
  // ----------------------------------------------------------------------
  
  public T clear_PreComments() {
    this.precomments.clear();
    return this.realBuilder;
  }
  
  public T add_PreComment(Comment precomment) {
    this.precomments.add(precomment);
    return this.realBuilder;
  }
  
  public T addAll_PreComments(Collection<Comment> precomments) {
    this.precomments.addAll(precomments);
    return this.realBuilder;
  }
  
  public boolean contains_PreComment(Object element) {
    return this.precomments.contains(element);
  }
  
  public boolean containsAll_PreComments(Collection<?> element) {
    return this.precomments.containsAll(element);
  }
  
  public boolean isEmpty_PreComments() {
    return this.precomments.isEmpty();
  }
  
  public Iterator<Comment> iterator_PreComments() {
    return this.precomments.iterator();
  }
  
  public T remove_PreComment(Object element) {
    this.precomments.remove(element);
    return this.realBuilder;
  }
  
  public T removeAll_PreComments(Collection<?> element) {
    this.precomments.removeAll(element);
    return this.realBuilder;
  }
  
  public T retainAll_PreComments(Collection<?> element) {
    this.precomments.retainAll(element);
    return this.realBuilder;
  }
  
  public int size_PreComments() {
    return this.precomments.size();
  }
  
  public Comment[] toArray_PreComments(Comment[] array) {
    return this.precomments.toArray(array);
  }
  
  public T removeIf_PreComment(Predicate<? super Comment> filter) {
    this.precomments.removeIf(filter);
    return this.realBuilder;
  }
  
  public Spliterator<Comment> spliterator_PreComments() {
    return this.precomments.spliterator();
  }
  
  public Stream<Comment> stream_PreComments() {
    return this.precomments.stream();
  }
  
  public Stream<Comment> parallelStream_PreComments() {
    return this.precomments.parallelStream();
  }
  
  public T forEach_PreComments(Consumer<? super Comment> action) {
    this.precomments.forEach(action);
    return this.realBuilder;
  }
  
  public T add_PreComment(int index, Comment precomment) {
    this.precomments.add(index, precomment);
    return this.realBuilder;
  }
  
  public T addAll_PreComments(int index, Collection<Comment> precomments) {
    this.precomments.addAll(index, precomments);
    return this.realBuilder;
  }
  
  public Comment get_PreComment(int index) {
    return this.precomments.get(index);
  }
  
  public int indexOf_PreComment(Object element) {
    return this.precomments.indexOf(element);
  }
  
  public int lastIndexOf_PreComment(Object element) {
    return this.precomments.lastIndexOf(element);
  }
  
  public boolean equals_PreComments(Object element) {
    return this.precomments.equals(element);
  }
  
  public int hashCode_PreComments() {
    return this.precomments.hashCode();
  }
  
  public ListIterator<Comment> listIterator_PreComments() {
    return this.precomments.listIterator();
  }
  
  public T remove_PreComment(int index) {
    this.precomments.remove(index);
    return this.realBuilder;
  }
  
  public List<Comment> subList_PreComments(int start, int end) {
    return this.precomments.subList(start, end);
  }
  
  public T replaceAll_PreComments(UnaryOperator<Comment> operator) {
    this.precomments.replaceAll(operator);
    return this.realBuilder;
  }
  
  public T sort_PreComments(Comparator<? super Comment> comparator) {
    this.precomments.sort(comparator);
    return this.realBuilder;
  }
  
  public T set_PreCommentList(List<Comment> preComments) {
    this.precomments = preComments;
    return this.realBuilder;
  }
  
  public List<Comment> get_PreCommentList() {
    return this.precomments;
  }
  
  public ListIterator<Comment> listIterator_PreComments(int index) {
    return this.precomments.listIterator(index);
  }
  
  public T set_PreComment(int index, Comment precomment) {
    this.precomments.set(index, precomment);
    return this.realBuilder;
  }
  
  public Object[] toArray_PreComments() {
    return this.precomments.toArray();
  }
  
  // ----------------------------------------------------------------------
  // Handle Post Comments
  // ----------------------------------------------------------------------
  
  public T clear_PostComments() {
    this.postcomments.clear();
    return this.realBuilder;
  }
  
  public T add_PostComment(Comment postcomment) {
    this.postcomments.add(postcomment);
    return this.realBuilder;
  }
  
  public T addAll_PostComments(Collection<Comment> postcomments) {
    this.postcomments.addAll(postcomments);
    return this.realBuilder;
  }
  
  public boolean contains_PostComment(Object element) {
    return this.postcomments.contains(element);
  }
  
  public boolean containsAll_PostComments(Collection<?> element) {
    return this.postcomments.containsAll(element);
  }
  
  public boolean isEmpty_PostComments() {
    return this.postcomments.isEmpty();
  }
  
  public Iterator<Comment> iterator_PostComments() {
    return this.postcomments.iterator();
  }
  
  public T remove_PostComment(Object element) {
    this.postcomments.remove(element);
    return this.realBuilder;
  }
  
  public T removeAll_PostComments(Collection<?> element) {
    this.postcomments.removeAll(element);
    return this.realBuilder;
  }
  
  public T retainAll_PostComments(Collection<?> element) {
    this.postcomments.retainAll(element);
    return this.realBuilder;
  }
  
  public int size_PostComments() {
    return this.postcomments.size();
  }
  
  public Comment[] toArray_PostComments(Comment[] array) {
    return this.postcomments.toArray(array);
  }
  
  public T removeIf_PostComment(Predicate<? super Comment> filter) {
    this.postcomments.removeIf(filter);
    return this.realBuilder;
  }
  
  public Spliterator<Comment> spliterator_PostComments() {
    return this.postcomments.spliterator();
  }
  
  public Stream<Comment> stream_PostComments() {
    return this.postcomments.stream();
  }
  
  public Stream<Comment> parallelStream_PostComments() {
    return this.postcomments.parallelStream();
  }
  
  public T forEach_PostComments(Consumer<? super Comment> action) {
    this.postcomments.forEach(action);
    return this.realBuilder;
  }
  
  public T add_PostComment(int index, Comment postcomment) {
    this.postcomments.add(index, postcomment);
    return this.realBuilder;
  }
  
  public T addAll_PostComments(int index, Collection<Comment> postcomments) {
    this.postcomments.addAll(index, postcomments);
    return this.realBuilder;
  }
  
  public Comment get_PostComment(int index) {
    return this.postcomments.get(index);
  }
  
  public int indexOf_PostComment(Object element) {
    return this.postcomments.indexOf(element);
  }
  
  public int lastIndexOf_PostComment(Object element) {
    return this.postcomments.lastIndexOf(element);
  }
  
  public boolean equals_PostComments(Object element) {
    return this.postcomments.equals(element);
  }
  
  public int hashCode_PostComments() {
    return this.postcomments.hashCode();
  }
  
  public ListIterator<Comment> listIterator_PostComments() {
    return this.postcomments.listIterator();
  }
  
  public T remove_PostComment(int index) {
    this.postcomments.remove(index);
    return this.realBuilder;
  }
  
  public List<Comment> subList_PostComments(int start, int end) {
    return this.postcomments.subList(start, end);
  }
  
  public T replaceAll_PostComments(UnaryOperator<Comment> operator) {
    this.postcomments.replaceAll(operator);
    return this.realBuilder;
  }
  
  public T sort_PostComments(Comparator<? super Comment> comparator) {
    this.postcomments.sort(comparator);
    return this.realBuilder;
  }
  
  public T set_PostCommentList(List<Comment> postComments) {
    this.postcomments = postComments;
    return this.realBuilder;
  }
  
  public List<Comment> get_PostCommentList() {
    return this.postcomments;
  }
  
  public ListIterator<Comment> listIterator_PostComments(int index) {
    return this.postcomments.listIterator(index);
  }
  
  public T set_PostComment(int index, Comment precomment) {
    this.postcomments.set(index, precomment);
    return this.realBuilder;
  }
  
  public Object[] toArray_PostComments() {
    return this.postcomments.toArray();
  }
  
}
