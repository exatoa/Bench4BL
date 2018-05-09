/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.indexer;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.skku.selab.blp.*;
import edu.skku.selab.blp.blia.indexer.SourceFileCorpusCreator;
import edu.skku.selab.blp.blia.indexer.BugCorpusCreator;
import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.db.dao.DbUtil;
import edu.skku.selab.blp.db.dao.SourceFileDAO;
import edu.skku.selab.blp.utils.Util;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BugCorpusCreatorTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		double alpha = 0.2f;
		double beta = 0.5f;
		int pastDays = 50;
		
		Property prop = Property.loadInstance(Property.ZXING);
		prop.alpha = alpha;
		prop.beta = beta;
		prop.pastDays = pastDays;

		DbUtil dbUtil = new DbUtil();
		String dbName = Property.getInstance().productName;
		dbUtil.openConnetion(dbName);
		dbUtil.initializeAllData();
		dbUtil.closeConnection();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void verifyCreateWithSourceFileCorpusCreator() throws Exception {
		String version = SourceFileDAO.DEFAULT_VERSION_STRING;
		SourceFileCorpusCreator sourceFileCorpusCreator = new SourceFileCorpusCreator();
		sourceFileCorpusCreator.create(version);
		
		BugCorpusCreator bugCorpusCreator = new BugCorpusCreator();
		boolean stackTraceAnalysis = false;
		bugCorpusCreator.create(stackTraceAnalysis);
	}
	
	@Test
	public void verifyCreateWithStructuredSourceFileCorpusCreator() throws Exception {
		long startTime = System.currentTimeMillis();

		System.out.printf("[STARTED] StructuredSourceFileCorpusCreator.create()\n");
		String version = SourceFileDAO.DEFAULT_VERSION_STRING;
		StructuredSourceFileCorpusCreator sourceFileCorpusCreator = new StructuredSourceFileCorpusCreator();
		sourceFileCorpusCreator.create(version);
		System.out.printf("[DONE] StructuredSourceFileCorpusCreator.create() (%s sec)\n", Util.getElapsedTimeSting(startTime));
		
		System.out.printf("[STARTED] BugCorpusCreator.create()\n");
		BugCorpusCreator bugCorpusCreator = new BugCorpusCreator();
		boolean stackTraceAnalysis = true;
		bugCorpusCreator.create(stackTraceAnalysis);
		System.out.printf("[DONE] BugCorpusCreator.create() (%s sec)\n", Util.getElapsedTimeSting(startTime));
	}
	
	@Test
	public void verifyExtractClassName() {
		BugCorpusCreator bugCorpusCreator = new BugCorpusCreator();
		Bug bug = new Bug();
		
		// Bug ID: 81242, Project: swt-3.1
		bug.setID(81242);
		String description = "PLUGIN_PATH &lt;null&gt; Resolved file name for empty.txt = bluebird/teamswt/torres/linux/motif/eclipse/workspace/org.eclipse.swt.tests/bin/empty.txt An unexpected exception has been detected in native code outside the VM. Unexpected Signal : 11 occurred at PC=0x0 Function=[Unknown.] Library=(N/A) NOTE: We are unable to locate the function name symbol for the error just occurred. Please refer to release documentation for possible reason and solutions. Current Java thread: at org.eclipse.swt.internal.motif.OS._XtGetSelectionValue(Native Method) at org.eclipse.swt.internal.motif.OS.XtGetSelectionValue(OS.java:3688) at org.eclipse.swt.dnd.ClipboardProxy.getAvailableTypes(ClipboardProxy.java:150) at org.eclipse.swt.dnd.ClipboardProxy.getContents(ClipboardProxy.java:118) at org.eclipse.swt.dnd.Clipboard.getContents(Clipboard.java:275) at org.eclipse.swt.dnd.Clipboard.getContents(Clipboard.java:223) at org.eclipse.swt.tests.junit.Test_org_eclipse_swt_custom_StyledText.test_copy(Test_org_eclipse_swt_custom_StyledText.java:555) at org.eclipse.swt.tests.junit.Test_org_eclipse_swt_custom_StyledText.runTest(Test_org_eclipse_swt_custom_StyledText.java:4198) at junit.framework.TestCase.runBare(TestCase.java:127) at junit.framework.TestResult$1.protect(TestResult.java:106) at junit.framework.TestResult.runProtected(TestResult.java:124) at junit.framework.TestResult.run(TestResult.java:109) at junit.framework.TestCase.run(TestCase.java:118) at junit.framework.TestSuite.runTest(TestSuite.java:208) at junit.framework.TestSuite.run(TestSuite.java:203) at junit.framework.TestSuite.runTest(TestSuite.java:208) at junit.framework.TestSuite.run(TestSuite.java:203) at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:468) at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:343) at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:195) Dynamic libraries: 08048000-08056000 r-xp 00000000 00:0a 538294580 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/bin/java 08056000-08059000 rw-p 0000d000 00:0a 538294580 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/bin/java 40000000-40014000 r-xp 00000000 03:03 505027 /lib/ld-2.3.2.so 40014000-40015000 rw-p 00014000 03:03 505027 /lib/ld-2.3.2.so 40015000-4001d000 r-xp 00000000 00:0a 337128161 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/native_threads/libhpi.so 4001d000-4001e000 rw-p 00007000 00:0a 337128161 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/native_threads/libhpi.so 4001e000-40022000 rw-s 00000000 03:03 1988430 /tmp/hsperfdata_torres/20700 40022000-40030000 r--s 00000000 00:0a 386697846 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/ext/ldapsec.jar 40030000-40031000 r--s 00000000 00:0a 539462609 /bluebird/teamswt/torres/linux/motif/eclipse/plugins/org.eclipse.swt.motif_3.1.0/ws/motif/swt-gtk.jar 40032000-4003f000 r-xp 00000000 03:03 505047 /lib/libpthread.so.0 4003f000-40040000 rw-p 0000d000 03:03 505047 /lib/libpthread.so.0 40083000-40085000 r-xp 00000000 03:03 505036 /lib/libdl.so.2 40085000-40086000 rw-p 00001000 03:03 505036 /lib/libdl.so.2 40086000-401b5000 r-xp 00000000 03:03 505033 /lib/libc.so.6 401b5000-401b9000 rw-p 0012f000 03:03 505033 /lib/libc.so.6 401bc000-405b8000 r-xp 00000000 00:0a 369997783 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/client/libjvm.so 405b8000-405d4000 rw-p 003fb000 00:0a 369997783 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/client/libjvm.so 405e6000-405f8000 r-xp 00000000 03:03 505039 /lib/libnsl.so.1 405f8000-405f9000 rw-p 00011000 03:03 505039 /lib/libnsl.so.1 405fb000-4061c000 r-xp 00000000 03:03 505037 /lib/libm.so.6 4061c000-4061d000 rw-p 00020000 03:03 505037 /lib/libm.so.6 4061d000-4062d000 r-xp 00000000 00:0a 318930479 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/libverify.so 4062d000-4062f000 rw-p 0000f000 00:0a 318930479 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/libverify.so 4062f000-4064f000 r-xp 00000000 00:0a 318930480 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/libjava.so 4064f000-40651000 rw-p 0001f000 00:0a 318930480 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/libjava.so 40651000-40665000 r-xp 00000000 00:0a 318930482 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/libzip.so 40665000-40668000 rw-p 00013000 00:0a 318930482 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/libzip.so 40668000-413c9000 r--s 00000000 00:0a 285807727 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/rt.jar 41416000-4142d000 r--s 00000000 00:0a 285807737 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/sunrsasign.jar 4142d000-4149e000 r--s 00000000 00:0a 285800486 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/jsse.jar 4149e000-414b0000 r--s 00000000 00:0a 285807731 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/jce.jar 414b0000-41791000 r--s 00000000 00:0a 285807720 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/charsets.jar 43839000-4383c000 r--s 00000000 00:0a 386722929 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/ext/dnsns.jar 4383c000-4383e000 r-xp 00000000 03:03 684353 /usr/X11R6/lib/X11/locale/lib/common/xlcDef.so.2 4383e000-4383f000 rw-p 00001000 03:03 684353 /usr/X11R6/lib/X11/locale/lib/common/xlcDef.so.2 4b8c0000-4bac0000 r--p 00000000 03:03 2117686 /usr/lib/locale/locale-archive 4bac0000-4bb28000 r--s 00000000 00:0a 386697841 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/ext/localedata.jar 4bb28000-4bb44000 r--s 00000000 00:0a 386725087 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/ext/sunjce_provider.jar 4bb44000-4bb90000 r--s 00000000 00:0a 269221741 /bluebird/teamswt/torres/linux/motif/eclipse/plugins/org.eclipse.jdt.junit_3.1.0/junitsupport.jar 4bb90000-4bb95000 r--s 00000000 00:0a 253875361 /bluebird/teamswt/torres/linux/motif/eclipse/plugins/org.eclipse.jdt.junit.runtime_3.1.0/junitruntime.jar 4bb95000-4bbb3000 r--s 00000000 00:0a 386176619 /bluebird/teamswt/torres/linux/motif/eclipse/plugins/org.junit_3.8.1/junit.jar 4bbb3000-4bbc3000 r-xp 00000000 00:0a 319238016 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/libnet.so 4bbc3000-4bbc4000 rw-p 0000f000 00:0a 319238016 /bluebird/teamswt/torres/linux/vm/jdk1.4.2_06/jre/lib/i386/libnet.so 4bbc4000-4bcc6000 r--s 00000000 00:0a 539462612 /bluebird/teamswt/torres/linux/motif/eclipse/plugins/org.eclipse.swt.motif_3.1.0/ws/motif/swt.jar 4bcc6000-4bcd3000 r--s 00000000 00:0a 539462610 /bluebird/teamswt/torres/linux/motif/eclipse/plugins/org.eclipse.swt.motif_3.1.0/ws/motif/swt-mozilla.jar 4bcd3000-4bce5000 r--s 00000000 00:0a 539462611 /bluebird/teamswt/torres/linux/motif/eclipse/plugins/org.eclipse.swt.motif_3.1.0/ws/motif/swt-pi.jar 4bce5000-4bd2c000 r-xp 00000000 00:0a 589158311 /bluebird/teamswt/torres/linux/motif/eclipse/plugins/org.eclipse.swt.motif_3.1.0/os/linux/x86/libswt-motif-3116.so 4bd2c000-4bd2f000 rw-p 00046000 00:0a 589158311 /bluebird/teamswt/torres/linux/motif/eclipse/plugins/org.eclipse.swt.motif_3.1.0/os/linux/x86/libswt-motif-3116.so 4bd30000-4beb4000 r-xp 00000000 00:0a 138076468 /bluebird/teamswt/torres/linux/motif/eclipse/libXm.so.2.1 4beb4000-4bec7000 rw-p 00183000 00:0a 138076468 /bluebird/teamswt/torres/linux/motif/eclipse/libXm.so.2.1 4bec8000-4bece000 r--s 00000000 03:03 602997 /usr/lib/gconv/gconv-modules.cache 4bece000-4bed0000 r-xp 00000000 03:03 602948 /usr/lib/gconv/ISO8859-1.so 4bed0000-4bed1000 rw-p 00001000 03:03 602948 /usr/lib/gconv/ISO8859-1.so 4bed1000-4beda000 r-xp 00000000 03:03 684357 /usr/X11R6/lib/X11/locale/lib/common/xomGeneric.so.2 4beda000-4bedb000 rw-p 00008000 03:03 684357 /usr/X11R6/lib/X11/locale/lib/common/xomGeneric.so.2 4bee5000-4bfaf000 r-xp 00000000 03:03 2150128 /usr/X11R6/lib/libX11.so.6.2 4bfaf000-4bfb3000 rw-p 000c9000 03:03 2150128 /usr/X11R6/lib/libX11.so.6.2 4bfb3000-4bfc1000 r-xp 00000000 03:03 2150138 /usr/X11R6/lib/libXext.so.6.4 4bfc1000-4bfc2000 rw-p 0000d000 03:03 2150138 /usr/X11R6/lib/libXext.so.6.4 4bfc2000-4c012000 r-xp 00000000 03:03 2150160 /usr/X11R6/lib/libXt.so.6.0 4c012000-4c015000 rw-p 00050000 03:03 2150160 /usr/X11R6/lib/libXt.so.6.0 4c016000-4c01d000 r-xp 00000000 03:03 2150152 /usr/X11R6/lib/libXp.so.6.2 4c01d000-4c01e000 rw-p 00006000 03:03 2150152 /usr/X11R6/lib/libXp.so.6.2 4c01e000-4c023000 r-xp 00000000 03:03 2150162 /usr/X11R6/lib/libXtst.so.6.1 4c023000-4c024000 rw-p 00004000 03:03 2150162 /usr/X11R6/lib/libXtst.so.6.1 4c024000-4c02c000 r-xp 00000000 03:03 2150126 /usr/X11R6/lib/libSM.so.6.0 4c02c000-4c02d000 rw-p 00007000 03:03 2150126 /usr/X11R6/lib/libSM.so.6.0 4c02d000-4c041000 r-xp 00000000 03:03 2150124 /usr/X11R6/lib/libICE.so.6.3 4c041000-4c043000 rw-p 00013000 03:03 2150124 /usr/X11R6/lib/libICE.so.6.3 4c044000-4c04c000 r-xp 00000000 03:03 2150136 /usr/X11R6/lib/libXcursor.so.1.0 4c04c000-4c04d000 rw-p 00007000 03:03 2150136 /usr/X11R6/lib/libXcursor.so.1.0 4c04d000-4c054000 r-xp 00000000 03:03 2150158 /usr/X11R6/lib/libXrender.so.1.2 4c054000-4c055000 rw-p 00006000 03:03 2150158 /usr/X11R6/lib/libXrender.so.1.2 4c055000-4c072000 r-xp 00000000 03:03 684352 /usr/X11R6/lib/X11/locale/lib/common/ximcp.so.2 4c072000-4c074000 rw-p 0001c000 03:03 684352 /usr/X11R6/lib/X11/locale/lib/common/ximcp.so.2 Heap at VM Abort: Heap def new generation total 2240K, used 108K [0x43840000, 0x43aa0000, 0x43d20000) eden space 2048K, 2% used [0x43840000, 0x4384b998, 0x43a40000) from space 192K, 32% used [0x43a70000, 0x43a7f870, 0x43aa0000) to space 192K, 0% used [0x43a40000, 0x43a40000, 0x43a70000) tenured generation total 28844K, used 28004K [0x43d20000, 0x4594b000, 0x47840000) the space 28844K, 97% used [0x43d20000, 0x458793e0, 0x45879400, 0x4594b000) compacting perm gen total 5120K, used 5054K [0x47840000, 0x47d40000, 0x4b840000) the space 5120K, 98% used [0x47840000, 0x47d2f920, 0x47d2fa00, 0x47d40000) Local Time = Wed Dec 15 11:11:45 2004 Elapsed Time = 107 # # The exception above was detected in native code outside the VM # # Java VM: Java HotSpot(TM) Client VM (1.4.2_06-b02 mixed mode) # # An error report file has been saved as hs_err_pid20700.log. # Please refer to the file for further information. #";
		description = description.replace("&amp;", "&");
		description = description.replace("&quot;", "\"");
		description = description.replace("&lt;", "<");
		description = description.replace("&gt;", ">");
		
		bug.setDescription(description);
		ArrayList<String> classNames = bugCorpusCreator.extractClassName(bug.getDescription(), bug.getID());
		for(int i = 0; i < classNames.size(); i++) {
			System.out.printf("%d: %s\n", i + 1, classNames.get(i));
		}
		assertEquals("org.eclipse.swt.internal.motif.OS", classNames.get(0));
		assertEquals("org.eclipse.swt.internal.motif.OS", classNames.get(1));
		assertEquals("org.eclipse.swt.dnd.ClipboardProxy", classNames.get(2));
		assertEquals("org.eclipse.swt.dnd.ClipboardProxy", classNames.get(3));
		assertEquals("org.eclipse.swt.dnd.Clipboard", classNames.get(4));
		assertEquals("org.eclipse.swt.dnd.Clipboard", classNames.get(5));
		assertEquals("org.eclipse.swt.tests.junit.Test_org_eclipse_swt_custom_StyledText", classNames.get(6));
		assertEquals("org.eclipse.swt.tests.junit.Test_org_eclipse_swt_custom_StyledText", classNames.get(7));
		assertEquals("junit.framework.TestCase", classNames.get(8));
		assertEquals("junit.framework.TestResult", classNames.get(9));
		assertEquals("junit.framework.TestResult", classNames.get(10));
		assertEquals("junit.framework.TestResult", classNames.get(11));
		assertEquals("junit.framework.TestCase", classNames.get(12));
		assertEquals("junit.framework.TestSuite", classNames.get(13));
		assertEquals("junit.framework.TestSuite", classNames.get(14));
		assertEquals("junit.framework.TestSuite", classNames.get(15));
		assertEquals("junit.framework.TestSuite", classNames.get(16));
		assertEquals("org.eclipse.jdt.internal.junit.runner.RemoteTestRunner", classNames.get(17));
		assertEquals("org.eclipse.jdt.internal.junit.runner.RemoteTestRunner", classNames.get(18));
		assertEquals("org.eclipse.jdt.internal.junit.runner.RemoteTestRunner", classNames.get(19));
		
		
		// Bug ID: 77948, Project: swt-3.1
		bug.setID(77948);
		description = "I200411041200, GTK+ 2.4.9, KDE 3.3.0, Linux 2.6.9 I was creating new simple files in existing projects, and then deleting them. I was using the keyboard heavily for navigation. I found the exception below in the log. There was no major effect from this null pointer, but it might have been responsible for some buttons not disabling when they should. I'll investiage more. !ENTRY org.eclipse.ui 4 4 2004-11-05 08:51:21.199 !MESSAGE Unhandled event loop exception !ENTRY org.eclipse.ui 4 0 2004-11-05 08:51:21.235 !MESSAGE java.lang.NullPointerException !STACK 0 java.lang.NullPointerException at org.eclipse.swt.custom.CLabel.findMnemonic(CLabel.java:194) at org.eclipse.swt.custom.CLabel.onMnemonic(CLabel.java:334) at org.eclipse.swt.custom.CLabel$3.keyTraversed(CLabel.java:126) at org.eclipse.swt.widgets.TypedListener.handleEvent(TypedListener.java:221) at org.eclipse.swt.widgets.EventTable.sendEvent(EventTable.java:82) at org.eclipse.swt.widgets.Widget.sendEvent(Widget.java:989) at org.eclipse.swt.widgets.Widget.sendEvent(Widget.java:1013) at org.eclipse.swt.widgets.Widget.sendEvent(Widget.java:998) at org.eclipse.swt.widgets.Control.traverse(Control.java:3088) at org.eclipse.swt.widgets.Control.translateMnemonic(Control.java:2992) at org.eclipse.swt.widgets.Composite.translateMnemonic(Composite.java:811) at org.eclipse.swt.widgets.Composite.translateMnemonic(Composite.java:816) at org.eclipse.swt.widgets.Composite.translateMnemonic(Composite.java:816) at org.eclipse.swt.widgets.Composite.translateMnemonic(Composite.java:816) at org.eclipse.swt.widgets.Composite.translateMnemonic(Composite.java:816) at org.eclipse.swt.widgets.Composite.translateMnemonic(Composite.java:816) at org.eclipse.swt.widgets.Composite.translateMnemonic(Composite.java:816) at org.eclipse.swt.widgets.Control.translateMnemonic(Control.java:3011) at org.eclipse.swt.widgets.Control.gtk_key_press_event(Control.java:1855) at org.eclipse.swt.widgets.Composite.gtk_key_press_event(Composite.java:451) at org.eclipse.swt.widgets.Tree.gtk_key_press_event(Tree.java:637) at org.eclipse.swt.widgets.Widget.windowProc(Widget.java:1325) at org.eclipse.swt.widgets.Display.windowProc(Display.java:3214) at org.eclipse.swt.internal.gtk.OS.gtk_widget_event(Native Method) at org.eclipse.swt.widgets.Control.gtk_mnemonic_activate(Control.java:1899) at org.eclipse.swt.widgets.Widget.windowProc(Widget.java:1330) at org.eclipse.swt.widgets.Display.windowProc(Display.java:3214) at org.eclipse.swt.internal.gtk.OS.gtk_main_do_event(Native Method) at org.eclipse.swt.widgets.Display.eventProc(Display.java:901) at org.eclipse.swt.internal.gtk.OS.g_main_context_iteration(Native Method) at org.eclipse.swt.widgets.Display.readAndDispatch(Display.java:2363) at org.eclipse.jface.window.Window.runEventLoop(Window.java:718) at org.eclipse.jface.window.Window.open(Window.java:696) at org.eclipse.ui.actions.NewWizardAction.run(NewWizardAction.java:172) at org.eclipse.jface.action.Action.runWithEvent(Action.java:988) at org.eclipse.ui.commands.ActionHandler.execute(ActionHandler.java:188) at org.eclipse.ui.internal.commands.Command.execute(Command.java:130) at org.eclipse.ui.internal.keys.WorkbenchKeyboard.executeCommand(WorkbenchKeyboard.java:445) at org.eclipse.ui.internal.keys.WorkbenchKeyboard.press(WorkbenchKeyboard.java:724) at org.eclipse.ui.internal.keys.WorkbenchKeyboard.processKeyEvent(WorkbenchKeyboard.java:767) at org.eclipse.ui.internal.keys.WorkbenchKeyboard.filterKeySequenceBindings(WorkbenchKeyboard.java:536) at org.eclipse.ui.internal.keys.WorkbenchKeyboard.access$2 (WorkbenchKeyboard.java:479) at org.eclipse.ui.internal.keys.WorkbenchKeyboard$1.handleEvent(WorkbenchKeyboard.java:221) at org.eclipse.swt.widgets.EventTable.sendEvent(EventTable.java:82) at org.eclipse.swt.widgets.Display.filterEvent(Display.java:1058) at org.eclipse.swt.widgets.Widget.sendEvent(Widget.java:988) at org.eclipse.swt.widgets.Widget.sendEvent(Widget.java:1013) at org.eclipse.swt.widgets.Widget.sendEvent(Widget.java:998) at org.eclipse.swt.widgets.Widget.sendKeyEvent(Widget.java:1025) at org.eclipse.swt.widgets.Widget.gtk_key_press_event(Widget.java:593) at org.eclipse.swt.widgets.Control.gtk_key_press_event(Control.java:1866) at org.eclipse.swt.widgets.Composite.gtk_key_press_event(Composite.java:451) at org.eclipse.swt.widgets.Tree.gtk_key_press_event(Tree.java:637) at org.eclipse.swt.widgets.Widget.windowProc(Widget.java:1325) at org.eclipse.swt.widgets.Display.windowProc(Display.java:3214) at org.eclipse.swt.internal.gtk.OS.gtk_main_do_event(Native Method) at org.eclipse.swt.widgets.Display.eventProc(Display.java:901) at org.eclipse.swt.internal.gtk.OS.g_main_context_iteration(Native Method) at org.eclipse.swt.widgets.Display.readAndDispatch(Display.java:2363) at org.eclipse.ui.internal.Workbench.runEventLoop(Workbench.java:1527) at org.eclipse.ui.internal.Workbench.runUI(Workbench.java:1498) at org.eclipse.ui.internal.Workbench.createAndRunWorkbench(Workbench.java:276) at org.eclipse.ui.PlatformUI.createAndRunWorkbench(PlatformUI.java:144) at org.eclipse.ui.internal.ide.IDEApplication.run(IDEApplication.java:102) at org.eclipse.core.internal.runtime.PlatformActivator$1.run(PlatformActivator.java:335) at org.eclipse.core.runtime.adaptor.EclipseStarter.run(EclipseStarter.java:273) at org.eclipse.core.runtime.adaptor.EclipseStarter.run(EclipseStarter.java:129) at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:85) at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:58) at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:60) at java.lang.reflect.Method.invoke(Method.java:391) at org.eclipse.core.launcher.Main.basicRun(Main.java:185) at org.eclipse.core.launcher.Main.run(Main.java:684) at org.eclipse.core.launcher.Main.main(Main.java:668)";
		description = description.replace("&amp;", "&");
		description = description.replace("&quot;", "\"");
		description = description.replace("&lt;", "<");
		description = description.replace("&gt;", ">");
		
		bug.setDescription(description);
		classNames = bugCorpusCreator.extractClassName(bug.getDescription(), bug.getID());
		for(int i = 0; i < classNames.size(); i++) {
			System.out.printf("%d: %s\n", i + 1, classNames.get(i));
		}
		assertEquals("org.eclipse.swt.custom.CLabel", classNames.get(0));
		assertEquals("org.eclipse.swt.custom.CLabel", classNames.get(1));
		assertEquals("org.eclipse.swt.custom.CLabel", classNames.get(2));
		assertEquals("org.eclipse.swt.widgets.TypedListener", classNames.get(3));
		assertEquals("org.eclipse.swt.widgets.EventTable", classNames.get(4));
		assertEquals("org.eclipse.swt.widgets.Widget", classNames.get(5));
		assertEquals("org.eclipse.swt.widgets.Widget", classNames.get(6));
		assertEquals("org.eclipse.swt.widgets.Widget", classNames.get(7));
		assertEquals("org.eclipse.swt.widgets.Control", classNames.get(8));
		

		// Bug ID: 87855, Project: swt-3.1
		bug.setID(87855);
		description = "Here is a stack trace I found when trying to kill a running process by pressing the &quot;kill&quot; button in the console view. I use 3.1M5a. !ENTRY org.eclipse.ui 4 0 2005-03-12 14:26:25.58 !MESSAGE java.lang.NullPointerException !STACK 0 java.lang.NullPointerException at org.eclipse.swt.widgets.Table.callWindowProc(Table.java:156) at org.eclipse.swt.widgets.Table.sendMouseDownEvent(Table.java:2084) at org.eclipse.swt.widgets.Table.WM_LBUTTONDOWN(Table.java:3174) at org.eclipse.swt.widgets.Control.windowProc(Control.java:3057) at org.eclipse.swt.widgets.Display.windowProc(Display.java:3480) at org.eclipse.swt.internal.win32.OS.DispatchMessageW(Native Method) at org.eclipse.swt.internal.win32.OS.DispatchMessage(OS.java:1619) at org.eclipse.swt.widgets.Display.readAndDispatch(Display.java:2539) at org.eclipse.ui.internal.Workbench.runEventLoop(Workbench.java:1612) at org.eclipse.ui.internal.Workbench.runUI(Workbench.java:1578) at org.eclipse.ui.internal.Workbench.createAndRunWorkbench (Workbench.java:293) at org.eclipse.ui.PlatformUI.createAndRunWorkbench(PlatformUI.java:144) at org.eclipse.ui.internal.ide.IDEApplication.run (IDEApplication.java:102) at org.eclipse.core.internal.runtime.PlatformActivator$1.run (PlatformActivator.java:228) at org.eclipse.core.runtime.adaptor.EclipseStarter.run (EclipseStarter.java:333) at org.eclipse.core.runtime.adaptor.EclipseStarter.run (EclipseStarter.java:150) at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) at sun.reflect.NativeMethodAccessorImpl.invoke (NativeMethodAccessorImpl.java:39) at sun.reflect.DelegatingMethodAccessorImpl.invoke (DelegatingMethodAccessorImpl.java:25) at java.lang.reflect.Method.invoke(Method.java:585) at org.eclipse.core.launcher.Main.invokeFramework(Main.java:268) at org.eclipse.core.launcher.Main.basicRun(Main.java:260) at org.eclipse.core.launcher.Main.run(Main.java:887) at org.eclipse.core.launcher.Main.main(Main.java:871)";
		description = description.replace("&amp;", "&");
		description = description.replace("&quot;", "\"");
		description = description.replace("&lt;", "<");
		description = description.replace("&gt;", ">");
		
		bug.setDescription(description);
		classNames = bugCorpusCreator.extractClassName(bug.getDescription(), bug.getID());
		for(int i = 0; i < classNames.size(); i++) {
			System.out.printf("%d: %s\n", i + 1, classNames.get(i));
		}
		assertEquals("org.eclipse.swt.widgets.Table", classNames.get(0));
		assertEquals("org.eclipse.swt.widgets.Table", classNames.get(1));
		assertEquals("org.eclipse.swt.widgets.Table", classNames.get(2));
		assertEquals("org.eclipse.swt.widgets.Control", classNames.get(3));
		assertEquals("org.eclipse.swt.widgets.Display", classNames.get(4));
		assertEquals("org.eclipse.swt.internal.win32.OS", classNames.get(5));
		assertEquals("org.eclipse.swt.internal.win32.OS", classNames.get(6));
		assertEquals("org.eclipse.swt.widgets.Display", classNames.get(7));

		// Bug ID: 113971, Project: swt-3.1
		bug.setID(113971);
		description = "The &quot;org.eclipse.ui.tests&quot; had test failures on MacOS X last night. The failures were in Tree code. One was a NPE, which I believe has already been fixed. The other is as follows: java.lang.ArrayIndexOutOfBoundsException at java.lang.System.arraycopy(Native Method) at org.eclipse.swt.widgets.Tree.createItem(Tree.java:714) at org.eclipse.swt.widgets.TreeItem.&lt;init&gt;(TreeItem.java:191) at org.eclipse.swt.widgets.TreeItem.&lt;init&gt;(TreeItem.java:148) at org.eclipse.jface.viewers.TreeViewer.newItem(TreeViewer.java:507) at org.eclipse.jface.viewers.AbstractTreeViewer.updatePlus(AbstractTreeViewer.java:1875) at org.eclipse.jface.viewers.AbstractTreeViewer.internalRefresh(AbstractTreeViewer.java:1235) at org.eclipse.jface.viewers.AbstractTreeViewer.internalRefresh(AbstractTreeViewer.java:1201) at org.eclipse.jface.viewers.AbstractTreeViewer.internalRefresh(AbstractTreeViewer.java:1188) at org.eclipse.jface.viewers.StructuredViewer$7.run(StructuredViewer.java:1264) at org.eclipse.jface.viewers.StructuredViewer.preservingSelection(StructuredViewer.java:1201) at org.eclipse.jface.viewers.StructuredViewer.refresh(StructuredViewer.java:1262) at org.eclipse.jface.tests.viewers.TestModelContentProvider.doStructureChange(TestModelContentProvider.java:78) at org.eclipse.jface.tests.viewers.TestModelContentProvider.testModelChanged(TestModelContentProvider.java:130) at org.eclipse.jface.tests.viewers.TestModel.fireModelChanged(TestModel.java:38) at org.eclipse.jface.tests.viewers.TestElement.addChild(TestElement.java:69) at org.eclipse.jface.tests.viewers.TestElement.addChild(TestElement.java:63) at org.eclipse.jface.tests.viewers.AbstractTreeViewerTest.testRefreshWithAddedChildren(AbstractTreeViewerTest.java:161) at org.eclipse.test.EclipseTestRunner.run(EclipseTestRunner.java:330) at org.eclipse.test.EclipseTestRunner.run(EclipseTestRunner.java:204) at org.eclipse.test.CoreTestApplication.runTests(CoreTestApplication.java:35) at org.eclipse.test.CoreTestApplication.run(CoreTestApplication.java:31) at org.eclipse.core.internal.runtime.PlatformActivator$1.run(PlatformActivator.java:226) at org.eclipse.core.runtime.adaptor.EclipseStarter.run(EclipseStarter.java:386) at org.eclipse.core.runtime.adaptor.EclipseStarter.run(EclipseStarter.java:165) at org.eclipse.core.launcher.Main.invokeFramework(Main.java:338) at org.eclipse.core.launcher.Main.basicRun(Main.java:282) at org.eclipse.core.launcher.Main.run(Main.java:977) at org.eclipse.core.launcher.Main.main(Main.java:952)";
		description = description.replace("&amp;", "&");
		description = description.replace("&quot;", "\"");
		description = description.replace("&lt;", "<");
		description = description.replace("&gt;", ">");
		
		bug.setDescription(description);
		classNames = bugCorpusCreator.extractClassName(bug.getDescription(), bug.getID());
		for(int i = 0; i < classNames.size(); i++) {
			System.out.printf("%d: %s\n", i + 1, classNames.get(i));
		}
		assertEquals("java.lang.System", classNames.get(0));
		assertEquals("org.eclipse.swt.widgets.Tree", classNames.get(1));
		assertEquals("org.eclipse.swt.widgets.TreeItem", classNames.get(2));
		assertEquals("org.eclipse.swt.widgets.TreeItem", classNames.get(3));
		assertEquals("org.eclipse.jface.viewers.TreeViewer", classNames.get(4));
		assertEquals("org.eclipse.jface.viewers.AbstractTreeViewer", classNames.get(5));
		assertEquals("org.eclipse.jface.viewers.AbstractTreeViewer", classNames.get(6));
		assertEquals("org.eclipse.jface.viewers.AbstractTreeViewer", classNames.get(7));
		assertEquals("org.eclipse.jface.viewers.AbstractTreeViewer", classNames.get(8));
		assertEquals("org.eclipse.jface.viewers.StructuredViewer", classNames.get(9));
		

		// Bug ID: 122580, Project: aspectj
		bug.setID(122580);
		
		description = "Stack Trace: " +
			"java.lang.IllegalArgumentException: null kind " +
				" at org.aspectj.bridge.Message.&lt;init&gt;(Ljava/lang/String;Ljava/lang/String;Lorg/aspectj/bridge/IMessage$Kind;Lorg/aspectj/bridge/ISourceLocation;Ljava/lang/Throwable;[Lorg/aspectj/bridge/ISourceLocation;ZIII)V(Message.java:89)" +
				" at org.aspectj.bridge.Message.&lt;init&gt;(Ljava/lang/String;Ljava/lang/String;Lorg/aspectj/bridge/IMessage$Kind;Lorg/aspectj/bridge/ISourceLocation;Ljava/lang/Throwable;[Lorg/aspectj/bridge/ISourceLocation;)V(Message.java:67)" +
				" at org.aspectj.bridge.Message.&lt;init&gt;(Ljava/lang/String;Lorg/aspectj/bridge/IMessage$Kind;Ljava/lang/Throwable;Lorg/aspectj/bridge/ISourceLocation;)V(Message.java:110)" +
				" at org.aspectj.bridge.MessageUtil.info(Ljava/lang/String;)Lorg/aspectj/bridge/IMessage;(MessageUtil.java:211)" +
				" at org.aspectj.bridge.MessageUtil.info(Lorg/aspectj/bridge/IMessageHandler;Ljava/lang/String;)Z(MessageUtil.java:98)" +
				" at org.aspectj.weaver.tools.WeavingAdaptor.info(Ljava/lang/String;)Z(WeavingAdaptor.java:343)" +
				" at org.aspectj.weaver.tools.WeavingAdaptor.createMessageHandler()V(WeavingAdaptor.java:168)" +
				" at org.aspectj.weaver.tools.WeavingAdaptor.&lt;init&gt;()V(WeavingAdaptor.java:80)" +
				" at org.aspectj.weaver.loadtime.ClassLoaderWeavingAdaptor.&lt;init&gt;(Ljava/lang/ClassLoader;Lorg/aspectj/weaver/loadtime/IWeavingContext;)V(ClassLoaderWeavingAdaptor.java:70)" +
				" at org.aspectj.weaver.loadtime.Aj$WeaverContainer.getWeaver(Ljava/lang/ClassLoader;Lorg/aspectj/weaver/loadtime/IWeavingContext;)Lorg/aspectj/weaver/tools/WeavingAdaptor;(Aj.java:94)" +
				" at org.aspectj.weaver.loadtime.Aj.preProcess(Ljava/lang/String;[BLjava/lang/ClassLoader;)[B(Aj.java:61)" +
				" at org.aspectj.weaver.loadtime.JRockitAgent.preProcess(Ljava/lang/ClassLoader;Ljava/lang/String;[B)[B(JRockitAgent.java:74)";
		description = description.replace("&amp;", "&");
		description = description.replace("&quot;", "\"");
		description = description.replace("&lt;", "<");
		description = description.replace("&gt;", ">");
		
		bug.setDescription(description);
		classNames = bugCorpusCreator.extractClassName(bug.getDescription(), bug.getID());
		for(int i = 0; i < classNames.size(); i++) {
			System.out.printf("%d: %s\n", i + 1, classNames.get(i));
		}
		assertEquals("org.aspectj.bridge.Message", classNames.get(0));
		assertEquals("org.aspectj.bridge.Message", classNames.get(1));
		assertEquals("org.aspectj.bridge.Message", classNames.get(2));
		assertEquals("org.aspectj.bridge.MessageUtil", classNames.get(3));
		assertEquals("org.aspectj.bridge.MessageUtil", classNames.get(4));
		assertEquals("org.aspectj.weaver.tools.WeavingAdaptor", classNames.get(5));
		assertEquals("org.aspectj.weaver.tools.WeavingAdaptor", classNames.get(6));
		assertEquals("org.aspectj.weaver.tools.WeavingAdaptor", classNames.get(7));
		assertEquals("org.aspectj.weaver.loadtime.ClassLoaderWeavingAdaptor", classNames.get(8));
		assertEquals("org.aspectj.weaver.loadtime.Aj", classNames.get(9));
		assertEquals("org.aspectj.weaver.loadtime.Aj", classNames.get(10));
		assertEquals("org.aspectj.weaver.loadtime.JRockitAgent", classNames.get(11));
		
		
		// Bug ID: 79757, Project: eclipse
		bug.setID(79757);
		description = "!STACK 0 org.osgi.framework.BundleException: Exception in " +
				"org.eclipse.debug.internal.ui.DebugUIPlugin.start() of bundle org.eclipse.debug.ui." +""
						+ " at org.eclipse.osgi.framework.internal.core.BundleContextImpl.startActivator(BundleContextImpl.java:975)"
						+ " at org.eclipse.osgi.framework.internal.core.BundleContextImpl.start(BundleContextImpl.java:937)"
						+ " at org.eclipse.osgi.framework.internal.core.BundleHost.startWorker(BundleHost.java:421)"
						+ " at org.eclipse.osgi.framework.internal.core.AbstractBundle.start(AbstractBundle.java:293)"
						+ " at org.eclipse.core.runtime.adaptor.EclipseClassLoader.findLocalClass(EclipseClassLoader.java(Compiled Code))"
						+ " at org.eclipse.osgi.framework.internal.core.BundleLoader.findLocalClass(BundleLoader.java(Compiled Code))"
						+ " at org.eclipse.osgi.framework.internal.core.BundleLoader.requireClass(BundleLoader.java(Inlined Compiled Code))"
						+ " at org.eclipse.osgi.framework.internal.core.BundleLoader.findRequiredClass(BundleLoader.java(Compiled Code))"
						+ " at org.eclipse.osgi.framework.internal.core.BundleLoader.findClass(BundleLoader.java(Compiled Code))"
						+ " at org.eclipse.osgi.framework.adaptor.core.AbstractClassLoader.loadClass(AbstractClassLoader.java(Compiled Code))"
						+ " at java.lang.ClassLoader.loadClass(ClassLoader.java(Compiled Code))"
						+ " at org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil.getResourcesForBuildScope(ExternalToolsUtil.java:180)"
						+ " at org.eclipse.ui.externaltools.internal.model.ExternalToolBuilder.doBuildBasedOnScope(ExternalToolBuilder.java:120)";
		description = description.replace("&amp;", "&");
		description = description.replace("&quot;", "\"");
		description = description.replace("&lt;", "<");
		description = description.replace("&gt;", ">");
		
		bug.setDescription(description);
		classNames = bugCorpusCreator.extractClassName(bug.getDescription(), bug.getID());
		for(int i = 0; i < classNames.size(); i++) {
			System.out.printf("%d: %s\n", i + 1, classNames.get(i));
		}
		assertEquals("org.eclipse.osgi.framework.internal.core.BundleContextImpl", classNames.get(0));
		assertEquals("org.eclipse.osgi.framework.internal.core.BundleContextImpl", classNames.get(1));
		assertEquals("org.eclipse.osgi.framework.internal.core.BundleHost", classNames.get(2));
		assertEquals("org.eclipse.osgi.framework.internal.core.AbstractBundle", classNames.get(3));
		assertEquals("org.eclipse.core.runtime.adaptor.EclipseClassLoader", classNames.get(4));
		assertEquals("org.eclipse.osgi.framework.internal.core.BundleLoader", classNames.get(5));
		assertEquals("org.eclipse.osgi.framework.internal.core.BundleLoader", classNames.get(6));
		assertEquals("org.eclipse.osgi.framework.internal.core.BundleLoader", classNames.get(7));
		assertEquals("org.eclipse.osgi.framework.internal.core.BundleLoader", classNames.get(8));
		assertEquals("org.eclipse.osgi.framework.adaptor.core.AbstractClassLoader", classNames.get(9));
		assertEquals("java.lang.ClassLoader", classNames.get(10));
		assertEquals("org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil", classNames.get(11));
		assertEquals("org.eclipse.ui.externaltools.internal.model.ExternalToolBuilder", classNames.get(12));
	}
}
