<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superLangGrammarAst", "referencedSymbolToStates", "usageNameToStates")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>
package ${genHelper.getParserPackage()}._auxiliary;

import java.util.*;
import org.antlr.v4.runtime.ParserRuleContext;
import ${genHelper.getParserPackage()}.${ast.getName()}ParserInfo;
import <#if superLangGrammarAst.getPackageList()?has_content>${superLangGrammarAst.getPackageList()?join(".")}.</#if>${superLangGrammarAst.getName()?lower_case}._parser.Empty${superLangGrammarAst.getName()}ParserInfo;

public class ${ast.getName()}ParserInfoFor${superLangGrammarAst.getName()} extends Empty${superLangGrammarAst.getName()}ParserInfo {
  <#list referencedSymbolToStates as referencedSymbol, states>
  protected boolean _stateReferences${referencedSymbol?cap_first}Symbol(int state){
    return ${ast.getName()}ParserInfo.stateReferences${referencedSymbol?cap_first}Symbol(state);
  }
  </#list>
  <#list usageNameToStates as usageName, states>
  protected boolean _stateHasUsageName${usageName?cap_first}(int state){
    return ${ast.getName()}ParserInfo.stateHasUsageName${usageName?cap_first}(state);
  }
  </#list>

  protected boolean _stateDefinesName(int state){
    return ${ast.getName()}ParserInfo.stateDefinesName(state);
  }

  protected Optional<String> _getImplementationOfInterfaceProd(ParserRuleContext context) {
    return ${ast.getName()}ParserInfo.getImplementationOfInterfaceProd(context);
  }

  protected String _getUsageName(int state) {
    return ${ast.getName()}ParserInfo.getUsageName(state);
  }
  protected Set< String> _getInterfaceRulenames() {
    return ${ast.getName()}ParserInfo.getInterfaceRulenames();
  }

  protected Set< String> _getInterfacesOfRuleLC(String rulename) {
    return ${ast.getName()}ParserInfo.getInterfacesOfRuleLC(rulename);
  }

  protected String _getStatesReferencingSymbols(int state) {
    return ${ast.getName()}ParserInfo.getStatesReferencingSymbols(state);
  }
}
