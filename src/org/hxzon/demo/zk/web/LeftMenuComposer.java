package org.hxzon.demo.zk.web;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;

@SuppressWarnings("serial")
public class LeftMenuComposer extends GenericForwardComposer<Component> {

    private Page page;

    public void onClick$newTabDemo(Event event) {
        Tab newTab = new Tab("newTab");
        newTab.setClosable(true);
        Tabpanel newTabpanel = new Tabpanel();
        newTabpanel.appendChild(new Label("hello"));
        //
        Tabs tabs = (Tabs) page.getFellow("mainTabs");
        Tabpanels tabpanels = (Tabpanels) page.getFellow("mainTabpanels");
        tabs.insertBefore(newTab, null);
        tabpanels.insertBefore(newTabpanel, null);
    }

    public void onClick$tabDemo(Event event) {
        //page path,parent,args
        Component tabDemo = Executions.createComponents("/tabDemo.zul", null, null);
        Tab newTab = new Tab("tabDemo");
        newTab.setClosable(true);
        Tabpanel newTabpanel = new Tabpanel();
        newTabpanel.appendChild(tabDemo);
        //error
        //Tabbox tabbox = (Tabbox) page.getFellow("main");
        //tabbox.insertBefore(newTab, null);
        //tabbox.insertBefore(newTabpanel, null);
        Tabs tabs = (Tabs) page.getFellow("mainTabs");
        Tabpanels tabpanels = (Tabpanels) page.getFellow("mainTabpanels");
        tabs.insertBefore(newTab, null);
        tabpanels.insertBefore(newTabpanel, null);
    }
}
