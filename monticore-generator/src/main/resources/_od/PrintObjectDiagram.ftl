<#-- (c) https://github.com/MontiCore/monticore -->
  pp.clearBuffer();
  pp.setIndentLength(2);
  pp.print("objectdiagram ");
  pp.print(modelName);
  pp.println(" {");
  pp.indent();
  node.accept(getRealThis());
  pp.print(";");
  pp.unindent();
  pp.println("}");
  return pp.getContent();