(ns clotilde.core
  (:use clotilde.innards))

(defn initialize!
  "Evaluates to: nil.
  Side-effect(s): empty space, empty wait queue."
  []
  (io! (-init-local)))

(defn out!
  "expr: any expression.
  Evaluates to: nil.
  Side-effect(s): a waiting pattern-matching succeeds and is removed from queue, or space contains expr."
  [expr]
  (io! (-out expr -space -waitq) expr))

(defn rd! 
  "pattern: a valid pattern for pattern-matching (see core/match).
  Evaluates to: a pattern-matched expression from space (no ordering is assumed in space).
  Side-effect(s): if no match is found in space, then the wait queue contains a match-promise for pattern.
  In french: rd! will NOT remove the matched expression from space. 
  rd! will BLOCK untill a pattern-matching succeeds. 
  Hence, its use from the main thread/program is not common. 
  Passing rd! to eval! makes more sense, since it'll fork a new thread, 
  thus eliminating the risk of permanently blocking the main thread."
  [pattern] 
  (io! (-rdin pattern :rd -space -waitq)))

(defn in!
  "Just like rd!, but the pattern-matched expression will be removed from space."
  [pattern] 
  (io! (-rdin pattern :in -space -waitq)))

(defmacro eval!
  "exprs: one or more expressions.
  Evaluates to: nil. 
  Side-effect(s): exprs are evaluated (in an implicit do) in a new thread of execution.
  The value of (the last of) exprs is put in space."
  [& exprs]
  `(io! (future (-out (do ~@exprs) -space -waitq)) nil))
