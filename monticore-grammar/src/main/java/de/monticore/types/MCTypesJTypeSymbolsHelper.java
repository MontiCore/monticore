/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.*;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedReferenceType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccustomgenericstypes._ast.ASTMCCollectionTypesReferenceType;
import de.monticore.types.mcgenerictypes._ast.ASTMCComplexReferenceType;
import de.monticore.types.mcgenerictypes._ast.ASTMCTypeParameters;
import de.monticore.types.mcgenerictypes._ast.ASTMCTypeVariableDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Common methods for working with the {@link JTypeSymbol}s symbol table framework. E.g., they are
 * handy for creating symbol tables based on {@link JTypeSymbol}s.
 *
 */
public class MCTypesJTypeSymbolsHelper {
  
  private MCTypesJTypeSymbolsHelper() {
    // static class
  }
  
  /**
   * A factory to create type references. Implementations ensure the specific kind/type of the
   * created reference. If sufficient consider using the {@link CommonJTypeReferenceFactory}.
   *
   */
  public interface JTypeReferenceFactory<Y extends JTypeReference<?>> {
    Y create(String name, Scope definingScopeOfReference,
        int arrayDimension);
  }
  
  /**
   * Default implementation to create {@link CommonJTypeReference}s using the
   * {@link JTypeSymbolKind}.
   *
   */
  public static class CommonJTypeReferenceFactory
      implements JTypeReferenceFactory<CommonJTypeReference<JTypeSymbol>> {
    @Override
    public CommonJTypeReference<JTypeSymbol> create(String referencedSymbolName,
        Scope definingScopeOfReference,
        int arrayDimension) {
      CommonJTypeReference<JTypeSymbol> tref = new CommonJTypeReference<>(referencedSymbolName,
          JTypeSymbol.KIND,
          definingScopeOfReference);
      tref.setDimension(arrayDimension);
      return tref;
    }
  }
  
  /**
   * A factory to create {@link JTypeSymbol}s. Implementations ensure the specific kinds of the
   * created types.
   *
   */
  public interface JTypeFactory<U extends CommonJTypeSymbol<?, ?, ?, ?>> {
    // TODO introduce MutableJTypeSymbol to prevent forcing engineers to always subtype
    // CommonJTypeSymbol?
    U createTypeVariable(String name);
  }
  
  /**
   * Derives the (potentially nested) {@link ActualTypeArgument}s from the given {@link ASTMCType} and
   * adds them to the given {@link JTypeReference}. Since {@link ActualTypeArgument}s reference
   * types themselves, the given factory is used to create such references.
   *
   * @param typeReference the type reference to add the arguments to.
   * @param astType the parsed type name/reference being investigated. Note that ReturnType is a
   * super type of ASTType. are structured. Based on these the {@link ActualTypeArgument}s are
   * created.
   * @param definingScopeOfReference current {@link Scope} that the typeReference lives in.
   * @param typeRefFactory the factory to create type reference. This for example can be used to
   * ensure a more specific type of the created reference if {@link CommonJTypeReference} does not
   * fit.
   */
  public static void addTypeArgumentsToTypeSymbol(
          JTypeReference<?> typeReference,
          ASTMCReturnType astType, Scope definingScopeOfReference,
          JTypeReferenceFactory<?> typeRefFactory) {
    if (astType.getMCTypeOpt().isPresent() && astType.getMCType() instanceof ASTMCQualifiedReferenceType) {
      ASTMCQualifiedReferenceType astSimpleReferenceType = (ASTMCQualifiedReferenceType) astType.getMCType();
//
//      List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();
//      for (ASTTypeArgument astTypeArgument : astSimpleReferenceType.getTypeArguments()
//          .getTypeArgumentList()) {
//        if (astTypeArgument instanceof ASTWildcardType) {
//          ASTWildcardType astWildcardType = (ASTWildcardType) astTypeArgument;
//
//          // Three cases can occur here: lower bound, upper bound, no bound
//          if (astWildcardType.isPresentLowerBound() || astWildcardType.isPresentUpperBound()) {
//            // We have a bound.
//            // Examples: Set<? extends Number>, Set<? super Integer>
//
//            // new bound
//            boolean lowerBound = astWildcardType.isPresentLowerBound();
//            ASTType typeBound = lowerBound
//                ? astWildcardType.getLowerBound()
//                : astWildcardType
//                    .getUpperBound();
//            int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(typeBound);
//
//            JTypeReference<?> typeBoundSymbolReference = typeRefFactory.create(
//                TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(typeBound),
//                definingScopeOfReference, dimension);
//            ActualTypeArgument actualTypeArgument = new ActualTypeArgument(lowerBound, !lowerBound,
//                typeBoundSymbolReference);
//
//            // init bound
//            addTypeArgumentsToTypeSymbol(typeBoundSymbolReference, typeBound,
//                definingScopeOfReference,
//                typeRefFactory);
//            actualTypeArguments.add(actualTypeArgument);
//          }
//          else {
//            // No bound. Example: Set<?>
//            actualTypeArguments.add(
//                new ActualTypeArgument(false, false,
//                    typeRefFactory.create("?", definingScopeOfReference, 0)));
//          }
//        }
//        else if (astTypeArgument instanceof ASTType) {
//          // type argument is a type:
//          // Examples: Set<Integer>, Set<Set<?>>, Set<java.lang.String>
//          ASTType astTypeNoBound = (ASTType) astTypeArgument;
//          JTypeReference<?> typeArgumentSymbolReference = typeRefFactory.create(
//              TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astTypeNoBound),
//              definingScopeOfReference,
//              TypesHelper.getArrayDimensionIfArrayOrZero(astTypeNoBound));
//
//          addTypeArgumentsToTypeSymbol(typeArgumentSymbolReference, astTypeNoBound,
//              definingScopeOfReference,
//              typeRefFactory);
//
//          actualTypeArguments.add(new ActualTypeArgument(typeArgumentSymbolReference));
//        }
//        else {
//          Log.error("0xE0401 Unknown type argument " + astTypeArgument + " of type "
//              + typeReference);
//        }
//        typeReference.setActualTypeArguments(actualTypeArguments);
//      }
    }
    else if (astType.getMCTypeOpt().isPresent() && astType.getMCType() instanceof ASTMCComplexReferenceType) {
      ASTMCComplexReferenceType astComplexReferenceType = (ASTMCComplexReferenceType) astType.getMCType();
      for (ASTMCCollectionTypesReferenceType astSimpleReferenceType : astComplexReferenceType
          .getMCCollectionTypesReferenceTypeList()) {
        // TODO
        /* ASTComplexReferenceType represents types like class or interface types which always have
         * ASTSimpleReferenceType as qualification. For example: a.b.c<Arg>.d.e<Arg> */
      }
    }
//    else if (astType.getTypeOpt().isPresent() && astType.getType() instanceof ASTComplexArrayType) {
//      ASTComplexArrayType astComplexArrayType = (ASTComplexArrayType) astType;
//      // references to types with dimension>0, e.g., String[]
//      addTypeArgumentsToTypeSymbol(typeReference, astComplexArrayType.getComponentType(),
//          definingScopeOfReference, typeRefFactory);
//      int dimension = astComplexArrayType.getDimensions();
//      typeReference.setDimension(dimension);
//    }
  }
  
  /**
   * Adds the given ASTTypes as interfaces to the CommonJTypeSymbol. The CommonJTypeSymbol can be a
   * type variable. Interfaces may follow after the first extended Type. We treat the first Type
   * also as interface even though it may be a class.
   * <p>
   * class Bla implements SomeInterface, AnotherInterface, ... <br>
   * class Bla&ltT extends SomeClassOrInterface & SomeInterface & ...&gt
   * </p>
   * See also JLS7.
   *
   * @param jTypeSymbol
   * @param astInterfaceTypeList
   * @param definingScopeOfReference
   * @param typeRefFactory
   */
  public static <Y extends JTypeReference<?>, U extends CommonJTypeSymbol<?, ?, ?, Y>> void addInterfacesToType(
      U jTypeSymbol, List<ASTMCType> astInterfaceTypeList, Scope definingScopeOfReference,
      JTypeReferenceFactory<Y> typeRefFactory) {
//    for (ASTType astInterfaceType : astInterfaceTypeList) {
//      Y jInterfaceTypeSymbolReference = typeRefFactory
//          .create(
//              TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astInterfaceType),
//              definingScopeOfReference, 0);
//
//      // Add the ASTTypeArguments to astInterfaceType
//      // Before we can do that we have to cast.
//      if (astInterfaceType instanceof ASTSimpleReferenceType) {
//        addTypeArgumentsToTypeSymbol(jInterfaceTypeSymbolReference, astInterfaceType,
//            definingScopeOfReference, typeRefFactory);
//      }
//      else if (astInterfaceType instanceof ASTComplexReferenceType) {
//        ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astInterfaceType;
//        for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
//            .getSimpleReferenceTypeList()) {
//          // TODO: tested for only one
//          addTypeArgumentsToTypeSymbol(jInterfaceTypeSymbolReference, astSimpleReferenceType,
//                  definingScopeOfReference, typeRefFactory);
//        }
//      }
//
//      jTypeSymbol.addInterface(jInterfaceTypeSymbolReference);
//    }
  }
  
  /**
   * Adds the TypeParameters to the {@link JMethodSymbol} if it declares {@link ASTMCTypeParameters}.
   * Example:
   * <p>
   * public <U extends SomeClass<?>>& SomeInterface> U myMethod()
   * </p>
   * U is added to myMethod.
   *
   * @param jMethodSymbol
   * @param typeParameters
   * @param definingScope
   * @param symbolFactory
   * @param typeRefFactory
   * @return newly created type parameters
   */
  public static <Y extends JTypeReference<?>, U extends CommonJTypeSymbol<?, ?, ?, Y>> List<U> addTypeParametersToMethod(
      CommonJMethodSymbol<U, ?, ?> jMethodSymbol, Optional<ASTMCTypeParameters> typeParameters,
      Scope definingScope, JTypeFactory<U> symbolFactory, JTypeReferenceFactory<Y> typeRefFactory) {
//    List<U> jTypeParameterSymbols = new ArrayList<>();
//    if (typeParameters.isPresent()) {
//      ASTTypeParameters astTypeParameters = typeParameters.get();
//      for (ASTTypeVariableDeclaration astTypeParameter : astTypeParameters
//          .getTypeVariableDeclarationList()) {
//        // new type parameter
//        U typeParameter = symbolFactory
//            .createTypeVariable(astTypeParameter.getName());
//        jTypeParameterSymbols.add(typeParameter);
//
//        // Treat type bounds are implemented interfaces, even though the
//        // first bound might be a class. See also JLS7.
//        List<ASTType> types = new ArrayList<ASTType>(astTypeParameter.getUpperBoundList());
//        addInterfacesToType(typeParameter, types, definingScope, typeRefFactory);
//
//        jMethodSymbol.addFormalTypeParameter(typeParameter);
//      }
//    }
//    return jTypeParameterSymbols;
    return null;
  }
  
  /**
   * Adds the TypeParameters to the JTypeSymbol if the class or interface declares TypeVariables.
   * Example:
   * <p>
   * class Bla<T, S extends SomeClass<T> & SomeInterface>
   * </p>
   * T and S are added to Bla.
   *
   * @param jTypeSymbol
   * @param optionalTypeParameters
   * @param definingScope
   * @param symbolFactory
   * @param typeRefFactory
   * @return list of all type parameters of jTypeSymbol including the new ones.
   */
  public static <Y extends CommonJTypeReference<?>, U extends CommonJTypeSymbol<?, ?, ?, Y>> List<U> addTypeParametersToType(
      CommonJTypeSymbol<U, ?, ?, ?> jTypeSymbol, Optional<ASTMCTypeParameters> optionalTypeParameters,
      Scope definingScope, JTypeFactory<U> symbolFactory, JTypeReferenceFactory<Y> typeRefFactory) {
    if (optionalTypeParameters.isPresent()) {
      ASTMCTypeParameters astTypeParameters = optionalTypeParameters.get();
      for (ASTMCTypeVariableDeclaration astTypeParameter : astTypeParameters
          .getMCTypeVariableDeclarationList()) {
        // new type parameter
        
        // TypeParameters/TypeVariables are seen as type declarations.
        // For each variable instantiate a JTypeSymbol.
        final String typeVariableName = astTypeParameter.getName();
        U jTypeVariableSymbol = symbolFactory.createTypeVariable(typeVariableName);
        
        // Treat type bounds are implemented interfaces, even though the
        // first bound might be a class. See also JLS7.
        List<ASTMCType> types = new ArrayList<ASTMCType>(astTypeParameter.getUpperBoundList());
        addInterfacesToType(jTypeVariableSymbol, types, definingScope, typeRefFactory);
        
        // add type parameter
        jTypeSymbol.addFormalTypeParameter(jTypeVariableSymbol);
      }
    }
    return jTypeSymbol.getFormalTypeParameters();
  }
  
  /**
   * initializes the jAttributeSymbol using information of the given astType and the
   * additionalDimensions. E.g., the typeReference of the symbol is set including its type
   * arguments.
   *
   * @param jAttributeSymbol
   * @param astType
   * @param additionalDimensions
   * @param definingScope
   * @param typeRefFactory
   */
  public static <Y extends JTypeReference<?>> void initializeJAttributeSymbol(
      // TODO introduce MutableJAttributeSymbol to prevent enforcing engineers to subclass
      // CommonJFieldSymbol?
      CommonJFieldSymbol<Y> jAttributeSymbol, ASTMCType astType, int additionalDimensions,
      Scope definingScope, JTypeReferenceFactory<Y> typeRefFactory) {
//    final String fieldTypeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);
//    Y fieldTypeReference = typeRefFactory.create(fieldTypeName, definingScope,
//        TypesHelper.getArrayDimensionIfArrayOrZero(astType) + additionalDimensions);
//
//    if (astType instanceof ASTSimpleReferenceType) {
//      ASTSimpleReferenceType astSimpleReferenceType = (ASTSimpleReferenceType) astType;
//      if (astSimpleReferenceType.isPresentTypeArguments()) {
//        addTypeArgumentsToTypeSymbol(fieldTypeReference, astSimpleReferenceType, definingScope,
//            typeRefFactory);
//      }
//    }
//    else if (astType instanceof ASTComplexReferenceType) {
//      ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astType;
//      for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
//          .getSimpleReferenceTypeList()) {
//        // TODO: tested for only one
//        addTypeArgumentsToTypeSymbol(fieldTypeReference, astSimpleReferenceType,
//                definingScope, typeRefFactory);      }
//    }
//    jAttributeSymbol.setType(fieldTypeReference);
  }
}
