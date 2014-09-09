package org.hxzon.configdesigner.zk;

import org.hxzon.configdesigner.core.CfgValue;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class CfgValueLink extends Button {

    public CfgValueLink(final CfgValue cfgValue) {
        this.setLabel(cfgValue.getCfgInfo().getLabel());
        this.addEventListener(Events.ON_CLICK, new EventListener<MouseEvent>() {

            @Override
            public void onEvent(MouseEvent event) throws Exception {
                Window dialog = new Window();
                dialog.setParent(CfgValueLink.this.getParent());
                dialog.appendChild(new CfgValuePanel(cfgValue));
                dialog.setMode(Window.MODAL);
                dialog.setTitle(cfgValue.getCfgInfo().getLabel());
                dialog.setClosable(true);
                dialog.setSizable(true);
                dialog.setWidth("50%");
                dialog.setHeight("50%");
            }

        });
    }

}
