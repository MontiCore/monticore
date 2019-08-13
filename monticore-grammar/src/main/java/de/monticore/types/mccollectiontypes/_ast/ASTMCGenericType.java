/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import java.util.List;

public interface ASTMCGenericType extends ASTMCGenericTypeTOP {

    List<String> getNameList() ;

    List<ASTMCTypeArgument> getMCTypeArgumentList() ;


}
