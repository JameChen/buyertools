package com.nahuo.buyertool.model;

/**
 * Created by Administrator on 2017/3/20 0020.
 */
public class LabelItemModel {
    private boolean isCheck;
    private LabelModel label;
    private boolean isSystem;

    public LabelItemModel() {

    }

    public LabelItemModel(boolean isCheck, LabelModel label) {
        this.isCheck = isCheck;
        this.label = label;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public void setLabel(LabelModel label) {
        this.label = label;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
    public boolean isCheck(){return isCheck;}
    public boolean isSystem(){return isSystem;}

    public LabelModel getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof SizeItemModel && this.label != null){
            LabelItemModel cm = (LabelItemModel) o;
            if(cm.getLabel() == null){
                return false;
            }
            if(this.isSystem == cm.isSystem() && this.label.getName().equals(cm.getLabel().getName())){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
