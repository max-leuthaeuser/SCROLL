package sorm.ddl

case class ForeignKey
(table: String,
 bindings: Seq[(String, String)],
 onDelete: ReferenceOption = ReferenceOption.NoAction,
 onUpdate: ReferenceOption = ReferenceOption.NoAction)
