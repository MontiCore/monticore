/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccollectiontypes._ast;

public class ASTMCMapTypeBuilder extends ASTMCMapTypeBuilderTOP {

  @Override
  public ASTMCMapType build() {
    if (!isValid()) {
      throw new IllegalStateException();
    }
    ASTMCMapType value;

    value = new ASTMCMapType();
    if (this.mCTypeArguments.size() == 2) {
      value.setMCTypeArgumentsList(this.mCTypeArguments);
    } else if (this.key != null && this.value != null) {
      value.setKey(this.key);
      value.setValue(this.value);
    }
    if (this.isPresent_SourcePositionEnd()) {
      value.set_SourcePositionEnd(this.get_SourcePositionEnd());
    } else {
      value.set_SourcePositionEndAbsent();
    }
    if (this.isPresent_SourcePositionStart()) {
      value.set_SourcePositionStart(this.get_SourcePositionStart());
    } else {
      value.set_SourcePositionStartAbsent();
    }
    value.set_PreCommentList(this.precomments);
    value.set_PostCommentList(this.postcomments);

    return value;
  }

  @Override
  public boolean isValid() {
    return !(key == null && value == null && (mCTypeArguments == null || mCTypeArguments.size() != 2));
  }
}
