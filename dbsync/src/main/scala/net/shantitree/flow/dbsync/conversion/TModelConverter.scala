package net.shantitree.flow.dbsync.conversion

import net.shantitree.flow.sys.lib.model.TModel

trait TModelConverter[M <: TModel] {
  def convertPRow(pRow: Product): M
  def convert(pRows: List[Product]): List[M] = pRows.map(convertPRow)
  def apply(pRows: List[Product]): List[M] = pRows.map(convertPRow)
}
