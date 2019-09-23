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

public class ASTMCMapType extends ASTMCMapTypeTOP {

  public ASTMCMapType() {
    names = Lists.newArrayList("Map");
    // todo find better way
    setMCTypeArgumentList(Lists.newArrayList(null, null));
  }

  public ASTMCMapType(ASTMCTypeArgument key, ASTMCTypeArgument value, List<ASTMCTypeArgument> astmcTypeArguments, List<String> name) {
    if (key != null && value != null) {
      setMCTypeArgumentList(Lists.newArrayList(key, value));
    } else if (astmcTypeArguments.size() == 2) {
      setMCTypeArgumentList(astmcTypeArguments);
    }
    names = Lists.newArrayList("Map");
  }

  public ASTMCMapType(ASTMCTypeArgument key, ASTMCTypeArgument value) {
    this(key, value, Lists.newArrayList(), Lists.newArrayList("Map"));
  }

  @Override
  public String getBaseName() {
    return getName();
  }

  public void setName(String name) {
    // Name is fixed to "Map"   :  TODO: Internal Error, Error Msg
  }

  @Override
  public List<ASTMCTypeArgument> getMCTypeArgumentList() {
    return this.mCTypeArguments;
  }

  @Override
  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument getKey() {
    return this.mCTypeArguments.get(0);
  }

  @Override
  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument getValue() {
    return this.mCTypeArguments.get(1);
  }

  @Override
  public void setKey(de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument key) {
    this.mCTypeArguments.set(0, key);
  }

  @Override
  public void setValue(de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument value) {
    this.mCTypeArguments.set(1, value);
  }


  public List<String> getNameList() {
    // copy of name List, so that the list cannot be changed
    return Lists.newArrayList(this.names);
  }

  public void setNameList(List<String> names) {
    // Name is fixed to "Map" TODO: Internal Error, Error Msg
  }

  public void clearNames() {
    // Name is fixed to "Map"
  }

  public boolean addName(String element) {
    // Name is fixed to "Map"
    return false;
  }

  public boolean addAllNames(Collection<? extends String> collection) {
    // Name is fixed to "Map"
    return false;
  }

  public boolean removeName(Object element) {
    // Name is fixed to "Map"
    return false;
  }

  public boolean removeAllNames(Collection<?> collection) {
    // Name is fixed to "Map"
    return false;
  }

  public boolean retainAllNames(Collection<?> collection) {
    // Name is fixed to "Map"
    return false;
  }

  public boolean removeIfName(Predicate<? super String> filter) {
    // Name is fixed to "Map"
    return false;
  }

  public void forEachNames(Consumer<? super String> action) {
    // Name is fixed to "Map"
  }

  public void addName(int index, String element) {
    // Name is fixed to "Map"
  }

  public boolean addAllNames(int index, Collection<? extends String> collection) {
    // Name is fixed to "Map"
    return false;
  }

  public String removeName(int index) {
    // Name is fixed to "Map"
    return "";
  }

  public String setName(int index, String element) {
    // Name is fixed to "Map"
    return "";
  }

  public void replaceAllNames(UnaryOperator<String> operator) {
    // Name is fixed to "Map"
  }

  public void sortNames(Comparator<? super String> comparator) {
    // Name is fixed to "Map"
  }

  /**
   * overwrite setter for mcTypeArgument, because only two elements are allowed
   */

  public void clearMCTypeArguments() {
    Log.error("0xA6013 Not allowed to clear MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
  }

  public boolean addMCTypeArgument(de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6014 Not allowed to add an element to MCTypeArgumentList of ASTMCMapType. A MCTypeArgumentList must always have two elements.");
    return false;
  }

  public boolean addAllMCTypeArguments(Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6015 Not allowed to addAll elements to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    return false;
  }

  public boolean removeMCTypeArgument(Object element) {
    Log.error("0xA6016 Not allowed to remove an element to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    return false;
  }

  public boolean removeAllMCTypeArguments(Collection<?> collection) {
    Log.error("0xA6017 Not allowed to removeAll elements to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    return false;
  }

  public boolean retainAllMCTypeArguments(Collection<?> collection) {
    if (collection.contains(getKey()) && collection.contains(getValue())) {
      return true;
    } else {
      Log.error("0xA6018 Not allowed to retrainAll elements of MCTypeArgumentList of ASTMCMapType, without an match found.A MCTypeArgumentList must always have two elements.");
      return false;
    }
  }

  public boolean removeIfMCTypeArgument(Predicate<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> filter) {
    if (getMCTypeArgumentList().stream().noneMatch(filter)) {
      return getMCTypeArgumentList().removeIf(filter);
    } else {
      Log.error("0xA6019 Not allowed to remove an element to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
      return false;
    }
  }

  public void forEachMCTypeArguments(Consumer<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> action) {
    this.getMCTypeArgumentList().forEach(action);
  }

  public void addMCTypeArgument(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6020 Not allowed to add an element to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
  }

  public boolean addAllMCTypeArguments(int index, Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6021 Not allowed to addAll elements to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    return false;
  }

  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument removeMCTypeArgument(int index) {
    Log.error("0xA6022 Not allowed to remove an element to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    return getKey();
  }

  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument setMCTypeArgument(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    if (index == 0 || index == 1) {
      return this.getMCTypeArgumentList().set(index, element);
    } else {
      Log.error("0xA6023 Not allowed to set an element of MCTypeArgumentList of ASTMCMapType to a other index than 0.A MCTypeArgumentList must always have two elements.");
      return getKey();
    }
  }

  public void replaceAllMCTypeArguments(UnaryOperator<de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> operator) {
    this.getMCTypeArgumentList().replaceAll(operator);
  }

  public void sortMCTypeArguments(Comparator<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> comparator) {
    this.getMCTypeArgumentList().sort(comparator);
  }

  public void setMCTypeArgumentList(List<de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> mCTypeArguments) {
    if (mCTypeArguments.size() == 2) {
      this.mCTypeArguments = mCTypeArguments;
    } else {
      Log.error("0xA6024 Not allowed to set a list with a size other than 2 to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    }
  }
}
