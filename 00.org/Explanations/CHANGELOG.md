<!-- (c) https://github.com/MontiCore/monticore -->

# Release Notes

## MontiCore 7.6.0
released: --to be determined--

### Additions
* template replacement: addBefore & addAfter 
* CoCo ensuring only lvalues are valid values for assignments
* new grammars:
  * CompSymbols for C&C symbol interfaces
  * RegularExpressions (ported from RegEx project)
  * RegExType (ported from RegEx project)
  * UglyExpressions to move reflection based expressions to and 'new'
  * OCLExpressions (ported from OCL project)
  * OptionalOperators (ported from OCL project)
  * SetExpressions (ported from OCL project)
* a generated toString() method for symbols
* grammar options ParserSuperClass and LexerSuperClass to influence ANTLR
* CoCo to forbid non-unique production names in composed grammars (A0144)
* new gradle plugin [docs](../../docs/Gradle.md)
* domain-specific tagging models:
  * new grammar Tags, to describe tags of symbols 
  * new grammar TagSchema
  * generate a language-specific Tagger class as an API to tags
  * derive a language-specific TagSchema grammar, to describe the set of allowed tags for a symbol kind
  * new TagRepository class to facilitate loading of tag models
* Scopes:
  * methods to check whether a scope is a sub-scope of another
  * method to check whether a type variable is bound in a given context
* method for shallow copying of AccessModifiers
* added initializeString to BasicSymbolsMill
* PrettyPrinter:
  * OCLExpressions
  * SetExpressions
* TypeCheck:
  * Added SymTypeOfRegEx
  * Added TypeVisitor classes which calculate the type of ASTNodes
  * Added NameExpressionTypeCalculator to calculate the type given a Name
    * Added OONameExpressionCalculator to filter out constructors
  * Added OOWithinTypeBasicSymbolsResolver to filter out constructors
  * Added Type4Ast map to TypeVisitors to store type results
  * Added MCCollectionTypeRelations to check relations on collection types
  * Added MCCollectionSymTypeFactory convenience methods to create collections
  * upper and lower bound for type variables
  * Added ILValueRelations to check if an expression is a l-value
    * Added CommonExpressionsLValueRelations as implementation
  * Added NominalSuperTypeCalculator to calculate nominal supertypes
    * can calculate them for object types,
      as well as structural types and variables
  * Added Wrapper to support the legacy interface using TypeCheck 3
  * Added method to clone SymTypeArray with different dimension
  * Added the possibility to use the type identifier as constructor
  * Added methods asX for SymTypeX to avoid casting
  * Added TypeSystem3 markdown documentation
* Expressions:
  * ASTRule: LambdaBodys have a return type

### Changes
* Findings of parse errors now contain additional context
* attribute isAbstract was moved from JavaMethodSymbol to MethodSymbol
* delete class MapUtil (replaced by Guava)
* delete deprecated class CoreTemplates
* report templates with qualified name
* severely refactored fieldAccessExpression TypeCalculation
* renamed TypeCheck 2 to 3
* updated ecj dependency
* removed ExtType extension points in grammars


### Fixes
* generated pretty printer behavior for end of lines with single line comments
* cache misses during resolution of fully qualified named in MCPath 
* multiple alternatives in an expression are now supported by the DSTLGen (0xA5C05)
* improved groovy workflow script setup time
* fix code blocks for alternatives in interfaces (ANTLR)
* delegate accect-methods in SymbolSurrogate
* ForConditionHasBooleanType removed incorrect casting
* fix StreamType SymType-Library instantiation
* corrected TypeVarSymbol deepEquals
* replaced some error codes that were used more than once
* asserts the existence of String symbol if required by the TypeCheck
* subTyping of generics is now a recursive check
* in TypeCheck 3 added workarounds for faulty symbol resolving
* in TypeCheck 3 added workarounds for faulty TypeDispatcher
* added null-pointer checks
* Fix TypeVar deepclone
* Remove CoCo that forbid token modes in component grammars

## MontiCore 7.5.0
released: 02.05.2023

### Additions
* type dispatcher for type-safe instance checks and casting
* pretty-printer generator
* parser generator produces code for the new rule "replcaekeyword"
* new coco NoForbiddenProdName
* TOP mechanism for generated ANTLr parser classes
* add class StreamType which can be used to add Stream symbol with corresponding functions to the global scope
* Additions to AccessModifiers
  * add class StaticAccessModifier to filter whether a symbol is considered as static or not.
  * add class WriteableAccessModifier to filter whether a symbol is considered as writable or not.
  * add class CompoundAccessModifier to compose multiple access modifier, e.g. public + static
* Additions to the TypeCheck
  * add new class TypeRelations to provide typecheck methods in a non static fashion. The TypeCheck class now delegates to the implementation
  * add new class SymTypeOfUnion to store the type of a union of types
  * add new class SymTypeOfIntersection to store the type of an intersection of types
  * add new interface ISymTypeVisitor to traverse SymTypeExpressions
  * add new class SymTypeDeepCloneVisitor to clone SymTypeExpressions
  * add new class SymTypeBoxingVisitor to box SymTypeExpressions. This implemenation fixes issues over the methods within the SymTypeExpression classes
  * add new class SymTypeUnboxingVisitor to unbox SymTypeExpressions. This implemenation fixes issues over the methods within the SymTypeExpression classes
  * add new class SymTypeNormalizeVisitor to normalize SymTypeExpressions. This is required to check for compatibility between SymTypeExpressions, especially regarding union and intersection types

### Changes
* Gradle projects containing multiple MCTasks can now use parallel builds
  * The MontiCore Gradle Plugin ensures that MCTasks are not run in parallel, other tasks, like compile, are run in parallel

### Fixes
* parser generation for optional keywords with usage name
* Overriding/Imlementing use of lexical productions
* in OOScopes, accessmodifier can be used to filter symbols

## MontiCore 7.4.0
released: 14.11.2022

### Additions
* new grammar rule replacekeyword
* add attribute derived to FieldSymbol
* generate XParserInfo
  * Used by generated language servers
  * Can be queried for additional info about specific parser states of the generated ANTLR parser
  * Contains methods stateHasUsageNameY to check the usage name of the nonterminal associated with a parser state
  * Contains methods stateReferencesZSymbol to check the referenced symbol kind of `Name` nonterminal associated with a parser state
* Additions to the TypeCheck
  * add new abstract classes AbstractDerive and AbstractSynthesize with basic functionality for FullDerivers and FullSynthesizers
  * add new class TypeCalculator that can be used to derive SymTypeExpressions from Expressions and synthesize them from types
  * add SymTypeObscure: new SymTypeExpression that is used when the SymTypeExpression for an expression could not be derived
  * add SymTypeFunction to store the type of a function
  * add the functionality for Function chaining, allowing to call functions returned by other functions
  * add varargs support for CallExpressions
  * add deriver for LambdaExpressions
* Add grammar MCFunctionTypes to write the type of a function in a model
* Add ExpressionStatementIsValid CoCo
* Add grammars LambdaExpressions and StreamExpressions

### Changes
* Java 11
* Gradle 7 compatibility
* delete deprecated method deepClone in ASTNodes
* use CDGenerator (cdanalysis)
* Visitor Pattern: Introduce state-based traversal of symbol table
  * Allows for combined AST and symbol table traversal from global and artifact scopes
  * Comes with integrated stand-alone symbol table traversal
* TypeCheck Refactoring
  * rename currentResult to result in TypeCheckResult
  * split TypeCheck facade into TypeCalculator (used to derive SymTypeExpressions from Expressions and synthesize them from types) and TypeCheck (static functions that are useful when checking for type compatibility or for comparing SymTypeExpressions)
  * rename SymTypeConstant to SymTypePrimitive
  * Deriver now evaluate all subexpressions of an expression and do not stop at the first error
  * do not log multiple errors for the same error: If an error occurs in the derivation of a subexpression and this error leads to another error in the derivation of the expression itself, do not log another error
  * remove the name of CallExpression
  * rework the calculation of CallExpression, NameExpression and FieldAccessExpression
  * rework TypeCheck error messages to make them more clear


### Fixes
* close all jars used to load models via MCPath
  * `gradle clean` should no longer fail because of dangling opened grammar jars
* TypeCheck
  * fix an error in the WildCardTypeArgument where ? super and ? extends were swapped
  * fix TypeCheck not logging an error for NameExpression and FieldAccessExpression in isolation
  * make short compatible to byte

## MontiCore 7.3.0
released: 04.04.2022

### Additions
* add cocos for lexical mode
* add coco for Expression
* add cocos for JavaLight
* new methods putSymbolDeSer, putXYSymbolDeSer and loadFileForModelNamed (GlobalScope Interface)
* new method getToken (MCParser)
* use CD4C in 02experiment.configTemplate

### Changes
* rename generated classes `XYCLI` -> `XYTool`

## MontiCore 7.2.0
released: 13.09.2021

### Changes
* Several modes can now be specified in a grammar (for further
explanations see [ANTLR](https://www.antlr.org/)). For the grammars, one
file is now generated for lexer rules and one for the parser rules.
* delete deprecated classes:
  `ModelPath`, `IterablePath`, `ModelCoordinate`, `ModelCoordinateImpl`, `ModelCoordinates`, and `FileFinder`
* The symbol table now stores for productions whether a 
  production is left-recursive.
* In the log class the dependency to ch.qos.logback:logback-core was removed
  
##  MontiCore 7.1.0
released: 05.07.2021

### Additions
* introduced language-specific CLI generation
* New class `MCPath` that manages a set of path entries. The class is used, e.g., for realizing symbol paths, model paths, handcoded paths, and template paths. `MCPath` replaces the classes `IterablePath` and `ModelPath`.
* Different modes can now be defined for lexical tokens in grammar.
  The corresponding generator will only be available in the next version.

### Changes
* the methods `serialize` and `deserialize` of the class `XDeSer` were moved to the class `XSymbols2Json`
* The following classes are marked as deprecated and will be removed in the near future: 
`ModelPath`, `IterablePath`, `ModelCoordinate`, `ModelCoordinateImpl`, `ModelCoordinates`, and `FileFinder`
 
### Fixes

##  MontiCore 7.0.0
released: 08.04.2021 

### Additions
* resolveXSubKinds(..) resolves for local symbols of all subkinds of a symbol kind X. This method is used
  by the implementation of the resolveXLocally(..) method. It enables proper handling of symbol kind hierarchies
  during symbol resolution beyond the borders of a language.
* new annotation @NonConservative for productions
* add configTemplate (-cf) mechanism to add a freemarker template for customizing the generation processed
* add two predefined groovy hook points (-gh1 and -gh2) in the monticore_standard.groovy for injecting 
  custom groovy scripts into the workflow

### Changes
* move grammars OCLExpressions and SetExpressions into OCL-project for further development
* DefsTypeBasic was moved to test. There are now only methods for creating symbols.
  Use the BasicSymbolsMill to create the basic data types like int, ...
* `deserialize(String)` method of scope DeSer classes is realized as default implementation in `IDeSer` interface
* `deserialize(String)` method of symbol DeSer classes is realized as default implementation in `ISymbolDeSer` interface
* `deserializeAddons()` and `serializeAddons()` methods  of scopes are realized as empty default implementation in `IDeSer` interface
* If deserialization encounters a symbol kind for which no DeSer is contained in the symbol Deser map in global scopes, a warning is produced instead of an error
* Boolean `isShadowing` property of scopes is only serialized if its value is "true". Deserialization assumes a default value of "false" if the property is not contained in a serialized scope
* `deserialize(String)` method of symbol DeSers do not produce errors if the serialized kind deviates from the symbol kind that the DeSer is originally engineered for
* The TypeCheck was reworked
  * The interface `ITypesCalculator` was renamed to `IDerive` and can now be used similar to the `ISynthesize` interface
  * no SymbolSurrogates are created anymore by the TypeCheck. The Synthesize-Classes will now log an error if a type cannot be resolved
  * SymTypeExpressions now have the method printFullName to print their full name
  * The class `TypeCheck` now needs one `IDerive` and one `ISynthesize` for its constructor instead of only one of them
  * The class `DeriveSymTypeOfBSCommonExpressions`, which does not pay attention to modifiers like `static` or `private`, can now be used as an alternative for the class `DeriveSymTypeOfCommonExpressions`

### Fixes
* Symbols with hierarchical symbol kinds are not serialized multiple times anymore.

##  MontiCore 6.7.0
released: 26.01.2021

### Additions
* Add new CLI for the MontiCore generator engine

### Changes
* The context conditions use the new traverser infrastructure. This leads to small changes in the api.
  The return value of the method addCoCo is void.
* Attribute fileExt in GlobalScopes now refers to a regular expression for file extensions of 
  symbol table files. The default value of the attribute is "*sym", which usually includes symbol 
  files of all MontiCore languages. Attention: If your language used the "setFileExt" method in
  previous versions of MontiCore to set the file extension of the model file (e.g., to "aut"), this 
  will cause problems now as the symbol files of the language have differen file extensions 
  (e.g., "autsym). To fix this, it is sufficient to remove all invocations of "setFileExt" from the 
  handwritten source code.
* For scopes, artifact scopes, and global scopes: Moved abstract methods that do not have a language-
  specific name or (argument, return) type from language-specific interface to MontiCore-runtime interfaces
* new experiment "strules" demonstrating the use of symbolrules and scoperules
* `deserialize` methods in SymTypeExpressionDeSers do not have an `enclosingScope` argument anymore.
  Internally, it uses the singleton global scope instead. 
* renamed `serializeAdditionalSSymbolAttributes` in `Symbols2Json` class to `serializeAddons` and moved
  to scope and symbol DeSers.
* `XScopeDeSer` is renamed to `XDeSer`
* In Symbols2Json classes:
  * now implementss Visitor2 
  * new attribute "XTraverser traverser" with getter and setter
  * Removed attribute "realThis" with getter and setter
  * New constructor with two arguments `XTraverser` and `JsonPrinter`
  * New zero args constructor
  * Removed constructor with single `JsonPrinter` argument
  * New attributes of all known symbol DeSers and current scope DeSers
  * New method "protected void init()", initializing the DeSer attributes with the GlobalScope
    and the traverser with symbols2json of inherited languages
   * adjusted store method to use traverser
   * visit methods for symbols delegate to serialize method of the symbol DeSer
   * visit and endVisit methods for scope interface and artifact scope interface print object stub 
     and delegate serialization to scope DeSers
* DeSers do not have an attribute of Symbols2Json class anymore, instead it is passed as argument 
  in the `serialize` methods 
* Default values of built-in types that occur in attributes of symbolrules or scoperules are 
  omitted during serialization and deserialization. The defaults are as follows:
  * Boolean : false
  * String : ""
  * Numeric types: 0 (and 0L and 0.0 and 0.0f)
* For symbolrule and scoperule attributes with non-built-in data type, no Log.error is thrown
  at execution time of the serialize method call anymore. Instead, these methods (and then, their 
  classes as well) are generated abstract to yield compilation errors instead.
* New interface `IDeSer` that all symbol and scope DeSers implement.
* GlobalScopes manage a map with all relevant DeSers. The map maps the serialized (symbol or scope)
  kind to the DeSer that (de)serialized this kind. This mechanism can be used to exchange the DeSer
  for a specific kind of symbol or scope.
* Scope DeSers have new `serialize` methods without `Symbols2Json` argment that can be used for
  for serializing (artifact) scopes for, e.g., unit tests 
* removed the generation of `XPhasedSymbolTableCreatorDelegator` classes
* Experiments now use ScopesGenitor-infrastructure instead of SymbolTableCreator-infrastructure
 
### Fixes
* The `initMe` and `reset` methods of the mill now initialize and reset all attributes properly

* The CD4Analysis keywords `ordered`, `composition`, `association`, `targetimport` and `classdiagram` 
  can be used in grammars again

##  MontiCore 6.6.0
released: 03.12.2020

### Additions
* The mill of a language now provides a method `parser()` to get the parser of the language 
    * mill initialization allows to reconfigure the mill to provide a parser for a sublanguage
    * parser delegator `XForYParser` are generated that extend a parser of a super language and delegate to the parser of the current language
    * Due to multiple inheritance, delegation and subclasses are used in combination 
* experiments now showcase the use of traversers   
* add coco (checks if additional attributes are declared twice)
* added built-in primitive types to the mills of grammars that extend the grammar BasicSymbols. Add to Mill by executing `BasicSymbolsMill.initializePrimitives()`

### Changes
* The generated parser uses the builder instead of the factory. This means that in grammars the variable `_aNode` is no longer available. Use instead `_builder`. 
* Multiple renamings and signature changes regarding the deser infrastructure
  * renamed `XSymbolTablePrinter` to `XSymbols2Json`
  * moved load and store methods form `XScopeDeSer` to `XSymbols2Json`
  * removed enclosing scope as method argument of symbol deser methods, as global scope shall be used instead
  * renamed `deserializeAdditionalSSymbolAttributes` to `deserializeAddons`
  * renamed `deserializeAdditionalXScopeAttributes` and `deserializeAdditionalXScopeAttributes` to `deserializeAddons`
  * added the JSON printer as a parameter to the methods of `XScopeDeSer`, `SSymbolDeSer` und `XSymbols2Json`
* `XScopeDeSer`, `SSymbolDeSer` und `XSymbols2Json` are no longer available via the mill. The constructors can be used instead.
* Scope builder have been removed as they did not support multiple inheritance, scope creation methods of the mill should be used instead
* Shortened the name of the scope creation methods in the mill from `xScope`, `xGlobalScope` and `xArtifactScope` to `scope`, `globalScope` and `artifactScope`
* Shortened the name of the `modelFileExtension` attribute in the `XGlobalScope` class to `fileExt`
* renamed `XScopeSkeletonCreator` and `XScopeSkeletonCreatorDelegator` to `XScopesGenitor` and `XScopesGenitorDelegator`
* Deprecated the `XPhasedSymbolTableCreatorDelegator`, will be removed without replacement in a future release
* PrettyPrinters and other visitors in monticore-grammar now use the new Traverser infrastructure instead of the old Visitor infrastructure
* generated `XScopeGenitor` and `XScopeGenitorDelegator` now use the new Traverser infrastructure instead of the old Visitor infrastructure
* Changes to resolving
  * if name of a topLevelSymbol in ArtifactScope = name of ArtifactScope: qualify symbols in spanned scopes of the topLevelSymbol like before with `<topLevelSymbolName>.<symbolName>`
  * if name of a topLevelSymbol in ArtifactScope != name of ArtifactScope: qualify symbols in spanned scope of the topLevelSymbol with `<ArtifactScopeName>.<topLevelSymbolName>.<symbolName>` 
* Traverser now support lists of `Visitor2` interfaces instead of only one instance
* Rename accessor of Traverser from `addXVisitor` to `add4X` 
* Methods returning referenced symbols save the symbols instead of the surroogates

### Fixes
* Traverser now properly delegate to handlers as intended
* ScopeSkeletonCreator now properly use the mill to create scope instances to ensure substitution via the mill pattern
* Fixed a bug where the SymbolSurrogates wrongly qualified their fullName
* The clear method of the GlobalScope now deletes all symbols stored in the GlobalScope
* Serializing symbolrule attributes of Strings now works properly

##  MontiCore 6.6.0
released: 11.11.2020

### Additions
* added an experiment `hwDeSers` showcasing serialization and deserialization
* added an experiment `hooks` showcasing hook point usage
* IncCheck provided by the MontiCore Gradle Plugin now considers local super grammar changes to trigger new generation
* Added new Traverser generation to replace the visitor infrastructure in a future release
    * `XTraverser`
    * `XTraverserImplementation`
    * `XVisitor2`
    * `XHandler`
* Added new ScopeSkeletonCreator generation to replace the SymbolTableCreator in a future release and to enable a phased symboltable creation
    * `XScopeSkeletonCreator`
    * `XScopeSkeletonCreatorDelegator`
    * `XPhasedSymbolTableCreatorDelegator`
* Added methods to directly obtain instances of the following classes in the mill (instead of their builders)
    * `XSymbolTableCreator` 
    * `XSymbolTableCreatorDelegator` 
    * `XScopeSkeletonCreator`
    * `XScopeSkeletonCreatorDelegator`
    * `XPhasedSymbolTableCreatorDelegator`
    * `XScopeDeSer`
    * `XSymbolDeSer` 
    * `XSymbolTablePrinter`
    * `IXScope`
    * `IXArtifactScope`


### Changes
* MontiCore now uses Gradle as build tool
  * some tasks have been introduced for the comfortable control of frequent activities, e.g., `buildMC`, `assembleMC` that can be found in the [`build.gradle`](https://github.com/MontiCore/monticore/blob/opendev/build.gradle)
  * relocated the EMF related subprojects:
    * `monticore-emf-grammar` to `monticore-grammar-emf`
    * `monticore-emf-runtime` to `monticore-runtime-emf`
  * relocated integration tests and experiments:
    * `monticore-generator/it` to `monticore-test/it`
    * `monticore-generator/it/experiments` to `monticore-test/01.experiments`
    * `monticore-generator/it/02.experiments` to `monticore-test/02.experiments`
    * `monticore-grammar/monticore-grammar-it` to `monticore-test/monticore-grammar-it`
* Remove the generation of `XModelloader`. Languages should now use `XScopeDeSer` to load symbol tables instead.
* Removed the generation of the following builder classes (also from the Mill; alternative solutions described below)
    * `XSymbolTableCreatorBuilder` 
    * `XSymbolTableCreatorDelegatorBuilder` 
    * `XScopeDeSerBuilder`
    * `XSymbolDeSerBuilder` 
    * `XSymbolTablePrinterBuilder`
* renamed `IXResolvingDelegate` to `IXResolver`
* outsourced Type expressions for arrays to a separate grammar
  * was `FullGenericTypes`, is now `MCArrayTypes`
* outsourced initialization for arrays to a separate grammar
  * was `MCVarDeclarationStatements`, is now `MCArrayStatements`
* In a composed language, mills of super languages now provide scope instances (scope, global scope and artifact scope) for the composed language
* non-existing template paths now result in an error instead of a warning
* Set current visitor infrastructure to deprecated
* Integrate new visitor infrastructure (i.e., traverser) into `XMill` to enable re-usability of visitors via language inheritance
* Set SymbolTableCreator, SymbolTableCreatorDelegator and their builder to deprecated
* Integrate new ScopeSkeletonCreator, ScopeSkeletonCreatorDelegator and PhasedSymbolTableCreatorDelegator into Mill
* Added a method `clear` to the GlobalScope that clears its cache and its resolvers and empties its ModelPath

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

### Additions
* added `@Override` annotation for nonterminal production to state that this production overrides a super grammars' production
  * overriding without annotation leads to a warning
  * using the annotation for a production that does not override an existing nonterminal results in an error
* added a context condition to ensure that external production do not have ast rules 
* added `DiagramSymbol` in `BasicSymbols`
* introduced generated interfaces for `GlobalScope` and `ArtifactScope`

### Changes
* serialization of symtype expression now serializes full name of symtype instead of simple name
* class `ASTNodes` is now deprecated and its usages in the generator are removed
* visitors no longer provide visit methods for concrete scope classes but their interfaces instead
* `SymTypeExpression` no longer use surrogates but `TypeSymbol`s instead
* reverted changes to appended `s` for list attributes made in previous release
* moved initialization of symbols to the `endVisit` method of the `SymbolTableCreator`


### Fixes
* Fixed missing sourcecode position for overriding warning
* Fixed an issue where the inheritance hierarchy was no considered correctly when overriding a nonterminal


##  MontiCore 6.2.0
released: 21.07.2020

### Additions
* added `isFinal` to `OOType` in `OOSymbols`
* extended the mill such that builder for DeSer related classes are provided by the mill
* added support for symbol usages in `NonterminalSeperator`
    * example: `Bar = (bla:Name@Foo || "," )+;`
* added reports for the symbol table structure of the processed grammar
* added `isReadOnly` to `Variable` in `BasicSymbols`
* added `isElliptic` to `Method` in `TypeSymbols`
* added a context condition to warn if keywords consist of numbers only 
    * these numbers will be tokenized as keywords instead of numbers
* added `splittoken` to express that the listed tokens should be split and not handled as a single token
    * example: `splittoken ":::";` results in three token `:`
* added `nokeyword` to express that the listed keywords should not be handled as tokens
   * example: `nokeyword "automaton", "state";` means that `automaton` and `state` should not be handled as keywords
* introduced symbol inheritance

### Changes

* renamed `de.monticore.type.TypeSymbols` to `de.monticore.symbols.OOSymbols`
* renamed `de.monticore.type.BasicTypeSymbols` `to de.monticore.symbols.BasicSymbols`
* reworked appended `s` for list attributes
* renamed SymbolLoader to SymbolSurrogate
  * Surrogates are now subclasses of their corresponding symbols
* `MCJavaBlock` in `MCCommonStatements` now spans a shadowing, non-exporting, ordered scope
* `MethodDeclaration` and `ConstructorDeclaration` in `JavaLight` use `MCJavaBlock` instead of `MCBlockStatement`
* `Label` in `MCLowLevelStatement` now is a symbol
* `VarDecl` in `MCVarDeclarationStatements` no longer exists
    * `DeclaratorId` now produces `FieldSymbol`s
* removed `isParameter` and `isVariable` from `Field` in `TypeSymbols`
* the language class is no longer generated
* moved creator expressions to `JavaClassExpression` 
* moved `PlusExpression` and `MinusExpression` from `AssignmentExpressions` to `CommonExpressions`


### Fixes
Fixed an issue where super and subtype comparison was wrong in type check
Fixed handling of capital letters in grammar package
  * using capital letters now produces a warning
* Fixed an issue were `setAbsent` methods in the generated SymbolBuilder where not properly overridden
* Fixed that non-shadowing scopes where not handled as intended

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

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

