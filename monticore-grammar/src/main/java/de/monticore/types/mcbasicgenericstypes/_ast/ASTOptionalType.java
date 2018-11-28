package de.monticore.types.mcbasicgenericstypes._ast;

import com.google.common.collect.Lists;

import java.util.List;

public class ASTOptionalType extends ASTOptionalTypeTOP {

    public ASTOptionalType() {
    }

    public ASTOptionalType(String name, ASTTypeArgument typeArgument) {
        super(name, typeArgument);
    }

    @Override
    public List<String> getNameList() {
        return Lists.newArrayList(getName());
    }

    @Override
    public void setNameList(List<String> names) {
        setName(names.get(0));
    }

    @Override
    public List<ASTTypeArgument> getTypeArgumentList() {
        return Lists.newArrayList(getTypeArgument());
    }

    @Override
    public void setTypeArgumentList(List<ASTTypeArgument> arguments) {
        setTypeArgument(arguments.get(0));
    }

}
