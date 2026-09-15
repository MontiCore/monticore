<#-- @ftlvariable name="grammarName" type="java.lang.String" -->
  protected void notifyDeepClone(ASTNode node) {
    ${grammarName}Traverser traverser = ${grammarName}Mill.inheritanceTraverser();
    traverser.add4IVisitor(new de.monticore.visitor.IVisitor() {

      final Stack<ASTNode> stack = new Stack<>();

      @Override
      public void visit(ASTNode node) {
        modelAccessor.notifyNodeCreation(node);
        stack.push(node);
      }

      @Override
      public void endVisit(ASTNode node) {
        stack.pop();
        modelAccessor.notifyNodeAttach(node, stack.isEmpty() ? null : stack.peek());
      }
    });
    node.accept(traverser);
  }