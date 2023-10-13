<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("prodname")}
<#--Generate the visit method for Tag-Identifiers-->
<#assign service = glex.getGlobalVar("service")>

@Override
public void visit(AST${prodname}Identifier node) {
  String type = node.getIdentifies();

  for (ASTTag tag : tagsToAdd) {
    if (!tagConformanceChecker.verifyFor(tag, type)) {
      Log.error("0xA5141${service.getGeneratedErrorCode(prodname)}: Concrete-Syntax tag " + tag + " could not be found: " + tag.get_SourcePositionStart());
    }
  }
}
