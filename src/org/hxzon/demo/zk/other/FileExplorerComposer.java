package org.hxzon.demo.zk.other;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.West;

//import org.zkoss.zkex.zul.West;

@SuppressWarnings("serial")
public class FileExplorerComposer extends GenericForwardComposer<Component> {

    Tree explorerTree;
    Listbox explorerBox;
    West west;

    String root = ".";

    public FileExplorerComposer() {
    }

    public String getRoot() {
        return root;
    }

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        explorerTree.setModel(new ExploerTreeModel(new File(root)));
        explorerTree.setItemRenderer(new ExploerTreeRenderer());
        explorerTree.addEventListener(Events.ON_SELECT, new DirectorySelectListener());
        explorerBox.setItemRenderer(new ExploerBoxRenderer());
        west.setTitle(root);

    }

    class ExploerTreeModel extends AbstractTreeModel<File> {

        public ExploerTreeModel(File root) {
            super(root);
        }

        public File getChild(File parent, int index) {
            File fs[] = parent.listFiles(new DirectoryOnlyFilter());
            return fs == null ? null : fs[index];
        }

        public int getChildCount(File parent) {
            File fs[] = parent.listFiles(new DirectoryOnlyFilter());
            return fs == null ? 0 : fs.length;
        }

        public boolean isLeaf(File node) {
            return getChildCount(node) == 0;
        }
    }

    class ExploerTreeRenderer implements TreeitemRenderer<File> {
        public void render(Treeitem item, File data, int index) throws Exception {
            File f = (File) data;
            Treerow row = new Treerow();
            row.setParent(item);
            Treecell cell1 = new Treecell(f.getName(), "folder.png");
            File fs[] = f.listFiles(new FileOnlyFilter());
            Treecell cell2 = new Treecell(Integer.toString((fs == null ? 0 : fs.length)));
            cell1.setParent(row);
            cell2.setParent(row);
            item.setValue(data);
        }

    }

    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    class ExploerBoxRenderer implements ListitemRenderer<File> {
        public void render(Listitem item, File data, int index) throws Exception {
            File f = (File) data;
            new Listcell(f.getName()).setParent(item);
            new Listcell(Long.toString(f.length())).setParent(item);
            new Listcell(format.format(f.lastModified())).setParent(item);

            Listcell actCell = new Listcell();
            Button btn = new Button("Download");
            btn.setParent(actCell);
            btn.addEventListener(Events.ON_CLICK, new FileDownloadListener(f));

            actCell.setParent(item);

        }
    }

    class DirectorySelectListener implements EventListener<Event> {
        public void onEvent(Event event) throws Exception {
            File f = (File) explorerTree.getSelectedItem().getValue();
            File[] fs = f.listFiles(new FileOnlyFilter());
            explorerBox.setModel(new ListModelList<File>(fs));
        }
    }

    class FileDownloadListener implements EventListener<Event> {
        File file;

        public FileDownloadListener(File file) {
            this.file = file;
        }

        public void onEvent(Event event) throws Exception {
            Filedownload.save(file, null);
        }
    }

    class FileOnlyFilter implements FileFilter {
        public boolean accept(File f) {
            return f.isFile() && !f.isHidden();
        }
    }

    class DirectoryOnlyFilter implements FileFilter {
        public boolean accept(File f) {
            return f.isDirectory() && !f.isHidden();
        }
    }

}
