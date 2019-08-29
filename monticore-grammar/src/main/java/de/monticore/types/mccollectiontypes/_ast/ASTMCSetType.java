/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

import com.google.common.collect.Lists;

import java.util.List;
public class ASTMCSetType extends ASTMCSetTypeTOP {

    public ASTMCSetType() {
        setNameList(Lists.newArrayList("Set"));
    }

    public ASTMCSetType(List<ASTMCTypeArgument> typeArgument, List<String> name ) {
        super(typeArgument ,Lists.newArrayList("Set"));
    }

    public ASTMCSetType(List<ASTMCTypeArgument> typeArgument) {
        super(typeArgument,Lists.newArrayList("Set"));
    }

    // TODO BR/RE: Methoden überarbeiten, sobald geklärt wie
    // selbiges bei List, Map, Optional
    // TODO BR: die damaligen astrules konnten noch nicht so viel wie heute
    // durch geschicktes hinzufügen von attributen/methoden per astrule sind
    // viele methoden der Topklassen überflüssig geworden

    public ASTMCTypeArgument getMCTypeArgument() {
        return this.getMCTypeArgument(0);
    }

    @Override
    public String getBaseName() {
        return getName();
    }

    public void setName(String name) {
        // Name is fixed to "Set"
    }

}
