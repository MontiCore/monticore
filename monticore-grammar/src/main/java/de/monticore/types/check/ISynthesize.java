/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

/**
 * A common interface that can be used to synthesize SymTypeExpressions from MCTypes
 * @deprecated use {@link de.monticore.types3.TypeCheck3}
 */
@Deprecated
public interface ISynthesize {

  TypeCheckResult synthesizeType(ASTMCType type);

  TypeCheckResult synthesizeType(ASTMCReturnType type);

  TypeCheckResult synthesizeType(ASTMCQualifiedName qName);

}
