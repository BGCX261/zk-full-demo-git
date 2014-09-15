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
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

public class CfgValueViewer2 implements CfgValueHolder {

    private CfgValue cfgValue;
    //
    private boolean embed;
    private Component view;
    private Component viewParent;
    //
    private Component mainPanel;
    private Component buttonPanel;
    private Button addBtn;
    private Button saveBtn;
    private List<CfgValueHolder> valueHolders;

    public CfgValueViewer2(CfgValue cfgValue, Component viewParent) {
        this(cfgValue, viewParent, false);
    }

    protected CfgValueViewer2(CfgValue cfgValue, Component viewParent, boolean embed) {
        this.cfgValue = cfgValue;
        this.viewParent = viewParent;
        this.embed = embed;
        //
        createView();
        if (!embed) {
            viewParent.appendChild(view);
        }
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
        view = createComboPane();
    }

    private Component createComboPane() {
        valueHolders = new ArrayList<CfgValueHolder>();
        //
        Vlayout pane = new Vlayout();
        Hlayout titleLayout = new Hlayout();

        titleLayout.appendChild(createTitle(cfgValue));
        if (cfgValue.getParent() != null) {
            Button delBtn = createDeleteBtn(cfgValue, pane);
            titleLayout.appendChild(delBtn);
        }
        addBtn = createAddBtn();
        titleLayout.appendChild(addBtn);
        if (cfgValue.isElement()) {
            titleLayout.appendChild(createCopyElementBtn(cfgValue));
        }
        pane.appendChild(titleLayout);
        pane.appendChild(new Space());
        Component body = createBody(cfgValue);
        pane.appendChild(body);
        //
        return pane;
    }

    private Component createBody(CfgValue cfgValue) {
        int type = cfgValue.getCfgInfo().getType();
        if (type == CfgInfo.Type_Struct) {
            return createBody_struct();
        } else {
            return createBody_listOrMap();
        }
    }

    private Component createBody_struct() {
        buttonPanel = new Vlayout();
        //
        mainPanel = new Vlayout();
        ((Vlayout) mainPanel).setSpacing("0.5em");
        //
        CfgInfo cfgInfo = cfgValue.getCfgInfo();
        for (CfgInfo cCfgInfo : cfgInfo.getParts()) {
            CfgValue cCfgValue = cfgValue.getValue(cCfgInfo.getId());
            if (cCfgValue == null) {
                Button btn = newAddPartBtn(cCfgInfo);
                buttonPanel.appendChild(btn);
            } else {
                Component partPane = createPane(cCfgValue);
                mainPanel.appendChild(partPane);
            }
        }
        createSaveBtn();
        addBtn.setVisible(!buttonPanel.getChildren().isEmpty());
        return mainPanel;
    }

    private Component createBody_listOrMap() {
        mainPanel = new Vlayout();
        for (CfgValue cCfgValue : cfgValue.getChildren()) {
            Component elePane = createPane(cCfgValue);
            mainPanel.appendChild(elePane);
        }
        createSaveBtn();
        return mainPanel;
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
        CfgValueViewer2 viewer = new CfgValueViewer2(cfgValue, viewParent, true);
        valueHolders.add(viewer);
        return viewer.getView();
    }

    private Component createTextareaPane(CfgValue cfgValue) {
        CfgValueTextbox inputComp = new CfgValueTextbox(cfgValue);
        valueHolders.add(inputComp);
        Vlayout pane = new Vlayout();
        Hlayout hlayout = new Hlayout();
        Button delBtn = createDeleteBtn(cfgValue, pane);
        hlayout.appendChild(createLabel(cfgValue));
        hlayout.appendChild(delBtn);
        if (cfgValue.isElement()) {
            hlayout.appendChild(createCopyElementBtn(cfgValue));
        }
        pane.appendChild(hlayout);
        pane.appendChild(inputComp);
        return pane;
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
        }
        valueHolders.add((CfgValueHolder) inputComp);
        Hlayout pane = new Hlayout();
        Button delBtn = createDeleteBtn(cfgValue, pane);
        pane.appendChild(createLabel(cfgValue));
        pane.appendChild(inputComp);
        pane.appendChild(delBtn);
        if (cfgValue.isElement()) {
            pane.appendChild(createCopyElementBtn(cfgValue));
        }
        return pane;
    }

    private Component createLinkPane(final CfgValue cfgValue) {
        Hlayout pane = new Hlayout();
        Button btn = new Button();
        btn.setImage("images/easyicon_open.png");
        btn.setTooltiptext("打开");
        btn.addEventListener(Events.ON_CLICK, new EventListener<MouseEvent>() {

            @Override
            public void onEvent(MouseEvent event) throws Exception {
                viewParent.removeChild(view);
                new CfgValueViewer2(cfgValue, viewParent);
            }

        });
        pane.appendChild(createLabel(cfgValue));
        pane.appendChild(btn);
        return pane;
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

    private void deleteValue(Component btn) {
        CfgValue deleteCfgValue = (CfgValue) btn.getAttribute("cfgValue");
        CfgValue parent = deleteCfgValue.getParent();
        int parentType = parent.getCfgInfo().getType();
        if (parentType == CfgInfo.Type_Struct) {
            buttonPanel.appendChild(newAddPartBtn(deleteCfgValue.getCfgInfo()));
            addBtn.setVisible(true);
        }
        parent.removeValue(deleteCfgValue);
        ((Component) btn.getAttribute("partPane")).setParent(null);
    }

    private void addPart(Component btn) {
        CfgInfo cfgInfo = (CfgInfo) btn.getAttribute("cfgInfo");
        CfgValue newPartValue = CfgParser.buildCfgValue(cfgInfo, null, 1, 1);
        btn.setParent(null);
        if (buttonPanel.getChildren().isEmpty()) {
            addBtn.setVisible(false);
        }
        addValue_(newPartValue);
    }

    private void addValue() {
        if (buttonPanel != null) {
            newDialog(buttonPanel, "添加字段");
        } else {
            CfgValue newEle = CfgParser.buildListElementCfgValue(cfgValue, 1);
            addValue_(newEle);
        }
    }

    private void copyValue(Component btn) {
        CfgValue origValue = (CfgValue) btn.getAttribute("cfgValue");
        CfgValue newEle = CfgParser.copy(origValue);
        addValue_(newEle);
    }

    private void addValue_(CfgValue newValue) {
        cfgValue.addValue(newValue);
        if (!embed) {
            mainPanel.removeChild(saveBtn);
        }
        Component elePane = createPane(newValue);
        mainPanel.appendChild(elePane);
        if (!embed) {
            mainPanel.appendChild(saveBtn);
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

    private final EventListener<MouseEvent> CopyValueBtnEventListener = new EventListener<MouseEvent>() {
        @Override
        public void onEvent(MouseEvent event) throws Exception {
            copyValue(event.getTarget());
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

    private Button createDeleteBtn(CfgValue cfgValue, Component partPane) {
        Button btn = new Button();
        btn.setAttribute("cfgValue", cfgValue);
        btn.setImage("images/easyicon_remove.png");
        btn.setTooltiptext("删除");
        btn.addEventListener(Events.ON_CLICK, DeleteValueBtnEventListener);
        btn.setAttribute("partPane", partPane);
        return btn;
    }

    private Button createAddBtn() {
        Button btn = new Button();
        btn.setImage("images/easyicon_add.png");
        btn.setTooltiptext("添加");
        btn.addEventListener(Events.ON_CLICK, AddValueBtnEventListener);
        return btn;
    }

    private Button createCopyElementBtn(CfgValue origValue) {
        Button btn = new Button();
        btn.setAttribute("cfgValue", origValue);
        btn.setImage("images/easyicon_copy.png");
        btn.setTooltiptext("复制");
        btn.addEventListener(Events.ON_CLICK, CopyValueBtnEventListener);
        return btn;
    }

    private Component newDialog(Component content, String title) {
        Window dialog = new Window();
        dialog.appendChild(content);
        dialog.setTitle(title == null ? "hh" : title);
        dialog.setClosable(true);
        dialog.setSizable(true);
        dialog.setWidth("50%");
        dialog.setHeight("50%");
        dialog.setBorder(true);
        dialog.setParent(view);
        dialog.setMode(Window.MODAL);
        return dialog;
    }

    private Label createLabel(CfgValue cfgValue) {
        return new Label(cfgValue.getLabel() + "：");
    }

    private Label createTitle(CfgValue cfgValue) {
        return new Label(cfgValue.getTitle());
    }

    //==============
    private Component getView() {
        return view;
    }
}
