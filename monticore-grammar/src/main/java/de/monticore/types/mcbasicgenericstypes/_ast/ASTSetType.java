package de.monticore.types.mcbasicgenericstypes._ast;

import com.google.common.collect.Lists;

import java.util.List;

public class ASTSetType extends ASTSetTypeTOP {

    public ASTSetType() {
    }

    public ASTSetType(ASTTypeArgument TypeArgument, String name) {
        super(TypeArgument, name);
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
