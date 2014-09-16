package org.hxzon.configdesigner.zk;

import java.util.ArrayList;
import java.util.List;

import org.hxzon.configdesigner.core.CfgInfo;
import org.hxzon.configdesigner.core.CfgParser;
import org.hxzon.configdesigner.core.CfgValue;
import org.hxzon.util.json.JSONCollection;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
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

        titleLayout.appendChild(embed ? createLabel(cfgValue) : createTitle(cfgValue));
        if (cfgValue.getParent() != null) {
            Button delBtn = createDeleteBtn(cfgValue, pane);
            titleLayout.appendChild(delBtn);
        }
        addBtn = createAddBtn();
        titleLayout.appendChild(addBtn);
        if (cfgValue.isElement()) {
            titleLayout.appendChild(createCopyElementBtn(cfgValue));
        }
        if (!embed && cfgValue.getParent() != null) {
            Button returnBtn = createReturnBtn();
            titleLayout.appendChild(returnBtn);
        }
        titleLayout.appendChild(createViewDataBtn(cfgValue));
        pane.appendChild(titleLayout);
        pane.appendChild(new Space());
        if (cfgValue.isMapElement()) {
            pane.appendChild(createKeyPane(cfgValue));
        }
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
                Button btn = createAddPartBtn(cCfgInfo);
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
        if (cfgValue.isMapElement()) {
            pane.appendChild(createKeyPane(cfgValue));
        }
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
        if (cfgValue.isMapElement()) {
            pane.appendChild(createKeyPane(cfgValue));
        } else {
            pane.appendChild(createLabel(cfgValue));
        }
        pane.appendChild(inputComp);
        pane.appendChild(delBtn);
        if (cfgValue.isElement()) {
            pane.appendChild(createCopyElementBtn(cfgValue));
        }
        return pane;
    }

    private Component createLinkPane(CfgValue cfgValue) {
        Hlayout pane = new Hlayout();
        pane.appendChild(createLabel(cfgValue));
        pane.appendChild(createDeleteBtn(cfgValue, pane));
        pane.appendChild(createEnterBtn(cfgValue));
        pane.appendChild(createViewDataBtn(cfgValue));
        return pane;
    }

    private Component createKeyPane(CfgValue cfgValue) {
        CfgValueKeybox keyInputComp = new CfgValueKeybox(cfgValue);
        valueHolders.add(keyInputComp);
        Hlayout pane = new Hlayout();
        pane.appendChild(new Label("键："));
        pane.appendChild(keyInputComp);
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
        if (deleteCfgValue == cfgValue) {
            CfgValue parent = deleteCfgValue.getParent();
            parent.removeValue(deleteCfgValue);
            viewParent.removeChild(view);
            new CfgValueViewer2(parent, viewParent);
            return;
        }

        CfgValue parent = deleteCfgValue.getParent();
        int parentType = parent.getCfgInfo().getType();
        if (parentType == CfgInfo.Type_Struct) {
            buttonPanel.appendChild(createAddPartBtn(deleteCfgValue.getCfgInfo()));
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
            createDialog(buttonPanel, "添加字段");
        } else {
            CfgValue newEle = CfgParser.buildListElementCfgValue(cfgValue, 1);
            if (newEle.isMapElement()) {
                newEle.setKey("_new");
            }
            addValue_(newEle);
        }
    }

    private void copyValue(Component btn) {
        CfgValue origValue = (CfgValue) btn.getAttribute("cfgValue");
        CfgValue newValue = CfgParser.copy(origValue);
        if (newValue.isMapElement()) {
            newValue.setKey(newValue.getKey() + "_copy");
        }
        if (origValue == cfgValue) {
            origValue.getParent().addValue(newValue);
            viewParent.removeChild(view);
            new CfgValueViewer2(newValue, viewParent);
            return;
        }
        addValue_(newValue);
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

    private void returnParent() {
        CfgValue parent = cfgValue.getParent();
        viewParent.removeChild(view);
        new CfgValueViewer2(parent, viewParent);
    }

    private void enter(Component btn) {
        CfgValue cfgValue = (CfgValue) btn.getAttribute("cfgValue");
        viewParent.removeChild(view);
        new CfgValueViewer2(cfgValue, viewParent);
    }

    private void viewData(Component btn) {
        CfgValue cfgValue = (CfgValue) btn.getAttribute("cfgValue");
        Object json = CfgParser.toJson(cfgValue, true);
        String jsonStr = json == null ? "" : //
                (json instanceof JSONCollection) ? ((JSONCollection) json).toString(false) : json.toString();
        Textbox v = new Textbox();
        v.setMultiline(true);
        v.setReadonly(true);
        v.setWidth("100%");
        v.setHeight("100%");
        v.setValue(jsonStr);
        createDialog(v, "查看数据");
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

    private final EventListener<MouseEvent> ReturnBtnEventListener = new EventListener<MouseEvent>() {
        @Override
        public void onEvent(MouseEvent event) throws Exception {
            returnParent();
        }
    };

    private final EventListener<MouseEvent> EnterBtnEventListener = new EventListener<MouseEvent>() {
        @Override
        public void onEvent(MouseEvent event) throws Exception {
            enter(event.getTarget());
        }
    };

    private final EventListener<MouseEvent> ViewDataBtnEventListener = new EventListener<MouseEvent>() {
        @Override
        public void onEvent(MouseEvent event) throws Exception {
            viewData(event.getTarget());
        }
    };

    private Button createAddPartBtn(CfgInfo cfgInfo) {
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

    private Button createReturnBtn() {
        Button btn = new Button();
        btn.setImage("images/easyicon_return.png");
        btn.setTooltiptext("返回");
        btn.addEventListener(Events.ON_CLICK, ReturnBtnEventListener);
        return btn;
    }

    private Button createEnterBtn(CfgValue cfgValue) {
        Button btn = new Button();
        btn.setAttribute("cfgValue", cfgValue);
        btn.setImage("images/easyicon_enter.png");
        btn.setTooltiptext("进入");
        btn.addEventListener(Events.ON_CLICK, EnterBtnEventListener);
        return btn;
    }

    private Button createViewDataBtn(CfgValue cfgValue) {
        Button btn = new Button();
        btn.setAttribute("cfgValue", cfgValue);
        btn.setImage("images/easyicon_view.png");
        btn.setTooltiptext("查看数据");
        btn.addEventListener(Events.ON_CLICK, ViewDataBtnEventListener);
        return btn;
    }

    private Component createDialog(Component content, String title) {
        Window dialog = new Window();
        dialog.appendChild(content);
        dialog.setTitle(title);
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
