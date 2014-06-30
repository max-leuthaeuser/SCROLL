<?xml version="1.0" encoding="UTF-8"?>
<model modelUID="r:33c0f47f-bb7e-4a8f-a997-56dcc82379eb(RoleDispatch.sandbox.model)">
  <persistence version="8" />
  <language namespace="246b4ac3-3cd0-4245-a5dc-907458b98cbf(RoleDispatch)" />
  <import index="tpck" modelUID="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" version="0" implicit="yes" />
  <import index="glc0" modelUID="r:88d1e025-2bd8-47ce-9876-3cc79a42a62c(RoleDispatch.structure)" version="1" implicit="yes" />
  <root type="glc0.Model" typeId="glc0.4706841621052048456" id="494922352952318450" nodeInfo="ng">
    <property name="name" nameId="tpck.1169194664001" value="SomeModel" />
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.DataType" typeId="glc0.4706841621052371215" id="494922352952318527" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Integer" />
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.DataType" typeId="glc0.4706841621052371215" id="494922352952318509" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="String" />
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.NaturalType" typeId="glc0.4706841621052050520" id="494922352952318469" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Person" />
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="494922352952318473" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="name" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="494922352952318509" resolveInfo="String" />
      </node>
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.NaturalType" typeId="glc0.4706841621052050520" id="494922352952318599" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Account" />
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="494922352952318612" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="balance" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="494922352952318527" resolveInfo="Integer" />
      </node>
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.CompartmentType" typeId="glc0.4706841621052051290" id="494922352952318534" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Bank" />
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="494922352952318806" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="Transaction" />
        <node role="operations" roleId="glc0.4706841621052048579" type="glc0.Operation" typeId="glc0.4706841621052048382" id="494922352952318816" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="execute" />
          <property name="operation" nameId="glc0.4706841621052048383" value="// do execute ..." />
          <link role="type" roleId="glc0.4706841621051924006" targetNodeId="494922352952318527" resolveInfo="Integer" />
        </node>
        <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="494922352952318814" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="value" />
          <link role="type" roleId="glc0.4706841621051924006" targetNodeId="494922352952318527" resolveInfo="Integer" />
        </node>
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="494922352952318541" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="Customer" />
        <property name="alias" nameId="tpck.1156235010670" value="Customer" />
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.RoleGroup" typeId="glc0.4706841621052051008" id="494922352952318548" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="Accounts" />
        <property name="lower" nameId="glc0.4706841621052051059" value="1" />
        <property name="upper" nameId="glc0.4706841621052051061" value="1" />
        <property name="alias" nameId="tpck.1156235010670" value="Accounts" />
        <node role="elements" roleId="glc0.4706841621052051064" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="494922352952318554" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="SavingsAccount" />
          <property name="alias" nameId="tpck.1156235010670" value="SavingsAccount" />
        </node>
        <node role="elements" roleId="glc0.4706841621052051064" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="494922352952318560" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="NormalAccount" />
          <property name="alias" nameId="tpck.1156235010670" value="NormalAccount" />
        </node>
        <node role="outgoing" roleId="glc0.4706841621052048536" type="glc0.RoleProhibition" typeId="glc0.4706841621052050961" id="494922352952318574" nodeInfo="ng">
          <link role="first" roleId="glc0.4706841621052050734" targetNodeId="494922352952318554" resolveInfo="SavingsAccount" />
          <link role="second" roleId="glc0.4706841621052050736" targetNodeId="494922352952318560" resolveInfo="NormalAccount" />
        </node>
      </node>
      <node role="relationships" roleId="glc0.4706841621052051298" type="glc0.Relationship" typeId="glc0.4706841621052051180" id="494922352952318629" nodeInfo="ng">
        <node role="second" roleId="glc0.494922352951859024" type="glc0.Place" typeId="glc0.4706841621052051117" id="494922352952318630" nodeInfo="ng">
          <property name="lower" nameId="glc0.494922352952032297" value="1" />
          <property name="upper" nameId="glc0.494922352952032299" value="*" />
          <link role="holder" roleId="glc0.4706841621052051123" targetNodeId="494922352952318560" resolveInfo="NormalAccount" />
        </node>
        <node role="first" roleId="glc0.494922352951859012" type="glc0.Place" typeId="glc0.4706841621052051117" id="494922352952318631" nodeInfo="ng">
          <property name="lower" nameId="glc0.494922352952032297" value="1" />
          <link role="holder" roleId="glc0.4706841621052051123" targetNodeId="494922352952318541" resolveInfo="Customer" />
        </node>
      </node>
      <node role="relationships" roleId="glc0.4706841621052051298" type="glc0.Relationship" typeId="glc0.4706841621052051180" id="494922352952318658" nodeInfo="ng">
        <node role="second" roleId="glc0.494922352951859024" type="glc0.Place" typeId="glc0.4706841621052051117" id="494922352952318659" nodeInfo="ng">
          <property name="lower" nameId="glc0.494922352952032297" value="*" />
          <link role="holder" roleId="glc0.4706841621052051123" targetNodeId="494922352952318554" resolveInfo="SavingsAccount" />
        </node>
        <node role="first" roleId="glc0.494922352951859012" type="glc0.Place" typeId="glc0.4706841621052051117" id="494922352952318660" nodeInfo="ng">
          <property name="lower" nameId="glc0.494922352952032297" value="1" />
          <link role="holder" roleId="glc0.4706841621052051123" targetNodeId="494922352952318541" resolveInfo="Customer" />
        </node>
      </node>
      <node role="relations" roleId="glc0.4706841621052050354" type="glc0.Fulfillment" typeId="glc0.4706841621052050779" id="494922352952318704" nodeInfo="ng">
        <link role="filler" roleId="glc0.4706841621052050780" targetNodeId="494922352952318599" resolveInfo="Account" />
        <link role="filled" roleId="glc0.4706841621052050824" targetNodeId="494922352952318548" resolveInfo="Accounts" />
      </node>
      <node role="relations" roleId="glc0.4706841621052050354" type="glc0.Fulfillment" typeId="glc0.4706841621052050779" id="494922352952318753" nodeInfo="ng">
        <link role="filled" roleId="glc0.4706841621052050824" targetNodeId="494922352952318541" resolveInfo="Customer" />
        <link role="filler" roleId="glc0.4706841621052050780" targetNodeId="494922352952318469" resolveInfo="Person" />
      </node>
      <node role="relations" roleId="glc0.4706841621052050354" type="glc0.Fulfillment" typeId="glc0.4706841621052050779" id="494922352952319078" nodeInfo="ng">
        <link role="filled" roleId="glc0.4706841621052050824" targetNodeId="494922352952318918" resolveInfo="Sender" />
        <link role="filler" roleId="glc0.4706841621052050780" targetNodeId="494922352952318599" resolveInfo="Account" />
      </node>
      <node role="relations" roleId="glc0.4706841621052050354" type="glc0.Fulfillment" typeId="glc0.4706841621052050779" id="494922352952319088" nodeInfo="ng">
        <link role="filled" roleId="glc0.4706841621052050824" targetNodeId="494922352952318923" resolveInfo="Receiver" />
        <link role="filler" roleId="glc0.4706841621052050780" targetNodeId="494922352952318599" resolveInfo="Account" />
      </node>
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.CompartmentType" typeId="glc0.4706841621052051290" id="494922352952318867" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Transaction" />
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="494922352952318918" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="Sender" />
        <property name="alias" nameId="tpck.1156235010670" value="Sender" />
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="494922352952318923" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="Receiver" />
        <property name="alias" nameId="tpck.1156235010670" value="Receiver" />
      </node>
      <node role="constraints" roleId="glc0.4706841621052051293" type="glc0.RoleProhibition" typeId="glc0.4706841621052050961" id="494922352952318927" nodeInfo="ng">
        <link role="first" roleId="glc0.4706841621052050734" targetNodeId="494922352952318918" resolveInfo="Sender" />
        <link role="second" roleId="glc0.4706841621052050736" targetNodeId="494922352952318923" resolveInfo="Receiver" />
      </node>
    </node>
  </root>
</model>

