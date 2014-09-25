package org.hxzon.admcmd;

import groovy.lang.GroovyShell;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GroovyShellUtil {

    private static final Queue<GroovyShell> shells = new ConcurrentLinkedQueue<GroovyShell>();

    public static GroovyShell getShell() {
        GroovyShell shell = shells.poll();
        if (shell == null) {
            shell = new GroovyShell();
        }
        return shell;
    }

    public static void returnShell(GroovyShell shell) {
        shell.getContext().getVariables().clear();
        shells.add(shell);
    }

}
