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

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import de.monticore.languages.grammar.MCTypeSymbol.KindType;
import de.se_rwth.commons.logging.Log;

public class MCTypeSymbols {

  public static boolean isSubtype(MCTypeSymbol subType, MCTypeSymbol superType) {
    return isSubtype(subType, superType, newLinkedHashSet(Arrays.asList(subType)));
  }

  private static boolean isSubtype(MCTypeSymbol subType, MCTypeSymbol superType, Set<MCTypeSymbol> handledTypes) {
    if (areSameTypes(subType, superType)) {
      return true;
    }

    if ((subType.getKindOfType() == KindType.IDENT) &&
        (superType.getKindOfType() == KindType.IDENT)) {
      return (subType.getLexType().equals(superType.getLexType()));
    }

    // Try to find superType in super types of this type
    final Collection<MCTypeSymbol> allSuperTypes = subType.getAllSuperTypes();
    if (allSuperTypes.contains(superType)) {
      return true;
    }

    // check transitive sub-type relation
    for (MCTypeSymbol t : allSuperTypes) {
      if (handledTypes.add(superType)) {
        boolean subtypeOf = isSubtype(t, superType, handledTypes);
        if (subtypeOf) {
          return true;
        }
      }
    }

    return false;
  }

  // TODO NN <- PN does this method really what it should?
  public static boolean areSameTypes(MCTypeSymbol type1, MCTypeSymbol type2) {
    Log.errorIfNull(type1);
    Log.errorIfNull(type2);

    if (type1 == type2) {
      return true;
    }

    if (!type1.getKind().equals(type2.getKind())) {
      return false;
    }

    if (!type1.getName().equals(type2.getName())) {
      return false;
    }
    // TODO NN <- PN needed?
    // if (getOriginalLanguage() != type2.getOriginalLanguage()) {
    // return false;
    // }

    return type1.getGrammarSymbol().getName().equals(type2.getGrammarSymbol().getName());
  }

  public static boolean isAssignmentCompatibleOrUndecidable(MCTypeSymbol subType, MCTypeSymbol superType) {
    return isAssignmentCompatibleOrUndecidable(subType, superType, newLinkedHashSet(Arrays.asList(subType)));
  }

  private static boolean isAssignmentCompatibleOrUndecidable(MCTypeSymbol subType, MCTypeSymbol superType, Set<MCTypeSymbol> handledTypes) {
    // Return true if this type or the other type are both external
    // TODO GV: check, wenn Java angebunden
    if (subType.getKindOfType().equals(KindType.EXTERN)
        || superType.getKindOfType().equals(KindType.EXTERN)) {
      return true;
    }

    // Return true if this type and the other type are the same
    if (areSameTypes(subType, superType)) {
      return true;
    }

    if ((subType.getKindOfType() == KindType.IDENT) && (superType.getKindOfType() == KindType.IDENT)) {
      return subType.getLexType().equals(superType.getLexType());
    }

    // Try to find superType in supertypes of this type
    Collection<MCTypeSymbol> allSuperTypes = subType.getAllSuperTypes();
    if (allSuperTypes.contains(superType)) {
      return true;
    }

    // check transitive sub-type relation
    for (MCTypeSymbol t : allSuperTypes) {
      if (handledTypes.add(superType)) {
        boolean subtypeOf = isAssignmentCompatibleOrUndecidable(t, superType, handledTypes);
        if (subtypeOf) {
          return true;
        }
      }
    }

    return false;
  }

  }
