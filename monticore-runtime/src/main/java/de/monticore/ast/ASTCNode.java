/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import de.se_rwth.commons.logging.Log;

import com.google.common.collect.Lists;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;

/**
 * Foundation class of all AST-classes Shouldn't be used in an implementation, all AST-classes also
 * share the interface ASTNode
 *
 * @author krahn, volkova
 */
public abstract class ASTCNode implements ASTNode, Cloneable {

    protected Optional<SourcePosition> start = Optional.empty();

    protected Optional<SourcePosition> end = Optional.empty();

    protected List<Comment> precomments = Lists.newArrayList();

    protected List<Comment> postcomments = Lists.newArrayList();

    protected Optional<? extends Symbol> symbol = Optional.empty();

    protected Optional<? extends Scope> enclosingScope = Optional.empty();

    protected Optional<? extends Scope> spannedScope = Optional.empty();

    public abstract ASTNode deepClone();
  
  // ----------------------------------------------------------------------
  // Handle the Optional SourcePosition end
  // ----------------------------------------------------------------------

    public SourcePosition get_SourcePositionEnd() {
        if (end.isPresent()) {
            return end.get();
        }
        return SourcePosition.getDefaultSourcePosition();
    }

    public void set_SourcePositionEnd(SourcePosition end) {
        this.end = Optional.ofNullable(end);
    }
  
    public Optional<SourcePosition> get_SourcePositionEndOpt() {
      return end;
    }
  
    public boolean isPresent_SourcePositionEnd()   {
      return get_SourcePositionEndOpt().isPresent();
    }
  
    public void set_SourcePositionEndAbsent()   {
      end = Optional.empty();
    }
  
    public void set_SourcePositionEndOpt(Optional<SourcePosition> value)   {
      this.end = value;
    }
  
  // ----------------------------------------------------------------------
  // Handle the Optional SourcePosition start
  // ----------------------------------------------------------------------
  
    public SourcePosition get_SourcePositionStart() {
        if (start.isPresent()) {
            return start.get();
        }
        return SourcePosition.getDefaultSourcePosition();
    }

    public void set_SourcePositionStart(SourcePosition start) {
        this.start = Optional.ofNullable(start);
    }
  
    public Optional<SourcePosition> get_SourcePositionStartOpt() {
      return start;
     }
  
    public boolean isPresent_SourcePositionStart()   {
      return get_SourcePositionStartOpt().isPresent();
    }
  
    public void set_SourcePositionStartAbsent()   {
      start = Optional.empty();
    }
  
    public void set_SourcePositionStartOpt(Optional<SourcePosition> value)   {
      this.start = value;
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
    public void set_PreComments(List<Comment> precomments) {
        this.precomments = precomments;
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
    public void set_PostComments(List<Comment> postcomments) {
        this.postcomments = postcomments;
    }
  
  // ----------------------------------------------------------------------
  // Handle the Optional Enclosing Scope
  // ----------------------------------------------------------------------
  
    @Override
    public void setEnclosingScope(Scope enclosingScope) {
        this.enclosingScope = Optional.ofNullable(enclosingScope);
    }

    @Override
    public void setEnclosingScopeOpt(Optional<? extends Scope > enclosingScopeOpt) {
        this.enclosingScope = enclosingScopeOpt ;
    }

    @Override
    public void setEnclosingScopeAbsent() {
        this.enclosingScope = Optional.empty();
    }

    @Deprecated
    @Override
    public Optional<? extends Scope> getEnclosingScope() {
        return enclosingScope;
    }


//    @Override
//    public Scope getEnclosingScope() {
//        if (getEnclosingScopeOpt().isPresent()) {
//            return getEnclosingScopeOpt().get();
//        }
//        Log.error("0xA7003 x222 getCloneASTOpt can't return a value. It is empty.");
//        // Normally this statement is not reachable
//        throw new IllegalStateException();
//    }

    @Override
    public Optional<? extends Scope> getEnclosingScopeOpt() {
        return this.enclosingScope;
    }

    @Override
    public boolean isPresentEnclosingScope() {
        return enclosingScope.isPresent();
    }
  
  // ----------------------------------------------------------------------
  // Handle the optional Symbol
  // ----------------------------------------------------------------------
  
    @Override
    public void setSymbol(Symbol symbol) {
        this.symbol = Optional.ofNullable(symbol);
    }

    @Override
    public void setSymbolOpt(Optional<? extends Symbol > enclosingSymbolOpt) {
        this.symbol = enclosingSymbolOpt ;
    }

    @Override
    public void setSymbolAbsent() {
        this.symbol = Optional.empty();
    }

    @Deprecated
    @Override
    public Optional<? extends Symbol> getSymbol() {
        return symbol;
    }

//    @Override
//    public Symbol getSymbol() {
//        if (getSymbolOpt().isPresent()) {
//            return getSymbolOpt().get();
//        }
//        Log.error("0xA7003 x222 getCloneASTOpt can't return a value. It is empty.");
//        // Normally this statement is not reachable
//        throw new IllegalStateException();
//    }

    @Override
    public Optional<? extends Symbol> getSymbolOpt() {
        return this.symbol;
    }

    @Override
    public boolean isPresentSymbol() {
        return symbol.isPresent();
    }
  
  // ----------------------------------------------------------------------
  // Handle the optional Spanned Scope
  // ----------------------------------------------------------------------
  
    @Override
    public void setSpannedScope(Scope spannedScope) {
        this.spannedScope = Optional.ofNullable(spannedScope);
    }

    @Override
    public void setSpannedScopeOpt(Optional<? extends Scope > spannedScopeOpt) {
        this.spannedScope = spannedScopeOpt ;
    }

    @Override
    public void setSpannedScopeAbsent() {
        this.spannedScope = Optional.empty();
    }

    @Deprecated
    @Override
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

//    @Override
//    public Scope getSpannedScope() {
//        if (getSpannedScopeOpt().isPresent()) {
//            return getSpannedScopeOpt().get();
//        }
//        Log.error("0xA7003 x222 getCloneASTOpt can't return a value. It is empty.");
//        // Normally this statement is not reachable
//        throw new IllegalStateException();
//    }

    @Override
    public Optional<? extends Scope> getSpannedScopeOpt() {
        return this.spannedScope;
    }

    @Override
    public boolean isPresentSpannedScope() {
        return spannedScope.isPresent();
    }
  
  // ----------------------------------------------------------------------
  // Handle Pre Comments
  // ----------------------------------------------------------------------
  
    @Override
    public void clear_PreComments() {
        this.precomments.clear();
    }

    @Override
    public boolean add_PreComment(Comment precomment) {
        return this.precomments.add(precomment);
    }

    @Override
    public boolean addAll_PreComments(Collection<Comment> precomments) {
        return this.precomments.addAll(precomments);
    }

    @Override
    public boolean contains_PreComment(Object element) {
        return this.precomments.contains(element);
    }

    @Override
    public boolean containsAll_PreComments(Collection<?> element) {
        return this.precomments.containsAll(element);
    }

    @Override
    public boolean isEmpty_PreComments() {
        return this.precomments.isEmpty();
    }

    @Override
    public Iterator<Comment> iterator_PreComments() {
        return this.precomments.iterator();
    }

    @Override
    public boolean remove_PreComment(Object element) {
        return this.precomments.remove(element);
    }

    @Override
    public boolean removeAll_PreComments(Collection<?> element) {
        return this.precomments.removeAll(element);
    }

    @Override
    public boolean retainAll_PreComments(Collection<?> element) {
        return this.precomments.retainAll(element);
    }

    @Override
    public int size_PreComments() {
        return this.precomments.size();
    }

    @Override
    public Comment[] toArray_PreComments(Comment[] array) {
        return this.precomments.toArray(array);
    }

    @Override
    public boolean removeIf_PreComment(Predicate<? super Comment> filter) {
        return this.precomments.removeIf(filter);
    }

    @Override
    public Spliterator<Comment> spliterator_PreComments() {
        return this.precomments.spliterator();
    }

    @Override
    public Stream<Comment> stream_PreComments() {
        return this.precomments.stream();
    }

    @Override
    public Stream<Comment> parallelStream_PreComments() {
        return this.precomments.parallelStream();
    }

    @Override
    public void forEach_PreComments(Consumer<? super Comment> action) {
        this.precomments.forEach(action);
    }

    @Override
    public void add_PreComment(int index, Comment precomment) {
        this.precomments.add(index, precomment);
    }

    @Override
    public boolean addAll_PreComments(int index, Collection<Comment> precomments) {
        return this.precomments.addAll(index, precomments);
    }

    @Override
    public Comment get_PreComment(int index) {
        return this.precomments.get(index);
    }

    @Override
    public int indexOf_PreComment(Object element) {
        return this.precomments.indexOf(element);
    }

    @Override
    public int lastIndexOf_PreComment(Object element) {
        return this.precomments.lastIndexOf(element);
    }

    @Override
    public boolean equals_PreComments(Object element) {
        return this.precomments.equals(element);
    }

    @Override
    public int hashCode_PreComments() {
        return this.precomments.hashCode();
    }

    @Override
    public ListIterator<Comment> listIterator_PreComments() {
        return this.precomments.listIterator();
    }

    @Override
    public Comment remove_PreComment(int index) {
        return this.precomments.remove(index);
    }

    @Override
    public List<Comment> subList_PreComments(int start, int end) {
        return this.precomments.subList(start, end);
    }

    @Override
    public void replaceAll_PreComments(UnaryOperator<Comment> operator) {
        this.precomments.replaceAll(operator);
    }

    @Override
    public void sort_PreComments(Comparator<? super Comment> comparator) {
        this.precomments.sort(comparator);
    }

    @Override
    public void set_PreCommentList(List<Comment> preComments) {
        this.precomments = preComments;
    }

    @Override
    public List<Comment> get_PreCommentList() {
        return this.precomments;
    }

    @Override
    public ListIterator<Comment> listIterator_PreComments(int index) {
        return this.precomments.listIterator(index);
    }

    @Override
    public Comment set_PreComment(int index, Comment precomment) {
        return this.precomments.set(index, precomment);
    }

    @Override
    public Object[] toArray_PreComments() {
        return this.precomments.toArray();
    }
  
  // ----------------------------------------------------------------------
  // Handle Post Comments
  // ----------------------------------------------------------------------
  
    @Override
    public void clear_PostComments() {
        this.postcomments.clear();
    }

    @Override
    public boolean add_PostComment(Comment postcomment) {
        return this.postcomments.add(postcomment);
    }

    @Override
    public boolean addAll_PostComments(Collection<Comment> postcomments) {
        return this.postcomments.addAll(postcomments);
    }

    @Override
    public boolean contains_PostComment(Object element) {
        return this.postcomments.contains(element);
    }

    @Override
    public boolean containsAll_PostComments(Collection<?> element) {
        return this.postcomments.containsAll(element);
    }

    @Override
    public boolean isEmpty_PostComments() {
        return this.postcomments.isEmpty();
    }

    @Override
    public Iterator<Comment> iterator_PostComments() {
        return this.postcomments.iterator();
    }

    @Override
    public boolean remove_PostComment(Object element) {
        return this.postcomments.remove(element);
    }

    @Override
    public boolean removeAll_PostComments(Collection<?> element) {
        return this.postcomments.removeAll(element);
    }

    @Override
    public boolean retainAll_PostComments(Collection<?> element) {
        return this.postcomments.retainAll(element);
    }

    @Override
    public int size_PostComments() {
        return this.postcomments.size();
    }

    @Override
    public Comment[] toArray_PostComments(Comment[] array) {
        return this.postcomments.toArray(array);
    }

    @Override
    public boolean removeIf_PostComment(Predicate<? super Comment> filter) {
        return this.postcomments.removeIf(filter);
    }

    @Override
    public Spliterator<Comment> spliterator_PostComments() {
        return this.postcomments.spliterator();
    }

    @Override
    public Stream<Comment> stream_PostComments() {
        return this.postcomments.stream();
    }

    @Override
    public Stream<Comment> parallelStream_PostComments() {
        return this.postcomments.parallelStream();
    }

    @Override
    public void forEach_PostComments(Consumer<? super Comment> action) {
        this.postcomments.forEach(action);
    }

    @Override
    public void add_PostComment(int index, Comment postcomment) {
        this.postcomments.add(index, postcomment);
    }

    @Override
    public boolean addAll_PostComments(int index, Collection<Comment> postcomments) {
        return this.postcomments.addAll(index, postcomments);
    }

    @Override
    public Comment get_PostComment(int index) {
        return this.postcomments.get(index);
    }

    @Override
    public int indexOf_PostComment(Object element) {
        return this.postcomments.indexOf(element);
    }

    @Override
    public int lastIndexOf_PostComment(Object element) {
        return this.postcomments.lastIndexOf(element);
    }

    @Override
    public boolean equals_PostComments(Object element) {
        return this.postcomments.equals(element);
    }

    @Override
    public int hashCode_PostComments() {
        return this.postcomments.hashCode();
    }

    @Override
    public ListIterator<Comment> listIterator_PostComments() {
        return this.postcomments.listIterator();
    }

    @Override
    public Comment remove_PostComment(int index) {
        return this.postcomments.remove(index);
    }

    @Override
    public List<Comment> subList_PostComments(int start, int end) {
        return this.postcomments.subList(start, end);
    }

    @Override
    public void replaceAll_PostComments(UnaryOperator<Comment> operator) {
        this.postcomments.replaceAll(operator);
    }

    @Override
    public void sort_PostComments(Comparator<? super Comment> comparator) {
        this.postcomments.sort(comparator);
    }

    @Override
    public void set_PostCommentList(List<Comment> postComments) {
        this.postcomments = postComments;
    }

    @Override
    public List<Comment> get_PostCommentList() {
        return this.postcomments;
    }

    @Override
    public ListIterator<Comment> listIterator_PostComments(int index) {
        return this.postcomments.listIterator(index);
    }

    @Override
    public Comment set_PostComment(int index, Comment precomment) {
        return this.postcomments.set(index, precomment);
    }

    @Override
    public Object[] toArray_PostComments() {
        return this.postcomments.toArray();
    }

}
