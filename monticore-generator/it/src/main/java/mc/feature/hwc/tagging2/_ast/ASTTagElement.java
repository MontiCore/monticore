package mc.feature.hwc.tagging2._ast;

import mc.feature.hwc.tagging2._ast.ASTTagElementTOP;

public class ASTTagElement extends ASTTagElementTOP {
  protected  ASTTagElement (String name, String tagValue) {
    super(name, tagValue);
  }

  protected ASTTagElement () {
    super();
  }

  public void setTagValue(String tagValue) {
    if (tagValue != null) {
      if (tagValue.startsWith("=")) {
        tagValue = tagValue.substring(1);
      }
      if (tagValue.endsWith(";")) {
        tagValue = tagValue.substring(0, tagValue.length() - 1);
      }
      tagValue = tagValue.trim();
    }
    super.setTagValue(tagValue);
  }
}


