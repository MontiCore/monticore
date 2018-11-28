package de.monticore.types.mcbasicgenericstypes._ast;

import java.util.List;

public interface ASTGenericReferenceType extends ASTGenericReferenceTypeTOP {

    List<String> getNameList() ;

    void setNameList(List<String> names) ;

    List<ASTTypeArgument> getTypeArgumentList() ;

    void setTypeArgumentList(List<ASTTypeArgument> arguments) ;

}
