package sorm.mappings

case class EntitySettings
(indexes: Set[Seq[String]] = Set.empty,
 uniqueKeys: Set[Seq[String]] = Set.empty)
