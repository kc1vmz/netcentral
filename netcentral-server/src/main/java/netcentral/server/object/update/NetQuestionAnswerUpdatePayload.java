package netcentral.server.object.update;

import netcentral.server.object.NetQuestionAnswer;

public class NetQuestionAnswerUpdatePayload {
    private String id;
    private String action;
    private NetQuestionAnswer object;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public NetQuestionAnswer getObject() {
        return object;
    }
    public void setObject(NetQuestionAnswer object) {
        this.object = object;
    }
}
