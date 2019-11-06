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

  public void setName(String name) {
    Log.error("0xA6103 Not allowed to set the name of a ASTMCOMapType, because ist is fixed to \"Map\".");
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
    Log.error("0xA6040 Not allowed to set the name of a ASTMCMapType, because ist is fixed to \"Map\".");
  }

  public void clearNames() {
    Log.error("0xA6041 Not allowed to clear the name of a ASTMCMapType, because ist is fixed to \"Map\".");
  }

  public boolean addName(String element) {
    Log.error("0xA6042 Not allowed to add a name of a ASTMCMapType, because ist is fixed to \"Map\".");
    return false;
  }

  public boolean addAllNames(Collection<? extends String> collection) {
    Log.error("0xA6043 Not allowed to addAll names of a ASTMCMapType, because ist is fixed to \"Map\".");
    return false;
  }

  public boolean removeName(Object element) {
    Log.error("0xA6044 Not allowed to remove a name of a ASTMCMapType, because ist is fixed to \"Map\".");
    return false;
  }

  public boolean removeAllNames(Collection<?> collection) {
    Log.error("0xA6045 Not allowed to removeAll names of a ASTMCMapType, because ist is fixed to \"Map\".");
    return false;
  }

  public boolean retainAllNames(Collection<?> collection) {
    if (collection.contains(getKey()) && collection.contains(getValue())) {
      return true;
    } else {
      Log.error("0xA6046 Not allowed to retainAll names of a ASTMCMapType, because ist is fixed to \"Map\".");
      return false;
    }
  }

  public boolean removeIfName(Predicate<? super String> filter) {
    if (getNameList().stream().noneMatch(filter)) {
      return getNameList().removeIf(filter);
    } else {
      Log.error("0xA6047 Not allowed to remove names if of a ASTMCMapType, because ist is fixed to \"Map\".");
      return false;
    }
  }

  public void forEachNames(Consumer<? super String> action) {
    Log.error("0xA6048 Not allowed to execute forEachNames in ASTMCMapType, because ist is fixed to \"Map\".");
  }

  public void addName(int index, String element) {
    Log.error("0xA6049 Not allowed to execute addName in ASTMCMapType, because ist is fixed to \"Map\".");
  }

  public boolean addAllNames(int index, Collection<? extends String> collection) {
    Log.error("0xA6050 Not allowed to execute addAllNames in ASTMCMapType, because ist is fixed to \"Map\".");
    return false;
  }

  public String removeName(int index) {
    Log.error("0xA6051 Not allowed to execute removeName in ASTMCMapType, because ist is fixed to \"Map\".");
    return "";
  }

  public String setName(int index, String element) {
    Log.error("0xA6052 Not allowed to execute setName in ASTMCMapType, because ist is fixed to \"Map\".");
    return "";
  }

  public void replaceAllNames(UnaryOperator<String> operator) {
    Log.error("0xA6053 Not allowed to execute replaceAllNames in ASTMCMapType, because ist is fixed to \"Map\".");
  }

  public void sortNames(Comparator<? super String> comparator) {
    Log.error("0xA6054 Not allowed to execute sortNames in ASTMCMapType, because ist is fixed to \"Map\".");
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
