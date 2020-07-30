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
    setMCTypeArgumentsList(Lists.newArrayList(null, null));
  }

  public ASTMCMapType(ASTMCTypeArgument key, ASTMCTypeArgument value, List<ASTMCTypeArgument> astmcTypeArguments, List<String> name) {
    if (key != null && value != null) {
      setMCTypeArgumentsList(Lists.newArrayList(key, value));
    } else if (astmcTypeArguments.size() == 2) {
      setMCTypeArgumentsList(astmcTypeArguments);
    }
    names = Lists.newArrayList("Map");
  }

  public ASTMCMapType(ASTMCTypeArgument key, ASTMCTypeArgument value) {
    this(key, value, Lists.newArrayList(), Lists.newArrayList("Map"));
  }

  public void setName(String name) {
    // Name is fixed to "Map"
  }

  @Override
  public List<ASTMCTypeArgument> getMCTypeArgumentsList() {
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


  @Override
  public List<String> getNamesList() {
    // copy of name List, so that the list cannot be changed
    return Lists.newArrayList(this.names);
  }

  @Override
  public void setNamesList(List<String> names) {
    // Name is fixed to "Map"
  }

  @Override
  public void clearNames() {
    // Name is fixed to "Map"
  }

  @Override
  public boolean addNames(String element) {
    // Name is fixed to "Map"
    return false;
  }

  @Override
  public boolean addAllNames(Collection<? extends String> collection) {
    // Name is fixed to "Map"
    return false;
  }

  @Override
  public boolean removeNames(Object element) {
    // Name is fixed to "Map"
    return false;
  }

  @Override
  public boolean removeAllNames(Collection<?> collection) {
    // Name is fixed to "Map"
    return false;
  }

  @Override
  public boolean retainAllNames(Collection<?> collection) {
    // Name is fixed to "Map"
    return false;
  }

  @Override
  public boolean removeIfNames(Predicate<? super String> filter) {
    // Name is fixed to "Map"
    return false;
  }

  @Override
  public void forEachNames(Consumer<? super String> action) {
    // Name is fixed to "Map"
  }

  @Override
  public void addNames(int index, String element) {
    // Name is fixed to "Map"
  }

  @Override
  public boolean addAllNames(int index, Collection<? extends String> collection) {
    // Name is fixed to "Map"
    return false;
  }

  @Override
  public String removeNames(int index) {
    // Name is fixed to "Map"
    return "";
  }

  @Override
  public String setNames(int index, String element) {
    // Name is fixed to "Map"
    return "";
  }

  @Override
  public void replaceAllNames(UnaryOperator<String> operator) {
    // Name is fixed to "Map"
  }

  @Override
  public void sortNames(Comparator<? super String> comparator) {
    // Name is fixed to "Map"
  }

  /**
   * overwrite setter for mcTypeArgument, because only two elements are allowed
   */

  @Override
  public void clearMCTypeArguments() {
    Log.error("0xA6039 Not allowed to clear MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
  }

  @Override
  public boolean addMCTypeArguments(de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6040 Not allowed to add an element to MCTypeArgumentList of ASTMCMapType. A MCTypeArgumentList must always have two elements.");
    return false;
  }

  @Override
  public boolean addAllMCTypeArguments(Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6041 Not allowed to addAll elements to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    return false;
  }

  @Override
  public boolean removeMCTypeArguments(Object element) {
    Log.error("0xA6042 Not allowed to remove an element to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    return false;
  }

  @Override
  public boolean removeAllMCTypeArguments(Collection<?> collection) {
    Log.error("0xA6043 Not allowed to removeAll elements to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    return false;
  }

  @Override
  public boolean retainAllMCTypeArguments(Collection<?> collection) {
    if (collection.contains(getKey()) && collection.contains(getValue())) {
      return true;
    } else {
      Log.error("0xA6044 Not allowed to retrainAll elements of MCTypeArgumentList of ASTMCMapType, without an match found.A MCTypeArgumentList must always have two elements.");
      return false;
    }
  }

  @Override
  public boolean removeIfMCTypeArguments(Predicate<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> filter) {
    if (getMCTypeArgumentsList().stream().noneMatch(filter)) {
      return getMCTypeArgumentsList().removeIf(filter);
    } else {
      Log.error("0xA6045 Not allowed to remove an element to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
      return false;
    }
  }

  @Override
  public void forEachMCTypeArguments(Consumer<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> action) {
    this.getMCTypeArgumentsList().forEach(action);
  }

  @Override
  public void addMCTypeArguments(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6046 Not allowed to add an element to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
  }

  @Override
  public boolean addAllMCTypeArguments(int index, Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6047 Not allowed to addAll elements to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    return false;
  }

  @Override
  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument removeMCTypeArguments(int index) {
    Log.error("0xA6048 Not allowed to remove an element to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    return getKey();
  }

  @Override
  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument setMCTypeArguments(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    if (index == 0 || index == 1) {
      return this.getMCTypeArgumentsList().set(index, element);
    } else {
      Log.error("0xA6049 Not allowed to set an element of MCTypeArgumentList of ASTMCMapType to a other index than 0.A MCTypeArgumentList must always have two elements.");
      return getKey();
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
    if (mCTypeArguments.size() == 2) {
      this.mCTypeArguments = mCTypeArguments;
    } else {
      Log.error("0xA6050 Not allowed to set a list with a size other than 2 to MCTypeArgumentList of ASTMCMapType.A MCTypeArgumentList must always have two elements.");
    }
  }
}
