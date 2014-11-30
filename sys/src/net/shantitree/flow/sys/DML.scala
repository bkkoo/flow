package net.shantitree.flow.sys

import net.shantitree.flow.sys.lib.model.{Model, ModelDef, TModel, TModelDef}

abstract class DML[M <: Model](implicit val modelDef: ModelDef[M])
abstract class TDML[M <: TModel](implicit val modelDef: TModelDef[M])
