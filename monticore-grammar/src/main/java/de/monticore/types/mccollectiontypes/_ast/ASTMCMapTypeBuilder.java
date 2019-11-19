package de.monticore.types.mccollectiontypes._ast;

import de.se_rwth.commons.logging.Log;

public class ASTMCMapTypeBuilder extends ASTMCMapTypeBuilderTOP {

  @Override
  public ASTMCMapType build() {
    if (!isValid()) {
      throw new IllegalStateException();
    }
    ASTMCMapType value;

    value = new ASTMCMapType();
    if (this.mCTypeArguments.size() == 2) {
      value.setMCTypeArgumentList(this.mCTypeArguments);
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
    if (key == null && value == null && (mCTypeArguments == null || mCTypeArguments.size() != 2)) {
      return false;
    }
    return true;
  }
}
