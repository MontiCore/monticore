# Glossary on the Terms "model", "modelling", etc.

Bemerkung BR:
Insgesamt immer noch sehr viele Begriffe um das "modell" herum.
Ich habe das fegühl, dass der Versuch abstrakte Modelle von Modellen
und Modellierungssprachen abzugrenzen die Komplexität deutlich erhöht hat.
Was spräche denn dagegen zu akzeptieren, dass Modelle immer mit konkreter
Syntax versehen sind und diese dann für unsere fundamentalen
Überlegungen dann einfach zu ignorieren?
Das halbiert viele der Definitionen und Überlegungen (nicht nur zu
Modellen, sondern auch zu Viewpoints, und zu den
Universellen und Generic Models .

## Core terms

1. A *model* is an abstraction of an aspect of reality that is built 
   for a given purpose.
   * One might ad a longer discussion here about kinds of models, e.g. 
     written more explicitly in a modelling language, 
     or more implicitly; whether it is a mental model only versus a
     syntactically expressed model; prescriptive or descriptive; etc.
1. A *modeling language* defines a set of models that can be used for
   modeling purposes. Its definition consists of (1) the syntax,
   describing how models appear, (2) the semantics, describing what each
   of its models means, and (3) its pragmatics, describing how to use its
   models according to their purpose.
1. A *system model* is a model of a system (usually a system to be
   developed).
1. A *formal model* is a model that comprises concrete and abstract
   syntax. That is a *formal model* is an element of an explicitly
   defined modelling language.
   * Synonym: "explicitly represented model"?
   * Variant: A *formal model* has an explicitly, mathematically
     defined concrete and abstract syntax, semantic domain and
     semantics definition.
1. An *abstract formal model* (or in short *abstract model*)
   is a formal model without concrete syntax.
   * Option could be: only call it "abstract model" or "model
     essence". Not even sure we need that, just call thsi model and
     ignore that it has a concrete syntax.
1. A *modelling theory* is a modelling language, whose models also
   have a formal semantics, and that includes a logical calculus of
   operations that allow to manipulate, transform, and reason about these
   models in semantically useful ways.

## Language Stuff

1. The *concrete syntax* of a modelling language is used to describe the
concrete reputation of the models and is used by humans to read,
understand, and create models. The concrete syntax must be sufficiently
formal to be processable by tools.
1. The *abstract syntax* of a modeling language contains the
essential information of a model, disregarding all details of the
concrete syntax that do not contribute to the model’s purpose. It
is of particular interest for use by software tools and semantic
definitions.
1. A *metamodel* is a model describing the abstract syntax of a language.
   * Metamodels are usually defined using a class diagram,
     a grammar or a mathematical structure.
   * A metamodel describes the syntax of ALL valid models (not of a
     single particular one)
1. A *concept model* is a model informally visualizing
   the concepts and their connections described in a text.
   * Concept models are not to be developed or used by system
     developers, but to explain certain relationships on the
     method, the used models, etc.
   * Any subject occurring in the text may be visualized as a concept
     any relation as connection between concepts in such a model.
   * A concept model as to some extent and illustrative character and
     is therefore allowed to be informal.
   * Still often class diagrams are used, where boxes are concepts
     and association's are the relations between these concepts.
   * The often used *reference models* are a certain form of concept
     model, metamodels describing the abstract syntax are,
     methodological connections are.
1. *development model* is a model that is used by the developer to
   specify the system
   * as opposed to the concept models

## Development process / Method

1. An *artifact model* is a *concept model* whose model elements
  denote artifacts in the development process and their relationships.
1. A *generic model*
  * Bedeutung unklar, kann es sein, dass "concept model" das auch trifft?
1. *context model* is a development model of a systems context.
1. The *universal system model*
  * Variant A: is a concept model that describes the relevant SPES
    concepts, such as abstraction, refinement, context, SPES models,
    composition, interface, state machine, code, etc.
  * Variant B: is a concept in SPES comprising what development models
    have in common, namely context, system border, system interface
    internal structure and their semantics in focus.
    * The universal system model needs to be put to life by concrete
      modelling languages whose models play this role.
    * The universal system model is hierarchically composed.


## Classes of models

1. *interface model*
1. *architecture model*
1. *behavior model*
1. *Data model*
1. and all the classes of models mentioned for the viewpoints
  * Requirements Viewpoint: Models for system goals, UseCases and
    requirements
  * Functional Viewpoint: Models that describe system
    functionality
  * Logical Viewpoint: Models for the implementation
    independent system architecture
  * Technical Architecture Viewpoint:
    Models for the implementation related architecture,
    including special models related to software and hardware
    architectures.
