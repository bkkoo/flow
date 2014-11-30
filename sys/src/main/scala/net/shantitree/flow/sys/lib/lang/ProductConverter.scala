package net.shantitree.flow.sys.lib.lang

object ProductConverter {

  //product: (String, Any) or ((String, Any), ...)

  implicit def toMap(product: Product): Map[String, Any] = {

    val arity = product.productArity
    if (arity == 2)
      // value can be (String->Any)  or ((String -> Any), (String -> Any))
      product match {
        case ((k1:String, v1), (k2:String, v2))  =>
          Map(k1->v1, k2->v2)
        case (k0:String, v0) =>
          Map(k0->v0)
        case _ =>
          throw new IllegalArgumentException(
            s"Invalid 'product' argument. \r\n" +
              s"value = $product \r\n" +
              s"(must be ((String, Any) ...) or (String, Any)) "
          )
      }
    else {
      (Range(0, product.productArity) map { i =>
        product.productElement(i) match {
          case (k:String, value) =>
            (k, value)
          case (k, value) =>
            throw new IllegalArgumentException(
              s"Invalid 'product' argument. \r\n" +
                s"value = $product \r\n" +
                s"The key part must be of 'String' type! (key='$k' and value='$value') ")
        }
      }).toMap

    }

  }
}
