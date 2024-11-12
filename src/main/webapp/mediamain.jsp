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
<iframe src="http://contextual.media.net/mediamain.html?&cid=8CU83IV56&pid=8PO8PVF26&cpnet=yVb1sHm-0KKoFeunLBVJxdytJUvp88vF3sbVaJvJmE8%3D&cme=KkKUG6JLaWptcd7ygVoZgW_gowIx32vSl5tlbUoHIlTwG_Hajq4qm8TPaunw8hu-gs5H0j_cu9dNg1SpNWSXZvR1SG6WXrx8XntCSTil8tpIsAxVeTtUbdxoI_0VB68x||NDHRnZ9Gz3KXlI-i9OnZqQ%3D%3D|5gDUJdTGiJzedmq9hanWYg%3D%3D|N7fu2vKt8_s%3D|YdjFvixrVaFa1YV9GubMTxVFBxYviI5qSHcgsD7Ue_g0NJKlK44sPosAbKyag9PBbMDSaslhQ3IxUtTqIFx6IVP2qzK59c51|sRBSg3CPSiQ%3D|&https=1&cc=US&bf=0&vif=1&vi=1530280515619007760&lw=1&ugd=4&ib=0&tpid=<%=templateTag%>&size=<%=request.getParameter("width")%>x<%=request.getParameter("height")%>"
        height="100%"
        marginwidth="0" marginheight="0" scrolling="NO" width="100%"
        frameBorder="0">
</iframe>
<%}%>
</body>
</html>