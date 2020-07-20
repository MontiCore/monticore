/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ASTMCSetType extends ASTMCSetTypeTOP {

  public ASTMCSetType() {
    names = Lists.newArrayList("Set");
  }

  public ASTMCSetType(List<ASTMCTypeArgument> typeArgument, List<String> name) {
    this(typeArgument);
  }

  public ASTMCSetType(List<ASTMCTypeArgument> typeArgumentList) {
    if (typeArgumentList.size() == 1) {
      setMCTypeArgumentsList(typeArgumentList);
    } else {
      Log.error("0xA6038 Not allowed to set a TypeArgumentList greater than 1 in ASTMCSetType. Has to be exactly one.");
    }
    names = Lists.newArrayList("Set");
  }

  // TODO BR/RE: Methoden überarbeiten, sobald geklärt wie
  // selbiges bei List, Map, Optional
  // TODO BR: die damaligen astrules konnten noch nicht so viel wie heute
  // durch geschicktes hinzufügen von attributen/methoden per astrule sind
  // viele methoden der Topklassen überflüssig geworden

  public ASTMCTypeArgument getMCTypeArgument() {
    return this.getMCTypeArguments(0);
  }

  @Override
  public List<String> getNamesList() {
    // copy of name List, so that the list cannot be changed
    return Lists.newArrayList(this.names);
  }

  @Override
  public void setNamesList(List<String> names) {
    // Name is fixed to "Set"
  }

  @Override
  public void clearNames() {
    // Name is fixed to "Set"
  }

  @Override
  public boolean addNames(String element) {
    // Name is fixed to "Set"
    return false;
  }

  @Override
  public boolean addAllNames(Collection<? extends String> collection) {
    // Name is fixed to "Set"
    return false;
  }

  @Override
  public boolean removeNames(Object element) {
    // Name is fixed to "Set"
    return false;
  }

  @Override
  public boolean removeAllNames(Collection<?> collection) {
    // Name is fixed to "Set"
    return false;
  }

  @Override
  public boolean retainAllNames(Collection<?> collection) {
    // Name is fixed to "Set"
    return false;
  }

  @Override
  public boolean removeIfNames(Predicate<? super String> filter) {
    // Name is fixed to "Set"
    return false;
  }

  @Override
  public void forEachNames(Consumer<? super String> action) {
    // Name is fixed to "Set"
  }
  @Override

  public void addNames(int index, String element) {
    // Name is fixed to "Set"
  }

  @Override
  public boolean addAllNames(int index, Collection<? extends String> collection) {
    // Name is fixed to "Set"
    return false;
  }

  @Override
  public String removeNames(int index) {
    // Name is fixed to "Set"
    return "";
  }

  @Override
  public String setNames(int index, String element) {
    // Name is fixed to "Set"
    return "";
  }

  @Override
  public void replaceAllNames(UnaryOperator<String> operator) {
    // Name is fixed to "Set"
  }

  @Override
  public void sortNames(Comparator<? super String> comparator) {
    // Name is fixed to "Set"
  }


  /**
   * overwrite setter for mcTypeArgument, because only one element is allowed
   */

  @Override
  public void clearMCTypeArguments() {
    Log.error("0xA6026 Not allowed to clear MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
  }

  @Override
  public boolean addMCTypeArguments(de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6027 Not allowed to add an element to MCTypeArgumentList of ASTMCSetType. A MCTypeArgumentList must always have one element.");
    return false;
  }

  @Override
  public boolean addAllMCTypeArguments(Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6028 Not allowed to addAll elements to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  @Override
  public boolean removeMCTypeArguments(Object element) {
    Log.error("0xA6029 Not allowed to remove an element to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  @Override
  public boolean removeAllMCTypeArguments(Collection<?> collection) {
    Log.error("0xA6030 Not allowed to removeAll elements to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  @Override
  public boolean retainAllMCTypeArguments(Collection<?> collection) {
    if (collection.contains(getMCTypeArgument())) {
      return true;
    } else {
      Log.error("0xA6031 Not allowed to retrainAll elements of MCTypeArgumentList of ASTMCSetType, without an match found.A MCTypeArgumentList must always have one element.");
      return false;
    }
  }

  @Override
  public boolean removeIfMCTypeArguments(Predicate<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> filter) {
    if (getMCTypeArgumentsList().stream().noneMatch(filter)) {
      return getMCTypeArgumentsList().removeIf(filter);
    } else {
      Log.error("0xA6032 Not allowed to remove an element to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
      return false;
    }
  }

  @Override
  public void forEachMCTypeArguments(Consumer<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> action) {
    this.getMCTypeArgumentsList().forEach(action);
  }

  @Override
  public void addMCTypeArguments(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6033 Not allowed to add an element to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
  }

  @Override
  public boolean addAllMCTypeArguments(int index, Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6034 Not allowed to addAll elements to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  @Override
  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument removeMCTypeArguments(int index) {
    Log.error("0xA6035 Not allowed to remove an element to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    return getMCTypeArgument();
  }

  @Override
  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument setMCTypeArguments(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    if (index == 0) {
      return this.getMCTypeArgumentsList().set(index, element);
    } else {
      Log.error("0xA6036 Not allowed to set an element of MCTypeArgumentList of ASTMCSetType to a other index than 0.A MCTypeArgumentList must always have one element.");
      return getMCTypeArgument();
    }
  }

  @Override
  public void replaceAllMCTypeArguments(UnaryOperator<de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> operator) {
    this.getMCTypeArgumentsList().replaceAll(operator);
  }

  @Override
  public void sortMCTypeArguments(Comparator<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> comparator) {
    this.getMCTypeArgumentsList().sort(comparator);
  }

  @Override
  public void setMCTypeArgumentsList(List<de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> mCTypeArguments) {
    if (mCTypeArguments.size() == 1) {
      this.mCTypeArguments = mCTypeArguments;
    } else {
      Log.error("0xA6037 Not allowed to set a list with a size greater than 1 to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    }
  }
}
