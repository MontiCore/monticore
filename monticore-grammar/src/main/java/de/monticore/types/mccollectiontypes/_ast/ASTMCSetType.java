package de.monticore.types.mccollectiontypes._ast;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.List;
import java.util.Optional;

public class ASTMCSetType extends ASTMCSetTypeTOP {

    public ASTMCSetType() {
    }

    public ASTMCSetType(String name, ASTMCTypeArgument typeArgument) {
        super("Set", typeArgument);
    }

    public ASTMCSetType(ASTMCTypeArgument typeArgument) {
        super("Set", typeArgument);
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
