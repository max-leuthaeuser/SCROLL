<?xml version="1.0" encoding="UTF-8"?>
<model modelUID="r:33c0f47f-bb7e-4a8f-a997-56dcc82379eb(RoleDispatch.sandbox.model)">
  <persistence version="8" />
  <language namespace="246b4ac3-3cd0-4245-a5dc-907458b98cbf(RoleDispatch)" />
  <language namespace="f3061a53-9226-4cc5-a443-f952ceaf5816(jetbrains.mps.baseLanguage)" />
  <import index="tpck" modelUID="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" version="0" implicit="yes" />
  <import index="glc0" modelUID="r:88d1e025-2bd8-47ce-9876-3cc79a42a62c(RoleDispatch.structure)" version="6" implicit="yes" />
  <root type="glc0.Model" typeId="glc0.4706841621052048456" id="4837479777289983691" nodeInfo="ng">
    <property name="name" nameId="tpck.1169194664001" value="MinimalModel" />
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.NaturalType" typeId="glc0.4706841621052050520" id="4837479777289983736" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="SomeNatural" />
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="4837479777289983738" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="this" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="4837479777289983736" resolveInfo="SomeNatural" />
      </node>
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.NaturalType" typeId="glc0.4706841621052050520" id="4837479777289983835" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="SomeOtherNatural" />
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="4837479777289983840" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="this" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="4837479777289983835" resolveInfo="SomeOtherNatural" />
      </node>
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="4837479777289983842" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="other" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="4837479777289983736" resolveInfo="SomeNatural" />
      </node>
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.CompartmentType" typeId="glc0.4706841621052051290" id="4837479777289983902" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="SomeCompartment" />
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.Part" typeId="glc0.1609475155070818610" id="4837479777289984020" nodeInfo="ng">
        <property name="lower" nameId="glc0.1609475155070818611" value="1" />
        <property name="upper" nameId="glc0.1609475155070818613" value="1" />
        <node role="role" roleId="glc0.1609475155070819458" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="4837479777289984024" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="SomeRole" />
          <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="4837479777289984027" nodeInfo="ng">
            <property name="name" nameId="tpck.1169194664001" value="this" />
            <link role="type" roleId="glc0.4706841621051924006" targetNodeId="4837479777289984024" resolveInfo="SomeRole" />
          </node>
        </node>
      </node>
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="4837479777289983910" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="this" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="4837479777289983902" resolveInfo="SomeCompartment" />
      </node>
    </node>
  </root>
  <root type="glc0.Model" typeId="glc0.4706841621052048456" id="4837479777297445822" nodeInfo="ng">
    <property name="name" nameId="tpck.1169194664001" value="BankModel" />
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.DataType" typeId="glc0.4706841621052371215" id="4837479777297445893" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="String" />
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.DataType" typeId="glc0.4706841621052371215" id="6046698647072490666" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="MyInt" />
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.CompartmentType" typeId="glc0.4706841621052051290" id="4837479777297445898" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Bank" />
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.Part" typeId="glc0.1609475155070818610" id="4837479777297445904" nodeInfo="ng">
        <property name="lower" nameId="glc0.1609475155070818611" value="1" />
        <property name="upper" nameId="glc0.1609475155070818613" value="1" />
        <node role="role" roleId="glc0.1609475155070819458" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="4837479777297445908" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="CEO" />
        </node>
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.Part" typeId="glc0.1609475155070818610" id="4837479777297445911" nodeInfo="ng">
        <property name="lower" nameId="glc0.1609475155070818611" value="*" />
        <node role="role" roleId="glc0.1609475155070819458" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="4837479777297445917" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="Customer" />
          <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="6046698647072490565" nodeInfo="ng">
            <property name="name" nameId="tpck.1169194664001" value="name" />
            <link role="type" roleId="glc0.4706841621051924006" targetNodeId="4837479777297445893" resolveInfo="String" />
          </node>
          <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="6046698647072490567" nodeInfo="ng">
            <property name="name" nameId="tpck.1169194664001" value="age" />
            <link role="type" roleId="glc0.4706841621051924006" targetNodeId="6046698647072490666" resolveInfo="MyInt" />
          </node>
        </node>
      </node>
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="4837479777297445902" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="name" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="4837479777297445893" resolveInfo="String" />
      </node>
    </node>
  </root>
</model>

