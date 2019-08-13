/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;



import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.List;
import java.util.Optional;

public class ASTMCMapType extends ASTMCMapTypeTOP {

    public ASTMCMapType() {
    }

    public ASTMCMapType(String name, ASTMCTypeArgument key, ASTMCTypeArgument value) {
        super("Map", key, value);
    }
    public ASTMCMapType( ASTMCTypeArgument key, ASTMCTypeArgument value) {
        super("Map", key, value);
    }


    @Override
    public List<String> getNameList() {
        return Lists.newArrayList(getName());
    }
    @Override
    public String getBaseName() {
        return getName();
    }

    public void setNameList(List<String> names) {
        setName(names.get(0));
    }

    @Override
    public List<ASTMCTypeArgument> getMCTypeArgumentList() {
        return Lists.newArrayList(getKey(),getValue());
    }


    public void setMCTypeArgumentList(List<ASTMCTypeArgument> arguments) {
        setKey(arguments.get(0));
        setValue(arguments.get(1));
    }

}
