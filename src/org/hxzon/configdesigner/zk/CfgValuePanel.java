package org.hxzon.configdesigner.zk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.core.CfgParser;
import org.hxzon.configdesigner.core.CfgValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Vlayout;

@SuppressWarnings("serial")
public class CfgValuePanel extends Panel {

    private CfgValue cfgValue;
    //
    private Component mainPanel;
    private Component buttonPanel;
    private Button saveBtn;
    private boolean embed;
    private List<Component> inputComps = new ArrayList<Component>();
    private Map<Button, Component> delBtnMaps = new HashMap<Button, Component>();

    public CfgValuePanel(CfgValue cfgValue) {
        this(cfgValue, false);
    }

    protected CfgValuePanel(CfgValue cfgValue, boolean embed) {
        this.cfgValue = cfgValue;
        this.embed = embed;
        init();
        //this.setStyle("border:1px solid black;padding:2px 2px");
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
                mainPanel.appendChild(cComponent);
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
        if (!missValues.isEmpty()) {
            for (CfgInfo cCfgInfo : missValues) {
                Button btn = newAddPartBtn(cCfgInfo);
                buttonPanel.appendChild(btn);
            }
        }
        Hlayout layout = new Hlayout();
        layout.appendChild(mainPanel);
        Div space = new Div();
        space.setWidth("30px");
        layout.appendChild(space);
        layout.appendChild(buttonPanel);
        pc.appendChild(layout);
    }

    private void initMap(Panelchildren pc) {
        mainPanel = new Vlayout();
        for (CfgValue cCfgValue : cfgValue.getChildren()) {
            Component cComponent = createComponent(cCfgValue);
            mainPanel.appendChild(cComponent);
        }
        pc.appendChild(mainPanel);
    }

    private void initList(Panelchildren pc) {
        initMap(pc);
    }

    public Component createComponent(CfgValue cfgValue) {
        CfgInfo info = cfgValue.getCfgInfo();
        int type = info.getType();
        if (type > CfgInfo.Type_Combo || info.isTextArea()) {
            return createComponent_block(cfgValue);
        } else {
            return createComponent_line(cfgValue);
        }
    }

    private Component createComponent_line(CfgValue cfgValue) {
        CfgInfo info = cfgValue.getCfgInfo();
        int type = info.getType();
        Component inputComp = null;

        switch (type) {
        case CfgInfo.Type_Boolean:
            inputComp = new CfgValueCheckbox(cfgValue);
            break;
        case CfgInfo.Type_Integer:
            inputComp = new CfgValueLongbox(cfgValue);
            break;
        case CfgInfo.Type_Real:
            inputComp = new CfgValueDoublebox(cfgValue);
            break;
        case CfgInfo.Type_String:
            inputComp = new CfgValueTextbox(cfgValue);
            break;
        default:
            inputComp = new Label("未知");
        }
        inputComps.add(inputComp);
        Hlayout layout = new Hlayout();
        Label label = new Label(info.getLabel() + "：");
        Button delBtn = newDeleteValueBtn(cfgValue, layout);
        layout.appendChild(label);
        layout.appendChild(inputComp);
        layout.appendChild(delBtn);
        if (cfgValue.isElement()) {
            layout.appendChild(newCopyElementBtn(cfgValue));
        }
        return layout;
    }

    private Component createComponent_block(CfgValue cfgValue) {
        CfgInfo info = cfgValue.getCfgInfo();
        int type = info.getType();
        Component inputComp = null;
        if (type == CfgInfo.Type_String) {
            inputComp = new CfgValueTextbox(cfgValue);
        } else if (info.isEmbed()) {
            inputComp = new CfgValuePanel(cfgValue, true);
        }
        if (inputComp != null) {
            inputComps.add(inputComp);
            Label label = new Label(info.getLabel() + "：");
            Hlayout hlayout = new Hlayout();
            Vlayout vlayout = new Vlayout();
            Button delBtn = newDeleteValueBtn(cfgValue, vlayout);
            hlayout.appendChild(label);
            hlayout.appendChild(delBtn);
            if (type == CfgInfo.Type_Map || type == CfgInfo.Type_List) {
                hlayout.appendChild(newAddElementBtn(cfgValue));
            }
            if (cfgValue.isElement()) {
                hlayout.appendChild(newCopyElementBtn(cfgValue));
            }
            vlayout.appendChild(inputComp);
            vlayout.appendChild(hlayout);
            return vlayout;
        }
        Component link = new CfgValueLink(cfgValue);
        return link;
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
        mainPanel.appendChild(cComponent);
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
        parent.removeValue(deleteCfgValue);
        delBtnMaps.get(btn).setParent(null);
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
        }
    };

    private Button newAddPartBtn(CfgInfo cfgInfo) {
        Button btn = new CfgInfoButton(cfgInfo);
        btn.setLabel(cfgInfo.getLabel());
        btn.addEventListener(Events.ON_CLICK, AddPartBtnEventListener);
        return btn;
    }

    private Button newDeleteValueBtn(CfgValue cfgValue, Component delComp) {
        Button btn = new CfgValueButton(cfgValue);
        btn.setLabel("删除");
        btn.addEventListener(Events.ON_CLICK, DeleteValueBtnEventListener);
        delBtnMaps.put(btn, delComp);
        return btn;
    }

    private Button newAddElementBtn(CfgValue parent) {
        Button btn = new Button();
        btn.setLabel("添加元素");
        return btn;
    }

    private Button newCopyElementBtn(CfgValue origValue) {
        Button btn = new Button();
        btn.setLabel("复制元素");
        return btn;
    }

}
