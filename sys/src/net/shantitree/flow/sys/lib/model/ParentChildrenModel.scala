package net.shantitree.flow.sys.lib.model

abstract class ParentChildrenModel[PM <: Model,CM <:Model](val parent: PM, val children: List[CM]) extends TModel
