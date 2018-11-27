package de.monticore.types.mcbasicgenericstypes._ast;

import com.google.common.collect.Lists;

import java.util.List;

public class ASTMapType extends ASTMapTypeTOP {

    public ASTMapType() {
    }

    public ASTMapType(ASTTypeArgument key, ASTTypeArgument value, String name) {
        super(key, value, name);
    }

//    @Override
//    public List<String> getNameList() {
//        return Lists.newArrayList(getName());
//    }
//
//    @Override
//    public void setNameList(List<String> names) {
//        setName(names.get(0));
//    }
//
//    @Override
//    public List<ASTTypeArgument> getArgumentList() {
//        return Lists.newArrayList(getKey(),getValue());
//    }
//
//    @Override
//    public void setArgumentList(List<ASTTypeArgument> arguments) {
//        setKey(arguments.get(0));
//        setValue(arguments.get(1));
//    }
}
