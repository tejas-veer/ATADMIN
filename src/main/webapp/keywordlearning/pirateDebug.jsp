<%--
  Created by IntelliJ IDEA.
  User: rohit.aga
  Date: 8/16/18
  Time: 5:37 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="net.media.keywordlearning.Utils.Utils" %>
<%@ page import="net.media.keywordlearning.beans.*" %>
<%@ page import="net.media.keywordlearning.dal.KeywordMaster55DAL" %>
<%@ page import="net.media.keywordlearning.dal.KeywordMasterDAL" %>
<%@ page import="net.media.keywordlearning.servlets.InboundParams" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="net.media.keywordlearning.servlets.PirateDebugController" %>
<%@ page import="java.util.Objects" %>
<%@ page import="com.sun.org.apache.xpath.internal.operations.Bool" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script src="//testadmin.media.net//includes/jquery.js"></script>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.1/themes/base/jquery-ui.css"/>
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="css/bootstrap-responsive.css" rel="stylesheet">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <%
        InboundParams inboundParams = new InboundParams(request);
        PirateDebugInput pirateDebugInput = inboundParams.getPirateInput();
        System.out.print(request.getParameterMap());
    %>
    <script type="text/javascript">
        function csvDownload() {
            csvData = 'data:application/csv;charset=utf-8,' + encodeURIComponent(csvStr);
            console.log(csvStr);
            var anchorExport= document.getElementsByClassName('export')[0];
            anchorExport.setAttribute('download', 'csvTemp.csv');
            anchorExport.setAttribute('href', csvData);
            anchorExport.setAttribute('target', '_blank');
        }

        $(document).ready(function () {
            (document.getElementsByClassName('export')[0]).setAttribute("onclick", "csvDownload()");
        });
    </script>
</head>
<body>
<div id='loadingDiv'></div>
<div id='alertDiv'></div>
<div class="container container-fluid">
    <div class="well well-large">
        <div class="row-fluid">
            <h1 id='headBlock'>Pirate Stats</h1>
        </div>
        <div class="row-fluid">
            <%
                String siteName = inboundParams.getPirateInput().getSiteName();
                if(!Utils.isEmpty(siteName)){
            %>
                    <div class="row-fluid">
                        <h4>Site Name: <%=siteName%> </h4>
                    </div>
            <%
                }
            %>
        </div>
    </div>
</div>
<div class="row-fluid">
    <form class="form-inline" id="siteForm" method="get"  onsubmit="return checkFormSubmission(this);" action="pirateDebug.jsp">
        <fieldset>
            <label for="siteFormSiteName"></label>
            <input type="text" class="span1" style="width: 180px" placeholder="Please enter a Site Name" id="siteFormSiteName"
                   name="siteName" value="<%= Utils.isEmpty(siteName) ? "" : siteName %>" />

            <select name="keywordType" class="span1" style="width: 200px">

                <%
                    Map<Integer, KeywordType> kwdTypeMap = KeywordMasterDAL.getKeywordTypeConfig();
                    for(Map.Entry<Integer, KeywordType> entry: kwdTypeMap.entrySet()){
                %>
                        <option value= <%= entry.getKey() %>    <%=pirateDebugInput.getKeywordType() == entry.getKey() ? "selected" : "" %> >  <%=entry.getKey() + " (" + entry.getValue().getKeywordType() + ")" %></option>
                <%    } %>on

            </select>

            <input type="text" class="span1" placeholder="Please enter a Basis"  style = "width: 150px" id="basis"
                   name="basis" value="<%= Utils.isEmpty(pirateDebugInput.getBasis()) ? "" : pirateDebugInput.getBasis() %>" />

            <select name="urlCount" class = "span1" style = "width: 60px">
                <option value = 10 <%=pirateDebugInput.getUrlCount() == 10 ? "selected" : "" %>> <%= "10" %> </option>
                <option value = 20 <%=pirateDebugInput.getUrlCount() == 20 ? "selected" : "" %>> <%= "20" %> </option>
                <option value = 50 <%=pirateDebugInput.getUrlCount() == 50 ? "selected" : "" %>> <%= "50" %> </option>
                <option value = 100 <%=pirateDebugInput.getUrlCount() == 100 ? "selected" : "" %>> <%= "100" %> </option>
            </select>


            <select name="learnerId" class="span1" style="width: 180px">
                <% List<PirateLearnerId> pirateLearnerList = KeywordMasterDAL.getPirateLearnerId();

                for(PirateLearnerId pirateLearner : pirateLearnerList){ %>
                    <option value=<%=pirateLearner.getLearnerId()%> <%= getLearnerIdIsSelected(pirateDebugInput, pirateLearner.getLearnerId(), pirateLearner.getActive())%> > <%=pirateLearner.getLearnerId() + "-" + pirateLearner.getDescription()%></option>

                <% } %>

            </select>

            <br><br>&nbsp;

            <%--for ad tag--%>
            <input list="ad_tag_list" name="adTagId"  type="text" class="span1" style="width: 180px"
                   <%=pirateDebugInput.getAdTagId() > 0 ? "value =" + pirateDebugInput.getAdTagId() : ""%>
            placeholder="Please enter ad_tag_id" id="adTagId">
            <datalist id="ad_tag_list">
                <% List<DomainAdTag> domainAdTagList = KeywordMaster55DAL.getDomainAdTag();
                    for(DomainAdTag domainAdTag : domainAdTagList){
                        for(String adTag : domainAdTag.getAdTagList()){
                %>
                <option value=<%=adTag%>> <%=domainAdTag.getDomainName() + " -> " + adTag %></option>

                <% } } %>
            </datalist>

            <%--for bucket id--%>
            <select name="bucketId" class="span1" style="width: 180px">
                <% List<GlobalBucket> bucketList = KeywordMasterDAL.getBucketId();

                    for(GlobalBucket globalBucket : bucketList){
                        int bucketId = (globalBucket.getBucketId() == -115) ? -1 : globalBucket.getBucketId();%>
                    <option value=<%=bucketId%> <%= getBucketIdIsSelected(pirateDebugInput, bucketId)%> >
                    <%=bucketId + " - " + globalBucket.getBucketName()%></option>

                <% } %>
            </select>


            <%--<input type="checkbox" name="actualData" value="ActualData"> <?if($actualData == "ActualData") echo 'checked'?>>Actual Data</input>--%>
            <%--<!----%>
            <%--<input type="checkbox" name="canonHash" value="CanonHash" <?if($canonHash == "CanonHash") echo 'checked'?>>Canonical Hash</input>--%>
            <%---->--%>
            <button type="submit" class="btn btn-primary">Get Data</button>
        </fieldset>
    </form>
</div>




<%if (!Utils.isEmpty(siteName) && !Utils.isEmpty(pirateDebugInput.getKeywordType())){%>
<%

    PirateDebugController pirateDebugController = PirateDebugController.getInstance(pirateDebugInput.getSiteName(),pirateDebugInput.getKeywordType(),pirateDebugInput.getBasis(),pirateDebugInput.getUrlCount(), pirateDebugInput.getCanonHash(), 0,
            pirateDebugInput.getLearnerId(), pirateDebugInput.getBucketId() , pirateDebugInput.getAdTagId());
    //check every new
    List<PirateDebugResult> pirateDebugResults = pirateDebugController.getFinalResult();
        if(pirateDebugResults.size() != 0){%>
            <a style="margin-left: 880px;font-weight: bold; font-size: 120%; color: #000000">Pirate Score:
                <%= Utils.formatDouble(pirateDebugController.getCurScore()) %>
            </a>
            <%

            String preUrl = "";
            int flag = 0;
            int count = 0;
            for(PirateDebugResult pirateDebugResult : pirateDebugResults){
                if(pirateDebugResult.getUrl().compareTo(preUrl) != 0){
                    count = 1;
                    preUrl = pirateDebugResult.getUrl();
                    Performance performance = pirateDebugController.getUrlPerformance(pirateDebugResult.getUrl());
                    if(flag == 0){
                        flag = 1;
                    }else{%>
                        <%=closeKwdTableString()%>
                    <%}%>
                    <%=getUrlTableString(pirateDebugResult.getUrl(), performance.getImpression(), performance.getConversion(), performance.getRevenue(), performance.getScaledImpression())%>
                    <%=getKwdTableString()%>
                <%}%>
                <%=addRowToKwdTableString(count, pirateDebugResult.getImpression(), pirateDebugResult.getConversion(), pirateDebugResult.getRevenue(), pirateDebugResult.getScaledImpression(),
                        pirateDebugResult.getScaledConversion(), pirateDebugResult.getPirateScore(), pirateDebugResult.getKwdTypeId(), pirateDebugInput.getKeywordType(),
                        pirateDebugResult.getBasis(), pirateDebugInput.getBasis(), kwdTypeMap)%>
            <%
            count++;
            }
        }
    }
%>

 <%--Misc functions--%>
    <%! public String getLearnerIdIsSelected(PirateDebugInput pirateDebugInput, int curLearnerId, Boolean isSelected){
                    if(Utils.isEmpty(pirateDebugInput.getLearnerId()) && isSelected){
                        return "selected";
                    } else if(pirateDebugInput.getLearnerId() == curLearnerId){
                            return "selected";
                    } else {
                        return "";
                    }
                };
                %>

    <%! public String getBucketIdIsSelected(PirateDebugInput pirateDebugInput, int curBucketId){
                    if(Utils.isEmpty(pirateDebugInput.getBucketId()) && curBucketId == -1){
                        return "selected";
                    }
                    if(pirateDebugInput.getBucketId() == curBucketId){
                        return "selected";
                    } else {
                        return "";
                    }

                };
                %>

    <%!
        public String getUrlTableString(String url, Double impression, Double conversion, Double revenue, Double scaledImpression) {
            String str =
                            "            <table bordercolor=\"#ffffff\" cellspacing=\"0\" cellpadding=\"5\" border=\"0\" width=\"98%\" class=\"UrlTable\">" +
                            "            <thead>" +
                            "            <tr style=\"cursor: pointer\">" +
                            "                <td align=\"center\" style=\"border: 1px solid gray; width: 40%;\"  bgcolor=\"#6699CC\" >" +
                            "                    <span style=\"color:white;font-weight:bold\">URL</span>" +
                            "                </td>" +
                            "                <td align=\"center\" style=\"border: 1px solid gray;\"  bgcolor=\"#6699CC\">" +
                            "                    <span style=\"color:white;font-weight:bold\">Impression</span>" +
                            "                </td>" +
                            "                <td align=\"center\" style=\"border: 1px solid gray;\"  bgcolor=\"#6699CC\">" +
                            "                    <span style=\"color:white;font-weight:bold\">Conversion</span>" +
                            "                </td>" +
                            "                <td align=\"center\" style=\"border: 1px solid gray;\"  bgcolor=\"#6699CC\">" +
                            "                    <span style=\"color:white;font-weight:bold\">Revenue</span>" +
                            "                </td>" +
                            "                <td align=\"center\" style=\"border: 1px solid gray;\"  bgcolor=\"#6699CC\">" +
                            "                    <span style=\"color:white;font-weight:bold\">Scaled Impression</span>" +
                            "                </td>" +
                            "                <td align=\"center\" style=\"border: 1px solid gray;\"  bgcolor=\"#6699CC\">" +
                            "                    <span style=\"color:white;font-weight:bold\">eL2A(%)</span>" +
                            "                </td>" +
                            "                <td style=\"display:none\"></td>" +
                            "            </tr>" +
                            "            </thead>" +
                            "            <tbody>" +
                            "            <tr>" +
                            "                <td align=\"left\" style=\"border: 1px solid gray; font-weight:bold\" class=\"urlValue\">" + url + "</td>" +
                            "                <td align=\"right\" style=\"border: 1px solid gray; font-weight:bold\">" + Utils.formatDouble(impression) + "</td>" +
                            "                <td align=\"right\" style=\"border: 1px solid gray; font-weight:bold\">" + Utils.formatDouble(conversion) + "</td>" +
                            "                <td align=\"right\" style=\"border: 1px solid gray; font-weight:bold\">" + Utils.formatDouble(revenue) + " </td>" +
                            "                <td align=\"right\" style=\"border: 1px solid gray; font-weight:bold\">" + Utils.formatDouble(scaledImpression) + "</td>" +
                            "                <td align=\"right\" style=\"border: 1px solid gray; font-weight:bold\">" + Utils.formatDouble((conversion/scaledImpression) * 100) + "</td>" +
                            "            </tr>";
            return str;
        }
    %>

    <%!
        public String getKwdTableString(){
            String str =
                    "          <tr>" +
                    "                <td style=\"\" colspan=\"10\"  >" +
                    "                    <table style=\"width: 100%;\" class=\"KeyTypeTable\" id='KeywordTypeTable'>" +
                    "                        <thead>" +
                    "                        <tr>" +
                    "                            <td align=\"center\" style=\"border: 1px solid gray; width: 2%;\" height=\"20px\" bgcolor=\"#6699CC\">" +
                    "                                <span onmouseout=\"this.style.fontSize='12px';\" style=\"color:white;font-weight:bold\"></span>" +
                    "                            </td>" +
                    "                            <td align=\"center\" style=\"border: 1px solid gray; width: 25%;\" height=\"20px\" bgcolor=\"#6699CC\">" +
                    "                                <span onmouseout=\"this.style.fontSize='12px';\" style=\"color: white; font-weight: bold; font-size: 12px;\">Keyword Type</span>" +
                    "                            </td>" +
                    "                            <td align=\"center\" style=\"border: 1px solid gray;\" bgcolor=\"#6699CC\">" +
                    "                                <span style=\"color:white;font-weight:bold\">Impression</span>" +
                    "                            </td>" +
                    "                            <td align=\"center\" style=\"border: 1px solid gray;\" bgcolor=\"#6699CC\">" +
                    "                                <span style=\"color:white;font-weight:bold\">Conversion</span>" +
                    "                            </td>" +
                    "                            <td align=\"center\" style=\"border: 1px solid gray;\" bgcolor=\"#6699CC\">" +
                    "                                <span style=\"color:white;font-weight:bold\">Revenue</span>" +
                    "                            </td>" +
                    "                            <td align=\"center\" style=\"border: 1px solid gray;\" bgcolor=\"#6699CC\">" +
                    "                                <span style=\"color:white;font-weight:bold\">Scaled Impression</span>" +
                    "                            </td>" +
                    "                            <td align=\"center\" style=\"border: 1px solid gray;\" bgcolor=\"#6699CC\">" +
                    "                                <span style=\"color:white;font-weight:bold\">RPC</span>" +
                    "                            </td>" +
                    "                            <td align=\"center\" style=\"border: 1px solid gray;\" bgcolor=\"#6699CC\">" +
                    "                                <span style=\"color:white;font-weight:bold\">eL2A(%)</span>" +
                    "                            </td>" +
                    "                            <td align=\"center\" style=\"border: 1px solid gray;\" bgcolor=\"#6699CC\">" +
                    "                                <span style=\"color:white;font-weight:bold\">Pirate Score</span>" +
                    "                            </td>" +
                    "                        </tr>" +
                    "                        </thead>";
                return str;
        }
    %>

    <%!
        public String closeKwdTableString(){
            String str =
                            "                    </table>" +
                            "                </td>" +
                            "            </tr>";
            return str;
        }
    %>

    <%!
        public String addRowToKwdTableString(Integer count, Double impression, Double conversion, Double revenue, Double scaledImpression, Double scaledConversion, Double pirateScore, Integer kwdType, Integer queryKwdType, String basis, String queryBasis, Map<Integer, KeywordType> map){
            String scaledRPM = scaledImpression > 0 ? Utils.formatDouble((scaledConversion * 100) / scaledImpression) : "0";
            if(scaledRPM.equals("NaN")){
                System.out.println(scaledImpression + " " + scaledConversion);
            }
            String rpc = conversion > 0 ? Utils.formatDouble(revenue / conversion) : "0";
            Boolean isCurrentQuery = false;
            String kwdTypeOrBasisName = "";
            if(Utils.isEmpty(queryBasis)) {
                isCurrentQuery = Objects.equals(queryKwdType, kwdType);
                kwdTypeOrBasisName = kwdType +  " -> " + map.get(kwdType).getKeywordType();
            } else {
                isCurrentQuery = Objects.equals(queryKwdType, kwdType) && Objects.equals(basis, queryBasis);
                //set the basis name over here
                //change the queryBasis also
            };
            String str =
                "                        <tbody>" +
                "                            <tr class = \"innerTableRows\" " + (isCurrentQuery ? "bgcolor=\"#add8e6 \"" : "") + ">" +
                "                                <td align=\"left\" style=\"border: 1px solid gray;\" class=\"value\">" + count + "</td>" +
                "                                <td align=\"left\" style=\"border: 1px solid gray;\" class=\"value\">" + kwdTypeOrBasisName + "</td>" +
                "                                <td align=\"right\" style=\"border: 1px solid gray;\" class=\"value\">" + Utils.formatDouble(impression) + "</td>" +
                "                                <td align=\"right\" style=\"border: 1px solid gray;\" class=\"value\">" + Utils.formatDouble(conversion) + "</td>" +
                "                                <td align=\"right\" style=\"border: 1px solid gray;\" class=\"value\">" + Utils.formatDouble(revenue) + "</td>" +
                "                                <td align=\"right\" style=\"border: 1px solid gray;\" class=\"value\">" + Utils.formatDouble(scaledImpression) + "</td>" +
                "                                <td align=\"right\" style=\"border: 1px solid gray;\" class=\"value\">" + rpc + "</td>" +
                "                                <td align=\"right\" style=\"border: 1px solid gray; font-weight:bold\" class=\"value\">" + scaledRPM + "</td>" +
                "                                <td align=\"right\" style=\"border: 1px solid gray;\" class=\"value\">" + Utils.formatDouble(pirateScore) + "</td>" +
                "                            </tr>" +
                "                            <tr>" +
                "                                <td style=\"display: none;\" colspan=\"11\" id=\"kbr2_12013102020131020\"></td>" +
                "                            </tr>" +
                "                        </tbody>";
                return str;
        }
    %>

 </div>
    <script src="js/utility.js"></script>
    <script>
    var csvStr = "<?echo $csvStr;?>";
    </script>


    </body>

    </html>
