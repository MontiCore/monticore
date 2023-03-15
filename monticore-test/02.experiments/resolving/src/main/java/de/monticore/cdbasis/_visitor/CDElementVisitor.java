/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._visitor;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import java.util.*;
import java.util.stream.Collectors;

/** Visitor to collect all CDElements of specific types */
public class CDElementVisitor
  implements CDBasisVisitor2 {
  protected final Set<Options> options;
  protected final List<ASTCDElement> elements;

  public CDElementVisitor(Options... options) {
    this.options = new HashSet<>(Arrays.asList(options));
    if (this.options.isEmpty()) {
      this.options.add(CDElementVisitor.Options.ALL);
    }
    this.elements = new ArrayList<>();
  }

  public List<ASTCDElement> getCDElementList() {
    return elements;
  }

  @Override
  public void visit(ASTCDPackage node) {
    if (options.contains(Options.ALL) || options.contains(Options.PACKAGES)) {
      elements.add(node);
    }
  }

  @Override
  public void visit(ASTCDClass node) {
    if (options.contains(Options.ALL) || options.contains(Options.CLASSES)) {
      elements.add(node);
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends ASTCDElement> List<T> getElements() {
    return elements.stream().map(e -> (T) e).collect(Collectors.toList());
  }

  public enum Options {
    ALL,
    PACKAGES,
    CLASSES,
  }
}

