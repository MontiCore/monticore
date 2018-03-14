/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;

import java.util.ArrayList;
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

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

/**
 * Foundation interface for all AST-classes
 */
public interface ASTNode {
  
  /**
   * Performs a deep clone of this ASTNode and all of its successors
   * 
   * @return Clone of current ASTNode with a parent which is equal to null
   */
  default public ASTNode deepClone(ASTNode result) {
    Log.errorIfNull(result,
        "0xA4040 The argument ASTNode of the 'deepClone' method must not be null.");
    result.set_SourcePositionStart(get_SourcePositionStart().clone());
    result.set_SourcePositionEnd(get_SourcePositionEnd().clone());
    for (de.monticore.ast.Comment x : get_PreCommentList()) {
      result.get_PreCommentList().add(new de.monticore.ast.Comment(x.getText()));
    }
    for (de.monticore.ast.Comment x : get_PostCommentList()) {
      result.get_PostCommentList().add(new de.monticore.ast.Comment(x.getText()));
    }
    
    return result;
  }
  
  default public boolean equalAttributes(Object o) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4078 Method equalAttributes is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  default public boolean equalsWithComments(Object o) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4042 Method equalsWithComments is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  /**
   * Compare this object to another Object. Do not take comments into account. This method returns
   * the same value as <tt>deepEquals(Object o, boolean 
   * forceSameOrder)</tt> method when using the default value for forceSameOrder of each Node.
   */
  default public boolean deepEquals(Object o) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4043 Method deepEquals is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  /**
   * Compare this object to another Object. Take comments into account.
   * 
   * @param o the object to compare this node to
   * @param forceSameOrder consider the order in ancestor lists, even if these lists are of
   * stereotype <tt>&lt;&lt;unordered&gt;&gt;</tt> in the grammar.
   */
  default public boolean deepEqualsWithComments(Object o) {
    throw new CompareNotSupportedException(
        "0xA4044 Method deepEqualsWithComments is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  /**
   * Compare this object to another Object. Do not take comments into account.
   * 
   * @param o the object to compare this node to
   * @param forceSameOrder consider the order in ancestor lists, even if these lists are of
   * stereotype <tt>&lt;&lt;unordered&gt;&gt;</tt> in the grammar.
   */
  default public boolean deepEquals(Object o, boolean forceSameOrder) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4045 Method deepEquals is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  /**
   * Compare this object to another Object. Take comments into account. This method returns the same
   * value as <tt>deepEqualsWithComment(Object o, boolean forceSameOrder)</tt> method when using the
   * default value for forceSameOrder of each Node.
   */
  default public boolean deepEqualsWithComments(Object o, boolean forceSameOrder) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4046 Method deepEqualsWithComments is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  /**
   * Performs a deep clone of this ASTNode and all of its successors
   * 
   * @return Clone of current ASTNode with a parent which is equal to null
   */
  ASTNode deepClone();
  
  /**
   * Returns the end position of this ASTNode
   * 
   * @return end position of this ASTNode
   */
  SourcePosition get_SourcePositionEnd();
  
  /**
   * Returns the optional end position of this ASTNode
   *
   * @return end position of this ASTNode
   */
  Optional<SourcePosition> get_SourcePositionEndOpt();
  
  /**
   * Sets the end position of this ASTNode
   * 
   * @param end end position of this ASTNode
   */
  void set_SourcePositionEnd(SourcePosition end);
  
  /**
   * Sets the optional end position of this ASTNode
   *
   * @param end end position of this ASTNode
   */
  void set_SourcePositionEndOpt(Optional<SourcePosition> end);
  
  /**
   * Sets the optional end position of this ASTNode absent
   */
  void set_SourcePositionEndAbsent();
  
  /**
   * @return true if the optional source position end of this ASTNode is present
   */
  boolean isPresent_SourcePositionEnd();
  
  /**
   * Returns the start source position of this ASTNode
   * 
   * @return start position of this ASTNode
   */
  SourcePosition get_SourcePositionStart();
  
  /**
   * Returns the optional end position of this ASTNode
   *
   * @return end position of this ASTNode
   */
  Optional<SourcePosition> get_SourcePositionStartOpt();
  
  /**
   * Sets the start position of this ASTNode
   * 
   * @param start start position of this ASTNode
   */
  void set_SourcePositionStart(SourcePosition start);
  
  /**
   * Sets the optional start position of this ASTNode
   *
   * @param start start position of this ASTNode
   */
  void set_SourcePositionStartOpt(Optional<SourcePosition> start);
  
  /**
   * Sets the optional start position of this ASTNode absent
   */
  void set_SourcePositionStartAbsent();
  
  /**
   * @return true if the optional source position start of this ASTNode is present
   */
  boolean isPresent_SourcePositionStart();
  
  /**
   * Returns list of all comments which are associated with this ASTNode and are prior to the
   * ASTNode in the input file
   * 
   * Can be removed after 4.5.5
   * Replace with  List<Comment> get_PreCommentList();
   * use {@link #List<Comment> get_PreCommentList()} instead
   * 
   * @return list of comments
   */
  
  @Deprecated
  List<Comment> get_PreComments();

  /**
   * Sets list of all comments which are associated with this ASTNode and are prior to the ASTNode
   * in the input file
   * 
   * Can be removed after 4.5.5
   * Replace with  List<Comment> set_PreCommentList();
   * use {@link #List<Comment> set_PreCommentList()} instead
   * 
   * @param precomments list of comments
   */
  @Deprecated
  void set_PreComments(List<Comment> precomments);
  
  /**
   * Clears the list of preComments, that only an empty list stays
   */
  void clear_PreComments();
  
  /**
   * Adds one new comment to the list of preComments
   * 
   * @param precomment one comment
   * @return boolean
   */
  boolean add_PreComment(Comment precomment);
  
  /**
   * Adds a list of comments to the already existing preComment list
   * 
   * @param precomments a list of comments
   * @return boolean
   */
  boolean addAll_PreComments(Collection<Comment> precomments);
  
  /**
   * Checks if the list contains the given Object Returns true if the Object is contained in the
   * list of comments and false if it is not
   * 
   * @param element which should be contained in the comments
   * @return boolean true if the list contains the Object
   */
  boolean contains_PreComment(Object element);
  
  /**
   * Checks if the list contains the given Collection of elements Returns true if the Collection is
   * contained in the list of comments and false if it is not
   * 
   * @param element collection which should be contained in the comment list
   * @return boolean true if the list contains the Object
   */
  boolean containsAll_PreComments(Collection<?> element);
  
  /**
   * Checks if the list is empty, has no elements Returns true if the list is empty and false if it
   * has elements
   * 
   * @return boolean true if the list is empty
   */
  boolean isEmpty_PreComments();
  
  /**
   * Returns the Iterator for the preComment list
   * 
   * @return Iterator<Comment> to iterate over the preComment list
   */
  Iterator<Comment> iterator_PreComments();
  
  /**
   * Removes one given element from the list if this element is contained
   * 
   * @param element which should be removed if it is present
   * @return boolean
   */
  boolean remove_PreComment(Object element);
  
  /**
   * Removes a collection of elements from the list if the elements are contained
   * 
   * @param element collection which should be removed if they are present
   * @return boolean
   */
  boolean removeAll_PreComments(Collection<?> element);
  
  /**
   * Retrains a collection of elements from the list if the elements are contained
   * 
   * @param element collection which should be retrained if they are present
   * @return boolean
   */
  boolean retainAll_PreComments(Collection<?> element);
  
  /**
   * counts the number of preComments in the preComment list and returns that number as an Int
   * 
   * @return int the number of comments is returned
   */
  int size_PreComments();
  
  /**
   * Converts the list of preComments into an array of comments and returns that array
   * 
   * @param array into which the comments should be added
   * @return Comment[] array which contains all the comments
   */
  Comment[] toArray_PreComments(Comment[] array);
  
  /**
   * removes if the comment if the predicate is fulfilled
   * 
   * @param filter which selects different comments
   * @return boolean
   */
  boolean removeIf_PreComment(Predicate<? super Comment> filter);
  
  /**
   * Returns the Spliterator of the preComment list
   * 
   * @return Spliterator<Comment> of the preComment list
   */
  Spliterator<Comment> spliterator_PreComments();
  
  /**
   * Returns the Steam of the preComment list
   * 
   * @return Steam<Comment> of the preComment list
   */
  Stream<Comment> stream_PreComments();
  
  /**
   * Returns the parallel Steam of the preComment list
   * 
   * @return Steam<Comment> parallel Stream of the preComment list
   */
  Stream<Comment> parallelStream_PreComments();
  
  /**
   * Consumer is given that performs an action but does not return a value
   * 
   * @param action that does something but has no return value
   */
  void forEach_PreComments(Consumer<? super Comment> action);
  
  /**
   * adds one comment to the preComment list at the position of the given index
   * 
   * @param index of the existing list where it should be added
   * @param  precomment the comment that is added to the existing list
   */
  void add_PreComment(int index, Comment precomment);
  
  /**
   * adds a list of comments to the preComment list at the position of the given index
   * 
   * @param index of the existing list where the new list should be added
   * @param  precomments list that is added to the existing list
   */
  boolean addAll_PreComments(int index, Collection<Comment> precomments);
  
  /**
   * Returns one comment of the list from the position of the given index
   * 
   * @param index in the existing list where the comment should be returned
   * @return Comment at the given index is returned
   */
  Comment get_PreComment(int index);
  
  /**
   * Returns the index of the given element if it exists in the list
   * 
   * @param element of which the index in the comment list should be returned
   * @return int index where the Object is found
   */
  int indexOf_PreComment(Object element);
  
  /**
   * Returns the last index of the given element if it exists in the list
   * 
   * @param element of which the last index in the comment list should be returned
   * @return int index where the Object is found latest
   */
  int lastIndexOf_PreComment(Object element);
  
  /**
   * Returns true if the object equals the preComment list Returns false if the object and the
   * preComment list are not equal
   * 
   * @param element which should be equal to the preComment list
   * @return boolean if the object an preComment list are equal or not
   */
  boolean equals_PreComments(Object element);
  
  /**
   * Returns the hashCode of the preComment as an int
   * 
   * @return int the hashCode of the preComment
   */
  int hashCode_PreComments();
  
  /**
   * Returns the ListIterator of the preComment list
   * 
   * @return ListIterator<Comment> which iterates over the list of preComments
   */
  ListIterator<Comment> listIterator_PreComments();
  
  /**
   * Returns the new preComment list without the removed element at the given index
   * 
   * @param index where the element should be removed
   * @return List<Comment> where the comment at the index is removed
   */
  Comment remove_PreComment(int index);
  
  /**
   * Returns the sub list form the preComment list form the start to the end index which are given
   * 
   * @param start index of the sublist
   * @param end index of the sublist
   * @return ListIterator<Comment> which iterates over the list of preComments
   */
  List<Comment> subList_PreComments(int start, int end);
  
  /**
   * replaces all preComments that fit to the given unaryOperator
   * 
   * @param operator that defines which preComments should be replaced
   */
  void replaceAll_PreComments(UnaryOperator<Comment> operator);
  
  /**
   * sorts the preComment list through a given way of comparing through the comparator
   * 
   * @param comparator that defines in which way the preComments should be
   * sorted
   */
  void sort_PreComments(Comparator<? super Comment> comparator);
  
  /**
   * sets the complete list of preComments to the given preComment list
   * 
   * @param preComments list that should be set
   */
  void set_PreCommentList(List<Comment> preComments);
  
  /**
   * returns the complete preComments list
   * 
   * @return List<Comment> that is contained in the preComment list at the moment
   */
  List<Comment> get_PreCommentList();
  
  /**
   * returns a ListIterator of the type Comment for the preComment list
   * 
   * @param index of the iterator
   * @return ListIterator<Comment> of a special index
   */
  ListIterator<Comment> listIterator_PreComments(int index);
  
  /**
   * sets the comment at the given index and returns that comment
   * 
   * @param index where the comment should be added to the list
   * @param precomment that should be added at the index
   * @return Comment at a special index
   */
   Comment set_PreComment(int index, Comment precomment);
   
   /**
    * converts the commentlist into an array of the type Object and returns thar array
    * 
    * @return an array of the type Object
    */
    Object[] toArray_PreComments();
  
  /**
   * Returns list of all comments which are associated with this ASTNode and can be found after the
   * ASTNode in the input file
   * 
   * Can be removed after 4.5.5
   * Replace with  List<Comment> get_PostCommentList();
   * use {@link #List<Comment> get_PostCommentList()} instead
   * 
   * @return list of comments
   */
  @Deprecated
  List<Comment> get_PostComments();
  
  /**
   * Sets list of all comments which are associated with this ASTNode and can be found after the
   * ASTNode in the input file
   * 
   * Can be removed after 4.5.5
   * Replace with  List<Comment> set_PostCommentList();
   * use {@link #List<Comment> set_PostCommentList()} instead
   * 
   * @param postcomments list of comments
   */
  @Deprecated
  void set_PostComments(List<Comment> postcomments);
  
  /**
   * Clears the list of postComments, that only an empty list stays
   */
  void clear_PostComments();
  
  /**
   * Adds one new comment to the list of postComments
   * 
   * @param postcomment one comment
   * @return boolean
   */
  boolean add_PostComment(Comment postcomment);
  
  /**
   * Adds a list of comments to the already existing postComment list
   * 
   * @param postcomments a list of comments
   * @return boolean
   */
  boolean addAll_PostComments(Collection<Comment> postcomments);
  
  /**
   * Checks if the list contains the given Object Returns true if the Object is contained in the
   * list of comments and false if it is not
   * 
   * @param element which should be contained in the comments
   * @return boolean true if the list contains the Object
   */
  boolean contains_PostComment(Object element);
  
  /**
   * Checks if the list contains the given Collection of elements Returns true if the Collection is
   * contained in the list of comments and false if it is not
   * 
   * @param collection of elements which should be contained in the comment list
   * @return boolean true if the list contains the Object
   */
  boolean containsAll_PostComments(Collection<?> element);
  
  /**
   * Checks if the list is empty, has no elements Returns true if the list is empty and false if it
   * has elements
   * 
   * @return boolean true if the list is empty
   */
  boolean isEmpty_PostComments();
  
  /**
   * Returns the Iterator for the postComment list
   * 
   * @return Iterator<Comment> to iterate over the postComment list
   */
  Iterator<Comment> iterator_PostComments();
  
  /**
   * removes one given element from the list if this element is contained
   * 
   * @param element which should be removed if it is postsent
   * @return boolean
   */
  boolean remove_PostComment(Object element);
  
  /**
   * removes a collection of elements from the list if the elements are contained
   * 
   * @param element collection  which should be removed if they are postsent
   * @return boolean true if the elements were postsent and are now removed
   */
  boolean removeAll_PostComments(Collection<?> element);
  
  /**
   * Retrains a collection of elements from the list if the elements are contained
   * 
   * @param  element collection which should be retrained if they are present
   * @return boolean
   */
  boolean retainAll_PostComments(Collection<?> element);
  
  /**
   * counts the number of postComments in the postComment list and returns that number as an Int
   * 
   * @return int the number of comments is returned
   */
  int size_PostComments();
  
  /**
   * Converts the list of postComments into an array of comments and returns that array
   * 
   * @param array into which the comments should be added
   * @return Comment[] array which contains all the comments
   */
  Comment[] toArray_PostComments(Comment[] array);
  
  /**
   * removes if the comment if the postdicate is fulfilled
   * 
   * @param filter which selects different comments
   * @return boolean
   */
  boolean removeIf_PostComment(Predicate<? super Comment> filter);
  
  /**
   * Returns the Spliterator of the postComment list
   * 
   * @return Spliterator<Comment> of the postComment list
   */
  Spliterator<Comment> spliterator_PostComments();
  
  /**
   * Returns the Steam of the postComment list
   * 
   * @return Steam<Comment> of the postComment list
   */
  Stream<Comment> stream_PostComments();
  
  /**
   * Returns the parallel Steam of the postComment list
   * 
   * @return Steam<Comment> parallel Stream of the postComment list
   */
  Stream<Comment> parallelStream_PostComments();
  
  /**
   * Consumer is given that performs an action but does not return a value
   * 
   * @param action that does something but has no return value
   */
  void forEach_PostComments(Consumer<? super Comment> action);
  
  /**
   * adds one comment to the postComment list at the position of the given index
   * 
   * @param index of the existing list where it should be added
   * @param postcomment the comment that is added to the existing list
   */
  void add_PostComment(int index, Comment postcomment);
  
  /**
   * adds a list of comments to the postComment list at the position of the given index
   * 
   * @param index of the existing list where the new list should be added
   * @param  postcomments the comment list that is added to the existing list
   */
  boolean addAll_PostComments(int index, Collection<Comment> postcomments);
  
  /**
   * Returns one comment of the list from the position of the given index
   * 
   * @param index of the existing list where the comment should be returned
   * @return Comment at the given index is returned
   */
  Comment get_PostComment(int index);
  
  /**
   * Returns the index of the given element if this exists in the list
   * 
   * @param element of which the index in the comment list should be returned
   * @return int index where the Object is found
   */
  int indexOf_PostComment(Object element);
  
  /**
   * Returns the last index of the given element if this exists in the list
   * 
   * @param element of which the last index in the comment list should be returned
   * @return int index where the Object is found latest
   */
  int lastIndexOf_PostComment(Object element);
  
  /**
   * Returns true if the object equals the postComment list Returns false if the object and the
   * postComment list are not equal
   * 
   * @param element which should be equal to the postComment list
   * @return boolean if the object an postComment list are equal or not
   */
  boolean equals_PostComments(Object element);
  
  /**
   * Returns the hashCode of the postComment as an int
   * 
   * @return int the hashCode of the postComment
   */
  int hashCode_PostComments();
  
  /**
   * Returns the ListIterator of the postComment list
   * 
   * @return ListIterator<Comment> which iterates over the list of postComments
   */
  ListIterator<Comment> listIterator_PostComments();
  
  /**
   * Returns the new postComment list without the removed element at the given index
   * 
   * @param index where the element should be removed
   * @return List<Comment> where the comment at the index is removed
   */
  Comment remove_PostComment(int index);
  
  /**
   * Returns the sub list form the postComment list form the start to the end index which are given
   * 
   * @param start index of the sublist
   * @param end index of the sublist
   * @return ListIterator<Comment> which iterates over the list of postComments
   */
  List<Comment> subList_PostComments(int start, int end);
  
  /**
   * replaces all postComments that fit to the given unaryOperator
   * 
   * @param operator that defines which postComments should be replaced
   */
  void replaceAll_PostComments(UnaryOperator<Comment> operator);
  
  /**
   * sorts the postComment list through a given way of comparing through the comparator
   * 
   * @param comparator that defines in which way the postComments should be
   * sorted
   */
  void sort_PostComments(Comparator<? super Comment> comparator);
  
  /**
   * sets the complete list of postComments to the given postComment list
   * 
   * @param postComments list that should be set
   */
  void set_PostCommentList(List<Comment> postComments);
  
  /**
   * returns the complete postComments list
   * 
   * @return List<Comment> that is contained in the postComment list at the moment
   */
  List<Comment> get_PostCommentList();
  
  /**
   * returns a ListIterator of the type Comment for the postComment list
   * 
   * @param index of the iterator
   * @return ListIterator<Comment> of a special index
   */
  ListIterator<Comment> listIterator_PostComments(int index);
  
  /**
   * sets the comment at the given index and returns that comment
   * 
   * @param index  where the comment should be added to the list
   * @param postcomment that should be added at the index
   * @return Comment at a special index
   */
   Comment set_PostComment(int index, Comment postcomment);
   
   /**
    * converts the commentlist into an array of the type Object and returns thar array
    * 
    * @return an array of the type Object
    */
    Object[] toArray_PostComments();
  
  
  /**
   * @returns a collection of all child nodes of this node Use new additional methods for
   * list-values attributes instead
   */
  @Deprecated
  default Collection<ASTNode> get_Children() {
    return new ArrayList<ASTNode>();
  }
  
  /**
   * This method removes the reference from this node to a child node, no matter in which attribute
   * it is stored.
   * 
   * @param child the target node of the reference to be removed Use new additional methods for
   * list-values attributes instead
   */
  @Deprecated
  default void remove_Child(ASTNode child) {
  }
  
  /**
   * Sets the enclosing scope of this ast node.
   *
   * @param enclosingScope the enclosing scope of this ast node
   */
  void setEnclosingScope(Scope enclosingScope);

  /**
   * Sets the enclosing scope of this ast node with an Optional as parameter.
   *
   * @param enclosingScopeOpt the enclosing scope of this ast node as an Optional
   */
  void setEnclosingScopeOpt(Optional<? extends Scope> enclosingScopeOpt);

  /**
   * Sets the enclosing scope optional to absent
   *
   */
  void setEnclosingScopeAbsent();

  /**
   * Deprecated method can be deleted after next release
   * will be replaced by the method that returns only a scope and no optional
   * 
   * Scope getEnclosingScope();
   * 
   * @return the enclosing scope of this ast node
   */
  @Deprecated
  Optional<? extends Scope> getEnclosingScope();

  /**
   * Gets the enclosing scope of this ast node as an Optional
   *
   * @return Optional<Scope> of the enclosing Scope
   */
  Optional<? extends Scope> getEnclosingScopeOpt();

  /**
   * @return true if the enclosing scope is present
   */
  boolean isPresentEnclosingScope();
  
  /**
   * Sets the corresponding symbol of this ast node.
   *
   * @param symbol the corresponding symbol of this ast node..
   */
  void setSymbol(Symbol symbol);

  /**
   * Sets the spanned symbol of this ast node with an Optional as parameter.
   *
   * @param spannedSymbolOpt the spanned symbol of this ast node as an Optional
   */
  void setSymbolOpt(Optional<? extends Symbol> spannedSymbolOpt);

  /**
   * Sets the spanned symbol optional to absent
   *
   */
  void setSymbolAbsent();

  /**
   * Deprecated method can be deleted after next release
   * will be replaced by the method that returns only a symbol and no optional
   * 
   * Symbol getSymbol();
   * 
   * @return the corresponding symbol of this ast node.
   */
  @Deprecated
  Optional<? extends Symbol> getSymbol();

  /**
   * Gets the spanned symbol of this ast node as an Optional
   *
   * @return Optional<Symbol> of the spanned Symbol
   */
  Optional<? extends Symbol> getSymbolOpt();


  /**
   * @return true if the symbol is present
   */
  boolean isPresentSymbol();
  
  /**
   * Sets the spanned scope of this ast node.
   *
   * @param spannedScope the spanned scope of this ast node
   */
  void setSpannedScope(Scope spannedScope);

  /**
   * Sets the spanned scope of this ast node with an Optional as parameter.
   *
   * @param spannedScopeOpt the spanned scope of this ast node as an Optional
   */
  void setSpannedScopeOpt(Optional<? extends Scope> spannedScopeOpt);

  /**
   * Sets the spanned scope optional to absent
   *
   */
  void setSpannedScopeAbsent();
  /**
   * Deprecated method can be deleted after next release
   * will be replaced by the method that returns only a scope and no optional
   * 
   * Scope getSpannedScope();
   * 
   * @return the spanned scope of this ast node
   */
  @Deprecated
  default Optional<? extends Scope> getSpannedScope() {
    return Optional.empty();
  }
  
  /**
   * Gets the spanned scope of this ast node as an Optional
   * 
   * @return Optional<Scope> of the spanned Scope
   */
  Optional<? extends Scope> getSpannedScopeOpt();

  /**
   * @return true if the spanned scope is present
   */
  boolean isPresentSpannedScope();

  
  
}
