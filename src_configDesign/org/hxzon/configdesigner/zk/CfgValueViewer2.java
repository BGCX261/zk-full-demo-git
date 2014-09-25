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
    private Component addPartDialog;
    private List<CfgValueHolder> valueHolders;

    //

    public CfgValueViewer2(CfgValue cfgValue, Component viewParent) {
        this(cfgValue, viewParent, false);
    }

    private CfgValueViewer2(CfgValue cfgValue, Component viewParent, boolean embed) {
        this.cfgValue = cfgValue;
        this.viewParent = viewParent;
        this.embed = embed;
        createView();
        //
        if (!embed) {
            Component rootViewOrig = (Component) viewParent.getAttribute("rootView");
            if (rootViewOrig != null) {
                viewParent.removeChild(rootViewOrig);
            }
            viewParent.appendChild(view);
            viewParent.setAttribute("rootView", view);
            viewParent.setAttribute("rootValue", cfgValue);
        }
    }

    private void createView() {
        CfgInfo info = cfgValue.getCfgInfo();
        if (info.isTextArea()) {
            view = createTextareaPane(cfgValue);
            return;
        }
        if (info.getType().isSimple()) {
            view = createSimpleValuePane(cfgValue);
            return;
        }
        //
        view = createComboPane();
    }

    private Component createComboPane() {
        valueHolders = new ArrayList<CfgValueHolder>();
        //
        Window window = new Window();
        Vlayout pane = new Vlayout();
        Hlayout titleLayout = new Hlayout();

        titleLayout.appendChild(embed ? createLabel(cfgValue) : createTitle(cfgValue));
        if (cfgValue.isDeletable()) {
            Button delBtn = createDeleteBtn(cfgValue, window);
            titleLayout.appendChild(delBtn);
        }
        addBtn = createAddBtn();
        titleLayout.appendChild(addBtn);
        if (cfgValue.isElement()) {
            titleLayout.appendChild(createCopyElementBtn(cfgValue, window));
        }
        if (!embed && cfgValue.getParent() != null) {
            Button returnBtn = createReturnBtn();
            titleLayout.appendChild(returnBtn);
        }
        titleLayout.appendChild(createViewDataBtn(cfgValue));
        titleLayout.setStyle("background-color:#eee");
        pane.appendChild(titleLayout);
        pane.appendChild(new Space());
        if (cfgValue.isMapElement()) {
            pane.appendChild(createKeyPane(cfgValue));
        }
        Component body = createBody(cfgValue);
        pane.appendChild(body);
        //
        window.appendChild(pane);
        window.setBorder(true);
        return window;
    }

    private Component createBody(CfgValue cfgValue) {
        if (cfgValue.getCfgInfo().getType().isStruct()) {
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
        for (CfgInfo cCfgInfo : cfgInfo.getPartsInfo()) {
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
            mainPanel.setAttribute("saveBtn", saveBtn);
        }
    }

    private Component createPane(CfgValue cfgValue) {
        CfgInfo info = cfgValue.getCfgInfo();
        if (info.isTextArea()) {
            return createTextareaPane(cfgValue);
        }
        if (info.getType().isSimple()) {
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
        CfgValueInputCompHolder inputCompHolder = new CfgValueInputCompHolder(cfgValue);
        valueHolders.add(inputCompHolder);
        Vlayout pane = new Vlayout();
        Hlayout hlayout = new Hlayout();
        hlayout.appendChild(createLabel(cfgValue));
        if (cfgValue.isDeletable()) {
            hlayout.appendChild(createDeleteBtn(cfgValue, pane));
        }
        if (cfgValue.isElement()) {
            hlayout.appendChild(createCopyElementBtn(cfgValue, pane));
        }
        pane.appendChild(hlayout);
        if (cfgValue.isMapElement()) {
            pane.appendChild(createKeyPane(cfgValue));
        }
        pane.appendChild(inputCompHolder.getInputComponent());
        return pane;
    }

    private Component createSimpleValuePane(CfgValue cfgValue) {
        CfgValueInputCompHolder inputCompHolder = new CfgValueInputCompHolder(cfgValue);
        valueHolders.add(inputCompHolder);
        Hlayout pane = new Hlayout();
        if (cfgValue.isMapElement()) {
            pane.appendChild(createKeyPane(cfgValue));
        } else {
            pane.appendChild(createLabel(cfgValue));
        }
        pane.appendChild(inputCompHolder.getInputComponent());
        if (cfgValue.isDeletable()) {
            pane.appendChild(createDeleteBtn(cfgValue, pane));
        }
        if (cfgValue.isElement()) {
            pane.appendChild(createCopyElementBtn(cfgValue, pane));
        }
        return pane;
    }

    private Component createLinkPane(CfgValue cfgValue) {
        Hlayout pane = new Hlayout();
        //pane.appendChild(createLabel(cfgValue));
        pane.appendChild(createEnterBtn(cfgValue));
        if (cfgValue.isDeletable()) {
            pane.appendChild(createDeleteBtn(cfgValue, pane));
        }
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
        CfgValue rootValue = (CfgValue) viewParent.getAttribute("rootValue");
        if (deleteCfgValue == rootValue) {
            CfgValue parentCfgValue = deleteCfgValue.getParent();
            parentCfgValue.removeValue(deleteCfgValue);
            new CfgValueViewer2(parentCfgValue, viewParent);
            return;
        }

        CfgValue parent = deleteCfgValue.getParent();
        if (parent.getCfgInfo().getType().isStruct()) {
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
            addPartDialog.setVisible(false);
        }
        addPartValue_(newPartValue);
    }

    private void addValue() {
        if (buttonPanel != null) {
            //复用buttonPanel，所以cfgInfo新增字段时，不会立即反映出来，需刷新页面
            //更新buttonPanel只解决新增“可选字段”的问题，新增“必填”字段，需要更新view面板
            //所以，让用户自己刷新页面，毕竟修改模式的情况不频繁
            //另一用户添加或删除了字段（或元素），目前也需要用户自己刷新页面
            addPartDialog = createDialog(buttonPanel, "添加字段");
        } else {
            CfgValue newEle = CfgParser.buildListElementCfgValue(cfgValue, 1);
            if (newEle.isMapElement()) {
                newEle.setKey("_new");
            }
            String idPrefix = newEle.getCfgInfo().getIdPrefix();
            String keyKey = newEle.getCfgInfo().getKeyKey();
            if (idPrefix != null && keyKey != null) {
                CfgValue keyValue = newEle.findCfgValue(keyKey);
                if (keyValue != null) {
                    String nextId = idPrefix + CfgParser.nextEntityId(idPrefix);
                    keyValue.setValue(nextId);
                    newEle.setKey(nextId);
                }
            }
            addPartValue_(newEle);
        }
    }

    private void addPartValue_(CfgValue newPartValue) {
        cfgValue.addValue(newPartValue);
        if (!embed) {
            mainPanel.removeChild(saveBtn);
        }
        Component partPane = createPane(newPartValue);
        mainPanel.appendChild(partPane);
        if (!embed) {
            mainPanel.appendChild(saveBtn);
        }
    }

    private void copyValue(Component btn) {
        CfgValue origValue = (CfgValue) btn.getAttribute("cfgValue");
        CfgValue newValue = CfgParser.copy(origValue);
        if (origValue.isMapElement()) {
            newValue.setKey(newValue.getKey() + "_copy");
        }
        String idPrefix = newValue.getCfgInfo().getIdPrefix();
        String keyKey = newValue.getCfgInfo().getKeyKey();
        if (idPrefix != null && keyKey != null) {
            CfgValue keyValue = newValue.findCfgValue(keyKey);
            if (keyValue != null) {
                String nextId = idPrefix + CfgParser.nextEntityId(idPrefix);
                keyValue.setValue(nextId);
                newValue.setKey(nextId);
            }
        }
        origValue.getParent().addValue(newValue);
        //
        CfgValue rootValue = (CfgValue) viewParent.getAttribute("rootValue");
        if (origValue == rootValue) {
            new CfgValueViewer2(newValue, viewParent);
            return;
        }
        //
        Component elePane = createPane(newValue);
        Component origValuePane = (Component) btn.getAttribute("partPane");
        Component parentComp = origValuePane.getParent();
        Component saveBtn = (Component) parentComp.getAttribute("saveBtn");
        if (saveBtn != null) {
            parentComp.removeChild(saveBtn);
        }
        parentComp.appendChild(elePane);
        if (saveBtn != null) {
            parentComp.appendChild(saveBtn);
        }
    }

    private void returnParent() {
        CfgValue parent = cfgValue.getParent();
        new CfgValueViewer2(parent, viewParent);
    }

    private void enter(Component btn) {
        CfgValue cfgValue = (CfgValue) btn.getAttribute("cfgValue");
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

    private Button createCopyElementBtn(CfgValue origValue, Component partPane) {
        Button btn = new Button();
        btn.setAttribute("cfgValue", origValue);
        btn.setImage("images/easyicon_copy.png");
        btn.setTooltiptext("复制");
        btn.addEventListener(Events.ON_CLICK, CopyValueBtnEventListener);
        btn.setAttribute("partPane", partPane);
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
        btn.setLabel(cfgValue.getLabel());
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
