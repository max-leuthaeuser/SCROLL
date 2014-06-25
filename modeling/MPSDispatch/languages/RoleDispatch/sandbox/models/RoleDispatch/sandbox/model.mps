<?xml version="1.0" encoding="UTF-8"?>
<model modelUID="r:33c0f47f-bb7e-4a8f-a997-56dcc82379eb(RoleDispatch.sandbox.model)">
  <persistence version="8" />
  <language namespace="246b4ac3-3cd0-4245-a5dc-907458b98cbf(RoleDispatch)" />
  <import index="tpck" modelUID="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" version="0" implicit="yes" />
  <import index="glc0" modelUID="r:88d1e025-2bd8-47ce-9876-3cc79a42a62c(RoleDispatch.structure)" version="1" implicit="yes" />
  <root type="glc0.Model" typeId="glc0.4706841621052048456" id="4706841621052401153" nodeInfo="ng">
    <property name="name" nameId="tpck.1169194664001" value="Bank" />
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.DataType" typeId="glc0.4706841621052371215" id="4706841621052401154" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="String" />
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.NaturalType" typeId="glc0.4706841621052050520" id="4706841621052401159" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Person" />
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="4706841621052401163" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="name" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="4706841621052401154" resolveInfo="String" />
      </node>
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.NaturalType" typeId="glc0.4706841621052050520" id="4706841621052401207" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Account" />
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.CompartmentType" typeId="glc0.4706841621052051290" id="4706841621052401170" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Bank" />
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.RoleGroup" typeId="glc0.4706841621052051008" id="4706841621052401231" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="SomeGroup" />
        <property name="lower" nameId="glc0.4706841621052051059" value="1" />
        <property name="upper" nameId="glc0.4706841621052051061" value="n" />
        <node role="elements" roleId="glc0.4706841621052051064" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="4706841621052431369" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="SavingsAccount" />
        </node>
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="4706841621052401178" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="Customer" />
        <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="4706841621052401180" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="account" />
          <link role="type" roleId="glc0.4706841621051924006" targetNodeId="4706841621052401207" resolveInfo="Account" />
        </node>
      </node>
      <node role="relations" roleId="glc0.4706841621052050354" type="glc0.Fulfillment" typeId="glc0.4706841621052050779" id="4706841621052401216" nodeInfo="ng">
        <link role="filler" roleId="glc0.4706841621052050780" targetNodeId="4706841621052401159" resolveInfo="Person" />
        <link role="filled" roleId="glc0.4706841621052050824" targetNodeId="4706841621052401178" resolveInfo="Customer" />
      </node>
    </node>
  </root>
</model>

