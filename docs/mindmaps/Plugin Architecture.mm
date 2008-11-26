<map version="0.8.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1227600074956" ID="Freemind_Link_1924919528" MODIFIED="1227600084515" TEXT="Plugin Architecture">
<node CREATED="1227600121916" ID="Freemind_Link_706919224" MODIFIED="1227600126562" POSITION="left" TEXT="deployment">
<node CREATED="1227601035290" ID="Freemind_Link_1045065889" MODIFIED="1227601037442" TEXT="structure">
<node CREATED="1227600087484" ID="_" MODIFIED="1227600100368" TEXT="each plugin controlled by XML file"/>
<node CREATED="1227600100891" ID="Freemind_Link_261759158" MODIFIED="1227600113436" TEXT="each plugin contained in jars on the classpath">
<node CREATED="1227600113899" ID="Freemind_Link_1754183359" MODIFIED="1227600117229" TEXT="what about ruby?"/>
</node>
</node>
<node CREATED="1227601038138" ID="Freemind_Link_238571415" MODIFIED="1227601040582" TEXT="installation"/>
<node CREATED="1227601040970" ID="Freemind_Link_305616203" MODIFIED="1227601044463" TEXT="enable/disable"/>
<node CREATED="1227601044851" ID="Freemind_Link_1834626865" MODIFIED="1227601048343" TEXT="uninstallation"/>
</node>
<node CREATED="1227600138008" ID="Freemind_Link_1721291725" MODIFIED="1227600143838" POSITION="right" TEXT="extension points">
<node CREATED="1227600144805" ID="Freemind_Link_793134273" MODIFIED="1227600149718" TEXT="schema">
<node CREATED="1227600150501" ID="Freemind_Link_1211466078" MODIFIED="1227600161870" TEXT="versions - each plugin defines its version"/>
</node>
<node CREATED="1227600163961" ID="Freemind_Link_766411110" MODIFIED="1227600167650" TEXT="db creation">
<node CREATED="1227600206795" ID="Freemind_Link_738014039" MODIFIED="1227600210084" TEXT="gui">
<node CREATED="1227600210835" ID="Freemind_Link_799288828" MODIFIED="1227600220488" TEXT="can add wizard pages"/>
</node>
<node CREATED="1227600276282" ID="Freemind_Link_472169724" MODIFIED="1227600280652" TEXT="access factory">
<node CREATED="1227600299898" ID="Freemind_Link_1405459232" MODIFIED="1227600324414" TEXT="can define DB objects (tables, views, etc.) for plugin&apos;s own use"/>
<node CREATED="1227600282422" ID="Freemind_Link_1444953340" MODIFIED="1227600297454" TEXT="can provide DAOs for plugin&apos;s own tables"/>
</node>
<node CREATED="1227600903809" ID="Freemind_Link_1281857975" MODIFIED="1227600906385" TEXT="extra files">
<node CREATED="1227600907211" ID="Freemind_Link_1053287442" MODIFIED="1227600920543" TEXT="should plugins be given the db path?">
<node CREATED="1227600921670" ID="Freemind_Link_1166727473" MODIFIED="1227600929990" TEXT="so they can create non-db files"/>
<node CREATED="1227600930366" ID="Freemind_Link_10451079" MODIFIED="1227600934182" TEXT="in the db directory"/>
<node CREATED="1227600935142" ID="Freemind_Link_1241263677" MODIFIED="1227600941625" TEXT="(to keep everything in one place)"/>
</node>
<node CREATED="1227600949663" ID="Freemind_Link_693050557" MODIFIED="1227600958472" TEXT="might mean giving plugins the password">
<node CREATED="1227600960079" ID="Freemind_Link_1791590797" MODIFIED="1227600972954" TEXT="unless framework mediates"/>
<node CREATED="1227600973316" ID="Freemind_Link_1752654046" MODIFIED="1227600978062" TEXT="all extra file I/O"/>
<node CREATED="1227600978652" ID="Freemind_Link_1784696447" MODIFIED="1227600999368" TEXT="undesirable if plugin needs"/>
<node CREATED="1227600987548" ID="Freemind_Link_1136849002" MODIFIED="1227600995468" TEXT="3rd party storage mechanism"/>
</node>
</node>
</node>
<node CREATED="1227600414627" ID="Freemind_Link_1298641557" MODIFIED="1227600417485" TEXT="db opening">
<node CREATED="1227601446542" ID="Freemind_Link_1949388575" MODIFIED="1227601464826" TEXT="consider opening DBs created via a plugin that&apos;s not installed"/>
<node CREATED="1227601468191" ID="Freemind_Link_826103459" MODIFIED="1227601473692" TEXT="or at the wrong version">
<node CREATED="1227601474699" ID="Freemind_Link_122703275" MODIFIED="1227601485352" TEXT="so migrate the DB"/>
</node>
</node>
<node CREATED="1227601488055" ID="Freemind_Link_1195655640" MODIFIED="1227601490447" TEXT="migration"/>
<node CREATED="1227600417803" ID="Freemind_Link_1951831187" MODIFIED="1227600419997" TEXT="db closing"/>
<node CREATED="1227600421699" ID="Freemind_Link_1172575226" MODIFIED="1227600434239" TEXT="lifecycle and its friends"/>
<node CREATED="1227601290388" ID="Freemind_Link_1076155058" MODIFIED="1227601301020" TEXT="stashing objects in the database descriptor">
<node CREATED="1227602185422" ID="Freemind_Link_924448185" MODIFIED="1227602204067" TEXT="Need to add to AttributeIdentifiers">
<node CREATED="1227602207195" ID="Freemind_Link_1980659030" MODIFIED="1227602211023" TEXT="but it&apos;s an enum"/>
</node>
</node>
<node CREATED="1227600337240" ID="Freemind_Link_1050407143" MODIFIED="1227600339957" TEXT="use of spring">
<node CREATED="1227600340756" ID="Freemind_Link_1166776732" MODIFIED="1227600346837" TEXT="plugins get the springloader">
<node CREATED="1227600383558" ID="Freemind_Link_1564656499" MODIFIED="1227600388089" TEXT="to get core beans"/>
</node>
<node CREATED="1227600368365" ID="Freemind_Link_375643949" MODIFIED="1227600380337" TEXT="plugins can define their own application contexts"/>
</node>
<node CREATED="1227601114397" ID="Freemind_Link_1188410727" MODIFIED="1227601120643" TEXT="interfaces">
<node CREATED="1227600351256" ID="Freemind_Link_1137750762" MODIFIED="1227600365707" TEXT="need separate plugin API jar"/>
<node CREATED="1227601125658" ID="Freemind_Link_433348804" MODIFIED="1227601129959" TEXT="for plugins to implement"/>
<node CREATED="1227601130746" ID="Freemind_Link_1892242546" MODIFIED="1227601140210" TEXT="need facade into the core app"/>
<node CREATED="1227601140831" ID="Freemind_Link_1424844577" MODIFIED="1227601145782" TEXT="for plugins to call into"/>
<node CREATED="1227601146027" ID="Freemind_Link_186453727" MODIFIED="1227601151954" TEXT="for common services"/>
</node>
<node CREATED="1227602171553" ID="Freemind_Link_1179784467" MODIFIED="1227602177393" TEXT="DSTA message IDs">
<node CREATED="1227602215711" ID="Freemind_Link_1250757724" MODIFIED="1227602220170" TEXT="are stored in an enum"/>
</node>
<node CREATED="1227600591309" ID="Freemind_Link_1295529083" MODIFIED="1227600596996" TEXT="views">
<node CREATED="1227600597830" ID="Freemind_Link_847274122" MODIFIED="1227600607166" TEXT="each plugin can define multiple views"/>
<node CREATED="1227600607594" ID="Freemind_Link_295467528" MODIFIED="1227600616398" TEXT="need to add these into TabIdentifier">
<node CREATED="1227600617214" ID="Freemind_Link_1768341502" MODIFIED="1227600624418" TEXT="but it&apos;s an enum!"/>
</node>
</node>
<node CREATED="1227600741675" ID="Freemind_Link_1401496359" MODIFIED="1227600744443" TEXT="menus">
<node CREATED="1227600745279" ID="Freemind_Link_1630647028" MODIFIED="1227600753989" TEXT="might need plugins to define complete menus"/>
<node CREATED="1227600846431" ID="Freemind_Link_422796363" MODIFIED="1227600888571" TEXT="switching between databases of different&#xa;&apos;personalities&apos; needs more of the menu to change"/>
</node>
<node CREATED="1227600756048" ID="Freemind_Link_1210776" MODIFIED="1227600759690" TEXT="undo / redo">
<node CREATED="1227600760568" ID="Freemind_Link_5435789" MODIFIED="1227600769710" TEXT="how to hook into this (not that it exists yet)"/>
</node>
<node CREATED="1227600647132" ID="Freemind_Link_419633745" MODIFIED="1227600651501" TEXT="plugin name">
<node CREATED="1227600655336" ID="Freemind_Link_653631362" MODIFIED="1227600668383" TEXT="for choosing &apos;personality&apos; of databases"/>
</node>
</node>
<node CREATED="1227600169253" ID="Freemind_Link_184839627" MODIFIED="1227600173996" POSITION="right" TEXT="security">
<node CREATED="1227600174598" ID="Freemind_Link_983925246" MODIFIED="1227600187608" TEXT="plugin access to database"/>
<node CREATED="1227600188414" ID="Freemind_Link_508729732" MODIFIED="1227600192340" TEXT="never given password"/>
<node CREATED="1227600192946" ID="Freemind_Link_1687438669" MODIFIED="1227600202112" TEXT="but each plugin still has access to whole db"/>
</node>
<node CREATED="1227600231176" ID="Freemind_Link_763821696" MODIFIED="1227600235061" POSITION="left" TEXT="philosophy?">
<node CREATED="1227600235869" ID="Freemind_Link_1550481636" MODIFIED="1227600241041" TEXT="core application plugins">
<node CREATED="1227600253769" ID="Freemind_Link_1268803478" MODIFIED="1227600263369" TEXT="a.k.a. new apps built on framework">
<node CREATED="1227601389352" ID="Freemind_Link_1299915251" MODIFIED="1227601408631" TEXT="e.g. data entry, core finances"/>
</node>
<node CREATED="1227600453564" ID="Freemind_Link_607651642" MODIFIED="1227600468400" TEXT="do all DBs get all core plugins&apos; schemas"/>
<node CREATED="1227600469029" ID="Freemind_Link_883330117" MODIFIED="1227600519954" TEXT="should each db only have one core functionality?">
<node CREATED="1227600692937" ID="Freemind_Link_981323648" MODIFIED="1227600704087" TEXT="yes, it would be confusing otherwise"/>
<node CREATED="1227600522003" ID="Freemind_Link_1068824807" MODIFIED="1227600527558" TEXT="how is this chosen?"/>
<node CREATED="1227600527975" ID="Freemind_Link_1188974458" MODIFIED="1227600564451" TEXT="at creation time"/>
<node CREATED="1227601425661" ID="Freemind_Link_601816675" MODIFIED="1227601430276" TEXT="via list of plugin names"/>
</node>
</node>
<node CREATED="1227600242380" ID="Freemind_Link_1811518463" MODIFIED="1227600252163" TEXT="extension plugins">
<node CREATED="1227600810222" ID="Freemind_Link_974240110" MODIFIED="1227600816328" TEXT="cross-application extensions"/>
<node CREATED="1227600816694" ID="Freemind_Link_783638457" MODIFIED="1227601374519" TEXT="e.g. notes"/>
<node CREATED="1227600825754" ID="Freemind_Link_1867916750" MODIFIED="1227600834330" TEXT="might need wider set of extension points"/>
</node>
</node>
</node>
</map>
