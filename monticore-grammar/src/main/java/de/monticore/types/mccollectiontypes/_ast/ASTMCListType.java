/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.List;
import java.util.Optional;

public class ASTMCListType extends ASTMCListTypeTOP {
    public ASTMCListType() {
    }

    public ASTMCListType(String name, ASTMCTypeArgument typeArgument) {
        super("List", typeArgument);
    }

    public ASTMCListType(ASTMCTypeArgument typeArgument) {
        super("List", typeArgument);
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
        return Lists.newArrayList(getMCTypeArgument());
    }


    public void setMCTypeArgumentList(List<ASTMCTypeArgument> arguments) {
        setMCTypeArgument(arguments.get(0));
    }

}
