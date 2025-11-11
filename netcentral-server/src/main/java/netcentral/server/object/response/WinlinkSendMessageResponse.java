package netcentral.server.object.response;

public class WinlinkSendMessageResponse {
   private String status;
   private boolean success;

   public WinlinkSendMessageResponse() {
   }
   public WinlinkSendMessageResponse(String status) {
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