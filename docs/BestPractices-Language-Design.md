<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - Designing Languages

[[_TOC_]]

[MontiCore](http://www.monticore.de) provides a number of options to design 
languages, access and modify the abstract syntax tree, and produce output files.

Some general questions on how to design a complete languages are adressed here. 

## **Designing A Language**

### Correct language vs. superset?
* When you know that the incoming model will be correct, because they are generated
  by algorithm, you can decide to pass a (slight) superset 
* This may simplify the development process for two reasons: 
  (a) you may derive a simpler grammar and (b) you may omit definition of 
  context conditions.
* But beware: (a) situations may change and manually changed models might come in
  or (b) the is adapted by an ill-behaving pre-processor or (c) the model
  may come in a wrong version.
* This applies mainly for unreadable languages, such as JSON or XML.
* Defined by: BR


### Versioning an evolving langauge?
* When languages evolve, models may become invalid, because 
  certain (now obligatory) parts are missing, or old keywords are used.
* We generally believe that a language that is made for long lasting 
  models should not embody its version in the models (i.e. like Java, C++ and 
  other GPLs and unlike XML dialects).
* When evolving a language, you should only evolve it in conservative form, i.e.
  * All new elements are optional by `.?`, `.*` or offer new alternatives `(old | new)`
  * Old elements or keywords are not simply removed, but 
    forbidden by coco warnings, marking them as deprecated for a while. 
* Downward compatibility of newer models, however, is not useful. 
  We can savely enforce developers should normally use the newest 
  versions of their tools.
* Defined by: BR



## **Language Design in the Large**


### Making Transitively Inherited Grammars Explicit?
* When the grammar inclusion hierachy becomes larger, there will be redundancy.
  In:
  ```
    grammar A { .. } ;
    grammar B extends A { .. } ;
    grammar C extends A,B { .. } ;
    grammar D extends B { .. } ;
  ```
  Grammars `C` and `D` actually include the same nonterminals.
* If `A` is made explicit, you have more information right at hand, but also
  larger grammars. It is a matter of taste.
* A recommendation: when you use nonterminals from A explicitly, then also 
  make the extension explicit. However, be consistent.


### How to Achieve Modularity (in the Sense of Decoupling)
* Modularity in general is an important design principle.
  In the case of model-based code generation, modularity involves the following 
  dimensions:
  1. Modelling languages
  2. Models
  3. Generator
  4. Generated code
  5. Runtime-Environment (RTE) including imported standard libraries
  6. Software architecture (of the overal system), software stack
* These dimensions are not orthogonal, but also not completely interelated.
  The actual organisation will depend on the form of project.
* A weak form of modularity would be to organize things in
  well understood substructures such as packages. 
  A deeper form of modularity deals with possibility for individual *reuse* 
  and thus an explicit *decoupling* of individual components. We aim for 
  decoupling (even if developed in the same git project).
* Modularity also deals with *extensibility* and *adaptation*.
* A principle for *adaptation* for the *generator*, 
  the *generated code*, and the *RTE* is to design each of them
  like a *framework* with explicit extension points.
  Extension points may be (empty) hook methods to be filled, Java interfaces
  to be implemented and their objects injected to the code e.g. via 
  factories, builders od simply method parameters.
* A principle for *modularity* for the the *generator*, 
  the *generated code*, and the *RTE* is to design parts of them as 
  independent library functions (or larger: components) that can be used if needed.
* We recommend to modularize whenever complexity overwhelms or extensibility and
  adaptability are important:
  1. MontiCore has powerful techniques for adaptation, extension and 
    composition of *modelling languages* (through their grammars). See the
    [reference manual](http://monticore.de/MontiCore_Reference-Manual.2017.pdf).
  2. MontiCore has powerful techniques for the *aggregation of models* --
    using the same principles as programming languages, namely allowing to keep 
    the models independent (and thus storable, versionable, reusable) artifacts,
    while they are semantically and through the generator technology well integrated. 
    The appropriate approach is based on *using* foreign models, e.g. through 
    `import` statements and sharing *symbol* infrastructures as described in the
    [reference manual](http://monticore.de/MontiCore_Reference-Manual.2017.pdf).
  3. The generator provides (a) many Java classes and methods that can be overridden
    (b) Freemarker templates hook points to extend and replace templates, and (c)
    can be customized using a groovy script.
    The generator iteself is often structured along the software architecture / stack,
    e.g. in frontend, application backend, data base, transport layer, etc.
  4. The generated code must be designed appropriately by the generator designer, 
    by generating builders, mills, etc. for each form of product - quite similar 
    to MontiCore itself.
    The generated code is usually structured along the components or sub-systems
    that the software architecture defines.
  5. The RTE is probably well designed if it is usable a normal framework.
* Please note: it is not easy to design modularity and extensibility from beginning.
  Framework design has shown that this is an iterative optimizing process.
  It must be avoided to design too many extension elements into the system
  from the beginning, because this adds a lot of complexity.
* Defined by: BR  

### Realizing Embedding through an Interface Nonterminal Extension Point

Consider the following scenario: 
A language `Host` defines an extension point through an interface nonterminal.

```
grammar Host { A = I*; interface I; }
```

Another language `Embedded` that has no connection to the `Host` language, 
defines a class nonterminal `E`.

```
grammar Embedded { E = "something"; }
```

MontiCore provides alternative solutions to embed the language `Embedded`
into the language `Host` at the extension point `I`. All solutions presented here
require to implement a new grammar `G` that extends the grammars `Embedded` and `Host`, 
which reuses the start nonterminal of the `Host` grammar:

```
grammar G extends Host, Embedded { start A; }
```

The connection between extension point and extension is performed by an additional
grammar rule in the grammar `G`. This can be realized in one of the following ways, each 
of which has its own advantages and disadvantages:

1. Embedding through overriding of extension rule and implementing extension point rule:
  * `E implements I;`
  * Advantage: simple embedding rule
  * Disadvantage: does not work in combination with inheritance of extension rule
  * Should therefore only be used, it `E` is not used anywhere else (= in not other language that is potentially used in combination with this language) 
2. Embedding through extending extension rule and implementing extension point rule:
  * `IE extends E implements I = "something";`
  * Advantage: does work in combination with inheritance of extension rule
  * Disadvantage: cloning of RHS of the extension rule can produce inconsistencies if `E` is changed
  * Can be used if it is assured that this rule is adjusted whenever `E` is changed, e.g., by assuming that `E` is not modified at all
3. Embedding through implementing extension point rule and providing extension on right-hand side:
  * `IE implements I = E;`
  * Advantage: does work in combination with inheritance of extension rule
  * Disadvantage: introduces new level of indirection in AST that invalidates check whether required abstract syntax (RHS of interface nonterminal) is present
  * Should therefore not be used, if the interface has a right-hand side
* Defined by: AB



## Further Information

* [Overview Best Practices](BestPractices.md)
* [MontiCore project](../../README.md) - MontiCore
* see also [**MontiCore Reference Manual**](http://www.monticore.de/)


