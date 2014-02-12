/*
 * The MIT License
 *
 * Copyright 2012 Jesse Glick.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package hudson.plugins.mercurial;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.tools.ToolProperty;
import hudson.util.StreamTaskListener;

import java.nio.charset.Charset;
import java.util.Collections;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.jvnet.hudson.test.JenkinsRule;

public class HgExeTest {

    @Rule public JenkinsRule j = new JenkinsRule();
    private TaskListener listener;

    @Before public void setUp() {
        listener = new StreamTaskListener(System.out, Charset.defaultCharset());
    }

    @Test public void pathEquals() {
        assertTrue(HgExe.pathEquals("http://nowhere.net/hg/", "http://nowhere.net/hg/"));
        assertTrue(HgExe.pathEquals("http://nowhere.net/hg", "http://nowhere.net/hg/"));
        assertTrue(HgExe.pathEquals("http://nowhere.net/hg/", "http://nowhere.net/hg"));
        assertTrue(HgExe.pathEquals("http://nowhere.net/hg", "http://nowhere.net/hg"));
        assertFalse(HgExe.pathEquals("https://nowhere.net/hg/", "http://nowhere.net/hg/"));
        if (  org.apache.commons.lang.SystemUtils.IS_OS_UNIX ) {
            assertTrue(HgExe.pathEquals("file:/var/hg/stuff", "/var/hg/stuff"));
            assertTrue(HgExe.pathEquals("file:///var/hg/stuff", "/var/hg/stuff"));
            assertFalse(HgExe.pathEquals("file:/var/hg/stuff", "/var/hg/other"));
            assertTrue(HgExe.pathEquals("/var/hg/stuff", "file:/var/hg/stuff"));
            assertTrue(HgExe.pathEquals("/var/hg/stuff", "file:///var/hg/stuff"));
            assertFalse(HgExe.pathEquals("/var/hg/other", "file:/var/hg/stuff"));
        }
    }

    @Test public void withouUseHgrc() throws Exception {
        EnvVars env = new EnvVars();
        MercurialInstallation inst = new MercurialInstallation("usehgrc", "",
                "hg", false, false, false, false, Collections
                        .<ToolProperty<?>> emptyList());
        Launcher launcher = j.jenkins.createLauncher(listener);

        HgExe hgexe = new HgExe(inst, null, launcher, j.jenkins, listener, env);
        assertTrue(env.containsKey("HGPLAIN"));
    }

    @Test public void withUseHgrc() throws Exception {
        EnvVars env = new EnvVars();
        MercurialInstallation inst = new MercurialInstallation("usehgrc", "",
                "hg", false, false, false, true, Collections
                        .<ToolProperty<?>> emptyList());
        Launcher launcher = j.jenkins.createLauncher(listener);

        HgExe hgexe = new HgExe(inst, null, launcher, j.jenkins, listener, env);
        assertFalse(env.containsKey("HGPLAIN"));
    }
}
