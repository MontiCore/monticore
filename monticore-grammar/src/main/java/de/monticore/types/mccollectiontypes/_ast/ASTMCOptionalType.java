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
      setMCTypeArgumentList(typeArgumentList);
    } else {
      Log.error("0xA60125 Not allowed to set a TypeArgumentList greater than 1 in ASTMCOptionalType. Has to be exactly one.");
    }
    names = Lists.newArrayList("Optional");
  }

  public ASTMCOptionalType(List<ASTMCTypeArgument> typeArgument, List<String> name) {
    this(typeArgument);
  }

  public void setName(String name) {
    // Name is fixed to "Optional"   :  TODO: Internal Error, Error Msg
  }

  public ASTMCTypeArgument getMCTypeArgument() {
    return this.getMCTypeArgument(0);
  }

  public List<String> getNameList() {
    // copy of name List, so that the list cannot be changed
    return Lists.newArrayList(this.names);
  }


  public void setNameList(List<String> names) {
    Log.error("0xA6071 Not allowed to set the name of a ASTMCOptionalType, because ist is fixed to \"Optional\".");
  }

  public void clearNames() {
    Log.error("0xA6072 Not allowed to clear the name of a ASTMCOptionalType, because ist is fixed to \"Optional\".");
  }

  public boolean addName(String element) {
    Log.error("0xA6073 Not allowed to add a name of a ASTMCOptionalType, because ist is fixed to \"Optional\".");
    return false;
  }

  public boolean addAllNames(Collection<? extends String> collection) {
    Log.error("0xA6074 Not allowed to addAll names of a ASTMCOptionalType, because ist is fixed to \"Optional\".");
    return false;
  }

  public boolean removeName(Object element) {
    Log.error("0xA6075 Not allowed to remove a name of a ASTMCOptionalType, because ist is fixed to \"Optional\".");
    return false;
  }

  public boolean removeAllNames(Collection<?> collection) {
    Log.error("0xA6076 Not allowed to removeAll names of a ASTMCOptionalType, because ist is fixed to \"Optional\".");
    return false;
  }

  public boolean retainAllNames(Collection<?> collection) {
    if (collection.contains(getNameList())) {
      return true;
    } else {
      Log.error("0xA6077 Not allowed to retainAll names of a ASTMCOptionalType, because ist is fixed to \"Optional\".");
      return false;
    }
  }

  public boolean removeIfName(Predicate<? super String> filter) {
    if (getNameList().stream().noneMatch(filter)) {
      return getNameList().removeIf(filter);
    } else {
      Log.error("0xA6078 Not allowed to remove names if of a ASTMCOptionalType, because ist is fixed to \"Optional\".");
      return false;
    }
  }

  public void forEachNames(Consumer<? super String> action) {
    Log.error("0xA6079 Not allowed to execute forEachNames in ASTMCOptionalType, because ist is fixed to \"Optional\".");
  }

  public void addName(int index, String element) {
    Log.error("0xA6080 Not allowed to execute addName in ASTMCOptionalType, because ist is fixed to \"Optional\".");
  }

  public boolean addAllNames(int index, Collection<? extends String> collection) {
    Log.error("0xA6081 Not allowed to execute addAllNames in ASTMCOptionalType, because ist is fixed to \"Optional\".");
    return false;
  }

  public String removeName(int index) {
    Log.error("0xA6081 Not allowed to execute removeName in ASTMCOptionalType, because ist is fixed to \"Optional\".");
    return "";
  }

  public String setName(int index, String element) {
    Log.error("0xA6082 Not allowed to execute setName in ASTMCOptionalType, because ist is fixed to \"Optional\".");
    return "";
  }

  public void replaceAllNames(UnaryOperator<String> operator) {
    Log.error("0xA6083 Not allowed to execute replaceAllNames in ASTMCOptionalType, because ist is fixed to \"Optional\".");
  }

  public void sortNames(Comparator<? super String> comparator) {
    Log.error("0xA6084 Not allowed to execute sortNames in ASTMCOptionalType, because ist is fixed to \"Optional\".");
  }

  /**
   * overwrite setter for mcTypeArgument, because only one element is allowed
   */

  public void clearMCTypeArguments() {
    Log.error("0xA6013 Not allowed to clear MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
  }


  public boolean addMCTypeArgument(de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6014 Not allowed to add an element to MCTypeArgumentList of ASTMCOptionalType. A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean addAllMCTypeArguments(Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6015 Not allowed to addAll elements to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean removeMCTypeArgument(Object element) {
    Log.error("0xA6016 Not allowed to remove an element to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean removeAllMCTypeArguments(Collection<?> collection) {
    Log.error("0xA6017 Not allowed to removeAll elements to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean retainAllMCTypeArguments(Collection<?> collection) {
    if (collection.contains(getMCTypeArgument())) {
      return true;
    } else {
      Log.error("0xA6018 Not allowed to retrainAll elements of MCTypeArgumentList of ASTMCOptionalType, without an match found.A MCTypeArgumentList must always have one element.");
      return false;
    }
  }

  public boolean removeIfMCTypeArgument(Predicate<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> filter) {
    if (getMCTypeArgumentList().stream().noneMatch(filter)) {
      return getMCTypeArgumentList().removeIf(filter);
    } else {
      Log.error("0xA6019 Not allowed to remove an element to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
      return false;
    }
  }

  public void forEachMCTypeArguments(Consumer<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> action) {
    this.getMCTypeArgumentList().forEach(action);
  }

  public void addMCTypeArgument(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6020 Not allowed to add an element to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
  }

  public boolean addAllMCTypeArguments(int index, Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6021 Not allowed to addAll elements to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument removeMCTypeArgument(int index) {
    Log.error("0xA6022 Not allowed to remove an element to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    return getMCTypeArgument();
  }

  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument setMCTypeArgument(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    if (index == 0) {
      return this.getMCTypeArgumentList().set(index, element);
    } else {
      Log.error("0xA6023 Not allowed to set an element of MCTypeArgumentList of ASTMCOptionalType to a other index than 0.A MCTypeArgumentList must always have one element.");
      return getMCTypeArgument();
    }
  }

  public void replaceAllMCTypeArguments(UnaryOperator<de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> operator) {
    this.getMCTypeArgumentList().replaceAll(operator);
  }

  public void sortMCTypeArguments(Comparator<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> comparator) {
    this.getMCTypeArgumentList().sort(comparator);
  }

  public void setMCTypeArgumentList(List<de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> mCTypeArguments) {
    if (mCTypeArguments.size() == 1) {
      this.mCTypeArguments = mCTypeArguments;
    } else {
      Log.error("0xA6024 Not allowed to set a list with a size greater than 1 to MCTypeArgumentList of ASTMCOptionalType.A MCTypeArgumentList must always have one element.");
    }
  }
}
