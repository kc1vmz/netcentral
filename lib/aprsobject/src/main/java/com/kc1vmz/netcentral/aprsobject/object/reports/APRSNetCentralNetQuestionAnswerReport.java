package com.kc1vmz.netcentral.aprsobject.object.reports;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

public class APRSNetCentralNetQuestionAnswerReport extends APRSNetCentralReport {

    private String questionNumber;
    private String answerText;
    private String senderCallsign;

    public APRSNetCentralNetQuestionAnswerReport(){
        super();
    }
    public APRSNetCentralNetQuestionAnswerReport(String objectName, String senderCallsign, String questionNumber, String answerText) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_NET_ANSWER);
        this.setReportData(senderCallsign+":"+questionNumber+":"+answerText);
        this.setSenderCallsign(senderCallsign);
        this.setQuestionNumber(questionNumber);
        this.setAnswerText(answerText);
    }

    public String getSenderCallsign() {
        return senderCallsign;
    }
    public void setSenderCallsign(String senderCallsign) {
        this.senderCallsign = senderCallsign;
    }
    public String setAnswerText() {
        return answerText;
    }
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    public String getQuestionNumber() {
        return questionNumber;
    }
    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }
    
   
    public static APRSNetCentralNetQuestionAnswerReport isValid(String objectName, String message) {
        APRSNetCentralNetQuestionAnswerReport ret = null;
        if (message != null) {
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_NET_QUESTION)) {
                    String remainder = message.substring(4);
                    String senderCallsign = null;
                    String questionNumber = null;

                    int index = remainder.indexOf(":");
                    if (index != -1) {
                        senderCallsign = remainder.substring(0, index);
                        remainder = remainder.substring(index+1);
                    }
                    index = remainder.indexOf(":");
                    if (index != -1) {
                        questionNumber = remainder.substring(0, index);
                        remainder = remainder.substring(index+1);
                    }

                    if ((senderCallsign != null) && (questionNumber != null) && (remainder != null)) {
                        try {
                            ret = new APRSNetCentralNetQuestionAnswerReport(objectName, senderCallsign, questionNumber, remainder);
                        } catch (Exception e) {
                            ret = null;
                        }
                    }
                }
            }
        }
        return ret;
    }
}

