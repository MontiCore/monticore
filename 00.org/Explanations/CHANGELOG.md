<!-- (c) https://github.com/MontiCore/monticore -->
# Release Notes

##  MontiCore 6.5.0-SNAPSHOT
to be released 

### Additions
* added an experiment showcasing serialization and deserialization
* IncCheck provided by the MontiCore Gradle Plugin now considers local super grammar changes to trigger new generation

### Changes
* renamed `IXResolvingDelegate` to `IXResolver`
* outsourced Type expressions for arrays to a separate grammar
  * was `FullGenericTypes`, is now `MCArrayTypes`
* moved array initialization to `JavaLight` (was `MCVarDeclarationStatements`)
* In a composed language, mills of super languages now provide scope instances (scope, global scope and artifact scope) for the composed language
* non-existing template paths now result in an error instead of a warning


### Fixes

* Fixed that global variable changes in child templates were not changed in parents
* Fixed handling of optional names of symbols in symbol table creator 
* Fixed an issue where surrogates hide symbol attributes


##  MontiCore 6.4.0
released: 12.10.2020

### Additions
* extended the generated incCheck files to contain information about local super grammars
    * the sh-file is now able to trigger generation if local super grammars are changed
    * the incCheck method provided by the plugin will support this behavior as well
    * will only be available in the next release
* extended the mill to manage the global scope instance centrally 
* added comfort methods for creating modifiers to the `ModifierBuilder`
    * `ModifierBuilder().PUBLIC()` short for `ModifierBuilder().setPublic(true)`
* added `MCShadowingJavaBlock` to `MCCommonStatements`
    * standard `MCJavaBlock` is no longer shadowing
* added a class diagram to the reports that represents the generated data structure for the given grammar
 (ast, symbol table visitors, etc.)
* added simple `BreakStatement` to `MCCommonStatements`
* added an `include2` alias for the template controller method for including templates in conjunction with templates arguments


### Changes
* CLI does no longer check whether a generation is needed (this should be handled by the build tool)
* rephrased messages for non-conservative extension (added super grammar name)
* added a context condition to prevent list of names in nonterminal production marked as symbols
  * might be supported in a future version of MontiCore
* moved XForYMills to a subpackage to reduce noise (subpackage: _auxiliary)
* deprecated the generated enum für constants 
    * will be removed without replacement in a future release
* moved `EnhancedForControl` production from `JavaLight` to `MCCommonStatements` as it is commonly used 
* standard `MCJavaBlock` is no longer shadowing
* renamed `BreakStatement` in `MCLowLevelStatements` to `LabelledBreakStatement`
* `ForStatement` now spans a non-exporting, ordered scope 
* shortened generated error codes to have 5 digits only
* renamed `MethOrConstr` to `JavaMethod` in `JavaLight`
* MontiCore Gradle plugin is no longer shipped as a fat jar

### Fixes

* Fixed error code calculation for generated error messages to no longer be random
* Fixed the report for involved files to contain handwritten files that were considered 
    * will only be available in the next release
* Fixed an issue where reports did not contain meaningful names for elements such as class diagram classes or interfaces

##  MontiCore 6.3.0
released: 16.09.2020

##  MontiCore 6.2.0
released: 21.07.2020

##  MontiCore 6.1.0
released: 07.05.2020

##  MontiCore 6.0.0
- Uses CD4Analysis 1.5.0
- replace get\*opt methods with get\*
- bugfixing

##  MontiCore 5.4.0.1
- Uses CD4Analysis 1.4.0
- add generation of serializers for grammars
- add SymbolLoader
- remove SymbolReferences
- add DeSers for TypeSymbols
- improved TypeCheck
- replace getName methods with printType methods

##  MontiCore 5.3.0
- Uses CD4Analysis 1.3.20.2
- new Generator based on Decorator-Pattern
- add Translation classes
- add grammar it-tests
- move TypesCalculator to TypeCheck, create derive classes and synthesize classes
- add TypeSymbols and SymTypeExpression structure
- added DeSers for SymTypeExpressions
- added keyword "key" for KeyTerminals 

##  MontiCore 5.2.0
- add "List"-Suffix to attribute name

##  MontiCore 5.1.0
- Remove the dependency to JavaDSL, add JavaLight
- Uses CD4Analysis 1.3.19
- added grammar TypeSymbols
- renamed SymbolDelegateList to SymbolResolvingDelegateList
- add methods for scoperule-attributes in interfaces
- add MCTypeVisitor to transform ASTTypes to TypeExpressions
- add Groovy Plugin
- add MontiCore Statements at de.monticore.statements

##  MontiCore 5.0.6
- The IncGen-reports are stored in the source code directory
- Removed MutableScope
- IncGen-Reports are stored 
- Removed deprecated keyword _ast_ (use _astrule_) in *.mc4
- Add visitors for symbol table
- Enable TOP mechanism for visitors
- add SymbolRules and ScopeRules
- renamed MCBasicLiterals to MCCommonLiterals, add MCLiteralsBasis
- move literals to package de.monticore.literals
- renamed ShiftExpressions to BitExpressions

##  MontiCore 5.0.3
- Use the following emf coordinates (MB):
- group: org.eclipse.emf
- version: 2.15.0
- artifact: org.eclipse.emf.ecore | org.eclipse.emf.ecore.xmi | org.eclipse.emf.common
- The runtime environment may need the following dependency (group: org.eclipse.platform; artifacitId: org.eclipse.equinox.common; version: 3.10.0)
- splitted Types.mc4 in MCBasicTypes, MCCollectionTypes, MCSimpleGenericTypes and MCFullGenericTypes
- moved expressions to de.monticore.expressions and added expressions

## MontiCore 5.0.2
- Generated by the MontiCore version 5.0.1
- Uses JavaDSL 4.3.13, Cd4Analysis 1.3.16, se-commons 1.7.9
- Introduce deprecated annotation in grammars (#2215)
- Serialization of symobls
- Add reporter IncGenCheckReporter
- Configuration of the report path
- Specific resolving methods in generated scope classes
- Bugfixes

## MontiCore 5.0.1
- Generated by the MontiCore version 5.0.0
- Uses JavaDSL 4.3.12, Cd4Analysis 1.3.13, se-commons 1.7.9
- Bugfixes
- New methods defineHookPointWithDefault in GlobalExtensionManagement **(MB)**
- new method cmpToken in MCParser **(MB)**
- every (non-)terminal defined in an interface must be present in the implementing production (including Name and Usage Name) (CoCo) **(BS)**
- to ensure that any terminal (with a specific name) has to be implemented, use an empty string, e.g. interface Expression = operator="";
- new methods are generated for the referenced symbol and definition and the definition is saved in an attribute (generated wenn you write sth. like "Name@Symbol") **(NP)**
- coco that gives a warning if you do not extend conservative **(NP)**
- coco that attributes with the same usage Name have to reference the same symbol **(NP)**
- SpannedScope and Symbol Methods in ASTNode set to deprecated **(NP)**

## MontiCore 5.0.0
- Generated by the MontiCore version 4.5.5.1
- Uses JavaDSL 4.3.11, Cd4Analysis 1.3.13, se-commons 1.7.8
- Changed name building for list attributes in grammars (x:Name* -> getXList)
- Changed api for GlobalExtensionMangament and TemplateController (see reference manual)
- New api for AST nodes (constructor, getter and setter for lists and optional attributes, ...)
- Builder classes for AST nodes are external now. Signatures are similar to those in the corresponding AST node, except those methods which set or add something, these return the Builder itself (which allows method chaining)
- Changed default script to noemf for the generation of MontiCore. If you want to use emf you can generate monticore-grammar and Java-DSL with the profile "emf". This profile also creates the emf jars. You are also able to test the integration-tests with the profile “emf-it-tests”, which contains extra tests for the generation with emf.
- Parsed grammars are not stored as class diagram anymore. CD is only stored as report.
- Removed deprecated method **filter(ResolvingInfo resolvingInfo, List<Symbol> symbols)** use **filter(ResolvingInfo, Collection)** instead
- Removed deprecated method **filter(ResolvingInfo resolvingInfo, String name, List<Symbol> symbols)** use **filter(ResolvingInfo, String, Map)** instead
- Removed deprecated method **create(Class symbolClass, SymbolKind symbolKind)** use **create(SymbolKind)** instead
- Removed deprecated method **getSymbols** use **getLocalSymbols** instead
- Removed deprecated method **resolve(SymbolPredicate predicate)** use *resolveMany(String, SymbolKind, Predicate)* instead
- Removed deprecated method *define* use *add* instead
- Removed deprecated method **resolve(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier)**
- Removed deprecated method **checkIfContinueWithEnclosing** use **checkIfContinueWithEnclosingScope** instead
- Removed deprecated method **addResolver** use **addFilter(String, ResolvingFilter)** instead
- Removed deprecated method **addTopScopeResolver** use **addDefaultFilter** instead
- Removed deprecated method **addTopScopeResolvers** use **addDefaultFilters** instead
- Removed deprecated method **getTopScopeResolvingFilters** use **getDefaultFilters** instead
- Removed deprecated constructer **CommonResolvingFilter(Class symbolClass, SymbolKind targetKind)** use **CommonResolvingFilter(SymbolKind)** instead
- Removed deprecated method **continueWithScope** and **continueWithEnclosingScope**
- Removed class **FaildLoadingSymbol**
- Removed deprecated method **putInScopeAndLinkWithAst** use **addToScopeAndLinkWithNode** instead
- Removed deprecated constructer **CommonModelingLanguage(String, String, SymbolKind)** use **CommonModelingLanguage(String, String)** instead
- Removed deprecated method **addResolver** use **addResolvingFilter** instead
- Removed deprecated method **addResolver** use **addResolvingFilter** instead
- Removed deprecated method **getResolvers** use **getResolvingFilters** instead
- Removed deprecated method **loadAmbiguousModelAndCreateSymbolTable** use **loadModelsIntoScope** instead
- Removed deprecated method **loadAmbiguousModels** use **loadModels** instead
- Removed deprecated method **defineHookPoint(String)** use **glex.defineHookPoint** instead
- Removed deprecated enum **ParserExecution**
- Removed deprecated method **getParserTarget**
- Removed deprecated method **setParserTarget**

## Further Information

* [MontiCore project](../../README.md) - MontiCore
* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)

