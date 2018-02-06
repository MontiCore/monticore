/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

package de.monticore.ast;

import com.google.common.collect.Lists;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Foundation class for all ASTBuilder.
 */
public abstract class ASTNodeBuilder<T extends ASTNodeBuilder> {

  protected Optional<SourcePosition> start = Optional.empty();

  protected Optional<SourcePosition> end = Optional.empty();

  protected List<Comment> precomments = Lists.newArrayList();

  protected List<Comment> postcomments = Lists.newArrayList();

  protected Optional<? extends Symbol> symbol = Optional.empty();

  protected Optional<? extends Scope> enclosingScope = Optional.empty();

  protected Optional<? extends Scope> spannedScope = Optional.empty();

  protected T realBuilder;

  protected ASTNodeBuilder() {
    this.realBuilder = (T) this;
  }


  public SourcePosition get_SourcePositionEnd() {
    if (end.isPresent()) {
      return end.get();
    }
    return SourcePosition.getDefaultSourcePosition();
  }


  public T set_SourcePositionEnd(SourcePosition end) {
    this.end = Optional.ofNullable(end);
    return realBuilder;
  }


  public SourcePosition get_SourcePositionStart() {
    if (start.isPresent()) {
      return start.get();
    }
    return SourcePosition.getDefaultSourcePosition();
  }


  public T set_SourcePositionStart(SourcePosition start) {
    this.start = Optional.ofNullable(start);
    return this.realBuilder;
  }

  /**
   * Can be removed after 4.5.5 Replace with List<Comment> get_PreCommentList(); use
   * {@link #List<Comment> get_PreCommentList()} instead
   */
  @Deprecated
  public List<Comment> get_PreComments() {
    return precomments;
  }

  /**
   * Can be removed after 4.5.5 Replace with List<Comment> set_PreCommentList(); use
   * {@link #List<Comment> set_PreCommentList()} instead
   */
  @Deprecated
  public T set_PreComments(List<Comment> precomments) {
    this.precomments = precomments;
    return this.realBuilder;
  }

  /**
   * Can be removed after 4.5.5 Replace with List<Comment> get_PostCommentList(); use
   * {@link #List<Comment> get_PostCommentList()} instead
   */
  @Deprecated
  public List<Comment> get_PostComments() {
    return postcomments;
  }

  /**
   * Can be removed after 4.5.5 Replace with List<Comment> set_PostCommentList(); use
   * {@link #List<Comment> set_PostCommentList()} instead
   */
  @Deprecated
  public T set_PostComments(List<Comment> postcomments) {
    this.postcomments = postcomments;
    return this.realBuilder;
  }


  public T setEnclosingScope(Scope enclosingScope) {
    this.enclosingScope = Optional.ofNullable(enclosingScope);
    return this.realBuilder;
  }


  public Optional<? extends Scope> getEnclosingScope() {
    return enclosingScope;
  }


  public boolean enclosingScopeIsPresent() {
    return enclosingScope.isPresent();
  }


  public T setSymbol(Symbol symbol) {
    this.symbol = Optional.ofNullable(symbol);
    return this.realBuilder;
  }


  public Optional<? extends Symbol> getSymbol() {
    return symbol;
  }


  public boolean symbolIsPresent() {
    return symbol.isPresent();
  }


  public T setSpannedScope(Scope spannedScope) {
    this.spannedScope = Optional.ofNullable(spannedScope);
    return this.realBuilder;
  }


  public Optional<? extends Scope> getSpannedScope() {
    if (spannedScope.isPresent()) {
      return spannedScope;
    }

    Optional<? extends Scope> result = Optional.empty();
    if (getSymbol().isPresent() && (getSymbol().get() instanceof ScopeSpanningSymbol)) {
      final ScopeSpanningSymbol sym = (ScopeSpanningSymbol) getSymbol().get();
      result = Optional.of(sym.getSpannedScope());
    }

    return result;
  }


  public boolean spannedScopeIsPresent() {
    return spannedScope.isPresent();
  }


  public T clear_PreComments() {
    this.precomments.clear();
    return this.realBuilder;
  }


  public T add_PreComments(Comment precomment) {
    this.precomments.add(precomment);
    return this.realBuilder;
  }


  public T addAll_PreComments(Collection<Comment> precomments) {
    this.precomments.addAll(precomments);
    return this.realBuilder;
  }


  public boolean contains_PreComments(Object element) {
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


  public T remove_PreComments(Object element) {
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


  public T removeIf_PreComments(Predicate<? super Comment> filter) {
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


  public T add_PreComments(int index, Comment precomment) {
    this.precomments.add(index, precomment);
    return this.realBuilder;
  }


  public T addAll_PreComments(int index, Collection<Comment> precomments) {
    this.precomments.addAll(index, precomments);
    return this.realBuilder;
  }


  public Comment get_PreComments(int index) {
    return this.precomments.get(index);
  }


  public int indexOf_PreComments(Object element) {
    return this.precomments.indexOf(element);
  }

  public int lastIndexOf_PreComments(Object element) {
    return this.precomments.lastIndexOf(element);
  }


  public boolean equals_PreComments(Object element) {
    return this.precomments.equals(element);
  }


  public int hashCode_PreComments() {
    return this.precomments.hashCode();
  }


  public ListIterator<Comment> ListIterator_PreComments() {
    return this.precomments.listIterator();
  }


  public T remove_PreComments(int index) {
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


  public T set_PreComments(int index, Comment precomment) {
    this.precomments.set(index, precomment);
    return this.realBuilder;
  }


  public Object[] toArray_PreComments() {
    return this.precomments.toArray();
  }


  public T clear_PostComments() {
    this.postcomments.clear();
    return this.realBuilder;
  }


  public T add_PostComments(Comment postcomment) {
    this.postcomments.add(postcomment);
    return this.realBuilder;
  }


  public T addAll_PostComments(Collection<Comment> postcomments) {
    this.postcomments.addAll(postcomments);
    return this.realBuilder;
  }


  public boolean contains_PostComments(Object element) {
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


  public T remove_PostComments(Object element) {
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


  public T removeIf_PostComments(Predicate<? super Comment> filter) {
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


  public T add_PostComments(int index, Comment postcomment) {
    this.postcomments.add(index, postcomment);
    return this.realBuilder;
  }


  public T addAll_PostComments(int index, Collection<Comment> postcomments) {
    this.postcomments.addAll(index, postcomments);
    return this.realBuilder;
  }


  public Comment get_PostComments(int index) {
    return this.postcomments.get(index);
  }


  public int indexOf_PostComments(Object element) {
    return this.postcomments.indexOf(element);
  }

  public int lastIndexOf_PostComments(Object element) {
    return this.postcomments.lastIndexOf(element);
  }


  public boolean equals_PostComments(Object element) {
    return this.postcomments.equals(element);
  }


  public int hashCode_PostComments() {
    return this.postcomments.hashCode();
  }


  public ListIterator<Comment> ListIterator_PostComments() {
    return this.postcomments.listIterator();
  }


  public T remove_PostComments(int index) {
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


  public T set_PostComments(int index, Comment precomment) {
    this.postcomments.set(index, precomment);
    return this.realBuilder;
  }


  public Object[] toArray_PostComments() {
    return this.postcomments.toArray();
  }

}
