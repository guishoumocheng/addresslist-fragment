package com.gui.widget.addresslistfragment.model;

public abstract class TreeNode {

    ChatAddressGroupModel parent;

    public ChatAddressGroupModel getParent() {
        return parent;
    }

    public void setParent(ChatAddressGroupModel parent) {
        this.parent = parent;
    }

}
