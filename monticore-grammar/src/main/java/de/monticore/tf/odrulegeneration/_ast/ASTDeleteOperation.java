/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrulegeneration._ast;

import java.util.HashMap;
import java.util.List;

public  class ASTDeleteOperation extends ASTDeleteOperationTOP {

    protected HashMap<String, List<String>> possibleParents;

    protected ASTDeleteOperation() {
        super();
    }

    protected  ASTDeleteOperation(
                                  String name,
                                  String type,
                                  String simpleType,
                                  String grammarType,
                                  String typepackage,
                                  List<String> parentsSets,
                                  boolean list)  {
        super();
        setName(name);
        setType(type);
        setSimpleType(simpleType);
        setGrammarType(grammarType);
        setTypepackage(typepackage);
        setParentsSetList(parentsSets);
        setList(list);
        //super(name, type, simpleType, grammarType, typepackage, parentsSets, list);
    }

    protected  ASTDeleteOperation(HashMap<String, List<String>> possibleParents,
                                  String name,
                                  String type,
                                  String simpleType,
                                  String grammarType,
                                  String typepackage,
                                  List<String> parentsSets,
                                  boolean list)  {
        super();
        setName(name);
        setType(type);
        setSimpleType(simpleType);
        setGrammarType(grammarType);
        setTypepackage(typepackage);
        setParentsSetList(parentsSets);
        setList(list);
        //super(name, type, simpleType, grammarType, typepackage, parentsSets, list);
        setPossibleParents(possibleParents);
    }


    public  HashMap<String, List<String>> getPossibleParents ()  {
       return this.possibleParents;
    }

    public  void setPossibleParents (HashMap<String, List<String>> possibleParents)  {
       this.possibleParents = possibleParents;
    }

}
