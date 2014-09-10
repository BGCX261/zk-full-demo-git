package org.hxzon.configdesigner.zk;

import java.util.ArrayList;
import java.util.List;

import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.core.CfgParser;
import org.hxzon.configdesigner.core.CfgValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
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
    private Component buttonPanel;
    private Button saveBtn;
    private boolean embed;

    public CfgValuePanel(CfgValue cfgValue) {
        this(cfgValue, false);
    }

    protected CfgValuePanel(CfgValue cfgValue, boolean embed) {
        this.cfgValue = cfgValue;
        this.embed = embed;
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
        buttonPanel = new Vlayout();
        buttonPanel.appendChild(new Label("添加配置选项"));
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
                Component cComponent = createComponent(cCfgValue);
                if (cCfgValue.getCfgInfo().isEmbed()) {
                    Label label = new Label(cCfgValue.getLabel());
                    Button delBtn = newDeleteValueBtn(cCfgValue);
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
        if (!embed) {
            saveBtn = new Button("保存");
            saveBtn.addEventListener(Events.ON_CLICK, new EventListener<MouseEvent>() {

                @Override
                public void onEvent(MouseEvent event) throws Exception {
                    saveValue();
                }

            });
            mainPanel.appendChild(saveBtn);
        }
        hl.appendChild(mainPanel);
        hl.appendChild(new Splitter());
        if (!missValues.isEmpty()) {
            for (CfgInfo cCfgInfo : missValues) {
                Button btn = newAddPartBtn(cCfgInfo);
                buttonPanel.appendChild(btn);
            }
            hl.appendChild(buttonPanel);
        }
        pc.appendChild(hl);
    }

    private void initMap(Panelchildren pc) {
        buttonPanel = new Vlayout();
        buttonPanel.appendChild(newAddElementBtn());
        buttonPanel.appendChild(newCopyElementBtn());
        buttonPanel.appendChild(newDeleteElementBtn());
        Hbox hl = new Hbox();
        mainPanel = new Vlayout();
        for (CfgValue cCfgValue : cfgValue.getChildren()) {
            Component cComponent = createComponent(cCfgValue);
            if (cCfgValue.getCfgInfo().isEmbed()) {
                Label label = new Label(cCfgValue.getLabel());
                Button delBtn = newDeleteValueBtn(cCfgValue);
                Hlayout cTitlePanel = new Hlayout();
                cTitlePanel.appendChild(label);
                cTitlePanel.appendChild(delBtn);
                mainPanel.appendChild(cTitlePanel);
                mainPanel.appendChild(cComponent);
            } else {
                mainPanel.appendChild(cComponent);
            }
        }
        hl.appendChild(mainPanel);
        hl.appendChild(new Splitter());
        hl.appendChild(buttonPanel);
        pc.appendChild(hl);
    }

    private void initList(Panelchildren pc) {
        initMap(pc);
    }

    public Component createComponent(CfgValue cfgValue) {
        CfgInfo info = cfgValue.getCfgInfo();
        int type = info.getType();
        switch (type) {
        case CfgInfo.Type_Boolean:
            return new CfgValueCheckbox(cfgValue);
        case CfgInfo.Type_Integer:
            return new CfgValueLongbox(cfgValue);
        case CfgInfo.Type_Real:
            return new CfgValueDoublebox(cfgValue);
        case CfgInfo.Type_String:
            return new CfgValueTextbox(cfgValue);
        case CfgInfo.Type_Struct:
        case CfgInfo.Type_List:
        case CfgInfo.Type_Map:
            return info.isEmbed() ? new CfgValuePanel(cfgValue, true) : new CfgValueLink(cfgValue);
        default:
            return new Label();
        }
    }

    //==============

    public void saveValue() {
        for (Component c : mainPanel.getChildren()) {
            if (c instanceof CfgValuePanel) {
                ((CfgValuePanel) c).saveValue();
            } else if (c instanceof CfgValueCheckbox) {
                ((CfgValueCheckbox) c).saveValue();
            } else if (c instanceof CfgValueLongbox) {
                ((CfgValueLongbox) c).saveValue();
            } else if (c instanceof CfgValueDoublebox) {
                ((CfgValueDoublebox) c).saveValue();
            } else if (c instanceof CfgValueTextbox) {
                ((CfgValueTextbox) c).saveValue();
            }
        }
    }

    private void addPart(CfgInfo cfgInfo) {
        if (!embed) {
            mainPanel.removeChild(saveBtn);
        }
        CfgValue newPartCfgValue = CfgParser.buildCfgValue(cfgInfo, null, 1, 1);
        //newPartCfgValue.setParent(cfgValue);
        cfgValue.addValue(newPartCfgValue);
        Component cComponent = createComponent(newPartCfgValue);
        if (cfgInfo.isEmbed()) {
            Label label = new Label(newPartCfgValue.getLabel());
            Button delBtn = newDeleteValueBtn(newPartCfgValue);
            Hlayout cTitilePanel = new Hlayout();
            cTitilePanel.appendChild(label);
            cTitilePanel.appendChild(delBtn);
            mainPanel.appendChild(cTitilePanel);
            mainPanel.appendChild(cComponent);
        } else {
            mainPanel.appendChild(cComponent);
        }
        if (!embed) {
            mainPanel.appendChild(saveBtn);
        }
    }

    private void deleteValue(CfgValueButton btn) {
        CfgValue deleteCfgValue = btn.getCfgValue();
        CfgValue parent = deleteCfgValue.getParent();
        int parentType = parent.getCfgInfo().getType();
        if (parentType == CfgInfo.Type_Struct) {
            buttonPanel.appendChild(newAddPartBtn(deleteCfgValue.getCfgInfo()));
            if (buttonPanel.getParent() == null) {
                buttonPanel.setParent(mainPanel.getParent());
            }
        }
        Component titlePanel = btn.getParent();
        Component cComponent = titlePanel.getNextSibling();
        parent.removeValue(deleteCfgValue);
        cComponent.getParent().removeChild(cComponent);
        titlePanel.getParent().removeChild(titlePanel);
    }

    private final EventListener<MouseEvent> AddPartBtnEventListener = new EventListener<MouseEvent>() {
        @Override
        public void onEvent(MouseEvent event) throws Exception {
            CfgInfoButton btn = (CfgInfoButton) event.getTarget();
            addPart(btn.getCfgInfo());
            btn.setParent(null);
        }
    };

    private final EventListener<MouseEvent> DeleteValueBtnEventListener = new EventListener<MouseEvent>() {
        @Override
        public void onEvent(MouseEvent event) throws Exception {
            CfgValueButton btn = (CfgValueButton) event.getTarget();
            deleteValue(btn);
            btn.setParent(null);
        }
    };

    private Button newAddPartBtn(CfgInfo cfgInfo) {
        Button btn = new CfgInfoButton(cfgInfo);
        btn.setLabel(cfgInfo.getLabel());
        btn.addEventListener(Events.ON_CLICK, AddPartBtnEventListener);
        return btn;
    }

    private Button newDeleteValueBtn(CfgValue cfgValue) {
        Button btn = new CfgValueButton(cfgValue);
        btn.setLabel("删除");
        btn.addEventListener(Events.ON_CLICK, DeleteValueBtnEventListener);
        return btn;
    }

    private Button newAddElementBtn() {
        Button btn = new Button();
        btn.setLabel("添加元素");
        return btn;
    }

    private Button newCopyElementBtn() {
        Button btn = new Button();
        btn.setLabel("复制元素");
        return btn;
    }

    private Button newDeleteElementBtn() {
        Button btn = new Button();
        btn.setLabel("删除元素");
        return btn;
    }

}
