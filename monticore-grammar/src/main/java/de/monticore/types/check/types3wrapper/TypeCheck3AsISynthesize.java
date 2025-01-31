// (c) https://github.com/MontiCore/monticore
package de.monticore.types.check.types3wrapper;

import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.visitor.ITraverser;

/**
 * ISynthesize using the TypeCheck3.
 * While the TypeCheck3 should be used directly,
 * this can be used to try the TypeCheck without major rewrites.
 */
public class TypeCheck3AsISynthesize implements ISynthesize {

  @Deprecated
  protected Type4Ast type4Ast;

  @Deprecated
  protected ITraverser typeTraverser;

  /**
   * type4Ast should be filled by the typeTraverser
   */
  @Deprecated
  public TypeCheck3AsISynthesize(
      ITraverser typeTraverser,
      Type4Ast type4Ast) {
    this.typeTraverser = typeTraverser;
    this.type4Ast = type4Ast;
  }

  public TypeCheck3AsISynthesize() {
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCType type) {
    TypeCheckResult result = new TypeCheckResult();
    if (type4Ast != null) {
      if (!type4Ast.hasTypeOfTypeIdentifier(type)) {
        type.accept(typeTraverser);
      }
      result.setResult(type4Ast.getTypeOfTypeIdentifier(type));
    }
    else {
      result.setResult(TypeCheck3.symTypeFromAST(type));
    }
    result.setType();
    return result;
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCReturnType type) {
    TypeCheckResult result = new TypeCheckResult();
    if (type4Ast != null) {
      if (!type4Ast.hasTypeOfTypeIdentifier(type)) {
        type.accept(typeTraverser);
      }
      result.setResult(type4Ast.getTypeOfTypeIdentifier(type));
    }
    else {
      result.setResult(TypeCheck3.symTypeFromAST(type));
    }
    result.setType();
    return result;
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCQualifiedName qName) {
    TypeCheckResult result = new TypeCheckResult();
    if (type4Ast != null) {
      if (!type4Ast.hasTypeOfTypeIdentifier(qName)) {
        qName.accept(typeTraverser);
      }
      result.setResult(type4Ast.getTypeOfTypeIdentifier(qName));
    }
    else {
      result.setResult(TypeCheck3.symTypeFromAST(qName));
    }
    result.setType();
    return result;
  }
}
