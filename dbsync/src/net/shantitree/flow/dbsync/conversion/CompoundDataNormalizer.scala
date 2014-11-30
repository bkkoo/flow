package net.shantitree.flow.dbsync.conversion

/**
 * Created by bkkoo on 14/11/2557.
 */
case class CompoundDataNormalizer(apply: (String, Map[String, Any])=> Map[String, Any])
