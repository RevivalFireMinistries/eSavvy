package za.org.rfm.beans;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/01
 * Time: 12:27 PM
 */
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import za.org.rfm.model.MenuItem;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;

@ManagedBean(name = "menuController")
@SessionScoped
public class MenuController implements Serializable {
    private TreeNode root;

    private TreeNode selectedNode;

    public MenuController() {
        root = new DefaultTreeNode("Root", null);
        TreeNode node0 = new DefaultTreeNode( new MenuItem("Members","members/viewMembers.faces"), root);
        TreeNode node1 = new DefaultTreeNode( new MenuItem("Finance","viewMembers.faces"), root);
        TreeNode node2 = new DefaultTreeNode( new MenuItem("Reports","viewMembers.faces"), root);

        TreeNode node00 = new DefaultTreeNode(new MenuItem("NEW","members/newMember.faces"), node0);
        TreeNode node01 = new DefaultTreeNode(new MenuItem("VIEW","members/viewMembers.faces"), node0);

    }

    public TreeNode getRoot() {
        return root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void onNodeExpand(NodeExpandEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Expanded", event.getTreeNode().toString());

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onNodeCollapse(NodeCollapseEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Collapsed", event.getTreeNode().toString());

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onNodeSelect(NodeSelectEvent event) {
        try {
            MenuItem menuItem = (MenuItem)event.getTreeNode().getData();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected",menuItem.getUrl());

            FacesContext.getCurrentInstance().addMessage(null, message);
            FacesContext.getCurrentInstance().getExternalContext().redirect(menuItem.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onNodeUnselect(NodeUnselectEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Unselected", event.getTreeNode().toString());

        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
