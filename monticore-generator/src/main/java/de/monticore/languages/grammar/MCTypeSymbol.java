/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.languages.grammar;

import static de.monticore.languages.grammar.MCTypeSymbols.areSameTypes;
import static de.monticore.languages.grammar.MCTypeSymbols.isSubtype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.monticore.ast.Comment;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.grammar.grammar._ast.ASTMethod;
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.types.JTypeSymbolKind;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

/**
 * Symbol for a type in MontiCore grammar. This class represents a Java type (class and
 * primitives) that is defined or used inside the grammar. Types are identified by their
 * unqualified name in the grammar symbol table if they are defined by a rule or the grammar. Use
 * "super." as a prefix to explicitly access the type of supergrammars this grammar overrides.
 * Native/external types are types that are not defined in the grammar but are referred from it.
 * These types are indicated by the suffix "/" in the grammar and refer to regular Java types. To
 * access these type use the prefix "/" e.g. "/String" or "/int". All names of types are case
 * sensitive although a syntactic check is applied during symboltable building that checks if a user
 * uses ambiguous names like "NAME" and "Name" in the same grammar. <br>
 *
 * @author Krahn, Volkova, Mir Seyed Nazari<br>
 */
public class MCTypeSymbol extends CommonScopeSpanningSymbol implements Comparable<MCTypeSymbol> {

  public static final MCTypeKind KIND = new MCTypeKind();

  private MCGrammarSymbol grammarSymbol;

  /*
   * kind of type, default is class
   */
  private KindType kindOfType = KindType.CLASS;

  // List of enum values for this type
  private Map<String, Set<String>> possibleValuesForEnum = new HashMap<>();

  private List<String> enumValues = new ArrayList<>();

  private List<MCTypeSymbol> superClasses = new ArrayList<>();

  private List<MCTypeSymbol> superInterfaces = new ArrayList<>();

  /*
   * List of super types (abstract syntax only)
   */
  private List<MCTypeSymbol> astSuperClasses = new ArrayList<>();
  /*
   * List of super interfaces (abstract syntax only)
   */
  private List<MCTypeSymbol> astSuperInterfaces = new ArrayList<>();

  private List<ASTMethod> methods = new ArrayList<>();

  // List of all comments for this type
  private List<Comment> grammarDoc = new ArrayList<>();

  private String convertFunction = "";
  private String lexType = "";

  private boolean isAbstract = false;
  private boolean isExternal = false;
  private boolean isInterface = false;

  protected MCTypeSymbol(String name) {
    super(name, KIND);
  }

  protected MCTypeSymbol(String typeName, MCGrammarSymbol grammarSymbol) {
    this(typeName);
    this.grammarSymbol = grammarSymbol;
  }

  @Override
  public int compareTo(MCTypeSymbol otherType) {
    boolean grammarCompare =
           (grammarSymbol == null)
        || (otherType.getGrammarSymbol() == null)
        || grammarSymbol.getFullName().equals(otherType.getGrammarSymbol().getFullName());

    int grComp = grammarCompare ? 0 : 2;
    return getName().compareTo(otherType.getName()) + grComp;
  }

  /**
   * @return true,  if this type is a subtype of <code>otherType</code>
   */
  public boolean isSubtypeOf(MCTypeSymbol otherType) {
    return isSubtype(this, otherType);
  }

  public boolean isAssignmentCompatibleOrUndecidable(MCTypeSymbol otherType) {
    return MCTypeSymbols.isAssignmentCompatibleOrUndecidable(this, otherType);
  }

  public boolean isSameType(MCTypeSymbol otherType) {
    return areSameTypes(this, otherType);
  }


  // -------------- Handling of methods -------------------------

  public boolean add(ASTMethod o) {
    return methods.add(o);
  }

  public List<ASTMethod> getAstMethods() {
    return methods;
  }

  // -------------- Handling of names -------------------------
  public String getQualifiedName(String prefix, String suffix) {
    if (getKindOfType().equals(KindType.EXTERN)) {
      return getName();
    }
    else {
      String string = getGrammarSymbol().getFullName().toLowerCase()
          + GeneratorHelper.AST_PACKAGE_SUFFIX_DOT + "." + prefix +
          StringTransformations.capitalize(getName() + suffix);

      if (string.startsWith(".")) {
        string = string.substring(1);
      }
      return string;
    }
  }

  /**
   * @return the qualified name for this type
   */
  public String getQualifiedName() {
    if (getKindOfType().equals(KindType.IDENT)) {
      return lexType;
    }
    if (getKindOfType().equals(KindType.CONST)) {
      return getConstantType();
    }
    if (getKindOfType().equals(KindType.ENUM)) {
      return getQualifiedName(GeneratorHelper.AST_PREFIX, "");
    }
    return getQualifiedName(GeneratorHelper.AST_PREFIX, "");
  }

  private String getConstantType() {
    return (this.getEnumValues().size() > 1) ? "int" : "boolean";
  }

  // -------------- Handling of super types -------------------------

  public List<MCTypeSymbol> getSuperTypes() {
    List<MCTypeSymbol> superTypes = superClasses.stream().collect(Collectors.toList());
    superTypes.addAll(astSuperInterfaces.stream().collect(Collectors.toList()));
    superTypes.addAll(astSuperClasses.stream().collect(Collectors.toList()));
    superTypes.addAll(superInterfaces.stream().collect(Collectors.toList()));

    return ImmutableList.copyOf(superTypes);
  }

  public List<MCTypeSymbol> getSuperInterfacesAbstractSyntaxOnly() {
    return ImmutableList.copyOf(astSuperInterfaces);
  }

  public List<MCTypeSymbol> getSuperClassesAbstractSyntaxOnly() {
    return ImmutableList.copyOf(astSuperClasses);
  }

  public List<MCTypeSymbol> getSuperInterfaces() {
    return ImmutableList.copyOf(superInterfaces);
  }

  public List<MCTypeSymbol> getSuperClasses() {
    return ImmutableList.copyOf(superClasses);
  }

  public void addSuperClass(MCTypeSymbol superClass, boolean astOnly) {
    Log.errorIfNull(superClass);

    if (astOnly) {
      if (!astSuperClasses.contains(superClass)) {
        astSuperClasses.add(superClass);
      }
    }
    else {
      if (!superClasses.contains(superClass)) {
        superClasses.add(superClass);
      }
    }

  }

  public void addSuperInterface(MCTypeSymbol superInterface, boolean astOnly) {
    Log.errorIfNull(superInterface);

    if (astOnly) {
      if (!astSuperInterfaces.contains(superInterface)) {
        astSuperInterfaces.add(superInterface);
      }
    }
    else {
      if (!superInterfaces.contains(superInterface)) {
        superInterfaces.add(superInterface);
      }
    }
  }

  /**
   * @return all direct AND indirect super types.
   */
  public Collection<MCTypeSymbol> getAllSuperTypes() {
    Set<MCTypeSymbol> supersHandled = new LinkedHashSet<>();
    List<MCTypeSymbol> supersToHandle = new ArrayList<>();
    supersToHandle.addAll(getSuperTypes());
    Set<MCTypeSymbol> supersNextRound = new LinkedHashSet<>();

    while (!supersToHandle.isEmpty()) {
      for (MCTypeSymbol superType : supersToHandle) {
        if (!supersHandled.contains(superType)) {
          supersNextRound.addAll(superType.getSuperTypes());
        }
        supersHandled.add(superType);
      }
      supersToHandle.clear();
      supersToHandle.addAll(supersNextRound);
      supersNextRound.clear();
    }

    return ImmutableSet.copyOf(supersHandled);
  }

  /**
   * @return all direct AND indirect super interfaces of this type.
   */
  public Collection<MCTypeSymbol> getAllSTSuperInterfaces() {
    Set<MCTypeSymbol> supersHandled = new LinkedHashSet<>();
    List<MCTypeSymbol> supersToHandle = Lists.newArrayList(getSuperInterfaces());
    Set<MCTypeSymbol> supersNextRound = new LinkedHashSet<>();

    while (!supersToHandle.isEmpty()) {
      for (MCTypeSymbol superType : supersToHandle) {
        if (!supersHandled.contains(superType)) {
          supersNextRound.addAll(superType.getSuperInterfaces());
        }
        supersHandled.add(superType);
      }
      supersToHandle.clear();
      supersToHandle.addAll(supersNextRound);
      supersNextRound.clear();
    }

    return ImmutableSet.copyOf(supersHandled);
  }

  // -------------- Handling of comments -------------------------

  /**
   * Add a grammarDoc comment to this type
   *
   * @param comment Grammardoc comment
   */
  public void addComment(Comment comment) {
    grammarDoc.add(comment);
  }

  /**
   * Returns all comments for this type
   *
   * @return Comments for tis type
   */
  public List<Comment> getComments() {
    return Collections.unmodifiableList(grammarDoc);
  }

  // -------------- Handling of enums -------------------------

  public void addEnum(String name, String constant) {
    Set<String> constantsInGrammar = possibleValuesForEnum.get(name.intern());
    if (constantsInGrammar == null) {
      constantsInGrammar = new LinkedHashSet<>();
      possibleValuesForEnum.put(name.intern(), constantsInGrammar);
    }
    constantsInGrammar.add(constant.intern());
    if (!enumValues.contains(name.intern())) {
      enumValues.add(name.intern());
    }
  }

  public int getEnumSize() {
    return enumValues.size();
  }

  public List<String> getEnumValues() {
    return ImmutableList.copyOf(enumValues);
  }

  public List<String> getEnumConstantInGrammar(String enumValue) {
    return ImmutableList.copyOf(possibleValuesForEnum.get(enumValue));
  }

  // -------------- Handling of attributes -------------------------
  // TODO NN <- PN are attributes really needed in the type symbol?


  /**
   * Gets a specific Attribute by its name
   *
   * @param attrName Name of attribute
   * @return Attribute
   */
  // TODO PN return optional value
  public MCAttributeSymbol getAttribute(String attrName) {
    return spannedScope.<MCAttributeSymbol>resolveLocally(attrName, MCAttributeSymbol.KIND).orElse(null);
  }

  /**
   * @return Set of all attribute names
   */
  public Set<String> getAttributeNames() {
    final Set<String> attrNames = getAttributes().stream()
        .map(MCAttributeSymbol::getName)
        .collect(Collectors.toSet());

    return ImmutableSet.copyOf(attrNames);
  }

  /**
   * Returns set of of all attributes
   *
   * @return Set of attributes
   */
  public Collection<MCAttributeSymbol> getAttributes() {
    return spannedScope.resolveLocally(MCAttributeSymbol.KIND);
  }

  public void addAttribute(MCAttributeSymbol attr) {
    spannedScope.add(attr);
  }



  // -------------- Getter and setter -------------------------

  public String getLexType() {
    return lexType;
  }

  public void setLexType(String lexType) {
    this.lexType = lexType;
  }

  // TODO NN <- PN why is this method called "isASTNode"? Rename it?
  public boolean isASTNode() {
    return getKindOfType().equals(KindType.INTERFACE) || getKindOfType().equals(KindType.CLASS);
  }

  public boolean isInterface() {
    return isInterface;
  }

  public void setInterface(boolean isInterface) {
    this.isInterface = isInterface;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public void setAbstract(boolean isAbstract) {
    this.isAbstract = isAbstract;
  }

  public boolean isImplicitEnum() {
    return getKindOfType().equals(KindType.CONST);
  }

  public boolean isExternal() {
    return isExternal;
  }

  public void setExternal(boolean external) {
    this.isExternal = external; // TODO GV: check kindOfType
  }

  public void setGrammarSymbol(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
  }

  public MCGrammarSymbol getGrammarSymbol() {
    return grammarSymbol;
  }

  public List<MCTypeSymbol> getAllSuperclasses() {
    List<MCTypeSymbol> ret = new ArrayList<>();
    for (MCTypeSymbol sup : superClasses) {
      MCTypeSymbol typeName = getGrammarSymbol().getTypeWithInherited(sup.getName());
      if (typeName != null) {
        ret.add(typeName);
      }
    }
    return ret;
  }

  public List<MCTypeSymbol> getAllSuperInterfaces() {
    List<MCTypeSymbol> allSuperInterfaces = new ArrayList<>();
    for (MCTypeSymbol sup : superInterfaces) {
      MCTypeSymbol type = getGrammarSymbol().getTypeWithInherited(sup.getName());
      if (type != null) {
        if (!allSuperInterfaces.contains(type)) {
          allSuperInterfaces.add(type);
        }
      }
    }

    return allSuperInterfaces;
  }

  /**
   * @return all grammar types with the same name as this type which have been defined in the super
   * grammars
   */
  public Set<MCTypeSymbol> getOverloadedTypes() {
    Set<MCTypeSymbol> overloadedTypes = new LinkedHashSet<>();
    overloadedTypes.addAll(grammarSymbol.getAllMCTypesWithGivenName(this.getSimpleName()));
    overloadedTypes.remove(this);

    return ImmutableSet.copyOf(overloadedTypes);
  }

  /**
   * Gets the unqualified name of this type
   *
   * @return
   */
  public String getSimpleName() {
    return Names.getSimpleName(getName());
  }

  public String toExtendedString() {
    return String.valueOf(getKindOfType()) + " " + getName() + " " + isExternal() + " in the "
        + "grammar: " + getGrammarSymbol();
  }

  @Override
  public String toString() {
    return getName() + " KIND " + getKind() + (grammarSymbol == null ? "" : " (in " +
        grammarSymbol.getFullName() + ")");
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    // TODO PN uncomment following check and overwrite this method in MCTypeSymbolReference
    // if (obj instanceof SymbolReference) {
    // return false;
    // }
    if (!(obj instanceof MCTypeSymbol)) {
      return false;
    }

    MCTypeSymbol other = (MCTypeSymbol) obj;
    // TODO PN needed?
    // if (getBestKnownVersion() != this || other.getBestKnownVersion() != other) {
    // return getBestKnownVersion().equals(other.getBestKnownVersion());
    // }
    // if (getEntryState() == STEntryState.UNQUALIFIED) {
    // return false;
    // }

    if (getName() == null && other.getName() != null) {
      return false;
    }
    else if (!getName().equals(other.getName())) {
      return false;
    }
    if (grammarSymbol != null && other.getGrammarSymbol() != null) {
      return grammarSymbol.getFullName().equals(other.getGrammarSymbol().getFullName());
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
    result = prime * result
        + ((getGrammarSymbol() == null) ? 0 : getGrammarSymbol().getFullName().hashCode());
    return result;
  }

  /**
   * Returns the kind of this type
   */
  public KindType getKindOfType() {
    return kindOfType;
  }

  /**
   * Sets interface variable (default is class)
   *
   * @param kindType
   */
  public void setKindOfType(KindType kindType) {
    this.kindOfType = kindType;
  }

  /**
   * Determines the kidnof this type
   *
   * @author krahn
   */
  public enum KindType {
    CLASS("CLASS"),
    INTERFACE("INTERFACE"),
    IDENT("IDENT"), ENUM("ENUM"),
    CONST("CONST"),
    EXTERN("EXTERN"),
    ASSOCIATION("ASSOCIATION");

    private String theType;

    KindType(String t) {
      theType = t;
    }

    @Override
    public String toString() {
      return theType;
    }
  }

  public static final class MCTypeKind extends JTypeSymbolKind {

    private MCTypeKind() {
    }
  }

  // ================================================================== //
  // ================ Generator-specific Information ================== //
  // ================================================================== //

  /**
   * Is used from within templates.
   *
   * @return the convert function
   */
  public String getConvertFunction() {
    return convertFunction;
  }

  public void setConvertFunction(String convertFunction) {
    this.convertFunction = convertFunction;
  }

  public String getDefaultValue() {
    if (getQualifiedName().equals("int")) {
      return "0";
    }
    else if (getQualifiedName().equals("boolean")) {
      return "false";
    }
    else {
      return "null";
    }
  }

  public String getListType() {
    if (isASTNode()) {
      return getQualifiedName() + "List";
    }
    else {
      return "java.util.List<" + getQualifiedName() + ">";
    }
  }

  public String getListImplementation() {
    if (isASTNode()) {
      return getQualifiedName() + "List";
    }
    else {
      return "java.util.ArrayList<" + getQualifiedName() + ">";
    }
  }

}
