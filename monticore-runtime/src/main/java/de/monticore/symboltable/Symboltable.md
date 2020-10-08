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

## Generated Symbol Table Infrastructure
MontiCore generates large parts of the symbol table infrastructure that is strongly typed for each
MontiCore language. The following gives a short and technical introduction of each of these
generated classes, interfaces, and enums. The concepts behind each of these infrastructure part if 
explained in the [MontiCore Reference Manual [HR17]](http://monticore.de/MontiCore_Reference-Manual.2017.pdf).

<!-- ################################################################################### -->
### Infrastructure Generated per Language
This section explains all prt of the symbol table infrastructure that MontiCore generated once per 
language.
%
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

#### ArtifactScope Interface
The artifact scope interface is generated once for each MontiCore language. It extends the scope
interface of the language and the artifact scope interface of the MontiCore runtime.
Artifact scopes are instantiated once for each processed artifact and, conceptually, slightly differ
from scopes established within a model. To this end, their realization overrides some methods
of the scope interface with a special behavior and adds new methods.

#### ArtifactScope Class
MontiCore generates a single artifact scope class for each language that extends the scope class of 
the language and implements the artifact scope interface of the language. 

#### GlobalScope Interface
Similar to artifact scope interfaces, global scope interfaces extends the scope interface of the language.
Additionally, they implement the global scope interfaces of their parent languages or the 
`IGlobalScope` of the MontiCore runtime if the languages do not inherit from another language.

#### GlobalScope Class
The global scope class is generated for each MontiCore language and realizes the concrete global
scope of a language. It extends the scope class and implements the global scope iterface of the 
language.

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

#### Symbol DeSer
The symbol DeSer classes are generated for each symbol and realize serialization and deserialization
of symbols of a certain kind. The serialization is visitor-based and, thus, delegated to the symbol
table printer. Symbol DeSers are used by scope DeSers to realize the deserialization of symbols and
as such, are reused for all languages that inherit from the current language. As serialization and 
deserialization of individual symbols is rarely triggered manually, no load and store methods exist
in symbol DeSer classes.

#### ResolvingDelegates
MontiCore generates a resolving delegate interface for each symbol kind of a language. Resolving delegates
have a method for resolving adapted symbol kinds. Language engineers can develop concrete resolving delegates
that implement a resolving delegate interface. Such classes can be added to the global scope of a language
to integrate resolving for adapted symbols into the resolution process. 
For example, an automata language defines the generated resolving delegate interface `IStateSymbolResolvingDelegate`.
This interface can be used by language engineers to implement a `CDClass2StateResolvingDelegate` class 
implementing the interface that resolves, for example, for symbols of a CD class whenever
resolving for state symbols is invoked. The result of this is typically an adapter symbol, which
adapts the foreign symbol (e.g., CDClassSymbol) to the expected symbol (e.g., StateSymbol).

<!-- ################################################################################### -->
## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)

* [**List of languages**](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)

