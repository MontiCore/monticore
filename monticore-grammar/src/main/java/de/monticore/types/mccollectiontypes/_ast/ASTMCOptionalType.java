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

public class ASTMCOptionalType extends ASTMCOptionalTypeTOP {

  public ASTMCOptionalType() {
    names = Lists.newArrayList("Optional");
  }

  public ASTMCOptionalType(List<ASTMCTypeArgument> typeArgumentList) {
    if (typeArgumentList.size() == 1) {
      setMCTypeArgumentsList(typeArgumentList);
    } else {
      Log.error("0xA60125 Not allowed to set a TypeArgumentList greater than 1 in ASTMCOptionalType. Has to be exactly one.");
    }
    names = Lists.newArrayList("Optional");
  }

  public ASTMCOptionalType(List<ASTMCTypeArgument> typeArgument, List<String> name) {
    this(typeArgument);
  }


  public ASTMCTypeArgument getMCTypeArgument() {
    return this.getMCTypeArguments(0);
  }

  @Override
  public List<String> getNamesList() {
    // copy of name List, so that the list cannot be changed
    return Lists.newArrayList(this.names);
  }

  @Override
  public void clearNames() {
    // Name is fixed to "Optional"
  }

  @Override
  public boolean addNames(String element) {
    // Name is fixed to "Optional"
    return false;
  }

  @Override
  public boolean addAllNames(Collection<? extends String> collection) {
    // Name is fixed to "Optional"
    return false;
  }

  @Override
  public boolean removeNames(Object element) {
    // Name is fixed to "Optional"
    return false;
  }

  @Override
  public boolean removeAllNames(Collection<?> collection) {
    // Name is fixed to "Optional"
    return false;
  }

  @Override
  public boolean retainAllNames(Collection<?> collection) {
    // Name is fixed to "Optional"
    return false;
  }

  @Override
  public boolean removeIfNames(Predicate<? super String> filter) {
    // Name is fixed to "Optional"
    return false;
  }

  @Override
  public void forEachNames(Consumer<? super String> action) {
    // Name is fixed to "Optional"
  }

  @Override
  public void addNames(int index, String element) {
    // Name is fixed to "Optional"
  }

  @Override
  public boolean addAllNames(int index, Collection<? extends String> collection) {
    // Name is fixed to "Optional"
    return false;
  }

  @Override
  public String removeNames(int index) {
    // Name is fixed to "Optional"
    return "";
  }

  @Override
  public String setNames(int index, String element) {
    // Name is fixed to "Optional"
    return "";
  }

  @Override
  public void replaceAllNames(UnaryOperator<String> operator) {
    // Name is fixed to "Optional"
  }

  @Override
  public void sortNames(Comparator<? super String> comparator) {
    // Name is fixed to "Optional"
  }

  @Override
  public void setNamesList(List<String> names) {
    // Name is fixed to "Optional"
  }

  /**
   * overwrite setter for mcTypeArgument, because only one element is allowed
   */

  @Override
  public void clearMCTypeArguments() {
    Log.error("0xA6013 Not allowed to clear MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
  }

  @Override
  public boolean addMCTypeArguments(de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6014 Not allowed to add an element to MCTypeArgumentList of ASTMCOptionalType. A MCTypeArgumentList must always have one element.");
    return false;
  }

  @Override
  public boolean addAllMCTypeArguments(Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6015 Not allowed to addAll elements to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  @Override
  public boolean removeMCTypeArguments(Object element) {
    Log.error("0xA6016 Not allowed to remove an element to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  @Override
  public boolean removeAllMCTypeArguments(Collection<?> collection) {
    Log.error("0xA6017 Not allowed to removeAll elements to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  @Override
  public boolean retainAllMCTypeArguments(Collection<?> collection) {
    if (collection.contains(getMCTypeArgument())) {
      return true;
    } else {
      Log.error("0xA6018 Not allowed to retrainAll elements of MCTypeArgumentList of ASTMCOptionalType, without an match found.A MCTypeArgumentList must always have one element.");
      return false;
    }
  }

  @Override
  public boolean removeIfMCTypeArguments(Predicate<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> filter) {
    if (getMCTypeArgumentsList().stream().noneMatch(filter)) {
      return getMCTypeArgumentsList().removeIf(filter);
    } else {
      Log.error("0xA6019 Not allowed to remove an element to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
      return false;
    }
  }

  @Override
  public void forEachMCTypeArguments(Consumer<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> action) {
    this.getMCTypeArgumentsList().forEach(action);
  }

  @Override
  public void addMCTypeArguments(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6020 Not allowed to add an element to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
  }

  @Override
  public boolean addAllMCTypeArguments(int index, Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6021 Not allowed to addAll elements to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  @Override
  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument removeMCTypeArguments(int index) {
    Log.error("0xA6022 Not allowed to remove an element to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    return getMCTypeArgument();
  }

  @Override
  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument setMCTypeArguments(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    if (index == 0) {
      return this.getMCTypeArgumentsList().set(index, element);
    } else {
      Log.error("0xA6023 Not allowed to set an element of MCTypeArgumentList of ASTMCOptionalType to a other index than 0.A MCTypeArgumentList must always have one element.");
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
      Log.error("0xA6024 Not allowed to set a list with a size greater than 1 to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    }
  }
}
