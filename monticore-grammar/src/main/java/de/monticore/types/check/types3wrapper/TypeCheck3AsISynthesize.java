// (c) https://github.com/MontiCore/monticore
package de.monticore.types.check.types3wrapper;

import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.Type4Ast;
import de.monticore.visitor.ITraverser;

/**
 * ISynthesize using the TypeCheck3.
 * While the TypeCheck3 should be used directly,
 * this can be used to try the TypeCheck without major rewrites.
 */
@Deprecated(forRemoval = true)
public class TypeCheck3AsISynthesize implements ISynthesize {

  protected Type4Ast type4Ast;

  protected ITraverser typeTraverser;

  /**
   * type4Ast should be filled by the typeTraverser
   */
  public TypeCheck3AsISynthesize(
      ITraverser typeTraverser,
      Type4Ast type4Ast) {
    this.typeTraverser = typeTraverser;
    this.type4Ast = type4Ast;
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCType type) {
    TypeCheckResult result = new TypeCheckResult();
    if (!type4Ast.hasTypeOfTypeIdentifier(type)) {
      type.accept(typeTraverser);
    }
    result.setResult(type4Ast.getTypeOfTypeIdentifier(type));
    result.setType();
    return result;
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCReturnType type) {
    TypeCheckResult result = new TypeCheckResult();
    if (!type4Ast.hasTypeOfTypeIdentifier(type)) {
      type.accept(typeTraverser);
    }
    result.setResult(type4Ast.getTypeOfTypeIdentifier(type));
    result.setType();
    return result;
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCQualifiedName qName) {
    TypeCheckResult result = new TypeCheckResult();
    if (!type4Ast.hasTypeOfTypeIdentifier(qName)) {
      qName.accept(typeTraverser);
    }
    result.setResult(type4Ast.getTypeOfTypeIdentifier(qName));
    result.setType();
    return result;
  }
}
