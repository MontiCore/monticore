// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypeOfRegEx;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types.check.SymTypeVoid;
import de.monticore.types3.ISymTypeVisitor;
import de.monticore.types3.SymTypeRelations;

import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;

public class SymTypeExpression2JavaClassVisitor implements ISymTypeVisitor {

  protected Optional<Class<?>> clazz;

  public Optional<Class<?>> calculate(SymTypeExpression symType) {
    Preconditions.checkNotNull(symType);
    clazz = Optional.empty();
    symType.accept(this);
    Optional<Class<?>> res = clazz;
    clazz = Optional.empty();
    return res;
  }

  public Optional<Class<?>> calculate(TypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    return loadClass(typeSymbol.getFullName());
  }

  public MethodType calculate(
      SymTypeExpression returnType,
      List<SymTypeExpression> argumentTypes
  ) {
    Preconditions.checkNotNull(returnType);
    Preconditions.checkNotNull(argumentTypes);

    Class<?> returnClass = calculate(returnType)
        .orElseThrow(() -> unsupportedType(returnType, "return type"));

    Class<?>[] argumentClasses = new Class<?>[argumentTypes.size()];
    for (int i = 0; i < argumentTypes.size(); i++) {
      SymTypeExpression argumentType =
          Preconditions.checkNotNull(argumentTypes.get(i));
      final int idx = i;
      argumentClasses[i] = calculate(argumentType)
          .orElseThrow(() ->
              unsupportedType(argumentType, "argument at index " + idx)
          );
    }
    return MethodType.methodType(returnClass, argumentClasses);
  }

  @Override
  public void visit(SymTypePrimitive primitive) {
    clazz = Optional.ofNullable(switch (primitive.getPrimitiveName()) {
      case BasicSymbolsMill.BOOLEAN -> boolean.class;
      case BasicSymbolsMill.BYTE -> byte.class;
      case BasicSymbolsMill.CHAR -> char.class;
      case BasicSymbolsMill.SHORT -> short.class;
      case BasicSymbolsMill.INT -> int.class;
      case BasicSymbolsMill.LONG -> long.class;
      case BasicSymbolsMill.FLOAT -> float.class;
      case BasicSymbolsMill.DOUBLE -> double.class;
      default -> null;
    });
  }

  @Override
  public void visit(SymTypeOfObject object) {
    clazz = calculate(object.getTypeInfo());
  }

  @Override
  public void visit(SymTypeOfGenerics generic) {
    clazz = calculate(generic.getTypeInfo());
  }

  @Override
  public void visit(SymTypeArray array) {
    clazz = calculate(array.getArgument())
        .map(componentClass -> Array.newInstance(
            componentClass, new int[array.getDim()]
        ).getClass());
  }

  @Override
  public void visit(SymTypeOfSIUnit siUnit) {
    clazz = Optional.of(double.class);
  }

  @Override
  public void visit(SymTypeOfNumericWithSIUnit numericWithSIUnit) {
    clazz = calculate(numericWithSIUnit.getNumericType());
  }

  @Override
  public void visit(SymTypeOfRegEx regex) {
    clazz = Optional.of(String.class);
  }

  @Override
  public void visit(SymTypeVoid voidSymType) {
    clazz = Optional.of(void.class);
  }

  @Override
  public void visit(SymTypeOfIntersection intersection) {
    // this may require a CoCo that stops usage of intersection types
    // to call native functions or assignments to native variables, etc.
    throw new UnsupportedOperationException(
        "Intersections are not supported for native Java"
    );
  }

  @Override
  public void visit(SymTypeOfUnion union) {
    Optional<SymTypeExpression> lubOpt = SymTypeRelations.leastUpperBound(union);
    clazz = lubOpt.flatMap(this::calculate);
  }

  // e.g., java.util.List.of.T
  @Override
  public void visit(SymTypeVariable typeVariable) {
    // could make it more precise by checking supertypes
    clazz = Optional.of(Object.class);
  }

  protected Optional<Class<?>> loadClass(String className) {
    try {
      return Optional.of(Class.forName(className));
    }
    catch (ClassNotFoundException e) {
      return Optional.empty();
    }
  }

  protected IllegalArgumentException unsupportedType(
      SymTypeExpression type,
      String location
  ) {
    return new IllegalArgumentException(
        "Could not map " + location
            + " to Java class: " + type.printFullName()
    );
  }

}
