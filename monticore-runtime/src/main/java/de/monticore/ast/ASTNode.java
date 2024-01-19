/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;

import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.symboltable.IScope;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.SourcePosition;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Foundation interface for all AST-classes
 */
public interface ASTNode {

  IScope getEnclosingScope();

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
   * Sets the end position of this ASTNode
   *
   * @param end end position of this ASTNode
   */
  void set_SourcePositionEnd(SourcePosition end);
  
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
   * Sets the start position of this ASTNode
   *
   * @param start start position of this ASTNode
   */
  void set_SourcePositionStart(SourcePosition start);
  
  /**
   * Sets the optional start position of this ASTNode absent
   */
  void set_SourcePositionStartAbsent();
  
  /**
   * @return true if the optional source position start of this ASTNode is present
   */
  boolean isPresent_SourcePositionStart();

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
   * @param element collection of elements which should be contained in the comment list
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

  default void accept (ITraverser visitor)  {
    visitor.handle(this);
  }

  default Value evaluate(ModelInterpreter interpreter) {
    return interpreter.interpret(this);
  }
}
