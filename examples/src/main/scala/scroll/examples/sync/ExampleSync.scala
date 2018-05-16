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
    
    //Add construction and deletion rules
    SynchronizationCompartment.changeConstructionRule(ModelBCConstructionCompartment)
    SynchronizationCompartment.changeDestructionRule(ModelABCDestructionCompartment)
    //Add synchronization rules
    SynchronizationCompartment.addSynchronizationRule(new SyncKnownList())
    SynchronizationCompartment.addSynchronizationRule(new SyncSpaceNames())
    
    //Create some instances of classes
    val johnson = new Family("Johnson")
    val dad = new Member("Dad", johnson, true, false, false, false)
    val mom = new Member("Mom", johnson, false, true, false, false)  
    
    //Show related Elements
    mom.listen()
    dad.listen()
    
    //Change last Name to Smith
    johnson.setLastName("Smith")
    
    mom.listen()
    dad.listen()
    
    //Change first Name to Mami
    mom.setFirstName("Mami")
    
    mom.listen()
    
    //Change first Name to Dadi
    dad.setFirstName("Dadi")
    
    dad.listen()
    
    //Help printing
    ModelElementLists.printALL()
    
    //Test 1: Model integration
    SynchronizationCompartment.changeConstructionRule(ModelABCConstructionCompartment)
    //rule change not necessary
    //destruction change not necessary
    SynchronizationCompartment.integrateNewModel(ModelAIntegrationCompartment)
    
    println("Model A was integrated")
    ModelElementLists.printALL()
    
    //Change last name to Johnson
    johnson.setLastName("Johnson")
    
    ModelElementLists.printALL()
    
    //Test 2: Rule Delete and new Add
    SynchronizationCompartment.deleteRule("SyncNameRule1")
    SynchronizationCompartment.addSynchronizationRule(new SyncCommaNames)
    
    println("Changed Rules")
    
    ModelElementLists.changeRegisterNames()
    
    println("Changed Names")
    ModelElementLists.printALL()
    
    mom.setFirstName("Mom")    
    dad.setFirstName("Dad") 
    
    println("Firstname change")
    ModelElementLists.printALL()  
    
    johnson.setLastName("Smith")
    
    println("Lastname to Smith")
    ModelElementLists.printALL()
        
    //Test 3: Rule change
    SynchronizationCompartment.changeRuleFromTo("SyncNameRule2", new SyncSpaceNames)
    
    ModelElementLists.changeRegisterNames()
    
    mom.setFirstName("Mami")    
    dad.setFirstName("Dadi")
    
    ModelElementLists.printALL() 
    
    johnson.setLastName("Johnson")
    
    println("Lastname to Johnson")
    ModelElementLists.printALL() 
    
    mom.deleteObjectFromSynchro();
    
    ModelElementLists.printALL()  
    
    //Test 4: Destructor change
    SynchronizationCompartment.changeDestructionRule(GeneralDestructor)
      
    dad.deleteObjectFromSynchro();
    
    ModelElementLists.printALL()
  }
  
}