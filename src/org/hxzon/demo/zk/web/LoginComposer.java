package org.hxzon.demo.zk.web;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class LoginComposer extends GenericForwardComposer<Window> {

    private Textbox username;
    private Textbox password;
    private Button login;

    public void onClick$login(Event event) {
        String usernameV = username.getValue();
        String passwordV = password.getValue();
        if (usernameV.equals("admin") && passwordV.equals("admin")) {
            Session s = Sessions.getCurrent();
            s.setAttribute("username", usernameV);
            s.setAttribute("password", passwordV);
            Executions.sendRedirect("main.zul");
        } else {
            Executions.sendRedirect("login.zul");
        }
    }
}
