package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Description:代理分组 2014-7-7下午5:48:53
 */
public class AgentGroup implements Serializable {

    private static final long serialVersionUID = -5810179691238933548L;
    @Expose
    @SerializedName("ID")
    private int               groupId;
    @Expose
    @SerializedName("Name")
    private String            name;
    /** 该分组下的用户数 */
    @Expose
    @SerializedName("UserCount")
    private int               agentNum;

    /** 是否可编辑，默认可编辑的 */
    private boolean           editable         = true;

    private boolean           selected;

    private List<AgentGroup>  subGroups;

    private List<Agent>       groupAgents;

    public AgentGroup() {

    }

    public AgentGroup(int groupId, String name) {
        this.groupId = groupId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAgentNum() {
        return agentNum;
    }

    public void setAgentNum(int agentNum) {
        this.agentNum = agentNum;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public List<AgentGroup> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<AgentGroup> subGroups) {
        this.subGroups = subGroups;
    }

    public List<Agent> getGroupAgents() {
        return groupAgents;
    }

    public void setGroupAgents(List<Agent> groupAgents) {
        this.groupAgents = groupAgents;
    }

}
