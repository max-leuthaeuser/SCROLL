<?xml version="1.0" encoding="UTF-8"?>
<model modelUID="r:33c0f47f-bb7e-4a8f-a997-56dcc82379eb(RoleDispatch.sandbox.model)">
  <persistence version="8" />
  <language namespace="246b4ac3-3cd0-4245-a5dc-907458b98cbf(RoleDispatch)" />
  <language namespace="f3061a53-9226-4cc5-a443-f952ceaf5816(jetbrains.mps.baseLanguage)" />
  <import index="tpck" modelUID="r:00000000-0000-4000-0000-011c89590288(jetbrains.mps.lang.core.structure)" version="0" implicit="yes" />
  <import index="glc0" modelUID="r:88d1e025-2bd8-47ce-9876-3cc79a42a62c(RoleDispatch.structure)" version="3" implicit="yes" />
  <root type="glc0.Model" typeId="glc0.4706841621052048456" id="1609475155071102905" nodeInfo="ng">
    <property name="name" nameId="tpck.1169194664001" value="BankModel" />
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.DataType" typeId="glc0.4706841621052371215" id="1609475155071102908" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="String" />
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.DataType" typeId="glc0.4706841621052371215" id="1609475155071102914" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Integer" />
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.NaturalType" typeId="glc0.4706841621052050520" id="1609475155071102922" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Person" />
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="1609475155071102927" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="name" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="1609475155071102908" resolveInfo="String" />
      </node>
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.NaturalType" typeId="glc0.4706841621052050520" id="1609475155071102935" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Account" />
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="1609475155071102942" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="balance" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="1609475155071102914" resolveInfo="Integer" />
      </node>
    </node>
    <node role="elements" roleId="glc0.4706841621052048457" type="glc0.CompartmentType" typeId="glc0.4706841621052051290" id="1609475155071102959" nodeInfo="ng">
      <property name="name" nameId="tpck.1169194664001" value="Bank" />
      <node role="attributes" roleId="glc0.4706841621052048581" type="glc0.Attribute" typeId="glc0.4706841621052048444" id="1609475155071102968" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="name" />
        <link role="type" roleId="glc0.4706841621051924006" targetNodeId="1609475155071102908" resolveInfo="String" />
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.Part" typeId="glc0.1609475155070818610" id="1609475155071898735" nodeInfo="ng">
        <property name="lower" nameId="glc0.1609475155070818611" value="*" />
        <node role="role" roleId="glc0.1609475155070819458" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="1609475155071898739" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="Customer" />
          <property name="alias" nameId="tpck.1156235010670" value="Customer" />
        </node>
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.Part" typeId="glc0.1609475155070818610" id="1609475155075218743" nodeInfo="ng">
        <property name="lower" nameId="glc0.1609475155070818611" value="1" />
        <property name="upper" nameId="glc0.1609475155070818613" value="1" />
        <node role="role" roleId="glc0.1609475155070819458" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="1609475155075218749" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="CEO" />
          <property name="alias" nameId="tpck.1156235010670" value="CEO" />
        </node>
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.Part" typeId="glc0.1609475155070818610" id="1609475155075218777" nodeInfo="ng">
        <property name="lower" nameId="glc0.1609475155070818611" value="1" />
        <property name="upper" nameId="glc0.1609475155070818613" value="*" />
        <node role="role" roleId="glc0.1609475155070819458" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="1609475155075218801" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="Employee" />
          <property name="alias" nameId="tpck.1156235010670" value="Employee" />
        </node>
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.Part" typeId="glc0.1609475155070818610" id="1609475155075218804" nodeInfo="ng">
        <property name="lower" nameId="glc0.1609475155070818611" value="1" />
        <property name="upper" nameId="glc0.1609475155070818613" value="5" />
        <node role="role" roleId="glc0.1609475155070819458" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="1609475155075218834" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="Programmer" />
          <property name="alias" nameId="tpck.1156235010670" value="Progammer" />
        </node>
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.Part" typeId="glc0.1609475155070818610" id="1609475155079413812" nodeInfo="ng">
        <property name="lower" nameId="glc0.1609475155070818611" value="1" />
        <property name="upper" nameId="glc0.1609475155070818613" value="1" />
        <node role="role" roleId="glc0.1609475155070819458" type="glc0.RoleType" typeId="glc0.4706841621052050693" id="1609475155079413829" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="Consultant" />
        </node>
      </node>
      <node role="parts" roleId="glc0.4706841621052051295" type="glc0.Part" typeId="glc0.1609475155070818610" id="1609475155075219403" nodeInfo="ng">
        <property name="lower" nameId="glc0.1609475155070818611" value="3" />
        <node role="role" roleId="glc0.1609475155070819458" type="glc0.RoleGroup" typeId="glc0.4706841621052051008" id="1609475155075219415" nodeInfo="ng">
          <property name="name" nameId="tpck.1169194664001" value="Employees" />
          <property name="lower" nameId="glc0.4706841621052051059" value="1" />
          <node role="elements" roleId="glc0.4706841621052051064" type="glc0.AbstractRoleName" typeId="glc0.1609475155074662706" id="1609475155075219421" nodeInfo="ng">
            <link role="role" roleId="glc0.1609475155074662709" targetNodeId="1609475155075218749" resolveInfo="CEO" />
          </node>
          <node role="elements" roleId="glc0.4706841621052051064" type="glc0.AbstractRoleName" typeId="glc0.1609475155074662706" id="1609475155075219426" nodeInfo="ng">
            <link role="role" roleId="glc0.1609475155074662709" targetNodeId="1609475155075218801" resolveInfo="Employee" />
          </node>
          <node role="elements" roleId="glc0.4706841621052051064" type="glc0.AbstractRoleName" typeId="glc0.1609475155074662706" id="1609475155075219433" nodeInfo="ng">
            <link role="role" roleId="glc0.1609475155074662709" targetNodeId="1609475155075218834" resolveInfo="Programmer" />
          </node>
        </node>
      </node>
      <node role="constraints" roleId="glc0.4706841621052051293" type="glc0.RoleImplication" typeId="glc0.4706841621052050870" id="1609475155076076957" nodeInfo="ng">
        <link role="first" roleId="glc0.4706841621052050734" targetNodeId="1609475155075218749" resolveInfo="CEO" />
        <link role="second" roleId="glc0.4706841621052050736" targetNodeId="1609475155075218801" resolveInfo="Employee" />
      </node>
      <node role="constraints" roleId="glc0.4706841621052051293" type="glc0.RoleProhibition" typeId="glc0.4706841621052050961" id="1609475155071901378" nodeInfo="ng">
        <link role="second" roleId="glc0.4706841621052050736" targetNodeId="1609475155071898739" resolveInfo="Customer" />
        <link role="first" roleId="glc0.4706841621052050734" targetNodeId="1609475155075218801" resolveInfo="Employee" />
      </node>
      <node role="constraints" roleId="glc0.4706841621052051293" type="glc0.ComplexConstraint" typeId="glc0.1609475155072168595" id="1609475155074940737" nodeInfo="ng">
        <property name="expression" nameId="glc0.1609475155072168671" value="// some expression" />
        <node role="targets" roleId="glc0.1609475155074662588" type="glc0.AbstractRoleName" typeId="glc0.1609475155074662706" id="1609475155075218866" nodeInfo="ng">
          <link role="role" roleId="glc0.1609475155074662709" targetNodeId="1609475155075218749" resolveInfo="CEO" />
        </node>
        <node role="targets" roleId="glc0.1609475155074662588" type="glc0.AbstractRoleName" typeId="glc0.1609475155074662706" id="1609475155075218870" nodeInfo="ng">
          <link role="role" roleId="glc0.1609475155074662709" targetNodeId="1609475155075218834" resolveInfo="Programmer" />
        </node>
        <node role="targets" roleId="glc0.1609475155074662588" type="glc0.AbstractRoleName" typeId="glc0.1609475155074662706" id="1609475155075218874" nodeInfo="ng">
          <link role="role" roleId="glc0.1609475155074662709" targetNodeId="1609475155075218801" resolveInfo="Employee" />
        </node>
      </node>
      <node role="relations" roleId="glc0.4706841621052050354" type="glc0.Fulfillment" typeId="glc0.4706841621052050779" id="1609475155071901427" nodeInfo="ng">
        <link role="filler" roleId="glc0.4706841621052050780" targetNodeId="1609475155071102922" resolveInfo="Person" />
        <link role="filled" roleId="glc0.4706841621052050824" targetNodeId="1609475155075218749" resolveInfo="CEO" />
      </node>
      <node role="relations" roleId="glc0.4706841621052050354" type="glc0.Fulfillment" typeId="glc0.4706841621052050779" id="1609475155071901432" nodeInfo="ng">
        <link role="filler" roleId="glc0.4706841621052050780" targetNodeId="1609475155071102922" resolveInfo="Person" />
        <link role="filled" roleId="glc0.4706841621052050824" targetNodeId="1609475155075218801" resolveInfo="Employee" />
      </node>
      <node role="relations" roleId="glc0.4706841621052050354" type="glc0.Fulfillment" typeId="glc0.4706841621052050779" id="1609475155071901440" nodeInfo="ng">
        <link role="filler" roleId="glc0.4706841621052050780" targetNodeId="1609475155071102922" resolveInfo="Person" />
        <link role="filled" roleId="glc0.4706841621052050824" targetNodeId="1609475155075218834" resolveInfo="Programmer" />
      </node>
      <node role="relationships" roleId="glc0.4706841621052051298" type="glc0.Relationship" typeId="glc0.4706841621052051180" id="1609475155071901466" nodeInfo="ng">
        <property name="name" nameId="tpck.1169194664001" value="makesAngry" />
        <node role="second" roleId="glc0.494922352951859024" type="glc0.Place" typeId="glc0.4706841621052051117" id="1609475155071901467" nodeInfo="ng">
          <property name="lower" nameId="glc0.494922352952032297" value="*" />
          <link role="holder" roleId="glc0.4706841621052051123" targetNodeId="1609475155075218834" resolveInfo="Programmer" />
        </node>
        <node role="first" roleId="glc0.494922352951859012" type="glc0.Place" typeId="glc0.4706841621052051117" id="1609475155071901468" nodeInfo="ng">
          <property name="lower" nameId="glc0.494922352952032297" value="1" />
          <link role="holder" roleId="glc0.4706841621052051123" targetNodeId="1609475155075218749" resolveInfo="CEO" />
        </node>
      </node>
    </node>
  </root>
</model>

