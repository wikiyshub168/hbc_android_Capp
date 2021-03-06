package com.hugboga.custom.data.event;

/**
 * event 事件，包含类型和数据
 */
public class EventAction {
    public EventType type;
    public Object data;

    public EventAction(EventType type) {
        this.type = type;
    }

    public EventAction(EventType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public EventType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
