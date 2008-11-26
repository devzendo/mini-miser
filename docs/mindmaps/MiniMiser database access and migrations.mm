<map version="0.8.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#000000" CREATED="1208933234718" ID="Freemind_Link_1225948423" MODIFIED="1208934904253" TEXT="MiniMiser database&#xa;access and migrations">
<font NAME="SansSerif" SIZE="20"/>
<hook NAME="accessories/plugins/AutomaticLayout.properties"/>
<node COLOR="#0033ff" CREATED="1208933260654" ID="_" MODIFIED="1208935603048" POSITION="right" TEXT="access factory">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1208935527799" ID="Freemind_Link_297598622" MODIFIED="1209667212153" TEXT="needs to use a connection pool?">
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1208935547540" ID="Freemind_Link_32852723" MODIFIED="1208935554867" TEXT="username would be hard coded">
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1208933282039" ID="Freemind_Link_86887252" MODIFIED="1208934789507" TEXT="throws a PasswordRequiredException&#xa;if open fails due to lack of password">
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1208933321094" ID="Freemind_Link_1248303662" MODIFIED="1208934789587" TEXT="throws PasswordWrong if it&apos;s wrong">
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1208933335415" ID="Freemind_Link_1337906553" MODIFIED="1208935505131" TEXT="allows schema version to be obtained from the db using JDBC">
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1208934055090" ID="Freemind_Link_1989676002" MODIFIED="1208934789620" TEXT="can obtain a JDBC connection">
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1208935335116" ID="Freemind_Link_1663035678" MODIFIED="1208935482002" TEXT="to be used by the migration code">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1208935340904" ID="Freemind_Link_920922267" MODIFIED="1208935348563" TEXT="or the SQL debug GUI console">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1208934095793" ID="Freemind_Link_1455656732" MODIFIED="1208935453884" TEXT="can obtain a Hibernate/Spring&#xa;connection with the appropriate&#xa;schema objects">
<arrowlink DESTINATION="Freemind_Link_1846406116" ENDARROW="Default" ENDINCLINATION="17;-8;" ID="Freemind_Arrow_Link_1088245180" STARTARROW="None" STARTINCLINATION="49;86;"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1208935456073" ID="Freemind_Link_528966508" MODIFIED="1208935476253" TEXT="to be used by the business domain logic">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1208935367814" ID="Freemind_Link_1120015565" MODIFIED="1208935438523" TEXT="it&apos;d probably cause big woe if&#xa;there were simultaneous JDBC&#xa;and Hibernate access">
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1208935402365" ID="Freemind_Link_1557783689" MODIFIED="1208935429069" TEXT="so the factory should&#xa;disallow both simultaneously">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1208935621533" ID="Freemind_Link_1769253372" MODIFIED="1208935638486" TEXT="add releaseConnection(JDBCConnection)">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1208935639555" ID="Freemind_Link_1631805197" MODIFIED="1208935646144" TEXT="add releaseConnection(Spring)">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1208935027100" ID="Freemind_Link_1922398237" MODIFIED="1208935325207" TEXT="must be able to attach a listener so the &#xa;gui can be informed of opening progress">
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1208935662204" ID="Freemind_Link_1752350786" MODIFIED="1208935682179" TEXT="or would this be handled&#xa;at a higher level?">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1208933416090" ID="Freemind_Link_1529343738" MODIFIED="1208935584243" POSITION="right" TEXT="current (max) dbschema version&#xa;supported by the app must&#xa;be obtainable">
<font NAME="SansSerif" SIZE="18"/>
</node>
<node COLOR="#0033ff" CREATED="1208933351129" ID="Freemind_Link_963145353" MODIFIED="1208934789818" POSITION="left" TEXT="schema">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1208933355997" ID="Freemind_Link_1732790866" MODIFIED="1208934789825" TEXT="version">
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1208933359162" ID="Freemind_Link_470195427" MODIFIED="1208934789835" TEXT="holds name=value pairs">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1208933367086" ID="Freemind_Link_863977323" MODIFIED="1208934789877" TEXT="dbschema.version=1">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1208933863692" ID="Freemind_Link_231428900" MODIFIED="1208934789888" TEXT="dbschema should be easily comparable - integers">
<edge WIDTH="thin"/>
</node>
</node>
<node COLOR="#990000" CREATED="1208933380971" ID="Freemind_Link_1290936913" MODIFIED="1208934789896" TEXT="application.version=0.2">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1208933904939" ID="Freemind_Link_292576977" MODIFIED="1208934789908" TEXT="gets updated on load, if different - human consumable"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1208933434259" ID="Freemind_Link_980544327" MODIFIED="1208934789925" POSITION="left" TEXT="migration">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1208933466153" ID="Freemind_Link_1242760082" MODIFIED="1208934789939" TEXT="done in discrete steps">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1208933473630" ID="Freemind_Link_772934968" MODIFIED="1208934789987" TEXT="need to define all schema versions&#xa;from 0 to &lt;current dbschema&gt;">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1208933511773" ID="Freemind_Link_1741239080" MODIFIED="1208934790063" TEXT="for each step define a set of transformations&#xa;to be applied as a set of SQL statements">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1208933591042" ID="Freemind_Link_528029012" MODIFIED="1208934790116" TEXT="alter table"/>
<node COLOR="#111111" CREATED="1208933594924" ID="Freemind_Link_1537546277" MODIFIED="1208934790120" TEXT="create table"/>
<node COLOR="#111111" CREATED="1208933598343" ID="Freemind_Link_1087043649" MODIFIED="1208934790124" TEXT="inserts, updates, deletes"/>
<node COLOR="#111111" CREATED="1208933606632" ID="Freemind_Link_1512544770" MODIFIED="1208934790127" TEXT="update version.dbschema.version"/>
</node>
<node COLOR="#990000" CREATED="1208934171263" ID="Freemind_Link_1693653471" MODIFIED="1208934790184" TEXT="also give a description&#xa;of what changes this&#xa;schema introduces&#xa;in the form &apos;this update&#xa;adds the following features&apos;">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1208934238420" ID="Freemind_Link_224128363" MODIFIED="1208934790248" TEXT="for the user to see what&apos;s being upgraded"/>
</node>
<node COLOR="#990000" CREATED="1208933621885" ID="Freemind_Link_478128610" MODIFIED="1208934790261" TEXT="steps applied by JDBC/Spring code">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1208933631814" ID="Freemind_Link_558334822" MODIFIED="1208934790283" TEXT="with a listener interface for GUI progress monitoring">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1208933650323" ID="Freemind_Link_1128973106" MODIFIED="1208934790297" TEXT="tests">
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1208933652899" ID="Freemind_Link_760322186" MODIFIED="1208934790331" TEXT="define directory for test&#xa;data to be created, used&#xa;by all persistence layer tests">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1208933763796" ID="Freemind_Link_1149540587" MODIFIED="1208934790403" TEXT="migration test&#xa;one of these for every migration step&#xa;between 0 and &lt;dbschema.current&gt;">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1208933699143" ID="Freemind_Link_581733946" MODIFIED="1208934790434" TEXT="use orm access layer to create version n schema"/>
<node COLOR="#111111" CREATED="1208933776145" ID="Freemind_Link_1227646439" MODIFIED="1208934790437" TEXT="populate with representative data with orm layer">
<edge STYLE="bezier"/>
</node>
<node COLOR="#111111" CREATED="1208933817272" ID="Freemind_Link_1383043742" MODIFIED="1208934790440" TEXT="close orm layer"/>
<node COLOR="#111111" CREATED="1208933789334" ID="Freemind_Link_1721540522" MODIFIED="1208934790444" TEXT="apply migration transform to schema version n+1"/>
<node COLOR="#111111" CREATED="1208933800027" ID="Freemind_Link_397290856" MODIFIED="1208934790447" TEXT="verify  correct schema structure"/>
<node COLOR="#111111" CREATED="1208933833261" ID="Freemind_Link_1228931585" MODIFIED="1208934790450" TEXT="verify correct data has been transformed or static data created"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1208933437915" ID="Freemind_Link_425186214" MODIFIED="1208934790457" POSITION="right" TEXT="orm access">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1208933443968" ID="Freemind_Link_18564278" MODIFIED="1208934790493" TEXT="each schema version&apos;s mappings&#xa;must be provided with the app">
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1208934000331" ID="Freemind_Link_1846406116" MODIFIED="1208934941888" TEXT="need to be able to create any dbschema version">
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1208934130404" ID="Freemind_Link_1778256195" MODIFIED="1208934790548" TEXT="&lt;&lt;abstract factory&gt;&gt;">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1208934009643" ID="Freemind_Link_1535025081" MODIFIED="1208934790554" TEXT="and to work with it via orm">
<font NAME="SansSerif" SIZE="16"/>
</node>
</node>
</node>
</map>
