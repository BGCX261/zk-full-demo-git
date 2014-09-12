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
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

public class CfgValuePaneInfo implements CfgValueHolder {

    private CfgValue cfgValue;
    //
    private boolean embed;
    private Component view;
    private Component mainPanel;
    private Component buttonPanel;
    private Button addBtn;
    private Button saveBtn;
    private List<CfgValueHolder> valueHolders;

    public CfgValuePaneInfo(CfgValue cfgValue) {
        this(cfgValue, false);
    }

    protected CfgValuePaneInfo(CfgValue cfgValue, boolean embed) {
        this.cfgValue = cfgValue;
        this.embed = embed;
        //
        createView();
    }

    private void createView() {
        CfgInfo info = cfgValue.getCfgInfo();
        if (info.isTextArea()) {
            view = createTextareaPane(cfgValue);
            return;
        }
        int type = info.getType();
        if (type < CfgInfo.Type_Combo) {
            view = createSimpleValuePane(cfgValue);
            return;
        }
        //
        valueHolders = new ArrayList<CfgValueHolder>();
        //
        Label label = new Label(cfgValue.getTitle());
        Hlayout titleLayout = new Hlayout();
        Vlayout vlayout = new Vlayout();
        titleLayout.appendChild(label);
        if (cfgValue.getParent() != null) {
            Button delBtn = newDeleteValueBtn(cfgValue, vlayout);
            titleLayout.appendChild(delBtn);
        }
        addBtn = newAddElementBtn();
        titleLayout.appendChild(addBtn);
        if (cfgValue.isElement()) {
            titleLayout.appendChild(newCopyElementBtn(cfgValue));
        }
        vlayout.appendChild(titleLayout);
        Div space = new Div();
        space.setHeight("10px");
        vlayout.appendChild(space);
        Component body = createBody();
        vlayout.appendChild(body);
        view = vlayout;//TODO scrollable
    }

    private Component createBody() {
        CfgInfo cfgInfo = cfgValue.getCfgInfo();
        int type = cfgInfo.getType();
        switch (type) {
        case CfgInfo.Type_Struct:
            return createBody_struct();
        case CfgInfo.Type_Map:
            return createBody_map();
        case CfgInfo.Type_List:
            return createBody_list();
        }
        return null;
    }

    private Component createBody_struct() {
        buttonPanel = new Vlayout();
        buttonPanel.appendChild(new Label("添加配置选项"));
        //
        mainPanel = new Vlayout();
        ((Vlayout) mainPanel).setSpacing("0.5em");
        List<CfgInfo> partsToAdd = new ArrayList<CfgInfo>();
        CfgInfo cfgInfo = cfgValue.getCfgInfo();
        for (CfgInfo cCfgInfo : cfgInfo.getParts()) {
            CfgValue cCfgValue = cfgValue.getValue(cCfgInfo.getId());
            if (cCfgValue == null) {
                partsToAdd.add(cCfgInfo);
            } else {
                Component cComponent = createPane(cCfgValue);
                mainPanel.appendChild(cComponent);
            }
        }
        createSaveBtn();
        for (CfgInfo cCfgInfo : partsToAdd) {
            Button btn = newAddPartBtn(cCfgInfo);
            buttonPanel.appendChild(btn);
        }
        addBtn.setVisible(!partsToAdd.isEmpty());
        return mainPanel;
    }

    private Component createBody_map() {
        mainPanel = new Vlayout();
        for (CfgValue cCfgValue : cfgValue.getChildren()) {
            Component cComponent = createPane(cCfgValue);
            mainPanel.appendChild(cComponent);
        }
        createSaveBtn();
        return mainPanel;
    }

    private Component createBody_list() {
        return createBody_map();
    }

    private void createSaveBtn() {
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
    }

    private Component createPane(CfgValue cfgValue) {
        CfgInfo info = cfgValue.getCfgInfo();
        if (info.isTextArea()) {
            return createTextareaPane(cfgValue);
        }
        int type = info.getType();
        if (type < CfgInfo.Type_Combo) {
            return createSimpleValuePane(cfgValue);
        }
        if (!info.isEmbed()) {
            return createLinkPane(cfgValue);
        }
        CfgValuePaneInfo paneInfo = new CfgValuePaneInfo(cfgValue, true);
        valueHolders.add(paneInfo);
        return paneInfo.getView();
    }

    private Component createTextareaPane(CfgValue cfgValue) {
        CfgInfo info = cfgValue.getCfgInfo();
        CfgValueTextbox inputComp = new CfgValueTextbox(cfgValue);
        valueHolders.add(inputComp);
        Label label = new Label(info.getLabelOrId() + "：");
        Hlayout hlayout = new Hlayout();
        Vlayout vlayout = new Vlayout();
        Button delBtn = newDeleteValueBtn(cfgValue, vlayout);
        hlayout.appendChild(label);
        hlayout.appendChild(delBtn);
        if (cfgValue.isElement()) {
            hlayout.appendChild(newCopyElementBtn(cfgValue));
        }
        vlayout.appendChild(hlayout);
        vlayout.appendChild(inputComp);
        return vlayout;
    }

    private Component createSimpleValuePane(CfgValue cfgValue) {
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

        }
        valueHolders.add((CfgValueHolder) inputComp);
        Hlayout layout = new Hlayout();
        Label label = new Label(info.getLabelOrId() + "：");
        Button delBtn = newDeleteValueBtn(cfgValue, layout);
        layout.appendChild(label);
        layout.appendChild(inputComp);
        layout.appendChild(delBtn);
        if (cfgValue.isElement()) {
            layout.appendChild(newCopyElementBtn(cfgValue));
        }
        return layout;
    }

    private Component createLinkPane(final CfgValue cfgValue) {
        Hlayout layout = new Hlayout();
        Label label = new Label(cfgValue.getCfgInfo().getLabel());
        Button btn = new Button();
        btn.setImage("images/easyicon_open.png");
        btn.setTooltiptext("打开");
        btn.addEventListener(Events.ON_CLICK, new EventListener<MouseEvent>() {

            @Override
            public void onEvent(MouseEvent event) throws Exception {
                newDialog(new CfgValuePaneInfo(cfgValue).getView(),//
                        cfgValue.getCfgInfo().getLabel());

            }

        });
        layout.appendChild(label);
        layout.appendChild(btn);
        return layout;
    }

    //==============

    @Override
    public void saveValue() {
        if (valueHolders != null) {
            for (CfgValueHolder c : valueHolders) {
                c.saveValue();
            }
        }
    }

    private void addPart(Component btn) {
        CfgInfo cfgInfo = (CfgInfo) btn.getAttribute("cfgInfo");
        if (!embed) {
            mainPanel.removeChild(saveBtn);
        }
        CfgValue newPartCfgValue = CfgParser.buildCfgValue(cfgInfo, null, 1, 1);
        cfgValue.addValue(newPartCfgValue);
        Component cComponent = createPane(newPartCfgValue);
        mainPanel.appendChild(cComponent);
        if (!embed) {
            mainPanel.appendChild(saveBtn);
        }
        btn.setParent(null);
        if (buttonPanel.getChildren().size() == 1) {
            addBtn.setVisible(false);
        }
    }

    private void deleteValue(Component btn) {
        CfgValue deleteCfgValue = (CfgValue) btn.getAttribute("cfgValue");
        CfgValue parent = deleteCfgValue.getParent();
        int parentType = parent.getCfgInfo().getType();
        if (parentType == CfgInfo.Type_Struct) {
            buttonPanel.appendChild(newAddPartBtn(deleteCfgValue.getCfgInfo()));
            addBtn.setVisible(true);
        }
        parent.removeValue(deleteCfgValue);
        ((Component) btn.getAttribute("delPane")).setParent(null);
    }

    private void addValue() {
        if (buttonPanel != null) {
            newDialog(buttonPanel, "添加字段");
        } else {
            CfgValue newEle = CfgParser.buildListElementCfgValue(cfgValue, 1);
            if (!embed) {
                mainPanel.removeChild(saveBtn);
            }
            Component cComponent = createPane(newEle);
            mainPanel.appendChild(cComponent);
            if (!embed) {
                mainPanel.appendChild(saveBtn);
            }
        }
    }

    private final EventListener<MouseEvent> AddPartBtnEventListener = new EventListener<MouseEvent>() {
        @Override
        public void onEvent(MouseEvent event) throws Exception {
            addPart(event.getTarget());
        }
    };

    private final EventListener<MouseEvent> DeleteValueBtnEventListener = new EventListener<MouseEvent>() {
        @Override
        public void onEvent(MouseEvent event) throws Exception {
            deleteValue(event.getTarget());
        }
    };

    private final EventListener<MouseEvent> AddValueBtnEventListener = new EventListener<MouseEvent>() {
        @Override
        public void onEvent(MouseEvent event) throws Exception {
            addValue();
        }
    };

    private Button newAddPartBtn(CfgInfo cfgInfo) {
        Button btn = new Button();
        btn.setAttribute("cfgInfo", cfgInfo);
        btn.setLabel(cfgInfo.getLabelOrId());
        btn.setImage("images/easyicon_add.png");
        btn.setTooltiptext("添加");
        btn.addEventListener(Events.ON_CLICK, AddPartBtnEventListener);
        return btn;
    }

    private Button newDeleteValueBtn(CfgValue cfgValue, Component delComp) {
        Button btn = new Button();
        btn.setAttribute("cfgValue", cfgValue);
        btn.setImage("images/easyicon_remove.png");
        btn.setTooltiptext("删除");
        btn.addEventListener(Events.ON_CLICK, DeleteValueBtnEventListener);
        btn.setAttribute("delPane", delComp);
        return btn;
    }

    private Button newAddElementBtn() {
        Button btn = new Button();
        btn.setImage("images/easyicon_add.png");
        btn.setTooltiptext("添加");
        btn.addEventListener(Events.ON_CLICK, AddValueBtnEventListener);
        return btn;
    }

    private Button newCopyElementBtn(CfgValue origValue) {
        Button btn = new Button();
        btn.setImage("images/easyicon_copy.png");
        btn.setTooltiptext("复制");
        return btn;
    }

    private Component newDialog(Component content, String title) {
        Window dialog = new Window();
        dialog.setParent(view);
        dialog.appendChild(content);
        dialog.setMode(Window.MODAL);
        dialog.setTitle(title);
        dialog.setClosable(true);
        dialog.setSizable(true);
        dialog.setWidth("50%");
        dialog.setHeight("50%");
        return dialog;
    }

    //==============
    public Component getView() {
        return view;
    }
}
