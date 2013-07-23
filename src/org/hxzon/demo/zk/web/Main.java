package org.hxzon.demo.zk.web;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class Main extends GenericForwardComposer<Window> {

    private Button logout;

    public void doAfterCompose(Window comp) {
        Session session = Sessions.getCurrent();
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        if (username == null || password == null) {
            Executions.sendRedirect("login.zul");
        }
    }

    public void onClick$logout() {
        Session session = Sessions.getCurrent();
        session.invalidate();
        Executions.sendRedirect("login.zul");
    }
}
