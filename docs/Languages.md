<!-- (c) https://github.com/MontiCore/monticore -->


# MontiCore Languages of Level II - an Overview

[[_TOC_]]

[MontiCore](https://www.monticore.de) is a language workbench
with an explicit notion of language components. It uses 
grammars to describe textual DSLs. 
MontiCore uses an extended grammar format that allows to compose 
language components via inheritance, embedding and aggregation (see the 
[**handbook**](https://www.monticore.de/handbook.pdf)
for details).

A **language component** is mainly represented through 
(1) the grammar describing concrete and abstract syntax of the language, 
(2) Java-classes implementing specific functionalities, and 
(3) Freemarker-Templates helping to print a model to text.
However, language components are often identified with their main 
component grammar.

Language components are currently organized in two levels:
In this list you mainly find grammars for 
**complete (but also reusable and adaptable) languages** (Level II).
A list of
[**grammar components**](../monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
with individual reusable nonterminals is also available in
the MontiCore core project 
([development status](../00.org/Explanations/StatusOfGrammars.md)) (Level I).

The following list contains the language grammars found in the
`MontiCore` projects, such as `cd4analysis/cd4analysis`.
They are usually contained in project folders like `src/main/grammars/` 
and organized in packages like `de.monticore.cd`.
Publicly available MontiCore projects are hosted at
[`https://github.com/MontiCore`](https://github.com/MontiCore).


## List of Languages 


### [Class Diagram For Analysis (CD4A)](https://github.com/MontiCore/cd4analysis) (MontiCore stable)
* CD4A is the textual representation to describe **UML class diagrams** 
  (it uses the [UML/P](https://mbse.se-rwth.de/) variant).
* CD4A covers **classes, interfaces, inheritance, attributes with types,
  visibilities**,
  and all kinds of **associations** and **composition**, including **qualified**
  and **ordered associations**. Classes can be placed in different **packages**.
  An example:
```
classdiagram MyLife { 
  abstract class Person {
    int age;
    Date birthday;
    List<String> nickNames;
  }
  package com.universityLib {
    <<myStereotype>> class Student extends Person {
      StudentStatus status;
    }
    enum StudentStatus { ENROLLED, FINISHED; }
  }
  
  composition Person -> Address [*]  {ordered};
  association [0..2] Person (parent) <-> (child) Person [*];
  association phonebook Person [String] -> PhoneNumber ;
}
```
* CD4A focuses on the analysis phase in typical data-driven development 
  projects and is therefore mainly for data modelling.
  Consequently, it omits method signatures and complex generics.
  The primary use of the CD4A language is therefore **data modelling**. The
  CD4A language opens various possibilities for the development of data
  structures, database tables as well as data transport infrastructures in
  cloud and distributed systems.
* [Main grammar `de.monticore.cd.CD4Analysis`](https://github.com/MontiCore/cd4analysis/tree/master/cdlang/src/main/grammars/de/monticore/CD4Analysis.mc4)
  and 
  [*detailed description*](https://github.com/MontiCore/cd4analysis/tree/master/cdlang/src/main/grammars/de/monticore/cd4analysis.md)


### [Class Diagram for Code (CD4Code)](https://github.com/MontiCore/cd4analysis) (MontiCore stable)
* CD4Code describes **UML class diagrams**.
* CD4Code is a conservative extension of **CD4A**, 
  which includes method signatures. An example:
```
classdiagram MyLife2 {
  // like CD4A but also allows:
  class Person {
    protected List<Person> closestFriends(int n);
    void addFriend(Person friends...);
    <<myStereotype>> void relocate();
  }
}
```
* CD4Code is often used as tool-internal AST that allows to
  map any kind of source models to a class/attribute/method/association based
  intermediate structure, before it is printed e.g. as Java code. 
  For example a transformation sequence could be: 
  * [MontiCoreCLI](https://github.com/MontiCore/monticore/blob/opendev/monticore-generator/src/main/java/de/monticore/codegen/cd2java/_symboltable/SymbolTableCDDecorator.java): 
    Grammar -> 
    [Grammar AST encoded in CD4Code](https://github.com/MontiCore/monticore/blob/opendev/monticore-generator/src/main/java/de/monticore/codegen/mc2cd/MC2CDTransformation.java) ->
    [Decoration for custom behavior](https://github.com/MontiCore/monticore/tree/opendev/monticore-generator/src/main/java/de/monticore/codegen/cd2java/_symboltable/SymbolTableCDDecorator.java) -> 
    [Java code](https://github.com/MontiCore/monticore/tree/opendev/monticore-generator/src/main/java/de/monticore/codegen/cd2java/_symboltable/SymbolTableCDDecorator.java)
  * Statechart -> State pattern encoded in CD4Code 
  -> Decoration by monitoring methods -> Java code.
* Main grammar [`de.monticore.cd.CD4Code`](https://github.com/MontiCore/cd4analysis/tree/master/cdlang/src/main/grammars/de/monticore/CD4Code.mc4)
  and 
  [*detailed description*](https://github.com/MontiCore/cd4analysis/tree/master/cdlang/src/main/grammars/de/monticore/cd4analysis.md) 
  (see Section *CD4Code*)


### [Feature Diagrams](https://github.com/MontiCore/feature-diagram) (MontiCore stable)
* Language for feature models and feature configurations.
* **Feature diagrams** are used to model (software) **product lines** and their **variants**.
* **Feature configurations** select a subset of features of a feature model 
  to describe a product of the product line. An example:
```
featurediagram MyPhones {
  Phone -> Memory & OS & Camera? & Screen;
  Memory -> Internal & External?;
  Internal -> [1..2] of {Small, Medium, Large};
  OS -> iOS ^ Android;
  Screen -> Flexible | FullHD;

  Camera requires (iOS && External) || Android ;
}
```
  Rules `F -> ...` have a parent feature (left-hand side) 
  and its child features (right-hand side). 
  Operators are: **optional** feature `?`, **and** `&`, **or** `|`, **xor** `^`,
  and **subset cardinality** constraints, like `[1..2] of ...`.
  Further, a feature model may define cross-tree constraints using logic 
  operators **and** `&&`, **or** `||`, **implication** `requires`, etc.
* Main grammar [`FeatureDiagram`](https://github.com/MontiCore/feature-diagram/blob/master/fd-lang/src/main/grammars/de/monticore/FeatureDiagram.mc4)
  and 
  [*detailed description*](https://github.com/MontiCore/feature-diagram/blob/master/fd-lang/src/main/grammars/de/monticore/FeatureDiagram.md)



### GUI DSL: (MontiCore stable) - not publicly available (links are private)
* Language for textual definition of Graphical User Interfaces of Web
Applications
* GUI DSL covers GUI elements and relevant configuration, which include
**layout elements, widgets**, their **style definition** and **references to
data sources**.
* Language is mainly used to describe **GUI of Web Applications**. The models of
the language represents graphical views or their parts, omitting smaller details
of style definition and simplifying connection between graphical elements and
data sources.
* Currently, new version of the `GUIDSL` is being developed:
  * [Basis grammar `GUIBasis`](https://git.rwth-aachen.de/monticore/languages/gui-dsl/-/blob/dev/src/main/grammars/de/monticore/GUIBasis.mc4)
includes constructs for general visualization component definitions, control
statements and components for layout description.
  * [Example models](https://git.rwth-aachen.de/monticore/languages/gui-dsl/-/tree/dev/src/test/resources/pages/room)
can be found in the same repository.
  * [Main grammar `GUIDSL`](https://git.rwth-aachen.de/monticore/languages/gui-dsl/-/blob/dev/src/main/grammars/de/monticore/GUIDSL.mc4)
includes basic concepts and more specific implementation of component
configuration.
* In projects legacy version is currently used:
  * Examples: [**MaCoCo**](https://www.se-rwth.de/projects/MaCoCo/),
              [Ford](https://www.se-rwth.de/projects/22.09.MontiGem.Ford.Press.Release/)
  * [Main grammar `GUIDSL`](https://git.rwth-aachen.de/monticore/languages/gui-dsl/-/blob/master/src/main/grammars/GUIDSL.mc4)
includes definitions of MontiGem visualisation components, which are based on
abstract concepts, described in
[core grammar `GUIDSLCore`](https://git.rwth-aachen.de/monticore/languages/gui-dsl/-/blob/master/src/main/grammars/GUIDSLCore.mc4).
[*Detailed description*](https://git.rwth-aachen.de/monticore/languages/gui-dsl/-/blob/master/src/main/grammars/GUIDSL.md)
and
[*documentation*](https://git.rwth-aachen.de/monticore/languages/gui-dsl/wikis/home).


### [MontiCore Grammar](https://github.com/MontiCore/monticore/tree/opendev/monticore-grammar/src/main/grammars/de/monticore/grammar) (MontiCore Stable)
* Language for MontiCore Grammars itself. It can be understood as 
  *meta language*, but also used as ordinary language.
* Its main use currently: A MontiCore grammar defines the 
  **concrete syntax** and the **abstract syntax** of a textual language.
  Examples: All languages on this page are defined using MontiCore grammars
  and thus conform to this Grammar.
* Main features: Define **nonterminals** and their **productions** in EBNF, 
  **lexical token** as regular expressions. 
* Most important extensions to standard grammars:
  * **Abstract**, **interface** and **external productions** allow to
    define extensible component grammars (object-oriented grammar style).
  * Inherited productions can be redefined (overwritten) as well
    as conservatively extended.
  * Symbol and scope infrastructure is defined by simple keywords.
  * **Symbols definition** places can be introduced and 
    **symbol referencing places** defined, such that for standard cases
    automatically symbol tables can be added.
  * Additional attributes and methods can be added to the abstract syntax only.
  * Various elements, such as **semantic predicates** and **actions**
    can be defined in the same style as the underlying ANTLR.
  * MontiCore grammars can be **left recursive** and even allow mutual recursion. 
    This is e.g. useful for expression hierarchies.
  * Additional elements, such as **enum productions** and comfortable 
    operations for grammar definitions exist.
* Main grammars 
  [`de.monticore.grammar.Grammar`](https://github.com/MontiCore/monticore/tree/opendev/monticore-grammar/src/main/grammars/de/monticore/grammar/Grammar.mc4)
  defines the language with some open parameters and
  [`de.monticore.grammar.Grammar_WithConcepts`](https://github.com/MontiCore/monticore/tree/opendev/monticore-grammar/src/main/grammars/de/monticore/grammar/Grammar_WithConcepts.mc4)
  binds the external, imported expressions, method bodies, etc.
* [*Detailed description*](https://www.monticore.de/handbook.pdf)
  in the MontiCore Handbook.
  

### [JSON](https://github.com/MontiCore/json) (MontiCore Stable)
* The MontiCore language for parsing JSON artifacts. An example:
```
{ "Alice": {
    "fullname": "Alice Anderson",
    "address": {
      "postal_code": 10459, 
      "street": "Beck Street",
      "number": 56              }  },
  "Bob": { ... },
  "Caroll": { ... }, ...
}
```
* The JSON grammar adheres to the common **JSON standard** and allows parsing 
  arbitrary JSON artifacts for further processing.
* Actually the grammar represents a slight superset to the official JSON standard. 
  It is intended for parsing JSON-compliant artifacts. Further well-formedness
  checks are not included, because we assume to parse correctly produced JSON 
  documents only.
* Please note that JSON (like XML or ASCII) is primarily a carrier language.
  The concrete JSON dialect and the question, how to recreate the
  real objects / data structures, etc. behind the JSON tree structure
  is beyond this grammar, but can be applied to the AST defined here.
* Main grammar 
  [`de.monticore.lang.JSON`](https://github.com/MontiCore/json/blob/develop/src/main/grammars/de/monticore/lang/JSON.mc4)
  and 
  [*detailed description*](https://github.com/MontiCore/json/blob/develop/src/main/grammars/de/monticore/lang/json.md)


### [MontiArc](https://github.com/MontiCore/montiarc) (MontiCore Stable) 
* MontiArc is an architecture and behavior modeling language and framework 
    that provides a platform independent structure and behavior 
    modeling language with an extensible code generation framework.
* MontiArc covers **components** their **ports**, **connectors** between 
  components and  
  embedded **statecharts** for component behavior description. 
* Statecharts define states and transitions with conditions on 
  the incoming messages as well as transition actions. 
  An example:
```
component InteriorLight {                           // MontiArc language
  port in Boolean lightSignal,          // ports
       in Boolean doorSignal
       out OnOff status;
  ORGate or;                            // used subcomponents
  lightSignal -> or.a;                  // connectors
  doorSignal -> or.b;
  or.c -> cntr.signal;
  component LightController cntr {      // freshly defined subcomponent 
    port in OnOff signal,
         out OnOff status;
    statechart {                        // with behavior by a Statechart
      initial state Off / {status = OFF};
      state On;
      Off -> On [ signal == true ] / {status = ON}
      On -> Off [ signal == false ] / {status = OFF}
    }
  }
  cntr.status -> status;
}
```
* MontiArc's main goal is to provide a textual notation for Component&Connector 
  diagrams, which is used quite often in various variants in industry.
  E.g. SysML's BDD, UML's component composition diagrams use the same 
  paradigm. 
* MontiArc does not define data types for their signals, but assumes 
  that these types can be imported (e.g. from a class diagram).
* MontiArc itself also has no timing predefined, but for a complete 
  language a concrete timing, such as formally grounded by Focus, 
  should be added.
* Main grammar 
  [`MontiArc.mc4`](https://github.com/MontiCore/montiarc/blob/develop/languages/montiarc/main/grammars/MontiArc.mc4)
  and 
  [*detailed description*](https://github.com/MontiCore/montiarc/blob/develop/languages/MontiArc.md)


### [OCL/P](https://github.com/monticore/OCL) (MontiCore Stable)
* OCL/P is the textual representation of the UML OCL standard, adapted 
  with Java-like syntax.
  Its main goal is the usage in combination with other languages like 
  CD4A or Object Diagrams as an integrated part of that languages.
* OCL/P allows to define **invariants** and **pre-/post-conditions** in 
  the known OCL style plus some extensions, such as 
  a generalized `let` construction. 
  Furthermore, it offers a large set **expressions**
  to model constraints. These expressions include **Java expressions**,
  **set operations**, **list operations** etc., completely covering the 
  OCL standard concepts, but extend it e.g. by **set comprehensions** 
  known from Haskell, a **typesafe cast** or a 
  **transitive closure operator**.
  An example shows several of the above-mentioned syntactic features:
```
ocl Bookshop {
  context Shop s inv CustomerPaysBeforeNewOrder:      // invariant
    forall Customer c in s.customers:                 // quantifiers available
      c.allowedToOrder implies !exists Invoice i in s.invoices:
        i.customer == c && i.moneyPayed < i.invoiceAmount ;

  // Method specification for selling a book
  context Invoice Stock.sellBook(String iban, int discountPercent, Customer c) 
    let availableBooks =                              // set comprehension
          { book | Book book in booksInStock, book.iban == iban }
    pre:  !availableBooks.isEmpty &&                  // precondition
          c.allowedToOrder;
    post: let discount = (100 - discountPercent)/100; // postcondition, let
              b = result.soldBook                     // result variable 
          in                                        
              !(b isin booksInStock) &&
              booksInStock.size@pre == booksInStock.size + 1 &&  // @pre
              result.invoiceAmount == b.price * discount;  // result variable 
}
```

* The OCL language component contains four grammars:
  * [OCL](https://github.com/MontiCore/ocl/blob/develop/src/main/grammars/de/monticore/ocl/OCL.mc4),
  * [OCLExpressions](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/ocl/OCLExpressions.mc4), 
  * [OptionalOperators](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/ocl/OptionalOperators.mc4), and
  * [SetExpressions](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/ocl/SetExpressions.mc4). 
* The [*detailed description*](https://github.com/MontiCore/ocl/blob/develop/src/main/grammars/de/monticore/ocl/OCL.md) provides an in-depth guide for language engineers.


### [Object Diagrams](https://github.com/MontiCore/object-diagram) (MontiCore Stable)
* OD is a language for textual denotation of object diagrams. The OD language
  has several purposes (when combined with appropriate language extensions):
  1. specification language for object structures (as part of the [UML/P](https://mbse.se-rwth.de/))
  1. store and transport of data sets (e.g. the artifact analysis toolchain), and
  1. report format for the MontiCore tool infrastructure. 
* OD covers **named and anonymous objects, object types, links, attributes, attribute values, 
  lists, maps**, and
  **visibilities**. Special data types, such as **Date** allow comfortable
  definition and reading of ODs. For a comfortable definition, objects may be **nested**
  into trees while easily retaining their full graph structure. An example:
```
objectdiagram MyFamily {
  alice:Person {
    age = 29;
    cars = [
      :BMW {
        color = BLUE;
      },
      tiger:Jaguar {
        color = RED;
        length = 5.3; 
      }
    ];
  };
  bob:Person {
    nicknames = ["Bob", "Bobby", "Robert"];
    cars = [tiger];
  };
  link married alice <-> bob;
}
```
* If ODs are used as specification technique, e.g. for tests or forbidden 
  situations,
  a more expressive version of expressions can be used for values 
  (e.g. by composing ODs with JavaExpressions). Furthermore, only 
  interesting attributes need to be defined (underspecification) and conformity
  to a CD4A model can be checked.
* The ODs differ from JSON structures, e.g., in 
  the possibility to give the object a name as it is the case for `tiger`, or `alice` 
  enabling the definition real graph structures.
* Main grammars (directly usable):
    * [OD4Data](https://github.com/MontiCore/object-diagram/blob/master/src/main/grammars/de/monticore/OD4Data.mc4)
    * [OD4Development](https://github.com/MontiCore/object-diagram/blob/master/src/main/grammars/de/monticore/OD4Development.mc4)
    * [OD4Report](https://github.com/MontiCore/object-diagram/blob/master/src/main/grammars/de/monticore/OD4Report.mc4)
* Main grammar components:
    * [DateLiterals](https://github.com/MontiCore/object-diagram/blob/master/src/main/grammars/de/monticore/DateLiterals.mc4)
    * [ODBasis](https://github.com/MontiCore/object-diagram/blob/master/src/main/grammars/de/monticore/ODBasis.mc4)
    * [ODAttribute](https://github.com/MontiCore/object-diagram/blob/master/src/main/grammars/de/monticore/ODAttribute.mc4)
    * [ODLink](https://github.com/MontiCore/object-diagram/blob/master/src/main/grammars/de/monticore/ODLink.mc4)
* [*Detailed description*](https://github.com/MontiCore/object-diagram/blob/master/src/main/grammars/de/monticore/OD4Report.md) 


### [Sequence Diagrams](https://github.com/MontiCore/sequence-diagram)  (MontiCore stable) 
* A textual sequence diagram (SD) language.
* [Detailed description](https://github.com/MontiCore/sequence-diagram/blob/master/src/main/grammars/de/monticore/lang/sd4development.md)
* The project includes grammars, a symbol table infrastructure, a PrettyPrinter, 
  and various CoCos for typechecking.
* The language is divided into the two grammars SDBasis and SD4Development.
* The grammar [SDBasis](https://github.com/MontiCore/sequence-diagram/blob/master/src/main/grammars/de/monticore/lang/SDBasis.mc4) is a component grammar providing basic SD language features.
* The grammar [SD4Development](https://github.com/MontiCore/sequence-diagram/blob/master/src/main/grammars/de/monticore/lang/SD4Development.mc4) extends the grammar SDBasis with concepts used in 
  UML/P SDs.
* SD4Development supports modeling *objects*, *method calls*, *returns*, exception 
  throws, *dynamic object instantiation*, various *match modifiers* for objects 
  (free, initial, visible, complete), *lifelines* with *activation regions*,
  static method calls, intermediate 
  variable declarations by using OCL, and conditions by using OCL.
* The grammars can easily be extended by further interactions and object modifiers.
* The following depicts a simple SD in its textual syntax. 
```
sequencediagram AuctionTest {
  kupfer912: Auction;         // Interacting objects
  bidPol: BiddingPolicy;
  timePol: TimingPolicy;
                              // Interaction sequence
  kupfer912 -> bidPol  : validateBid(bid)
  bidPol -> kupfer912  : return BiddingPolicy.OK;
  kupfer912 -> timePol : newCurrentClosingTime(kupfer912, bid) 
  timePol -> kupfer912 : return t;
  assert t.timeSec == bid.time.timeSec + extensionTime;
}
```

### [SI Units](https://github.com/MontiCore/siunits) (MontiCore Stable)
* The international system of units (SI units) is a physical unit system 
  widely used in the entire world. 
  It is based on the basis units `s, m, kg, A, K, mol, cd`, 
  provides a variety of derived units, and can be refined using prefixes such 
  as `m`(milli), `k`(kilo), etc.
* The SI Unit project aims to deliver SI units to MontiCore-based 
  languages with expressions. 
  It provides a grammar for all types of SI units and prefixes usable for type 
  definition.
* Second, it provides the SI Unit literals, such as `5 km` as expression values
  and a language for SI unit types, such as `km/h` or `km/h<long>`. Some examples:
```
  km/h speed = 5 m / 27 s                         // variable definition using type km/h
  speed = (3 * 4m  +  17km/h * 10h) / 3.5h        // values with SI unit types
  °C/s<float> coolingSpeed;                       // types (°C/s) with precision (float)
  g/mm^2<int> pressure; 
  Map<Location,°C> temperatures;                  // nesting of types 
```
* The SI unit literals integrate with MontiCore's expressions and the
  SI Unit types integrate with MontiCore's type system. 
  The SI unit language remains *fully type safe*.
* The math version uses `km/h` as idealistic full precision real number, while the
  computing version allows to contrain  the precision with `km/h<long>`. 
* Main grammar components:
    * [SI units](https://github.com/MontiCore/siunits/blob/master/src/main/grammars/de/monticore/SIUnits.mc4)
    * [SI unit literals](https://github.com/MontiCore/siunits/blob/master/src/main/grammars/de/monticore/SIUnitLiterals.mc4)
    * [SI unit types for math](https://github.com/MontiCore/siunits/blob/master/src/main/grammars/de/monticore/SIUnitTypes4Math.mc4)
    * [SI unit types for computations](https://github.com/MontiCore/siunits/blob/master/src/main/grammars/de/monticore/SIUnitTypes4Computing.mc4)
    *           (other alternatives are possible; SI has not standardized anything here)
* Example projects:
    * [SI Java](https://github.com/MontiCore/siunits/blob/master/src/test/grammars/de/monticore/lang/testsijava/TestSIJava.mc4) 
* [*detailed description*](https://github.com/MontiCore/siunits/blob/master/src/main/grammars/de/monticore/SIUnits.md)  


### [Statecharts](https://github.com/MontiCore/statecharts) (MontiCore stable)
* A set of language variants for Statecharts (UML-like or also embedded SysML-like).
* It is possible to define syntactically simpler or more complex and comfortable
  forms of statecharts using a subset of the eleven provided language components.
  Two complete Statechart language variants are composed for direct usability.
* A compact teaser for one variant of the Statechart languages:
```
statechart Door {
  state Opened
  initial state Closed
  state Locked

  Opened -> Closed close() /
  Closed -> Opened open(1) / {ringTheDoorBell();}
  Closed -> Locked timeOut(n) / { lockDoor(); } [doorIsLocked]
  Locked -> Closed [isAuthorized() && doorIsLocked] unlock() /
}
```
* This example models the different states of a door: `Opened`, 
  `Closed`, and `Locked`.
  A transition is triggered e.g. by function/method call `close()` that 
  changes a from a state `Opened` to state `Closed`. 
* Transitions can have actions, such as `{ringDoorBell();}` containing in 
  this case 
  Java statements, or preconditions, such  as `[ ... ]` containing a 
  Boolean expression.
* *State invariants* and *transition preconditions* are defined using 
  `Expressions`
  and *entry/exit/transition actions* are defined using `Statements`.
* A Statechart may also have hierarchically decomposed states and 
  other forms of 
  events (not shown here).
* [*Detailed description*](https://github.com/MontiCore/statecharts/blob/dev/src/main/grammars/de/monticore/Statecharts.md) 


### SysML_2 (Alpha: Intention to become stable) - public version in preparation (links are private)
* MontiCore language components for parsing artifacts of the 
  SysML 2 language. 
  Example model:
```
package 'Vehicles' {                      // a SysML block diagram
  private import ScalarValues::*; 
  block Vehicle; 
  block Truck is Vehicle; 
  value type Torque is ISQ::TorqueValue; 
}
```
```
package 'Coffee' {                      // a SysML activity diagram
  activity BrewCoffee (in beans : CoffeeBeans, in, water : Water, out coffee : Coffee) { 
    bind grind::beans = beans;
    action grind : Grind (in beans, out powder);
    flow grind::powder to brew::powder;
    bind brew::water = water;
    action brew : Brew (in powder, in water, out coffee); 
    bind brew::coffee = coffee;
  }
}
```
* The SysML v2 grammars adhere to the general upcoming SysML v2 specification 
* Actually these grammars represents a slight superset to the official SysML v2
  standard. It is intended for parsing SysML v2-compliant models. 
  Well-formedness checks are kept to a minimum, because we assume to parse
  correctly produced SysML v2 models only.
* MontiCore's SysML v2 is a language that comes with a textual 
  representation to describe SysML v2 diagrams with respect to the standard. 
* [Main grammars](https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/tree/master/language/src/main/grammars/de/monticore/lang)
  and 
  [*public documentation*](https://github.com/MontiCore/sysmlv2/blob/master/README.md)


### Tagging (Alpha: Intention to become stable) - not publicly available (links are private)
* **Tags** are known e.g. from the UML and SysML and mainly used to add
  extra information to a model element. 
  Normally tags (and **stereotypes**) are inserted within the models,
  which over time pollutes the models, especially when different sets of
  tags are needed for different technical platforms.
* MontiCore offers a solution that **separates a model and its tags into
  distinct artifacts**. Several independent tagging artifacts
  can be added without any need to adapt the core model.
  This allows fo reuse even of fixed library models.
* The tagging artifacts are dependent on two factors:
  * First, **tags** can be added to named elements of the base model.
    It is of great help that we have an elegant symbol mechanism included 
    in the MontiCore generator.
  * Second, the set of allowed tags can be constrained, by an explicit
    definition of allowed **tag types** and **tag values** and an explicit 
    declaration on which **kinds of symbols** a tag may be attached to.
  * Consequently, tagging is not a single language, but a method to 
  **automatically and schematically derive** languages:
    * A tagging schema language TSL (dependent on the available symbol types
      of the base grammar)
    * a tagging language TL (dependent on the tag schema models written in TSL)
* Because tagging models can e.g. be used as configuration techniques 
  in a code generator, appropriate infrastructure is generated as well.
* Some [**tagging language examples**](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging-Examples)
* Although concrete languages (and their grammars) are themselves generated,
  there is a 
  [main grammar `ocl.monticore.lang.Tagging`](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging/-/blob/master/src/main/grammars/de/monticore/lang/Tagging.mc4),
  where the tagging language is derived from.
  See also [*detailed description*](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging/-/blob/master/src/main/grammars/de/monticore/lang/Tagging.md)

### [Use Case Diagrams](https://github.com/MontiCore/ucd)  (MontiCore stable) 
* A textual use case diagram (UCD) language.
* [Detailed description](https://github.com/MontiCore/ucd/blob/master/src/main/grammars/UCD.md)
* The project includes a grammar, a symbol table infrastructure, 
  and a semantic differencing operator.
* The language is defined by the grammar [UCD](https://github.com/MontiCore/ucd/blob/master/src/main/grammars/UCD.mc4).
* It supports modeling *actors*, *use cases*, *preconditions*, 
  *associations* between actors and use cases,
  *extend relations* between use cases with *guards*, 
  *include relations* between use cases, and
  *specialization relations* between actors and use cases.
* The grammars can easily be extended.
* The following depicts a simple UCD in its textual syntax. 
``` 
usecasediagram Example {
  @Player --
    Play,
    Pay,
    ChangeProfilePicture;

  @AndroidPlayer specializes Player;
  @IOSPlayer specializes Player;

  @Server --
    ShowAd,
    RegisterScore;

  ShowAd extend Play [!isPremium];
  RegisterScore extend Play;

  abstract Pay include CheckPremium;
  CreditCard specializes Pay;
  Bank specializes Pay;
  ChangeProfilePicture [isPremium];
}
```

### [XML](https://github.com/MontiCore/xml) (MontiCore Stable)
* The MontiCore language for parsing XML artifacts. An example:
```
<Calendar>
  <Appointment name="lunch">
    <Date>24.04.2020</Date>
    <Time>11:30</Time>
    <Location>cafeteria</Location>
  </Appointment>
</Calendar>
```
* The XML grammar adheres to the common **XML standard** and allows parsing 
  arbitrary XML artifacts for further processing.
* Actually the grammar represents a slight superset to the official XML standard. 
  It is intended for parsing XML-compliant artifacts. Further well-formedness
  checks are not included, because we assume to parse correctly produced XML 
  documents only.
* Please note that XML (like JSON or ASCII) is mainly a carrier language.
  The concrete XML dialect and the question, how to recreate the
  real objects / data structures, etc. behind the XML structure
  is beyond this grammar, but can be applied to the AST defined here.
* Main grammar 
  [`de.monticore.lang.XML`](https://github.com/MontiCore/xml/blob/master/src/main/grammars/de/monticore/lang/XMLLight.mc4)
  and 
  [*detailed description*](https://github.com/MontiCore/xml/blob/master/src/main/grammars/de/monticore/lang/xml.md)


### [JavaLight](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.md) (MontiCore Stable)
* This is a reduced version of the **Java language**.
  JavaLight is meant to be used to integrate simplified Java-like parts 
  in modeling languages but not to parse complete Java implementations.
* It provides Java's **attribute** and **method definitions**, 
  **statements** and **expressions**, but 
  does not provide class or interface definitions and
  also no wildcards in the type system.
* One main usage of JavaLight is in the Grammar-language to model e.g. 
  Java methods. An example:
```
public void print(String name) {
  System.out.println("Hello " + name);
}
```
* [Main grammar `de.monticore.JavaLight`](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.mc4)
  and 
  [*detailed description*](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.md).



### Java (Beta: In Stabilization) - not publicly available (links are private)
* This is the full Java' Language (as Opposed to JavaLight).
* Main Grammar [`JavaDSL`](https://git.rwth-aachen.de/monticore/javaDSL/-/blob/dev/javaDSL/src/main/grammars/de/monticore/java/JavaDSL.mc4)
  and
  [*detailed description*](https://git.rwth-aachen.de/monticore/javaDSL/-/blob/dev/javaDSL/src/main/grammars/de/monticore/java/JavaDSL.md).


## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [License definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

