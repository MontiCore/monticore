/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;

/**
 * NodeIdentHelper for MCSimpleGenericTypes, mainly used for Reporting
 */
public class MCSimpleGenericTypesNodeIdentHelper extends MCCollectionTypesNodeIdentHelper {
  public String getIdent(ASTMCBasicGenericType type){
    return format(type.printWithoutTypeArguments(), Layouter.nodeName(type));
  }

  @Override
  public String getIdent(ASTMCGenericType a){
    return format(a.printWithoutTypeArguments(), Layouter.nodeName(a));
  }

  public String getIdent(ASTMCCustomTypeArgument a){
    return format(a.getMCType().printType(MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter()),Layouter.nodeName(a));
  }
}
