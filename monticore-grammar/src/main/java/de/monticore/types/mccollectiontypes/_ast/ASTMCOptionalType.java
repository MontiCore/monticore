/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import com.google.common.collect.Lists;

import java.util.List;

public class ASTMCOptionalType extends ASTMCOptionalTypeTOP {

    public ASTMCOptionalType() {
    }

    public ASTMCOptionalType( List<ASTMCTypeArgument> typeArgument) {
        super( typeArgument, Lists.newArrayList("Optional"));
    }
    public ASTMCOptionalType( List<ASTMCTypeArgument> typeArgument, List<String> name) {
        super(typeArgument, Lists.newArrayList("Optional") );

    }

    @Override
    public String getBaseName() {
        return getName();
    }

    public ASTMCTypeArgument getMCTypeArgument() {
        return this.getMCTypeArgument(0);
    }

}
