package de.monticore.types.mcbasicgenericstypes._ast;



import com.google.common.collect.Lists;

import java.util.List;

public class ASTMapType extends ASTMapTypeTOP {

    public ASTMapType() {
    }

    public ASTMapType(String name, ASTTypeArgument key, ASTTypeArgument value) {
        super(name, key, value);
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
        return Lists.newArrayList(getKey(),getValue());
    }


    @Override
    public void setTypeArgumentList(List<ASTTypeArgument> arguments) {
        setKey(arguments.get(0));
        setValue(arguments.get(1));
    }
}
