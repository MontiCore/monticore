/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.*;

/**
 * NodeIdentHelper for MCBasicTypes, mainly used for Reporting
 */
public class MCBasicTypesNodeIdentHelper extends ASTNodeIdentHelper {

  public String getIdent(ASTMCPrimitiveType a) {
    return format(a.printType(), Layouter.nodeName(a));
  }

  public String getIdent(ASTMCType a) {
    return format(a.printType(), Layouter.nodeName(a));
  }

  public String getIdent(ASTMCQualifiedName a) {
    return format(a.getQName(), Layouter.nodeName(a));
  }

  public String getIdent(ASTMCQualifiedType a) {
    return format(a.getMCQualifiedName().getQName(), Layouter.nodeName(a));
  }

  public String getIdent(ASTMCReturnType a) {
    if (a.isPresentMCType()) {
      return getIdent(a.getMCType());
    }
    else if (a.isPresentMCVoidType()) {
      return getIdent(a.getMCVoidType());
    }
    return "";
  }

  public String getIdent(ASTMCVoidType a) {
    return format("void", Layouter.nodeName(a));
  }
}
