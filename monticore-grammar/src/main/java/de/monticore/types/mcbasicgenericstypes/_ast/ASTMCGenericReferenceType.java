package de.monticore.types.mcbasicgenericstypes._ast;

import java.util.List;

public interface ASTMCGenericReferenceType extends ASTMCGenericReferenceTypeTOP {

    List<String> getNameList() ;

    void setNameList(List<String> names) ;

    List<ASTMCTypeArgument> getMCTypeArgumentList() ;

    void setMCTypeArgumentList(List<ASTMCTypeArgument> arguments) ;

}
