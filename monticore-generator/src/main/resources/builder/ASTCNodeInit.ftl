<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainClass")}
value = new ${domainClass.getName()}();
<#list domainClass.getCDAttributeList() as attribute>
value.set${attribute.getName()?cap_first}(this.${attribute.getName()});
</#list>
value.set_SourcePositionEndOpt(this.sourcePositionEnd);
value.set_SourcePositionStartOpt(this.sourcePositionStart);
value.setEnclosingScopeOpt(this.enclosingScope);
value.setSymbolOpt(this.symbol);
value.setSpannedScopeOpt(this.spannedScope);
value.set_PreCommentList(this.precomments);
value.set_PostCommentList(this.postcomments);
