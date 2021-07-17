package com.gui.widget.addresslistfragment.model;

import java.util.List;

public class ChatAddressGroupModel extends TreeNode {

    private int childCount;

    private List<ChatAddressGroupModel> childGroup;
    private List<ChatAddressUserModel> childUser;

    private boolean isExpand = false;

    private String groupName;
    private String groupNumber;

    public int getChildCount() {
        return childCount;
    }

    private void calcuChildCount() {
        int groupSize = childGroup == null ? 0 : childGroup.size();
        int userSize = childUser == null ? 0 : childUser.size();
        int oldCount = this.childCount;
        this.childCount = groupSize + userSize;
        if (parent != null) {
            parent.addChildCount(this.childCount - oldCount);
        }
    }

    private void addChildCount(int childCount) {
        synchronized (this) {
            this.childCount += childCount;
            if (parent != null) {
                parent.addChildCount(childCount);
            }
        }
    }

    public List<ChatAddressGroupModel> getChildGroup() {
        return childGroup;
    }

    public void setChildGroup(List<ChatAddressGroupModel> childGroup) {
        this.childGroup = childGroup;
//        calcuChildCount();
    }

    public List<ChatAddressUserModel> getChildUser() {
        return childUser;
    }

    public void setChildUser(List<ChatAddressUserModel> childUser) {
        this.childUser = childUser;
//        calcuChildCount();
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

}
