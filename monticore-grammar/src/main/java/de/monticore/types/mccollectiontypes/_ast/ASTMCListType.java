/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ASTMCListType extends ASTMCListTypeTOP {
  public ASTMCListType() {
    names = Lists.newArrayList("List");
  }

  public ASTMCListType(List<ASTMCTypeArgument> typeArgumentList, List<String> name) {
    this(typeArgumentList);
  }

  public ASTMCListType(List<ASTMCTypeArgument> typeArgumentList) {
    if (typeArgumentList.size() == 1) {
      setMCTypeArgumentList(typeArgumentList);
    } else {
      Log.error("0xA6012 Not allowed to set a TypeArgumentList greater than 1 in ASTMCListType. Has to be exactly one.");
    }
    names = Lists.newArrayList("List");
  }

  public void setName(String name) {
    // Name is fixed to "List"   :  TODO: Internal Error, Error Msg
  }

  public ASTMCTypeArgument getMCTypeArgument() {
    return this.getMCTypeArgument(0);
  }


  public List<String> getNameList() {
    // copy of name List, so that the list cannot be changed
    return Lists.newArrayList(this.names);
  }

  /**
   * overwrite setter for nameList, because only the value "List" is allowed
   */

  public void setNameList(List<String> names) {
    // Name is fixed to "List" TODO: Internal Error, Error Msg
  }

  public void clearNames() {
    // Name is fixed to "List"
  }

  public boolean addName(String element) {
    // Name is fixed to "List"
    return false;
  }

  public boolean addAllNames(Collection<? extends String> collection) {
    // Name is fixed to "List"
    return false;
  }

  public boolean removeName(Object element) {
    // Name is fixed to "List"
    return false;
  }

  public boolean removeAllNames(Collection<?> collection) {
    // Name is fixed to "List"
    return false;
  }

  public boolean retainAllNames(Collection<?> collection) {
    // Name is fixed to "List"
    return false;
  }

  public boolean removeIfName(Predicate<? super String> filter) {
    // Name is fixed to "List"
    return false;
  }

  public void forEachNames(Consumer<? super String> action) {
    // Name is fixed to "List"
  }

  public void addName(int index, String element) {
    // Name is fixed to "List"
  }

  public boolean addAllNames(int index, Collection<? extends String> collection) {
    // Name is fixed to "List"
    return false;
  }

  public String removeName(int index) {
    // Name is fixed to "List"
    return "";
  }

  public String setName(int index, String element) {
    // Name is fixed to "List"
    return "";
  }

  public void replaceAllNames(UnaryOperator<String> operator) {
    // Name is fixed to "List"
  }

  public void sortNames(Comparator<? super String> comparator) {
    // Name is fixed to "List"
  }

  /**
   * overwrite setter for mcTypeArgument, because only one element is allowed
   */

  public void clearMCTypeArguments() {
    Log.error("0xA6000 Not allowed to clear MCTypeArgumentList of ASTMCListType.A MCTypeArgumentList must always have one element.");
  }


  public boolean addMCTypeArgument(de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6001 Not allowed to add an element to MCTypeArgumentList of ASTMCListType. A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean addAllMCTypeArguments(Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA6002 Not allowed to addAll elements to MCTypeArgumentList of ASTMCListType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean removeMCTypeArgument(Object element) {
    Log.error("0xA6003 Not allowed to remove an element to MCTypeArgumentList of ASTMCListType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean removeAllMCTypeArguments(Collection<?> collection) {
    Log.error("0xA6004 Not allowed to removeAll elements to MCTypeArgumentList of ASTMCListType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public boolean retainAllMCTypeArguments(Collection<?> collection) {
    if (collection.contains(getMCTypeArgument())) {
      return true;
    } else {
      Log.error("0xA6005 Not allowed to retrainAll elements of MCTypeArgumentList of ASTMCListType, without an match found.A MCTypeArgumentList must always have one element.");
      return false;
    }
  }

  public boolean removeIfMCTypeArgument(Predicate<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> filter) {
    if (getMCTypeArgumentList().stream().noneMatch(filter)) {
      return getMCTypeArgumentList().removeIf(filter);
    } else {
      Log.error("0xA6006 Not allowed to remove an element to MCTypeArgumentList of ASTMCListType.A MCTypeArgumentList must always have one element.");
      return false;
    }
  }

  public void forEachMCTypeArguments(Consumer<? super de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> action) {
    this.getMCTypeArgumentList().forEach(action);
  }

  public void addMCTypeArgument(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    Log.error("0xA6007 Not allowed to add an element to MCTypeArgumentList of ASTMCListType.A MCTypeArgumentList must always have one element.");
  }

  public boolean addAllMCTypeArguments(int index, Collection<? extends de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument> collection) {
    Log.error("0xA608 Not allowed to addAll elements to MCTypeArgumentList of ASTMCListType.A MCTypeArgumentList must always have one element.");
    return false;
  }

  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument removeMCTypeArgument(int index) {
    Log.error("0xA6009 Not allowed to remove an element to MCTypeArgumentList of ASTMCListType.A MCTypeArgumentList must always have one element.");
    return getMCTypeArgument();
  }

  public de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument setMCTypeArgument(int index, de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument element) {
    if (index == 0) {
      return this.getMCTypeArgumentList().set(index, element);
    } else {
      Log.error("0xA6010 Not allowed to set an element of MCTypeArgumentList of ASTMCListType to a other index than 0.A MCTypeArgumentList must always have one element.");
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
      Log.error("0xA6011 Not allowed to set a list with a size greater than 1 to MCTypeArgumentList of ASTMCListType.A MCTypeArgumentList must always have one element.");
    }
  }
}
