/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.language;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.swt.graphics.Image;

import de.monticore.ast.ASTNode;

class OutlineElement<T extends ASTNode> {
  
  private final Class<T> ruleType;
  
  private final Function<T, String> nameGetter;
  
  private final Optional<Image> image;
  
  OutlineElement(
      Class<T> ruleType,
      Function<T, String> nameGetter,
      Optional<Image> image) {
    this.ruleType = ruleType;
    this.nameGetter = nameGetter;
    this.image = image;
  }
  
  Class<T> getRuleType() {
    return ruleType;
  }
  
  Optional<String> getName(Object context) {
    if (ruleType.isInstance(context)) {
      return Optional.of(nameGetter.apply(ruleType.cast(context)));
    }
    return Optional.empty();
  }
  
  Optional<Image> getImage() {
    return image;
  }
}
