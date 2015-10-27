package scroll.benchmarks.models

import scroll.internal.annotations.Role
import scroll.internal.Compartment

object SEATS {

  class Airport(id: Int, code: String, name: String)

  class Country(id: Int, code: String, name: String, code_2: String)

  class Plane(id: Int, name: String)

  class Company(id: Int, name: String, co_id: Int)

  class Person(id: Int)

  class Flight(id: Int) extends Compartment {

    import Relationship._

    val distance_between = Relationship("distance_between").from[ArrivalAirport](1).to[DepartureAirport](1)
    val flying_to = Relationship("flying_to").from[PlaneInComission](0 To Many()).to[DepartureAirport](1)
    val depart_from = Relationship("depart_from").from[PlaneInComission](0 To Many()).to[DepartureAirport](1)
    val owns = Relationship("owns").from[Airline](1).to[PlaneInComission](1 To Many())
    val reserves = Relationship("reserves").from[Customer](1 To Many()).to[PlaneInComission](1 To Many())
    val is_frequent_flyer = Relationship("is_frequent_flyer").from[Customer](0 To Many()).to[Airline](0 To 1)

    @Role
    class Airline

    @Role
    class Customer

    @Role
    class PlaneInComission

    @Role
    class DepartureAirport

    @Role
    class ArrivalAirport

  }

}
