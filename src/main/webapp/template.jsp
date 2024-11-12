<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="net.media.autotemplate.bean.Pair" %>
<%@ page import="net.media.autotemplate.dal.db.AutoTemplateDAL" %>
<%@ page import="net.media.autotemplate.util.Util" %>
<%@ page import="net.media.database.DatabaseException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Objects" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
</head>
<body>
<%
    String templateTag = null;
    String template = request.getParameter("tid");
    if (Objects.isNull(template)) {
        String framework = request.getParameter("fid");
        if (Objects.nonNull(framework)) {
            try {
                List<Pair<String, String>> frameworks = AutoTemplateDAL.getFrameworkDefaultTemplates();
                for (Pair<String, String> s : frameworks) {
                    if (s.first.equals(framework.trim())) {
                        template = s.second;
                        System.out.println(s.first + " " + framework);
                        break;
                    }

                }
            } catch (DatabaseException e) {
                template = null;
            }
        }
    }
    templateTag = Util.isStringSet(template) ? AutoTemplateDAL.getTemplateKey(template) : null;
    if (!Util.isStringSet(templateTag)) { %>
<%="No Template Key Exists, something might be wrong."%>
<%} else {%>
<script id="mNCC" language="javascript">
    medianet_width = "<%=request.getParameter("width")%>";
    medianet_height = "<%=request.getParameter("height")%>";
    medianet_crid = "511568574";
    medianet_versionId = "3111299";
    medianet_requrl = "https://auto-template-test.com/templateTest#8CU83IV56";
    medianet_bid = "212388"
</script>
<script src="https://contextual.media.net/nmedianet.js?cid=8CU83IV56&tpid=<%=templateTag%>"></script>
<%}%>
</body>
</html>