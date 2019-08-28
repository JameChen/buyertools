package com.nahuo.buyertool.uploadtask;

/**
 * ================================================
 * 描    述：具有优先级对象的公共类
 * 修订历史：
 * ================================================
 */
public class PriorityObject<E> {

    public final Priority priority;
    public final E obj;

    public PriorityObject(Priority priority, E obj) {
        this.priority = priority == null ? Priority.DEFAULT : priority;
        this.obj = obj;
    }
}
