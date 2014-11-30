package net.shantitree.flow.sys.lib.lang.bool

/**
 * Created by bkkoo on 18/10/2557.
 */
object Bool {
  type &&[A <: Bool, B <: Bool] = A#If[B, False, Bool]
  type || [A <: Bool, B <: Bool] = A#If[True, B, Bool]
  type Not[A <: Bool] = A#If[False, True, Bool]
}
sealed trait Bool {
  type If[T <: Up, F <: Up, Up] <: Up
}

sealed trait True extends Bool {
  type If[T <: Up, F <: Up, Up] = T
}

sealed trait False extends Bool {
  type If[T <: Up, F <: Up, Up] = F
}
