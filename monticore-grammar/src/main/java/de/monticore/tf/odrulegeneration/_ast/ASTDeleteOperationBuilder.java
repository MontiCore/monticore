/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrulegeneration._ast;

import java.util.HashMap;
import java.util.List;

public class ASTDeleteOperationBuilder extends ASTDeleteOperationBuilderTOP {

    protected HashMap<String, List<String>> possibleParents = new HashMap<>();

    public ASTDeleteOperationBuilder setPossibleParents (HashMap<String, List<String>> foldingHash)  {
        this.possibleParents = foldingHash;
        return this.realBuilder;
    }

    public HashMap<String, List<String>> getPossibleParents() {
        return this.possibleParents;
    }
}
