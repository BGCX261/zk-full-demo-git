package org.hxzon.configdesigner.zk;

import java.util.ArrayList;
import java.util.List;

import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.core.CfgValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Splitter;
import org.zkoss.zul.Vlayout;

@SuppressWarnings("serial")
public class CfgValuePanel extends Panel {

    private CfgValue cfgValue;
    private Component mainPanel;
    private Component missButtonPanel;

    public CfgValuePanel(CfgValue cfgValue) {
        this.cfgValue = cfgValue;
        init();
    }

    private void init() {
        CfgInfo cfgInfo = cfgValue.getCfgInfo();
        int type = cfgInfo.getType();
        Panelchildren pc = new Panelchildren();
        switch (type) {
        case CfgInfo.Type_Struct:
            initStruct(pc);
            break;
        case CfgInfo.Type_Map:
            initMap(pc);
            break;
        case CfgInfo.Type_List:
            initList(pc);
            break;
        }
        this.appendChild(pc);
    }

    private void initStruct(Panelchildren pc) {
        missButtonPanel = new Vlayout();
        missButtonPanel.appendChild(new Label("添加配置选项"));
        Hbox hl = new Hbox();
        //
        mainPanel = new Vlayout();
        List<CfgInfo> missValues = new ArrayList<CfgInfo>();
        CfgInfo cfgInfo = cfgValue.getCfgInfo();
        for (CfgInfo cCfgInfo : cfgInfo.getParts()) {
            CfgValue cCfgValue = cfgValue.getValue(cCfgInfo.getId());
            if (cCfgValue == null) {
                missValues.add(cCfgInfo);
            } else {
                Component cComponent = CfgValueZkUtil.createComponent(cCfgValue);
                if (cCfgValue.getCfgInfo().isEmbed()) {
                    Label label = new Label(cCfgValue.getLabel());
                    Button delBtn = new CfgValueDeleteButton(cCfgValue, this);
                    Hlayout cTitlePanel = new Hlayout();
                    cTitlePanel.appendChild(label);
                    cTitlePanel.appendChild(delBtn);
                    mainPanel.appendChild(cTitlePanel);
                    mainPanel.appendChild(cComponent);
                } else {
                    mainPanel.appendChild(cComponent);
                }
            }
        }
        hl.appendChild(mainPanel);
        hl.appendChild(new Splitter());
        if (!missValues.isEmpty()) {
            for (CfgInfo cCfgInfo : missValues) {
                Button btn = new CfgValuePartAddButton(cCfgInfo, this);
                missButtonPanel.appendChild(btn);
            }
            hl.appendChild(missButtonPanel);
        }
        pc.appendChild(hl);
    }

    private void initMap(Panelchildren pc) {
        mainPanel = new Vlayout();
        for (CfgValue cCfgValue : cfgValue.getChildren()) {
            Component cComponent = CfgValueZkUtil.createComponent(cCfgValue);
            if (cCfgValue.getCfgInfo().isEmbed()) {
                Label label = new Label(cCfgValue.getLabel());
                Button delBtn = new CfgValueDeleteButton(cCfgValue);
                Hlayout cTitlePanel = new Hlayout();
                cTitlePanel.appendChild(label);
                cTitlePanel.appendChild(delBtn);
                mainPanel.appendChild(cTitlePanel);
                mainPanel.appendChild(cComponent);
            } else {
                mainPanel.appendChild(cComponent);
            }
        }
        pc.appendChild(mainPanel);
    }

    private void initList(Panelchildren pc) {
        initList(pc);
    }

    //===============
    public Component getMainPanel() {
        return mainPanel;
    }

    public Component getMissButtonPanel() {
        return missButtonPanel;
    }

    public CfgValue getCfgValue() {
        return cfgValue;
    }

}
