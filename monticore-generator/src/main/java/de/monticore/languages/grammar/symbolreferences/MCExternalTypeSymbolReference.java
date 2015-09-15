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

package de.monticore.languages.grammar.symbolreferences;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.monticore.grammar.grammar._ast.ASTMethod;
import de.monticore.languages.grammar.MCAttributeSymbol;
import de.monticore.languages.grammar.MCExternalTypeSymbol;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.CommonSymbolReference;
import de.monticore.symboltable.references.SymbolReference;
import de.monticore.ast.Comment;

/**
 * Reference for {@link MCExternalTypeSymbol}.
 *
 * @author Pedram Mir Seyed Nazari
 */
public class MCExternalTypeSymbolReference extends MCExternalTypeSymbol
    implements SymbolReference<MCExternalTypeSymbol> {

  private final SymbolReference<MCExternalTypeSymbol> typeReference;

  public MCExternalTypeSymbolReference(String name, MCGrammarSymbol grammarSymbol, Scope definingScopeOfReference) {
    super(name, grammarSymbol);

    this.typeReference = new CommonSymbolReference<>(name, MCExternalTypeSymbol.KIND, definingScopeOfReference);
  }


  @Override
  public MCExternalTypeSymbol getReferencedSymbol() {
    return typeReference.getReferencedSymbol();
  }

  @Override
  public boolean existsReferencedSymbol() {
    return typeReference.existsReferencedSymbol();
  }

  @Override
  public boolean isReferencedSymbolLoaded() {
    return typeReference.isReferencedSymbolLoaded();
  }

  @Override
  public List<MCTypeSymbol> getSuperInterfaces() {
    return getReferencedSymbol().getSuperInterfaces();
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
    if (getName() == null && other.getName() != null) {
      return false;
    }
    else if (!getName().equals(other.getName())) {
      return false;
    }
    return true;
  }

  @Override
  public boolean isASTNode() {
    return getReferencedSymbol().isASTNode();
  }

  @Override
  public void setASTNode(boolean astNode) {
    getReferencedSymbol().setASTNode(astNode);
  }

  @Override
  public String getListType() {
    return getReferencedSymbol().getListType();
  }

  @Override
  public String getListImplementation() {
    return getReferencedSymbol().getListImplementation();
  }

  @Override
  public int compareTo(MCTypeSymbol otherType) {
    return getReferencedSymbol().compareTo(otherType);
  }

  @Override
  public boolean isSubtypeOf(MCTypeSymbol otherType) {
    return getReferencedSymbol().isSubtypeOf(otherType);
  }

  @Override
  public boolean isAssignmentCompatibleOrUndecidable(MCTypeSymbol otherType) {
    return getReferencedSymbol().isAssignmentCompatibleOrUndecidable(otherType);
  }

  @Override
  public boolean isSameType(MCTypeSymbol otherType) {
    return getReferencedSymbol().isSameType(otherType);
  }

  @Override
  public boolean add(ASTMethod o) {
    return getReferencedSymbol().add(o);
  }

  @Override
  public List<ASTMethod> getAstMethods() {
    return getReferencedSymbol().getAstMethods();
  }

  @Override
  public List<MCTypeSymbol> getSuperTypes() {
    return getReferencedSymbol().getSuperTypes();
  }

  @Override
  public List<MCTypeSymbol> getSuperInterfacesAbstractSyntaxOnly() {
    return getReferencedSymbol().getSuperInterfacesAbstractSyntaxOnly();
  }

  @Override
  public List<MCTypeSymbol> getSuperClassesAbstractSyntaxOnly() {
    return getReferencedSymbol().getSuperClassesAbstractSyntaxOnly();
  }

  @Override
  public List<MCTypeSymbol> getSuperClasses() {
    return getReferencedSymbol().getSuperClasses();
  }

  @Override
  public void addSuperClass(MCTypeSymbol superClass, boolean astOnly) {
    getReferencedSymbol().addSuperClass(superClass, astOnly);
  }

  @Override
  public void addSuperInterface(MCTypeSymbol superInterface, boolean astOnly) {
    getReferencedSymbol().addSuperInterface(superInterface, astOnly);
  }

  @Override
  public Collection<MCTypeSymbol> getAllSuperTypes() {
    return getReferencedSymbol().getAllSuperTypes();
  }

  @Override
  public Collection<MCTypeSymbol> getAllSTSuperInterfaces() {
    return getReferencedSymbol().getAllSTSuperInterfaces();
  }

  @Override
  public void addComment(Comment comment) {
    getReferencedSymbol().addComment(comment);
  }

  @Override
  public List<Comment> getComments() {
    return getReferencedSymbol().getComments();
  }

  @Override
  public void addEnum(String name, String constant) {
    getReferencedSymbol().addEnum(name, constant);
  }

  @Override
  public int getEnumSize() {
    return getReferencedSymbol().getEnumSize();
  }

  @Override
  public List<String> getEnumValues() {
    return getReferencedSymbol().getEnumValues();
  }

  @Override
  public List<String> getEnumConstantInGrammar(String enumValue) {
    return getReferencedSymbol().getEnumConstantInGrammar(enumValue);
  }

  @Override
  public MCAttributeSymbol getAttribute(String attrName) {
    return getReferencedSymbol().getAttribute(attrName);
  }

  @Override
  public Set<String> getAttributeNames() {
    return getReferencedSymbol().getAttributeNames();
  }

  @Override
  public Collection<MCAttributeSymbol> getAttributes() {
    return getReferencedSymbol().getAttributes();
  }

  @Override
  public void addAttribute(MCAttributeSymbol attr) {
    getReferencedSymbol().addAttribute(attr);
  }

  @Override
  public String getLexType() {
    return getReferencedSymbol().getLexType();
  }

  @Override
  public void setLexType(String lexType) {
    getReferencedSymbol().setLexType(lexType);
  }

  @Override
  public boolean isAbstract() {
    return getReferencedSymbol().isAbstract();
  }

  @Override
  public void setAbstract(boolean isAbstract) {
    getReferencedSymbol().setAbstract(isAbstract);
  }

  @Override
  public boolean isImplicitEnum() {
    return getReferencedSymbol().isImplicitEnum();
  }

  @Override
  public List<MCTypeSymbol> getAllSuperclasses() {
    return getReferencedSymbol().getAllSuperclasses();
  }

  @Override
  public List<MCTypeSymbol> getAllSuperInterfaces() {
    return getReferencedSymbol().getAllSuperInterfaces();
  }

  @Override
  public Set<MCTypeSymbol> getOverloadedTypes() {
    return getReferencedSymbol().getOverloadedTypes();
  }

  @Override
  public String getSimpleName() {
    return getReferencedSymbol().getSimpleName();
  }

  @Override
  public String toExtendedString() {
    return getReferencedSymbol().toExtendedString();
  }

  @Override
  public String toString() {
    return getReferencedSymbol().toString();
  }

  @Override
  public int hashCode() {
    return getReferencedSymbol().hashCode();
  }

  @Override
  public void setKindOfType(KindType kindType) {
    getReferencedSymbol().setKindOfType(kindType);
  }

  @Override
  public String getConvertFunction() {
    return getReferencedSymbol().getConvertFunction();
  }

  @Override
  public void setConvertFunction(String convertFunction) {
    getReferencedSymbol().setConvertFunction(convertFunction);
  }

  @Override
  public String getDefaultValue() {
    return getReferencedSymbol().getDefaultValue();
  }
}
