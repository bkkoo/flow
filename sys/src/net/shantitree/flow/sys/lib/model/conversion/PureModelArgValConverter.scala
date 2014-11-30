package net.shantitree.flow.sys.lib.model.conversion

import ModelArgValConverter.Converter
object PureModelArgValConverter extends ModelArgValConverter {
  val convert:Converter = {
    case (_, value, _) => value
  }
}
