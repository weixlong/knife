package com.wxl.mvp.knife;

import com.wxl.apt_annotation.ApiEvent;
import com.wxl.apt_annotation.Event;

/**
 * create file time : 2020/12/10
 * create user : wxl
 * subscribe :
 */
public class Target {
    private String name;
    private String id;
    private ApiEvent apiEvent;
    private Event event;
    private String lifeKey;
    private Object target;
    private String coverLifeKey;
    private boolean loadChild;
    private String parentName;

    public Target(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ApiEvent getApiEvent() {
        return apiEvent;
    }

    public void setApiEvent(String apiEvent) {
        loadApiEvent(apiEvent);
    }


    private void loadApiEvent(String event){
        switch (event) {
            case "CREATE":
                apiEvent = ApiEvent.CREATE;
                break;
            case "ACTIVITYCREATE":
                apiEvent = ApiEvent.ACTIVITYCREATE;
                break;
            case "START":
                apiEvent = ApiEvent.START;
                break;
            case "RESUME":
                apiEvent = ApiEvent.RESUME;
                break;
            case "PAUSE":
                apiEvent = ApiEvent.PAUSE;
                break;
            case "STOP":
                apiEvent = ApiEvent.STOP;
                break;
            case "DESTROYVIEW":
                apiEvent = ApiEvent.DESTROYVIEW;
                break;
                default: apiEvent = ApiEvent.DESTROY;
        }
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getLifeKey() {
        return lifeKey;
    }

    public void setLifeKey(String lifeKey) {
        this.lifeKey = lifeKey;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public String getCoverLifeKey() {
        return coverLifeKey;
    }

    public void setCoverLifeKey(String coverLifeKey) {
        this.coverLifeKey = coverLifeKey;
    }

    public boolean isLoadChild() {
        return loadChild;
    }

    public void setLoadChild(boolean loadChild) {
        this.loadChild = loadChild;
    }


    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
