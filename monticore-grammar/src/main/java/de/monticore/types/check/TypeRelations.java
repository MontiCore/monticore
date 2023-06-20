/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypePrimitive.unbox;

/**
 * This Class provides the default implementation of {@link ITypeRelations}
 */
public class TypeRelations implements ITypeRelations {

  @Override
  public boolean compatible(SymTypeExpression left, SymTypeExpression right) {
    if (left.isObscureType() || right.isObscureType()) {
      return true;
    }
    if (left.isPrimitive() && right.isPrimitive()) {
      SymTypePrimitive leftType = (SymTypePrimitive) left;
      SymTypePrimitive rightType = (SymTypePrimitive) right;
      if (isBoolean(leftType) && isBoolean(rightType)) {
        return true;
      }
      if (isDouble(leftType) && rightType.isNumericType()) {
        return true;
      }
      if (isFloat(leftType) && ((rightType.isIntegralType()) || isFloat(right))) {
        return true;
      }
      if (isLong(leftType) && rightType.isIntegralType()) {
        return true;
      }
      if (isInt(leftType) && rightType.isIntegralType() && !isLong(right)) {
        return true;
      }
      if (isChar(leftType) && isChar(right)) {
        return true;
      }
      if (isShort(leftType) && (isByte(right) || isShort(right))) {
        return true;
      }
      if (isByte(leftType) && isByte(right)) {
        return true;
      }
      return false;
    }
    else if (!left.isPrimitive() && right.isNullType()) {
      return true;
    }
    else if (unbox(left.printFullName()).equals(unbox(right.printFullName()))) {
      return true;
    }
    else if (isSubtypeOf(right, left)) {
      return true;
    }
    else if (right.printFullName().equals(left.printFullName())) {
      return true;
    }
    else if (left.deepEquals(right) || right.deepEquals(left)) {
      return true;
    }
    return false;
  }

  @Override
  public boolean isSubtypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    if (subType.isObscureType() || superType.isObscureType()) {
      return true;
    }
    if (unbox(subType.printFullName()).equals(unbox(superType.printFullName()))) {
      return true;
    }
    if (subType.isPrimitive() && superType.isPrimitive()) {
      SymTypePrimitive sub = (SymTypePrimitive) subType;
      SymTypePrimitive supert = (SymTypePrimitive) superType;
      if (isDouble(supert) && sub.isNumericType() && !isDouble(sub)) {
        return true;
      }
      if (isFloat(supert) && sub.isIntegralType()) {
        return true;
      }
      if (isLong(supert) && sub.isIntegralType() && !isLong(subType)) {
        return true;
      }
      if (isInt(supert) && sub.isIntegralType() && !isLong(subType) && !isInt(subType)) {
        return true;
      }
      return false;
    }
    else if ((subType.isPrimitive() && !superType.isPrimitive()) ||
        (superType.isPrimitive() && !subType.isPrimitive())) {
      return false;
    }
    return isSubtypeOfRec(subType, superType);
  }

  protected boolean isSubtypeOfRec(SymTypeExpression subType, SymTypeExpression superType) {
    if (!subType.getTypeInfo().getSuperTypesList().isEmpty()) {
      for (SymTypeExpression type : subType.getTypeInfo().getSuperTypesList()) {
        if (type.print().equals(superType.print())) {
          return true;
        }
        if (type.isGenericType() && superType.isGenericType()) {
          //TODO check recursively, this is only a hotfix, see #2977
          SymTypeOfGenerics typeGen = (SymTypeOfGenerics) type;
          SymTypeOfGenerics supTypeGen = (SymTypeOfGenerics) superType;
          if (typeGen.printTypeWithoutTypeArgument()
              .equals(supTypeGen.printTypeWithoutTypeArgument())
              && typeGen.sizeArguments() == supTypeGen.sizeArguments()) {
            boolean success = true;
            for (int i = 0; i < typeGen.sizeArguments(); i++) {
              if (!typeGen.getArgument(i).isTypeVariable()) {
                if (!typeGen.getArgument(i).print().equals(supTypeGen.getArgument(i).print())) {
                  success = false;
                }
              }
            }
            if (success) {
              return true;
            }
          }
        }
      }
    }
    boolean subtype = false;
    for (int i = 0; i < subType.getTypeInfo().getSuperTypesList().size(); i++) {
      if (isSubtypeOf(subType.getTypeInfo().getSuperTypesList().get(i), superType)) {
        subtype = true;
        break;
      }
    }
    return subtype;
  }

  @Override
  public int calculateInheritanceDistance(SymTypeExpression specific,
      SymTypeExpression general) {
    if (specific.isPrimitive() && general.isPrimitive()) {
      return calculateInheritanceDistance((SymTypePrimitive) specific, (SymTypePrimitive) general);
    }
    else if (specific.deepEquals(general)) {
      return 0;
    }
    else if (!isSubtypeOf(specific, general)) {
      return -1;
    }
    else {
      List<SymTypeExpression> superTypes = specific.getTypeInfo().getSuperTypesList();
      List<Integer> superTypesSpecificity = superTypes.stream()
          .map(s -> calculateInheritanceDistance(s, general)).collect(Collectors.toList());
      int min = -1;
      for (int specificity : superTypesSpecificity) {
        if (min != -1 && specificity != -1 && specificity < min) {
          min = specificity;
        }
        else if (min == -1 && specificity != -1) {
          min = specificity;
        }
      }
      if (min == -1) {
        return -1;
      }
      else {
        return min + 1;
      }
    }
  }

  @Override
  public int calculateInheritanceDistance(SymTypePrimitive specific, SymTypePrimitive general) {
    if (unbox(specific.print()).equals(unbox(general.print()))) {
      return 0;
    }
    if (specific.isNumericType() && general.isNumericType()) {
      if (isByte(specific)) {
        if (isShort(general)) {
          return 1;
        }
        else if (isInt(general)) {
          return 2;
        }
        else if (isLong(general)) {
          return 3;
        }
        else if (isFloat(general)) {
          return 4;
        }
        else if (isDouble(general)) {
          return 5;
        }
      }
      else if (isChar(specific) || isShort(specific)) {
        if (isInt(general)) {
          return 1;
        }
        else if (isLong(general)) {
          return 2;
        }
        else if (isFloat(general)) {
          return 3;
        }
        else if (isDouble(general)) {
          return 4;
        }
      }
      else if (isInt(specific)) {
        if (isLong(general)) {
          return 1;
        }
        else if (isFloat(general)) {
          return 2;
        }
        else if (isDouble(general)) {
          return 3;
        }
      }
      else if (isLong(specific)) {
        if (isFloat(general)) {
          return 1;
        }
        else if (isDouble(general)) {
          return 2;
        }
      }
      else if (isFloat(specific)) {
        if (isDouble(general)) {
          return 1;
        }
      }
      return -1;
    }
    else {
      return -1;
    }
  }

  @Override
  public boolean isBoolean(SymTypeExpression type) {
    return BasicSymbolsMill.BOOLEAN.equals(unbox(type.print()));
  }

  @Override
  public boolean isInt(SymTypeExpression type) {
    return BasicSymbolsMill.INT.equals(unbox(type.print()));
  }

  @Override
  public boolean isDouble(SymTypeExpression type) {
    return BasicSymbolsMill.DOUBLE.equals(unbox(type.print()));
  }

  @Override
  public boolean isFloat(SymTypeExpression type) {
    return BasicSymbolsMill.FLOAT.equals(unbox(type.print()));
  }

  @Override
  public boolean isLong(SymTypeExpression type) {
    return BasicSymbolsMill.LONG.equals(unbox(type.print()));
  }

  @Override
  public boolean isChar(SymTypeExpression type) {
    return BasicSymbolsMill.CHAR.equals(unbox(type.print()));
  }

  @Override
  public boolean isShort(SymTypeExpression type) {
    return BasicSymbolsMill.SHORT.equals(unbox(type.print()));
  }

  @Override
  public boolean isByte(SymTypeExpression type) {
    return BasicSymbolsMill.BYTE.equals(unbox(type.print()));
  }

  @Override
  public boolean isVoid(SymTypeExpression type) {
    return BasicSymbolsMill.VOID.equals(unbox(type.print()));
  }

  @Override
  public boolean isString(SymTypeExpression type) {
    return "String".equals(type.print());
  }

  @Override
  public boolean isNumericType(SymTypeExpression type) {
    return isIntegralType(type) || isFloat(type) || isDouble(type);
  }

  @Override
  public boolean isIntegralType(SymTypeExpression type) {
    return isLong(type) || isInt(type) || isChar(type) || isByte(type) || isShort(type);
  }
}

