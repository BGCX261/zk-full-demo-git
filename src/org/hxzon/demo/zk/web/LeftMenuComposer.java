package org.hxzon.demo.zk.web;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Treecell;

@SuppressWarnings("serial")
public class LeftMenuComposer extends GenericForwardComposer<Component> {

    private Treecell newTabDemo;
    private Page page;

    public void onClick$newTabDemo(Event event) {
        Tabs tabs = (Tabs) page.getFellow("mainTabs");
        Tab newTab = new Tab("newTab");
        newTab.setClosable(true);
        Tabpanel newTabpanel = new Tabpanel();
        newTabpanel.appendChild(new Label("hello"));
        tabs.insertBefore(newTab, newTabpanel);
    }
}
