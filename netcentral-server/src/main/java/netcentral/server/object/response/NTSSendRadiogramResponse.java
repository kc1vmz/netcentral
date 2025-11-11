package netcentral.server.object.response;

public class NTSSendRadiogramResponse {
   private String status;
   private boolean success;

   public NTSSendRadiogramResponse() {
   }
   public NTSSendRadiogramResponse(String status) {
      this.status = status;
   }
   public String getStatus() {
      return status;
   }
   public void setStatus(String status) {
      this.status = status;
   }
   public boolean isSuccess() {
      return success;
   }
   public void setSuccess(boolean success) {
      this.success = success;
   }
}