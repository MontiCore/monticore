/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._visitor;

import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDMember;

import java.util.*;
import java.util.stream.Collectors;

/** Visitor to collect all CDMembers of specific types */
public class CDMemberVisitor
  implements CDBasisVisitor2 {
  protected final Set<Options> options;
  protected final List<ASTCDMember> elements;

  public CDMemberVisitor(Options... options) {
    this.options = new HashSet<>(Arrays.asList(options));
    if (this.options.isEmpty()) {
      this.options.add(Options.ALL);
    }

    this.elements = new ArrayList<>();
  }

  public List<ASTCDMember> getCDMemberList() {
    return elements;
  }

  @Override
  public void visit(ASTCDAttribute node) {
    if (options.contains(Options.ALL)
      || options.contains(Options.FIELDS)
      || options.contains(Options.ATTRIBUTES)) {
      elements.add(node);
    }
  }

  public <T extends ASTCDMember> List<T> getElements() {
    return elements.stream().map(e -> (T) e).collect(Collectors.toList());
  }

  public enum Options {
    ALL,
    FIELDS,
    ATTRIBUTES,
  }

  public void run(ASTCDBasisNode ast) { // TBD: How to achieve this without downcasts?
    CDBasisTraverser t = CDBasisMill.traverser();
    t.add4CDBasis(this);

    ast.accept(t);
  }
}

