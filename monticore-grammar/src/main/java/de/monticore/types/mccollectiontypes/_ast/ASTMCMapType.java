/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;



import com.google.common.collect.Lists;

import java.util.List;

public class ASTMCMapType extends ASTMCMapTypeTOP {

    public ASTMCMapType() {
        setNameList(Lists.newArrayList("Map"));
    }

    public ASTMCMapType(ASTMCTypeArgument key, ASTMCTypeArgument value, List<ASTMCTypeArgument> astmcTypeArguments, List<String> name) {
        super(key, value,Lists.newArrayList(),Lists.newArrayList("Map"));
    }
    public ASTMCMapType( ASTMCTypeArgument key, ASTMCTypeArgument value) {
        super(key, value,Lists.newArrayList(),Lists.newArrayList("Map"));
    }
    public void setName(String name) {
        // Name is fixed to "Map"
    }

    @Override
    public String getBaseName() {
        return getName();
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
