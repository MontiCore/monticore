/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import com.google.common.collect.Lists;

import java.util.List;

public class ASTMCOptionalType extends ASTMCOptionalTypeTOP {

    public ASTMCOptionalType() {
        setNameList(Lists.newArrayList("Optional"));
    }

    public ASTMCOptionalType( List<ASTMCTypeArgument> typeArgument) {
        super( typeArgument, Lists.newArrayList("Optional"));
    }
    public ASTMCOptionalType( List<ASTMCTypeArgument> typeArgument, List<String> name) {
        super(typeArgument, Lists.newArrayList("Optional") );

    }
    public void setName(String name) {
        // Name is fixed to "Optional"
        //if(super.getNameList().size()
        super.setName(0,"Optional");
    }
    @Override
    public String getBaseName() {
        return getName();
    }

    public ASTMCTypeArgument getMCTypeArgument() {
        return this.getMCTypeArgument(0);
    }

}
