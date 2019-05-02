package de.monticore.types.mccollectiontypes._ast;

import java.util.List;

public interface ASTMCGenericType extends ASTMCGenericTypeTOP {

    List<String> getNameList() ;

//    void setNameList(List<String> names) ;

    List<ASTMCTypeArgument> getMCTypeArgumentList() ;

//    void setMCTypeArgumentList(List<ASTMCTypeArgument> arguments) ;

}
