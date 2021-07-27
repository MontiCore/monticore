/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrulegeneration._ast;

import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.List;

public class ASTTransformationStructureBuilder extends ASTTransformationStructureBuilderTOP {

    protected HashMap<String, List<String>> foldingHash = new HashMap<>();

    public  ASTTransformationStructureBuilder setFoldingHash (HashMap<String, List<String>> foldingHash)  {
        this.foldingHash = foldingHash;
        return this.realBuilder;
    }

    public HashMap<String, List<String>> getFoldingHash() {
        return this.foldingHash;
    }

    @Override
    public  ASTTransformationStructure build ()  {

        if (!isValid()) {
            if (r__package == null) {
                Log.error("0xA7222 r__package of type String must not be null");
            }
            if (classname == null) {
                Log.error("0xA7222 classname of type String must not be null");
            }
            if (pattern == null) {
                Log.error("0xA7222 pattern of type de.monticore.tf.odrulegeneration._ast.ASTPattern must not be null");
            }
            if (replacement == null) {
                Log.error("0xA7222 replacement of type de.monticore.tf.odrulegeneration._ast.ASTReplacement must not be null");
            }
            if (constraintExpression == null) {
                Log.error("0xA7222 constraintExpression of type String must not be null");
            }
            if (doStatement == null) {
                Log.error("0xA7222 doStatement of type String must not be null");
            }
            if (undoStatement == null) {
                Log.error("0xA7222 undoStatement of type String must not be null");
            }
            throw new IllegalStateException();
        }
        ASTTransformationStructure value;

        value = new ASTTransformationStructure();
        value.setPackage(this.r__package);
        value.setImportsList(this.imports);
        value.setClassname(this.classname);
        value.setPattern(this.pattern);
        value.setReplacement(this.replacement);
        value.setConstraintExpression(this.constraintExpression);
        value.setDoStatement(this.doStatement);
        value.setUndoStatement(this.undoStatement);
        value.setAssignmentsList(this.assignments);
        value.setVariableList(this.variables);
        if (this.sourcePositionEnd.isPresent()) {
            value.set_SourcePositionEnd(this.sourcePositionEnd.get());
        } else {
            value.set_SourcePositionEndAbsent();
        }
        if (this.sourcePositionStart.isPresent()) {
            value.set_SourcePositionStart(this.sourcePositionStart.get());
        } else {
            value.set_SourcePositionStartAbsent();
        }
        value.set_PreCommentList(this.precomments);
        value.set_PostCommentList(this.postcomments);
        value.setFoldingHash(this.foldingHash);

        return value;
    }
}
