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

    public CfgValueDeleteButton(CfgValue cfgValue) {
        this(cfgValue, null);
    }

    public CfgValueDeleteButton(final CfgValue cfgValue,//
            final CfgValuePanel panel) {
        this.setLabel("删除");
        this.addEventListener(Events.ON_CLICK, new EventListener<MouseEvent>() {

            @Override
            public void onEvent(MouseEvent event) throws Exception {
                CfgValue parent = cfgValue.getParent();
                int parentType = parent.getCfgInfo().getType();
                if (parentType == CfgInfo.Type_Struct) {
                    Component buttonPanel = panel.getMissButtonPanel();
                    buttonPanel.appendChild(new CfgValuePartAddButton(cfgValue.getCfgInfo(), panel));
                    if (buttonPanel.getParent() == null) {
                        buttonPanel.setParent(panel.getMainPanel().getParent());
                    }
                }
                Component titlePanel = CfgValueDeleteButton.this.getParent();
                Component cComponent = titlePanel.getNextSibling();
                parent.removeValue(cfgValue);
                cComponent.getParent().removeChild(cComponent);
                titlePanel.getParent().removeChild(titlePanel);
            }

        });
    }

}
