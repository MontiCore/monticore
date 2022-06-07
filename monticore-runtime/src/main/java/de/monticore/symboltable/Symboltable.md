<!-- (c) https://github.com/MontiCore/monticore -->

# Documentation of the Symbol Table Infrastructure



## Conceptual Model of Symbol Tables
* What is a symbol? 
* What is a symbol kind? 
* What is a scope? 
* What are properties of scopes? 
* What is symbol resolution?

## Define the Symbol Table of a Language via its Grammar
* Indicate that a nonterminal defines a symbol
* Indicate that a nonterminal spans a scope
* Indicate that a nonterminal uses the name of a symbol

## Runtime Environment for Symbol Table Infrastructure
This section explains classes and interfaces that are part of the MontiCore runtime environment.

### Symbol Table Infrastructure Interfaces
Most of the interfaces of the MontiCore runtime enviroenment are super types of generated classes
or interfaces that are explained [here](#generated-symbol-table-infrastructure).

#### IScope Interface
This interface is the super type of the generated scope interfaces and thus, it is also transitive
of global scope interfaces and artifact scope interfaces. `IScope` contains signatures for methods  
realizing the scope's connection to its environment (i.e., AST classes, sub scopes, enclosing scopes).
Generated, language-specific scope interfaces refine the types of these methods. 

#### IArtifactScope Interface
The `IArtifactScope` interface is an interface that all generated language-specific artifact scope
interfaces extend. It provides an abstract method for obtaining an artifact scope's package as String.
All further methods have either language-specific arguments or return types and are, thus, introduced
in the language-specific artifact scope interfaces.

#### IGlobalScope Interface
The `IGlobalScope` interface is an interface that all generated language-specific global scope
interfaces extend. It provides an abstract method for obtaining the global scope's Modelpath.
All further methods have either language-specific arguments or return types and are, thus, introduced
in the language-specific global scope interfaces.

#### ISymbol Interface
The `ISymbol` interface is an interface that all generated language-specific symbol classes
implement. It provides the signatures for methods to obtaining the symbol's name, its package, 
its fully-qualified name, and its enclosing scope, and its AST node. 
Further, the interface contains the signatures of methods for getting and setting the access 
modifier of the symbol and default implementations for getting the source position of the symbol.
It also includes a static method for sorting a collection of symbols by their source position, which
is handy for realizing the semantics of ordered scopes. 
All further methods have either language-specific arguments or return types and are, thus, introduced
in the specific symbol classes.

#### ISymbolPredicate Interface
An `ISymbolPredicate` is a predicate of a symbol and is used for filtering the results of symbol 
resolution. This is explained in more detail in [[HR17]](https://monticore.de/MontiCore_Reference-Manual.2017.pdf).
The MontiCore runtime contains the class `IncludesAccessModifierSymbolPredicate`, which is an 
implementation of a symbol predicate for filtering symbols based on their access modifier. 

#### IScopeSpanningSymbol Interface
The `IScopeSpanningSymbol` interface extends the interface `ISymbol` and adds a method signature
for obtaining the scope that this symbol spans. Symbols that span a scope (which is the case, e.g., 
if the respective nonterminal in the grammar is annotated with both the keywords `symbol` and `scope`)
implement this interface instead of the `ISymbol` interface.  

### Modifiers
The modifiers contained in the MontiCore runtime implement the interface `AccessModifier`, which again
is a `Modifier`. Out of the box, MontiCore supports the two access modifier 
implementations `BasicAccessModifier` and `NoAccessModifier`. Further, more sophisticated access modifiers
have to be engineered individually, dedicated to their use for a specific modeling language.

### JSON Infrastructure for Symbol Table Serialization
The MontiCore runtime contains classes that are required for serializing and deserializing 
symbol tables to Json-encoded Strings. The following explains these in short:

#### JsonPrinter Class
The class `JsonPrinter` wraps the concrete syntax of Json. It is an API for building Json-encoded 
String via a series of method calls. 

#### JsonParser Class
The class `JsonParser` parses a Json-encoded String into an instance of the Json 
[abstract syntax model](#json-model). The central method of this class is the static method 
`JsonElement parse(String s)`. 

#### Json Parsing Infrastructure
Besides the `JsonParser` class, the MontiCore runtime contains more classes required
for translating JSON-encoded Strings into instances of the Json [abstract syntax model](#json-model).
The class `JsonLexer` lexes an input String into a sequence of `JsonToken` instances. 
JsonToken instances realizes tokens that have a certain kind in form of a value from the 
enumeration `JsonTokenKind`. The `NumberLexer` is able to lex all kinds of valid numbers encoded in
Json.
 
#### Json Model
The MontiCore runtime contains a model of the abstract syntax of JSON
that is used by the `JsonParser` and the `JsonPrinter` for serialization of symbol tables.
Individual classes exist for the different abstract syntax types of JSON. 

#### JsonDeSers Class
The class `JsonDeSers` contains constants and static methods that support the generated language-specific 
symbol and scope DeSer classes.


## Generated Symbol Table Infrastructure
MontiCore generates large parts of the symbol table infrastructure that is strongly typed for each
MontiCore language. The following gives a short and technical introduction of each of these
generated classes, interfaces, and enums. The concepts behind each of these infrastructure part if 
explained in the [MontiCore Reference Manual [HR17]](https://monticore.de/MontiCore_Reference-Manual.2017.pdf).

<!-- ################################################################################### -->
### Infrastructure Generated per Language
This section explains all parts of the symbol table infrastructure that MontiCore generates once per 
language.
For scopes, artifact scopes, and global scopes, MontiCore separated classes and interfaces. The 
interfaces follow the (multiple) inheritance of the grammars and realized most behavior in form 
of default method implementations. The classes implement the interface and manage access to attributes.

#### Scope Interface
For each language, MontiCore generates a scope interface. The scope interface prescribes all public 
methods of the scope class and realized some methods as default implementations. The hierarchy of 
MontiCore grammars is also reflected in the hierarchy of scope interfaces. To realize the multiple 
inheritance of MontiCore in Java, the scope interface is separated from the scope class.
If a language inherits from one or more grammars, the scope interface of the language extends all
scope interfaces of the inherited languages. Otherwise, if a language does not inherit from any 
language, the scope interface extends the `IScope` interface from the MontiCore runtime.

#### Scope Class
The scope class is generated for each MontiCore language. It implements the scope interface of the 
language and realizes scope attributes as well as method implementations that realize direct access 
to scope attributes.

#### Scope Builder Class
MontiCore generated builder classes for each scope class. The instances of the builders are available through the language's mill. With the builder, the attributes of the scope class can be initialized and a new instance of the scope can be created. 

We highly recommend instantiating scope classes only through the builder obtained via the mill. All other forms of instantiations will prohibit reconfiguration through sublanguages.

#### ArtifactScope Interface
The artifact scope interface is generated once for each MontiCore language. It extends the scope
interface of the language and the artifact scope interface of the MontiCore runtime.
Artifact scopes are instantiated once for each processed artifact and, conceptually, slightly differ
from scopes established within a model. To this end, their realization overrides some methods
of the scope interface with a special behavior and adds new methods.

#### ArtifactScope Class
MontiCore generates a single artifact scope class for each language that extends the scope class of 
the language and implements the artifact scope interface of the language. 

#### ArtifactScope Builder Class
MontiCore generated builder classes for each artifact scope class. The instances of the builders are available through the language's mill. With the builder, the attributes of the artifact scope class can be initialized and a new instance of the artifact scope can be created. 

We highly recommend instantiating artifact scope classes only through the builder obtained via the mill. All other forms of instantiations will prohibit reconfiguration through sublanguages.

#### GlobalScope Interface
Similar to artifact scope interfaces, global scope interfaces extends the scope interface of the language.
Additionally, they implement the global scope interfaces of their parent languages or the 
`IGlobalScope` of the MontiCore runtime if the languages do not inherit from another language.

#### GlobalScope Class
The global scope class is generated for each MontiCore language and realizes the concrete global
scope of a language. It extends the scope class and implements the global scope iterface of the 
language.

#### GlobalScope Builder Class
MontiCore generated builder classes for each global scope class. The instances of the builders are available through the language's mill. With the builder, the attributes of the global scope class can be initialized and a new instance of the global scope can be created. 

We highly recommend instantiating global scope classes only through the builder obtained via the mill. All other forms of instantiations will prohibit reconfiguration through sublanguages.

#### SymbolTableCreator Interface
TODO: SymbolTableCreator Interface is about to be changed

#### SymbolTableCreator Class
TODO: SymbolTableCreator Class is about to be changed

#### Common Symbol Interface
The common symbol interface of a language extends the MontiCore runtime class `ISymbol` and provides
methods for the connection to the enclosing scope and the visitor of the language. As these are specifically
typed for each language, the common symbol interface is generated. All symbol classes of a language
implement the common symbol interface. 

#### SymbolTablePrinter
The symbol table printer traverses the scope tree of an artifact using a visitor and serializes it 
in form of a JSON-encoded String. Traversal typically begins with an artifact scope. In each scope, 
the local symbols are visited and serialized. If a symbol spans a scope, the spanned scope is visited
while visiting the symbol. It, therefore, realizes traversal of the scope tree along the 
enclosingScope <-> localSymbols and the symbol <-> spannedScope associations. Symbol table printers
are used by ScopeDeSers and SymbolDeSers. For language composition, the symbol table printers of
individual languages are combined with a delegator visitor in the DeSer classes.
   
#### ScopeDeSer
The scope deser class provides methods realizing the loading and storing of scope objects of a language.
Besides this, it realizes the deserialization of scopes. The deserialization of symbols within this
scope is delegated to the respective symbol DeSers and serialization of symbols and scopes is 
delegated to the symbol table printer. The reason for this separation is that employing a visitor is 
suitable for serialization, but not for deserialization. The latter would visit elements of the 
abstract syntax of JSON, such as a Json object, and would require a large number of case distinctions
within handling different objects that can be serialized as a Json object. Combining the visitor-based
serialization and the deserialization into a single class would be inefficient in terms of compilation
time.

<!-- ################################################################################### -->
### Generated per Symbol
This section explains parts of the symbol table infrastructure that MontiCore generates once for 
each symbol of a language.

#### Symbol Class
For each symbol of the language, MontiCore generates a symbol class that implements the common 
symbol interface of the language. The symbol class realizes symbols of a certain kind. For example,
the class `StateSymbol` realizes the kind StateSymbol and objects of this class are concrete symbols.
A symbol kind can inherit from at most one other symbol kind. This is reflected in the symbol classes
by extending the class of the super kind. 

#### Symbol Builder Class
MontiCore generated builder classes for each symbol. The instances of the builders are available through
the language's mill. With the builder, the attributes of the symbol class can be initialized and a new instance of the symbol can be created. 

We highly recommend instantiating symbol classes only through the builder obtained via the mill. All other forms of instantiations will prohibit reconfiguration through sublanguages, e.g., in case the symbol production is overridden in the grammar.

#### Symbol DeSer
The symbol DeSer classes are generated for each symbol and realize serialization and deserialization
of symbols of a certain kind. The serialization is visitor-based and, thus, delegated to the symbol
table printer. Symbol DeSers are used by scope DeSers to realize the deserialization of symbols and
as such, are reused for all languages that inherit from the current language. As serialization and 
deserialization of individual symbols is rarely triggered manually, no load and store methods exist
in symbol DeSer classes.

#### Symbol Surrogate Class 
Symbol surrogate classes extend the generated symbol classes and realize lazy loading of symbls of this kind. Surrogates have a delegate of the symbol class that is empty during initialization of the surrogate., where only the enclosing scope and the name are set. They further define a method for resolving the symbol
with the on demand.
Symbol surrogates must only be if both of the following two conditions are met:
1. If on type level, a symbol has an attribute of another symbol, the attribute *may* be initialized with the surrogate as the symbol's subtype.
2. If on instance level, the symbol definition of the 
Surrogates must never be used to simplify instantiation of local symbols, i.e., of symbols that are contained in a single model for which the symbol table currently is build. In this case, it is always possible to split symbol table creation into multiple phases: In the first phase, all symbol definitions 
instantiate symbol class objects, for which the symbol attributes are not instantiated yet. In a later phase, the symbol attributes are filled with values.

#### Symbol Surrogate Builder
MontiCore generated builder classes for each symbol surrogate. The instances of the builders are available through the language's mill. With the builder, the attributes of the symbol surrogate class can be initialized and a new instance of the symbol surrogate can be created. 

#### Resolvers
MontiCore generates a resolver interface for each symbol kind of a language. Resolvers
have a method for resolving adapted symbol kinds. Language engineers can develop concrete resolving delegates
that implement a resolver interface. Such classes can be added to the global scope of a language
to integrate resolving for adapted symbols into the resolution process. 
For example, an automata language defines the generated resolver interface `IStateSymbolResolver`.
This interface can be used by language engineers to implement a `CDClass2StateResolver` class 
implementing the interface that resolves, for example, for symbols of a CD class whenever
resolving for state symbols is invoked. The result of this is typically an adapter symbol, which
adapts the foreign symbol (e.g., CDClassSymbol) to the expected symbol (e.g., StateSymbol).

<!-- ################################################################################### -->
## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
