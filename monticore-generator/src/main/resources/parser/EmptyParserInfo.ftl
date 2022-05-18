<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("referencedSymbolToStates", "usageNameToStates", "superGrammars")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>
package ${genHelper.getParserPackage()};

import java.util.*;
<#list superGrammars as superLangGrammarAst>
import ${genHelper.getParserPackage()}._auxiliary.${ast.getName()}ParserInfoFor${superLangGrammarAst.getName()};
</#list>
<#list superGrammars as superLangGrammarAst>
import <#if superLangGrammarAst.getPackageList()?has_content>${superLangGrammarAst.getPackageList()?join(".")}.</#if>${superLangGrammarAst.getName()?lower_case}._parser.${superLangGrammarAst.getName()}ParserInfo;
</#list>

/**
 * Implementation of ${ast.getName()}ParserInfo where every method returns false.
 * This is used in the static delegate pattern as the superclass for the ParserInfoForSuperLang classes.
 * Therefore, the ParserInfoForSuperLang classes don't have to know all symbol references and usage names that are used in a super-language, but not in the sub-language.
 */
public abstract class Empty${ast.getName()}ParserInfo extends ${ast.getName()}ParserInfo{
  <#list referencedSymbolToStates as referencedSymbol, states>
    @Override
    protected boolean _stateReferences${referencedSymbol?cap_first}Symbol(int state){
      return false;
    }

  </#list>
  <#list usageNameToStates as usageName, states>
    @Override
    protected boolean _stateHasUsageName${usageName?cap_first}(int state){
      return false;
    }

  </#list>

  protected boolean _stateDefinesName(int state){
    return false;
  }
}
