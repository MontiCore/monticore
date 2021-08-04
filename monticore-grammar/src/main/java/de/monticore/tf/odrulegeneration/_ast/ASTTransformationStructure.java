/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrulegeneration._ast;

import java.util.HashMap;
import java.util.List;

public class ASTTransformationStructure extends ASTTransformationStructureTOP {

    protected  HashMap<String, List<String>> foldingHash = new HashMap<>();

    protected ASTTransformationStructure() {
        super();
    }

    protected  ASTTransformationStructure(
                                      String r__package,
                                      List<String> importss,
                                      String classname,
                                      ASTPattern pattern,
                                      ASTReplacement replacement,
                                      String constraintExpression,
                                      String doStatement,
                                      String undoStatement,
                                      List<String> assignmentss,
                                      List<ASTVariable> variables)  {
        super();
        setPackage(r__package);
        setImportsList(importss);
        setClassname(classname);
        setPattern(pattern);
        setReplacement(replacement);
        setConstraintExpression(constraintExpression);
        setDoStatement(doStatement);
        setUndoStatement(undoStatement);
        setAssignmentsList(assignmentss);
        setVariableList(variables);
        //super(r__package, importss, classname, pattern, replacement, constraintExpression, doStatement, assignmentss, variables);
    }

    protected  ASTTransformationStructure(HashMap<String, List<String>> foldingHash,
                                      String r__package,
                                      List<String> importss,
                                      String classname,
                                      ASTPattern pattern,
                                      ASTReplacement replacement,
                                      String constraintExpression,
                                      String doStatement,
                                      String undoStatement,
                                      List<String> assignmentss,
                                      List<ASTVariable> variables)  {
        super();
        setPackage(r__package);
        setImportsList(importss);
        setClassname(classname);
        setPattern(pattern);
        setReplacement(replacement);
        setConstraintExpression(constraintExpression);
        setDoStatement(doStatement);
        setUndoStatement(doStatement);
        setAssignmentsList(assignmentss);
        setVariableList(variables);
        //super(r__package, importss, classname, pattern, replacement, constraintExpression, doStatement, assignmentss, variables);
        setFoldingHash(foldingHash);
    }

    public HashMap<String, List<String>> getFoldingHash ()  {
       return this.foldingHash;
    }

    public  void setFoldingHash (HashMap<String, List<String>> foldingHash)  {
       this.foldingHash = foldingHash;
    }

}
