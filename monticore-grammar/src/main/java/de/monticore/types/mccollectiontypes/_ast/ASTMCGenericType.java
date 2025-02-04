/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import java.util.*;
import java.util.stream.Stream;

public interface ASTMCGenericType extends ASTMCGenericTypeTOP {

  default String printWithoutTypeArguments() {
    return String.join(".", getNameList());
  }

  /*
    Getter for MCTypeArgumentList
   */

  List<ASTMCTypeArgument> getMCTypeArgumentList();

  default boolean containsMCTypeArgument(Object element) {
    return getMCTypeArgumentList().contains(element);
  }

  default boolean containsAllMCTypeArguments(Collection<?> collection) {
    return getMCTypeArgumentList().containsAll(collection);
  }

  default boolean isEmptyMCTypeArguments() {
    return getMCTypeArgumentList().isEmpty();
  }

  default Iterator<ASTMCTypeArgument> iteratorMCTypeArguments() {
    return getMCTypeArgumentList().iterator();
  }

  default int sizeMCTypeArguments() {
    return getMCTypeArgumentList().size();
  }

  default ASTMCTypeArgument[] toArrayMCTypeArguments(ASTMCTypeArgument[] array) {
    return getMCTypeArgumentList().toArray(array);
  }

  default Object[] toArrayMCTypeArguments() {
    return getMCTypeArgumentList().toArray();
  }

  default Spliterator<ASTMCTypeArgument> spliteratorMCTypeArguments() {
    return getMCTypeArgumentList().spliterator();
  }

  default Stream<ASTMCTypeArgument> streamMCTypeArguments() {
    return getMCTypeArgumentList().stream();
  }

  default Stream<ASTMCTypeArgument> parallelStreamMCTypeArguments() {
    return getMCTypeArgumentList().parallelStream();
  }

  default ASTMCTypeArgument getMCTypeArgument(int index) {
    return getMCTypeArgumentList().get(index);
  }

  default int indexOfMCTypeArgument(Object element) {
    return getMCTypeArgumentList().indexOf(element);
  }

  default int lastIndexOfMCTypeArgument(Object element) {
    return getMCTypeArgumentList().lastIndexOf(element);
  }

  default int hashCodeMCTypeArguments() {
    return getMCTypeArgumentList().hashCode();
  }

  default ListIterator<ASTMCTypeArgument> listIteratorMCTypeArguments() {
    return getMCTypeArgumentList().listIterator();
  }

  default ListIterator<ASTMCTypeArgument> listIteratorMCTypeArguments(int index) {
    return getMCTypeArgumentList().listIterator(index);
  }

  default List<ASTMCTypeArgument> subListMCTypeArguments(int start, int end) {
    return getMCTypeArgumentList().subList(start, end);
  }

  /*
    Getter for NameList
   */

  List<String> getNameList();

  default boolean containsName(Object element) {
    return getNameList().contains(element);
  }

  default boolean containsAllNames(Collection<?> collection) {
    return getNameList().containsAll(collection);
  }

  default boolean isEmptyNames() {
    return getNameList().isEmpty();
  }

  default Iterator<String> iteratorNames() {
    return getNameList().iterator();
  }

  default int sizeNames() {
    return getNameList().size();
  }

  default String[] toArrayNames(String[] array) {
    return getNameList().toArray(array);
  }

  default Object[] toArrayNames() {
    return getNameList().toArray();
  }

  default Spliterator<String> spliteratorNames() {
    return getNameList().spliterator();
  }

  default Stream<String> streamNames() {
    return getNameList().stream();
  }

  default Stream<String> parallelStreamNames() {
    return getNameList().parallelStream();
  }

  default String getName(int index) {
    return getNameList().get(index);
  }

  default int indexOfName(Object element) {
    return getNameList().indexOf(element);
  }

  default int lastIndexOfName(Object element) {
    return getNameList().lastIndexOf(element);
  }

  default int hashCodeNames() {
    return getNameList().hashCode();
  }

  default ListIterator<String> listIteratorNames() {
    return getNameList().listIterator();
  }

  default ListIterator<String> listIteratorNames(int index) {
    return getNameList().listIterator(index);
  }

  default List<String> subListNames(int start, int end) {
    return getNameList().subList(start, end);
  }

}
