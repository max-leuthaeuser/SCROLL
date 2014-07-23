<?xml version="1.0" encoding="UTF-8"?>
<model modelUID="r:33c0f47f-bb7e-4a8f-a997-56dcc82379eb(RoleDispatch.sandbox.model)">
  <persistence version="8" />
  <language namespace="246b4ac3-3cd0-4245-a5dc-907458b98cbf(RoleDispatch)" />
  <language namespace="f3061a53-9226-4cc5-a443-f952ceaf5816(jetbrains.mps.baseLanguage)" />
  <import index="tpck" modelUID="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" version="0" implicit="yes" />
  <import index="glc0" modelUID="r:88d1e025-2bd8-47ce-9876-3cc79a42a62c(RoleDispatch.structure)" version="2" implicit="yes" />
  <root type="glc0.Model" typeId="glc0.4706841621052048456" id="1609475155070506672" nodeInfo="ng">
    <property name="name" nameId="tpck.1169194664001" value="BankModel" />
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.DataType" typeId="glc0.4706841621052371215" id="1609475155070506795" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="String" />
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.DataType" typeId="glc0.4706841621052371215" id="1609475155070506832" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Integer" />
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.NaturalType" typeId="glc0.4706841621052050520" id="1609475155070506840" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Person" />
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.NaturalType" typeId="glc0.4706841621052050520" id="1609475155070506850" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Account" />
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="1609475155070510884" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="balance" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="1609475155070506832" resolveInfo="Integer" />
      </node>
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.CompartmentType" typeId="glc0.4706841621052051290" id="1609475155070506897" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Bank" />
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="1609475155070586690" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="Customer" />
        <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="1609475155070586698" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="id" />
          <link role="type" roleId="glc0.4706841621051924006" targetNodeId="1609475155070506832" resolveInfo="Integer" />
        </node>
      </node>
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="1609475155070506940" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="name" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="1609475155070506795" resolveInfo="String" />
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.RoleGroup" typeId="glc0.4706841621052051008" id="1609475155070506942" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="SpecialAccounts" />
        <property name="lower" nameId="glc0.4706841621052051059" value="1" />
        <property name="upper" nameId="glc0.4706841621052051061" value="1" />
        <property name="alias" nameId="tpck.1156235010670" value="SpecialAccount" />
        <node role="elements" roleId="glc0.4706841621052051064" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="1609475155070506946" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="SavingsAccount" />
          <node role="operations" roleId="glc0.4706841621052048579" type="glc0.Operation" typeId="glc0.4706841621052048382" id="1609475155070506949" nodeInfo="ng">
            <property name="name" nameId="tpck.1169194664001" value="deposit" />
            <property name="operation" nameId="glc0.4706841621052048383" value="// do stuff" />
            <link role="type" roleId="glc0.4706841621051924006" targetNodeId="1609475155070506832" resolveInfo="Integer" />
            <node role="params" roleId="glc0.4706841621052048430" type="glc0.Parameter" typeId="glc0.4706841621052048403" id="1609475155070506951" nodeInfo="ng">
              <property name="name" nameId="tpck.1169194664001" value="amount" />
              <link role="type" roleId="glc0.4706841621051924006" targetNodeId="1609475155070506832" resolveInfo="Integer" />
            </node>
          </node>
        </node>
        <node role="elements" roleId="glc0.4706841621052051064" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="1609475155070510903" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="BonusAccount" />
          <property name="alias" nameId="tpck.1156235010670" value="BonusAccount" />
        </node>
      </node>
      <node role="relations" roleId="glc0.4706841621052050354" type="glc0.Fulfillment" typeId="glc0.4706841621052050779" id="1609475155070586518" nodeInfo="ng">
        <link role="filled" roleId="glc0.4706841621052050824" targetNodeId="1609475155070506942" resolveInfo="SpecialAccounts" />
        <link role="filler" roleId="glc0.4706841621052050780" targetNodeId="1609475155070506850" resolveInfo="Account" />
      </node>
      <node role="relations" roleId="glc0.4706841621052050354" type="glc0.Fulfillment" typeId="glc0.4706841621052050779" id="1609475155070586523" nodeInfo="ng">
        <link role="filled" roleId="glc0.4706841621052050824" targetNodeId="1609475155070510903" resolveInfo="BonusAccount" />
        <link role="filler" roleId="glc0.4706841621052050780" targetNodeId="1609475155070506850" resolveInfo="Account" />
      </node>
      <node role="relationships" roleId="glc0.4706841621052051298" type="glc0.Relationship" typeId="glc0.4706841621052051180" id="1609475155070586729" nodeInfo="ng">
        <node role="second" roleId="glc0.494922352951859024" type="glc0.Place" typeId="glc0.4706841621052051117" id="1609475155070586730" nodeInfo="ng">
          <property name="lower" nameId="glc0.494922352952032297" value="*" />
          <link role="holder" roleId="glc0.4706841621052051123" targetNodeId="1609475155070506946" resolveInfo="SavingsAccount" />
        </node>
        <node role="first" roleId="glc0.494922352951859012" type="glc0.Place" typeId="glc0.4706841621052051117" id="1609475155070586731" nodeInfo="ng">
          <property name="lower" nameId="glc0.494922352952032297" value="1" />
          <link role="holder" roleId="glc0.4706841621052051123" targetNodeId="1609475155070586690" resolveInfo="Customer" />
        </node>
      </node>
      <node role="relationships" roleId="glc0.4706841621052051298" type="glc0.Relationship" typeId="glc0.4706841621052051180" id="1609475155070586717" nodeInfo="ng">
        <node role="second" roleId="glc0.494922352951859024" type="glc0.Place" typeId="glc0.4706841621052051117" id="1609475155070586718" nodeInfo="ng">
          <property name="lower" nameId="glc0.494922352952032297" value="*" />
          <link role="holder" roleId="glc0.4706841621052051123" targetNodeId="1609475155070510903" resolveInfo="BonusAccount" />
        </node>
        <node role="first" roleId="glc0.494922352951859012" type="glc0.Place" typeId="glc0.4706841621052051117" id="1609475155070586719" nodeInfo="ng">
          <property name="lower" nameId="glc0.494922352952032297" value="1" />
          <link role="holder" roleId="glc0.4706841621052051123" targetNodeId="1609475155070586690" resolveInfo="Customer" />
        </node>
      </node>
    </node>
  </root>
</model>

