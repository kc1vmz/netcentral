package netcentral.server.object.response;


public class ObjectCreateResponseExternal {
   private String status;
   private String type;

   public ObjectCreateResponseExternal() {
   }

   public ObjectCreateResponseExternal(String status, String type) {
    this.status = status;
    this.type = type;
   }

   public String getStatus() {
    return status;
   }

   public void setStatus(String status) {
    this.status = status;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

}