<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "visitorTypeName","errorCode", "className", "superVisitorTypeName", "type")}
<#-- when overriding nonterminals we might have a visitor calling this
     method even if it is a sublanguage visitor, see #1708 -->
  if (visitor instanceof ${visitorTypeName}) {
    accept((${visitorTypeName}) visitor);
  } else {
    de.se_rwth.commons.logging.Log.error("${errorCode} ${type} type ${className} expected a visitor of type ${visitorTypeName}, but got ${superVisitorTypeName}. Visitors of a super language may not be used on Symbols containing nodes of the sub language. Use a visitor of the sub language.");
  }
