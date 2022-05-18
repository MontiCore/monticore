<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("referencedSymbolToStates", "usageNameToStates", "superGrammars", "nameDefiningStates")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>
package ${genHelper.getParserPackage()};

import java.util.*;
<#list superGrammars as superLangGrammarAst>
import ${genHelper.getParserPackage()}._auxiliary.${ast.getName()}ParserInfoFor${superLangGrammarAst.getName()};
</#list>
<#list superGrammars as superLangGrammarAst>
import <#if superLangGrammarAst.getPackageList()?has_content>${superLangGrammarAst.getPackageList()?join(".")}.</#if>${superLangGrammarAst.getName()?lower_case}._parser.${superLangGrammarAst.getName()}ParserInfo;
</#list>

public class ${ast.getName()}ParserInfo {
  // References
  <#list referencedSymbolToStates as referencedSymbol, states>
  protected <@compress single_line=true> static Set<Integer> statesReferencing${referencedSymbol?cap_first}
  <#if states?size == 0>
  = Collections.emptySet();
  <#else>
  = new HashSet<>(Arrays.asList(
    <#list states as state>
    ${state?c}<#if state?has_next>,</#if>
    </#list>
  ));
  </#if>
  </@compress>

  </#list>

  // Usage names
  <#list usageNameToStates as usageName, states>
  protected <@compress single_line=true> static Set<Integer> statesWithUsageName${usageName?cap_first}
  <#if states?size == 0>
    = Collections.emptySet();
  <#else>
    = new HashSet<>(Arrays.asList(
    <#list states as state>
      ${state?c}<#if state?has_next>,</#if>
    </#list>
    ));
  </#if>
  </@compress>

  </#list>

  // Named defining
  protected <@compress single_line=true> static Set<Integer> statesDefiningName
  <#if nameDefiningStates?size == 0>
    = Collections.emptySet();
  <#else>
    = new HashSet<>(Arrays.asList(
    <#list nameDefiningStates as state>
      ${state?c}<#if state?has_next>,</#if>
    </#list>
    ));
  </#if>
  </@compress>

  protected static ${ast.getName()}ParserInfo delegate;

  protected static ${ast.getName()}ParserInfo getDelegate(){
      if(delegate == null){
        init();
      }
      return delegate;
  }


  public static void init(){
    delegate = new ${ast.getName()}ParserInfo();
    <#list superGrammars as superLangGrammarAst>
    ${superLangGrammarAst.getName()}ParserInfo.initMe(new ${ast.getName()}ParserInfoFor${superLangGrammarAst.getName()}());
    </#list>
  }

  public static void initMe(${ast.getName()}ParserInfo _delegate){
    delegate = _delegate;
  }

  <#list referencedSymbolToStates as referencedSymbol, states>
  public static boolean stateReferences${referencedSymbol?cap_first}Symbol(int state){
    return getDelegate()._stateReferences${referencedSymbol?cap_first}Symbol(state);
  }

  protected boolean _stateReferences${referencedSymbol?cap_first}Symbol(int state){
    return statesReferencing${referencedSymbol?cap_first}.contains(state);
  }

  </#list>
  <#list usageNameToStates as usageName, states>
  public static boolean stateHasUsageName${usageName?cap_first}(int state){
    return getDelegate()._stateHasUsageName${usageName?cap_first}(state);
  }

  protected boolean _stateHasUsageName${usageName?cap_first}(int state){
    return statesWithUsageName${usageName?cap_first}.contains(state);
  }

  </#list>
  public static boolean stateDefinesName(int state){
    return getDelegate()._stateDefinesName(state);
  }

  protected boolean _stateDefinesName(int state){
    return statesDefiningName.contains(state);
  }
}
