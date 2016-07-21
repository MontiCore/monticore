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

package de.monticore.symboltable.types;

import java.util.ArrayList;
import java.util.List;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.JTypeSymbolKind;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTComplexArrayType;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTWildcardType;
import de.se_rwth.commons.logging.Log;

/**
 * Common methods for working with the JTypes-framework.
 *
 * @author Robert Heim
 */
public class JTypesHelper {
  /**
   * A factory to create type references. Implementations ensure the specific type of the created
   * reference. If sufficient consider using the {@link CommonJTypeReferenceFactory}.
   *
   * @author Robert Heim
   */
  public interface JTypeReferenceFactory {
    JTypeReference<? extends JTypeSymbol> create(String name, Scope definingScopeOfReference,
        int arrayDimension);
  }
  
  /**
   * Default implementation to create {@link CommonJTypeReference}s using the
   * {@link JTypeSymbolKind}.
   *
   * @author Robert Heim
   */
  public static class CommonJTypeReferenceFactory implements JTypeReferenceFactory {
    @Override
    public JTypeReference<? extends JTypeSymbol> create(String referencedSymbolName,
        Scope definingScopeOfReference,
        int arrayDimension) {
      JTypeReference<? extends JTypeSymbol> tref = new CommonJTypeReference<>(referencedSymbolName,
          JTypeSymbol.KIND,
          definingScopeOfReference);
      tref.setDimension(arrayDimension);
      return tref;
    }
  }
  
  /**
   * Derives the (potentially nested) {@link ActualTypeArgument}s from the given {@link ASTType} and
   * adds them to the given {@link JTypeReference}. Since {@link ActualTypeArgument}s reference
   * types themselves, the given factory is used to create such references.
   * 
   * @param typeReference the type reference to add the arguments to.
   * @param astType the parsed type name/reference being investigated. Note that ReturnType is a
   * super type of ASTType. are structured. Based on these the {@link ActualTypeArgument}s are
   * created.
   * @param typeRefFactory the factory to create type reference. This for example can be used to
   * ensure a more specific type of the created reference if {@link CommonJTypeReference} does not
   * fit.
   */
  public static void addTypeArgumentsToTypeSymbol(
      JTypeReference<? extends JTypeSymbol> typeReference,
      ASTReturnType astType, Scope definingScopeOfReference, JTypeReferenceFactory typeRefFactory) {
    if (astType instanceof ASTSimpleReferenceType) {
      ASTSimpleReferenceType astSimpleReferenceType = (ASTSimpleReferenceType) astType;
      if (!astSimpleReferenceType.getTypeArguments().isPresent()) {
        return;
      }
      List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();
      for (ASTTypeArgument astTypeArgument : astSimpleReferenceType.getTypeArguments().get()
          .getTypeArguments()) {
        if (astTypeArgument instanceof ASTWildcardType) {
          ASTWildcardType astWildcardType = (ASTWildcardType) astTypeArgument;
          
          // Three cases can occur here: lower bound, upper bound, no bound
          if (astWildcardType.lowerBoundIsPresent() || astWildcardType.upperBoundIsPresent()) {
            // We have a bound.
            // Examples: Set<? extends Number>, Set<? super Integer>
            
            // new bound
            boolean lowerBound = astWildcardType.lowerBoundIsPresent();
            ASTType typeBound = lowerBound
                ? astWildcardType.getLowerBound().get()
                : astWildcardType
                    .getUpperBound().get();
            int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(typeBound);
            
            JTypeReference<? extends JTypeSymbol> typeBoundSymbolReference = typeRefFactory.create(
                TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(typeBound),
                definingScopeOfReference, dimension);
            ActualTypeArgument actualTypeArgument = new ActualTypeArgument(lowerBound, !lowerBound,
                typeBoundSymbolReference);
            
            // init bound
            addTypeArgumentsToTypeSymbol(typeBoundSymbolReference, typeBound,
                definingScopeOfReference,
                typeRefFactory);
            actualTypeArguments.add(actualTypeArgument);
          }
          else {
            // No bound. Example: Set<?>
            actualTypeArguments.add(
                new ActualTypeArgument(false, false,
                    typeRefFactory.create("?", definingScopeOfReference, 0)));
          }
        }
        else if (astTypeArgument instanceof ASTType) {
          // type argument is a type:
          // Examples: Set<Integer>, Set<Set<?>>, Set<java.lang.String>
          ASTType astTypeNoBound = (ASTType) astTypeArgument;
          JTypeReference<? extends JTypeSymbol> typeArgumentSymbolReference = typeRefFactory.create(
              TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astTypeNoBound),
              definingScopeOfReference,
              TypesHelper.getArrayDimensionIfArrayOrZero(astTypeNoBound));
          
          addTypeArgumentsToTypeSymbol(typeArgumentSymbolReference, astTypeNoBound,
              definingScopeOfReference,
              typeRefFactory);
          
          actualTypeArguments.add(new ActualTypeArgument(typeArgumentSymbolReference));
        }
        else {
          Log.error("0xE0401 Unknown type argument " + astTypeArgument + " of type "
              + typeReference);
        }
        typeReference.setActualTypeArguments(actualTypeArguments);
      }
    }
    else if (astType instanceof ASTComplexReferenceType) {
      ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astType;
      for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
          .getSimpleReferenceTypes()) {
        // TODO
        /* ASTComplexReferenceType represents types like class or interface types which always have
         * ASTSimpleReferenceType as qualification. For example: a.b.c<Arg>.d.e<Arg> */
      }
    }
    else if (astType instanceof ASTComplexArrayType) {
      ASTComplexArrayType astComplexArrayType = (ASTComplexArrayType) astType;
      // references to types with dimension>0, e.g., String[]
      addTypeArgumentsToTypeSymbol(typeReference, astComplexArrayType.getComponentType(),
          definingScopeOfReference, typeRefFactory);
      int dimension = astComplexArrayType.getDimensions();
      typeReference.setDimension(dimension);
    }
  }
}
