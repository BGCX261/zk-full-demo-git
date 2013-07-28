package org.hxzon.demo.zk.web;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

@SuppressWarnings("serial")
public class LeftMenuComposer extends SelectorComposer<Component> {

    @Wire("#tree")
    private Tree tree;

    @Listen("onClick=treeitem")
    public void onClick(Event event) {
        Treeitem treeItem = (Treeitem) event.getTarget();
        String label = treeItem.getLabel();
        String path = treeItem.getValue();
        if (path == null) {
            return;
        }
        //page path,parent,args
        Component newTabContent = Executions.createComponents(path, null, null);
        addTab(label, newTabContent);
    }

    private void addTab(String label, Component newTabContent) {
        Tab newTab = new Tab(label);
        newTab.setClosable(true);
        newTab.setSelected(true);
        Tabpanel newTabpanel = new Tabpanel();
        newTabpanel.appendChild(newTabContent);
        //error
        //Tabbox tabbox = (Tabbox) page.getFellow("main");
        //tabbox.insertBefore(newTab, null);
        //tabbox.insertBefore(newTabpanel, null);
        Page page = tree.getPage();
        Tabs tabs = (Tabs) page.getFellow("mainTabs");
        Tabpanels tabpanels = (Tabpanels) page.getFellow("mainTabpanels");
        tabs.insertBefore(newTab, null);
        tabpanels.insertBefore(newTabpanel, null);
    }

}
