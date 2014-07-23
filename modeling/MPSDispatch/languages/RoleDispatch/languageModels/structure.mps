<?xml version="1.0" encoding="UTF-8"?>
<model modelUID="r:88d1e025-2bd8-47ce-9876-3cc79a42a62c(RoleDispatch.structure)" version="3">
  <persistence version="8" />
  <language namespace="c72da2b9-7cce-4447-8389-f407dc1158b7(jetbrains.mps.lang.structure)" />
  <devkit namespace="fbc25dd2-5da4-483a-8b19-70928e1b62d7(jetbrains.mps.devkit.general-purpose)" />
  <import index="tpce" modelUID="r:00000000-0000-4000-0000-011c89590292(jetbrains.mps.lang.structure.structure)" version="0" implicit="yes" />
  <import index="tpck" modelUID="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" version="0" implicit="yes" />
  <import index="glc0" modelUID="r:88d1e025-2bd8-47ce-9876-3cc79a42a62c(RoleDispatch.structure)" version="3" implicit="yes" />
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621051924005" nodeInfo="ig">
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <property name="name" nameId="tpck.1169194664001" value="TypedElement" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621051924006" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <property name="role" nameId="tpce.1071599776563" value="type" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052048560" resolveInfo="ORMType" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="4706841621052050431" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="4706841621052050327" resolveInfo="NamedElement" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052048382" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Operation" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Operation" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621051924005" resolveInfo="TypedElement" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052048430" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="params" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052048403" resolveInfo="Parameter" />
    </node>
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="4706841621052048383" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="operation" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052048403" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Parameter" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Parameter" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621051924005" resolveInfo="TypedElement" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052048444" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Attribute" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Attribute" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621051924005" resolveInfo="TypedElement" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052048456" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Model" />
    <property name="rootable" nameId="tpce.1096454100552" value="true" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="CROM" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052048457" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="elements" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050351" resolveInfo="ModelElement" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="4706841621052299607" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpck.1169194658468" resolveInfo="INamedConcept" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052048504" nodeInfo="ig">
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <property name="name" nameId="tpck.1169194664001" value="Relation" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052048535" nodeInfo="ig">
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <property name="name" nameId="tpck.1169194664001" value="RelationTarget" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052048536" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="outgoing" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052048504" resolveInfo="Relation" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052048538" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="incoming" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052048504" resolveInfo="Relation" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="4706841621052050404" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="4706841621052050327" resolveInfo="NamedElement" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052048560" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="ORMType" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052048535" resolveInfo="RelationTarget" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052048579" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="operations" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052048382" resolveInfo="Operation" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052048581" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="attributes" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052048444" resolveInfo="Attribute" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050278" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="RigidType" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052048560" resolveInfo="ORMType" />
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="4706841621052050459" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="4706841621052050351" resolveInfo="ModelElement" />
    </node>
  </root>
  <root type="tpce.InterfaceConceptDeclaration" typeId="tpce.1169125989551" id="4706841621052050327" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="NamedElement" />
    <node role="extends" roleId="tpce.1169127546356" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="4706841621052050328" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpck.1169194658468" resolveInfo="INamedConcept" />
    </node>
  </root>
  <root type="tpce.InterfaceConceptDeclaration" typeId="tpce.1169125989551" id="4706841621052050351" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="ModelElement" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052050354" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="relations" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052048504" resolveInfo="Relation" />
    </node>
    <node role="extends" roleId="tpce.1169127546356" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="4706841621052050352" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="4706841621052050327" resolveInfo="NamedElement" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050488" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Inheritance" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052048504" resolveInfo="Relation" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050517" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="NaturalInheritance" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="NaturalInheritance" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052050488" resolveInfo="Inheritance" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052050551" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="super" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050520" resolveInfo="NaturalType" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052050553" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="sub" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050520" resolveInfo="NaturalType" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050520" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="NaturalType" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Natural" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052050278" resolveInfo="RigidType" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050588" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="AntiRigidType" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052048560" resolveInfo="ORMType" />
  </root>
  <root type="tpce.InterfaceConceptDeclaration" typeId="tpce.1169125989551" id="4706841621052050622" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="AbstractRole" />
    <node role="extends" roleId="tpce.1169127546356" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="494922352953403336" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="tpck.1169194658468" resolveInfo="INamedConcept" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050657" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Constraint" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050693" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="RoleType" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Role" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052050588" resolveInfo="AntiRigidType" />
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="4706841621052050694" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="4706841621052050622" resolveInfo="AbstractRole" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050733" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="RoleConstraint" />
    <property name="abstract" nameId="tpce.4628067390765956802" value="true" />
    <property name="final" nameId="tpce.4628067390765956807" value="false" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052050657" resolveInfo="Constraint" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052050734" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="first" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050622" resolveInfo="AbstractRole" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052050736" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="second" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050622" resolveInfo="AbstractRole" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050779" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Fulfillment" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Fulfillment" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052048504" resolveInfo="Relation" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052050780" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="filler" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052048560" resolveInfo="ORMType" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052050824" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="filled" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050622" resolveInfo="AbstractRole" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050870" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="RoleImplication" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Implication" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052050733" resolveInfo="RoleConstraint" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050915" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="RoleEquivalence" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Equivalence" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052050733" resolveInfo="RoleConstraint" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052050961" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="RoleProhibition" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Prohibition" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052050733" resolveInfo="RoleConstraint" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052051008" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="RoleGroup" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="RoleGroup" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052048535" resolveInfo="RelationTarget" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052051064" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="elements" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050622" resolveInfo="AbstractRole" />
    </node>
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="4706841621052051059" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="lower" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="4706841621052051061" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="upper" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="4706841621052051009" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="4706841621052050622" resolveInfo="AbstractRole" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052051117" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Place" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Place" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="494922352952032297" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="lower" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="494922352952032299" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="upper" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052051123" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="holder" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050693" resolveInfo="RoleType" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052051180" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Relationship" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Relationship" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052048504" resolveInfo="Relation" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="494922352951859024" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="second" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052051117" resolveInfo="Place" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="494922352951859012" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="first" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052051117" resolveInfo="Place" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="1609475155070813800" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="4706841621052050327" resolveInfo="NamedElement" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052051290" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="CompartmentType" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="Compartment" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052050278" resolveInfo="RigidType" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052051293" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="constraints" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050657" resolveInfo="Constraint" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052051295" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="parts" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="1609475155070818610" resolveInfo="Part" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="4706841621052051298" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="relationships" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="0..n" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052051180" resolveInfo="Relationship" />
    </node>
    <node role="implements" roleId="tpce.1169129564478" type="tpce.InterfaceConceptReference" typeId="tpce.1169127622168" id="4706841621052355569" nodeInfo="ig">
      <link role="intfc" roleId="tpce.1169127628841" targetNodeId="4706841621052050622" resolveInfo="AbstractRole" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="4706841621052371215" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="DataType" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="DataType" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052050278" resolveInfo="RigidType" />
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="1609475155069615932" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="RoleInheritance" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="RoleInheritance" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052050488" resolveInfo="Inheritance" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="1609475155069615933" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="super" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050693" resolveInfo="RoleType" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="1609475155069615934" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="sub" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050693" resolveInfo="RoleType" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="1609475155070657416" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="ComparmentInheritance" />
    <property name="conceptAlias" nameId="tpce.5092175715804935370" value="CompartmentInheritance" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="4706841621052050488" resolveInfo="Inheritance" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="1609475155070657484" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="sub" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052051290" resolveInfo="CompartmentType" />
    </node>
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="1609475155070657486" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="reference" />
      <property name="role" nameId="tpce.1071599776563" value="super" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052051290" resolveInfo="CompartmentType" />
    </node>
  </root>
  <root type="tpce.ConceptDeclaration" typeId="tpce.1071489090640" id="1609475155070818610" nodeInfo="ig">
    <property name="name" nameId="tpck.1169194664001" value="Part" />
    <link role="extends" roleId="tpce.1071489389519" targetNodeId="tpck.1133920641626" resolveInfo="BaseConcept" />
    <node role="linkDeclaration" roleId="tpce.1071489727083" type="tpce.LinkDeclaration" typeId="tpce.1071489288298" id="1609475155070819458" nodeInfo="ig">
      <property name="metaClass" nameId="tpce.1071599937831" value="aggregation" />
      <property name="role" nameId="tpce.1071599776563" value="role" />
      <property name="sourceCardinality" nameId="tpce.1071599893252" value="1" />
      <link role="target" roleId="tpce.1071599976176" targetNodeId="4706841621052050622" resolveInfo="AbstractRole" />
    </node>
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="1609475155070818611" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="lower" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
    <node role="propertyDeclaration" roleId="tpce.1071489727084" type="tpce.PropertyDeclaration" typeId="tpce.1071489288299" id="1609475155070818613" nodeInfo="ig">
      <property name="name" nameId="tpck.1169194664001" value="upper" />
      <link role="dataType" roleId="tpce.1082985295845" targetNodeId="tpck.1082983041843" resolveInfo="string" />
    </node>
  </root>
</model>

