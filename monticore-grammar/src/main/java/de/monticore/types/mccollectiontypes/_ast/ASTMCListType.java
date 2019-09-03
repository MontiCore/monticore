/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import com.google.common.collect.Lists;

import java.util.List;

public class ASTMCListType extends ASTMCListTypeTOP {
    public ASTMCListType() {
        setNameList(Lists.newArrayList("List"));
    }

    public ASTMCListType(List<ASTMCTypeArgument> typeArgument, List<String> name ) {
        super(typeArgument, Lists.newArrayList("List") );
    }

    public ASTMCListType(List<ASTMCTypeArgument> typeArgument) {
        super(typeArgument, Lists.newArrayList("List"));
    }

    public ASTMCTypeArgument getMCTypeArgument() {
      return this.getMCTypeArgument(0);
    }

    public void setName(String name) {
        // Name is fixed to "List"   :  TODO: Internal Error, Error Msg
    }

    @Override
    public String getBaseName() {
        return getName();
    }
}
