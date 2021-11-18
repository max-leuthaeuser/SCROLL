package scroll.internal.compartment

import scroll.internal.graph.RoleGraphProxyApi
import scroll.internal.support.CompartmentRelationsApi
import scroll.internal.support.PlayerEqualityApi
import scroll.internal.support.RelationshipsApi
import scroll.internal.support.RoleConstraintsApi
import scroll.internal.support.RoleGroupsApi
import scroll.internal.support.RolePlayingApi
import scroll.internal.support.RoleQueriesApi
import scroll.internal.support.RoleRestrictionsApi

/** Defines the API for Compartments that implement an objectified collaboration with a limited
  * number of participating roles and a fixed scope.
  */
trait CompartmentApi {

  /** Public access to role-playing specific API */

  /** Get [[scroll.internal.graph.RoleGraphProxyApi]] API entry point.
    */
  lazy val roleGraph: RoleGraphProxyApi

  /** Get [[scroll.internal.support.RoleConstraintsApi]] API entry point.
    */
  lazy val roleConstraints: RoleConstraintsApi

  /** Get [[scroll.internal.support.RoleRestrictionsApi]] API entry point.
    */
  lazy val roleRestrictions: RoleRestrictionsApi

  /** Get [[scroll.internal.support.RolePlayingApi]] API entry point.
    */
  lazy val rolePlaying: RolePlayingApi

  /** Get [[scroll.internal.support.RoleQueriesApi]] API entry point.
    */
  lazy val roleQueries: RoleQueriesApi

  /** Get [[scroll.internal.support.CompartmentRelationsApi]] API entry point.
    */
  lazy val compartmentRelations: CompartmentRelationsApi

  /** Get [[scroll.internal.support.RelationshipsApi]] API entry point.
    */
  lazy val roleRelationships: RelationshipsApi

  /** Get [[scroll.internal.support.RoleGroupsApi]] API entry point.
    */
  lazy val roleGroups: RoleGroupsApi

  /** Get [[scroll.internal.support.PlayerEqualityApi]] API entry point.
    */
  lazy val playerEquality: PlayerEqualityApi

}
