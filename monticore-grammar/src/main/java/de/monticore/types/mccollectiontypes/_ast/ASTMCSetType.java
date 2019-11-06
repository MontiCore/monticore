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
      setMCTypeArgumentList(typeArgumentList);
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
    return this.getMCTypeArgument(0);
  }

  public void setName(String name) {
    Log.error("0xA6105 Not allowed to set the name of a ASTMCSetType, because ist is fixed to \"Set\".");
  }

  public List<String> getNameList() {
    // copy of name List, so that the list cannot be changed
    return Lists.newArrayList(this.names);
  }


  public void setNameList(List<String> names) {
    Log.error("0xA6085 Not allowed to set the name of a ASTMCSetType, because ist is fixed to \"Set\".");
  }

  public void clearNames() {
    Log.error("0xA6087 Not allowed to clear the name of a ASTMCSetType, because ist is fixed to \"Set\".");
  }

  public boolean addName(String element) {
    Log.error("0xA6088 Not allowed to add a name of a ASTMCSetType, because ist is fixed to \"Set\".");
    return false;
  }

  public boolean addAllNames(Collection<? extends String> collection) {
    Log.error("0xA6089 Not allowed to addAll names of a ASTMCSetType, because ist is fixed to \"Set\".");
    return false;
  }

  public boolean removeName(Object element) {
    Log.error("0xA6090 Not allowed to remove a name of a ASTMCSetType, because ist is fixed to \"Set\".");
    return false;
  }

  public boolean removeAllNames(Collection<?> collection) {
    Log.error("0xA6091 Not allowed to removeAll names of a ASTMCSetType, because ist is fixed to \"Set\".");
    return false;
  }

  public boolean retainAllNames(Collection<?> collection) {
    if (collection.contains(getNameList())) {
      return true;
    } else {
      Log.error("0xA6092 Not allowed to retainAll names of a ASTMCSetType, because ist is fixed to \"Set\".");
      return false;
    }
  }

  public boolean removeIfName(Predicate<? super String> filter) {
    if (getNameList().stream().noneMatch(filter)) {
      return getNameList().removeIf(filter);
    } else {
      Log.error("0xA6093 Not allowed to remove names if of a ASTMCSetType, because ist is fixed to \"Set\".");
      return false;
    }
  }

  public void forEachNames(Consumer<? super String> action) {
    Log.error("0xA6094 Not allowed to execute forEachNames in ASTMCSetType, because ist is fixed to \"Set\".");
  }

  public void addName(int index, String element) {
    Log.error("0xA6095 Not allowed to execute addName in ASTMCSetType, because ist is fixed to \"Set\".");
  }

  public boolean addAllNames(int index, Collection<? extends String> collection) {
    Log.error("0xA6096 Not allowed to execute addAllNames in ASTMCSetType, because ist is fixed to \"Set\".");
    return false;
  }

  public String removeName(int index) {
    Log.error("0xA6097 Not allowed to execute removeName in ASTMCSetType, because ist is fixed to \"Set\".");
    return "";
  }

  public String setName(int index, String element) {
    Log.error("0xA6098 Not allowed to execute setName in ASTMCSetType, because ist is fixed to \"Set\".");
    return "";
  }

  public void replaceAllNames(UnaryOperator<String> operator) {
    Log.error("0xA6099 Not allowed to execute replaceAllNames in ASTMCSetType, because ist is fixed to \"Set\".");
  }

  public void sortNames(Comparator<? super String> comparator) {
    Log.error("0xA6101 Not allowed to execute sortNames in ASTMCSetType, because ist is fixed to \"Set\".");
  }


  /**
   * overwrite setter for mcTypeArgument, because only one element is allowed
   */

  public void clearMCTypeArguments() {
    Log.error("0xA6026 Not allowed to clear MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
  }

  public boolean addMCTypeArgument(de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6027 Not allowed to add an element to MCTypeArgumentList of ASTMCSetType. A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean addAllMCTypeArguments(Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6028 Not allowed to addAll elements to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean removeMCTypeArgument(Object element) {
    Log.error("0xA6029 Not allowed to remove an element to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean removeAllMCTypeArguments(Collection<?> collection) {
    Log.error("0xA6030 Not allowed to removeAll elements to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean retainAllMCTypeArguments(Collection<?> collection) {
    if (collection.contains(getMCTypeArgument())) {
      return true;
    } else {
      Log.error("0xA6031 Not allowed to retrainAll elements of MCTypeArgumentList of ASTMCSetType, without an match found.A MCTypeArgumentList must always have one element.");
      return false;
    }
  }

  public boolean removeIfMCTypeArgument(Predicate<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> filter) {
    if (getMCTypeArgumentList().stream().noneMatch(filter)) {
      return getMCTypeArgumentList().removeIf(filter);
    } else {
      Log.error("0xA6032 Not allowed to remove an element to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
      return false;
    }
  }

  public void forEachMCTypeArguments(Consumer<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> action) {
    this.getMCTypeArgumentList().forEach(action);
  }

  public void addMCTypeArgument(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6033 Not allowed to add an element to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
  }

  public boolean addAllMCTypeArguments(int index, Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6034 Not allowed to addAll elements to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument removeMCTypeArgument(int index) {
    Log.error("0xA6035 Not allowed to remove an element to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    return getMCTypeArgument();
  }

  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument setMCTypeArgument(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    if (index == 0) {
      return this.getMCTypeArgumentList().set(index, element);
    } else {
      Log.error("0xA6036 Not allowed to set an element of MCTypeArgumentList of ASTMCSetType to a other index than 0.A MCTypeArgumentList must always have one element.");
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
      Log.error("0xA6037 Not allowed to set a list with a size greater than 1 to MCTypeArgumentList of ASTMCSetType.A MCTypeArgumentList must always have one element.");
    }
  }
}
