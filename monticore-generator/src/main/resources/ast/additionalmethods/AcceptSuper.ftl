<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("d1", "astType", "qualifiedCDName", "visitorTypeFQN", "superVisitorTypeFQN")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign plainName = genHelper.getPlainName(astType)>
    <#-- when overriding nonterminals we might have a visitor calling this
         method even if it is a sublanguage visitor, see #1708 -->
    if (visitor instanceof ${visitorTypeFQN}) {
      accept((${visitorTypeFQN}) visitor);      
    } else {
      de.se_rwth.commons.logging.Log.error("0xA7000${genHelper.getGeneratedErrorCode(ast)}AST node type ${plainName} of the sub language ${qualifiedCDName} expected a visitor of type ${visitorTypeFQN}, but got ${superVisitorTypeFQN}. Visitors of a super language may not be used on ASTs containing nodes of the sub language. Use a visitor of the sub language.");
    }
