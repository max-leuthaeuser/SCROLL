package scroll.examples.sync

import scroll.internal.Compartment
import scroll.examples.sync.models.modelB.Family
import scroll.examples.sync.models.modelB.Member
import scroll.examples.sync.compartments.ModelBCConstructionCompartment
import scroll.examples.sync.compartments.ModelABCDestructionCompartment
import scroll.examples.sync.compartments.ModelABCConstructionCompartment
import scroll.examples.sync.compartments.ModelAIntegrationCompartment
import scroll.examples.sync.compartments.SyncCommaNames
import scroll.examples.sync.compartments.GeneralDestructor
import scroll.examples.sync.models.modelA.Male
import scroll.examples.sync.compartments.SyncSpaceNames
import scroll.examples.sync.compartments.SyncKnownList

object ExampleSync extends App {

  new Compartment {
    
    SynchronizationCompartment.changeConstructionRule(ModelBCConstructionCompartment)
    SynchronizationCompartment.changeDestructionRule(ModelABCDestructionCompartment)
    SynchronizationCompartment.addSynchronizationRule(new SyncKnownList())
    SynchronizationCompartment.addSynchronizationRule(new SyncSpaceNames())
    
    val johnson = new Family("Johnson")
    val dad = new Member("Dad", johnson, true, false, false, false)
    val mom = new Member("Mom", johnson, false, true, false, false)  
    
    mom.listen()
    dad.listen()
    
    ModelElementLists.printALL()
    
    johnson.setLastName("Smith")
    mom.listen()
    dad.listen()
    
    mom.setFirstName("Mami")
    mom.listen()
    
    dad.setFirstName("Dadi")
    dad.listen()
    
    //ModelElementLists.setAllRegisterNames("First1 Last1")
    ModelElementLists.printALL()   
    println("")
    
    //Test 1: Model integration
    SynchronizationCompartment.changeConstructionRule(ModelABCConstructionCompartment)
    //rule change not necessary
    //destruction change not necessary
    SynchronizationCompartment.integrateNewModel(ModelAIntegrationCompartment)
    
    println("Model A was integrated")
    ModelElementLists.printALL()  
    println("")
    
    johnson.setLastName("Johnson")
    
    println("Lastname to Johnson")
    ModelElementLists.printALL()  
    println("")
    
    //Test 2: Rule Delete and new Add
    SynchronizationCompartment.deleteRule("SyncNameRule1")
    SynchronizationCompartment.addSynchronizationRule(new SyncCommaNames)
    
    println("Changed Rules")
    
    ModelElementLists.changeRegisterNames()
    
    println("Changed Names")
    ModelElementLists.printALL()  
    println("")
    
    mom.setFirstName("Mom")    
    dad.setFirstName("Dad") 
    
    println("Firstname change")
    ModelElementLists.printALL()  
    println("")
    
    johnson.setLastName("Smith")
    
    println("Lastname to Smith")
    ModelElementLists.printALL()  
    println("")
        
    //Test 3: Rule change
    SynchronizationCompartment.changeRuleFromTo("SyncNameRule2", new SyncSpaceNames)
    
    ModelElementLists.changeRegisterNames()
    
    mom.setFirstName("Mami")    
    dad.setFirstName("Dadi")
    
    println("")
    ModelElementLists.printALL()  
    println("")
    
    johnson.setLastName("Johnson")
    
    println("Lastname to Johnson")
    ModelElementLists.printALL()  
    println("")
    
    mom.deleteObjectFromSynchro();
    
    println("")
    ModelElementLists.printALL()  
    println("")
    
    //TODO Proof this two steps
    
    //Test 4: Destructor change
    SynchronizationCompartment.changeDestructionRule(GeneralDestructor)
      
    dad.deleteObjectFromSynchro();
    
    println("")
    ModelElementLists.printALL()  
    println("")
    
    val per = new Male("Heinz Gunter")    
    per.listen()
        
    per.setFullName("Heinz Munter")
    per.listen()
    
    per.setFullName("Gunter Munter")
    per.listen()
    
    per.deleteObjectFromSynchro()
    per.listen()
  }
  
}