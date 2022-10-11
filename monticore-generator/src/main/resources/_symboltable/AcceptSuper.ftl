<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "visitorTypeName","errorCode", "symbolClassName", "superVisitorTypeName")}
<#-- when overriding nonterminals we might have a visitor calling this
     method even if it is a sublanguage visitor, see #1708 -->
  if (visitor instanceof ${visitorTypeName}) {
    accept((${visitorTypeName}) visitor);
  } else {
    de.se_rwth.commons.logging.Log.error("0xA7010${errorCode} Symbol type ${symbolClassName} expected a visitor of type ${visitorTypeName}, but got ${superVisitorTypeName}. Visitors of a super language may not be used on Symbols containing nodes of the sub language. Use a visitor of the sub language.");
  }
