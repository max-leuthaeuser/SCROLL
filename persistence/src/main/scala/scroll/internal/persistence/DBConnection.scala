package scroll.internal.persistence

import sorm.{InitMode, Entity, Instance}

class DBConnection(entities: Traversable[Entity],
                   url: String,
                   user: String = "",
                   password: String = "",
                   poolSize: Int = 1,
                   initMode: InitMode = InitMode.Create,
                   timeout: Int = 30) extends Instance(entities ++ Set(Entity[PersistentScalaRoleGraph.Plays]()), url, user, password, poolSize, initMode, timeout)
