package de.monticore.types.mcbasicgenericstypes._ast;



import com.google.common.collect.Lists;

import java.util.List;

public class ASTMCMapType extends ASTMCMapTypeTOP {

    public ASTMCMapType() {
    }

    public ASTMCMapType(String name, ASTMCTypeArgument key, ASTMCTypeArgument value) {
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
    public List<ASTMCTypeArgument> getMCTypeArgumentList() {
        return Lists.newArrayList(getKey(),getValue());
    }


    @Override
    public void setMCTypeArgumentList(List<ASTMCTypeArgument> arguments) {
        setKey(arguments.get(0));
        setValue(arguments.get(1));
    }
}
