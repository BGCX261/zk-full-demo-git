package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.core.CfgValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Button;

@SuppressWarnings("serial")
public class CfgValueDeleteButton extends Button {

    public CfgValueDeleteButton(CfgValue cfgValue, Component component) {
        this(cfgValue, component, null);
    }

    public CfgValueDeleteButton(final CfgValue cfgValue, final Component component,//
            final CfgValuePanel panel) {
        this.setLabel("删除");
        this.addEventListener(Events.ON_CLICK, new EventListener<MouseEvent>() {

            @Override
            public void onEvent(MouseEvent event) throws Exception {
                CfgValue parent = cfgValue.getParent();
                int parentType = parent.getCfgInfo().getType();
                if (parentType == CfgInfo.Type_List || parentType == CfgInfo.Type_Map) {
                    parent.removeValue(cfgValue);
                    component.setParent(null);
                } else if (parentType == CfgInfo.Type_Struct) {
                    Component buttonPanel = panel.getMissButtonPanel();
                    buttonPanel.appendChild(new CfgValuePartAddButton(cfgValue, panel));
                    if (buttonPanel.getParent() == null) {
                        buttonPanel.setParent(panel.getMainPanel().getParent());
                    }
                    cfgValue.setValue(null);
                    Component tmp = component.getParent();
                    component.setParent(null);
                    tmp.invalidate();
                }
            }

        });
    }

}
