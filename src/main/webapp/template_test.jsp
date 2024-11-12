<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="net.media.autotemplate.bean.TemplateRenderingInboundParams" %>
<%@ page import="net.media.autotemplate.util.Util" %>
<%@ page import="net.media.autotemplate.dal.db.AutoTemplateDAL" %>
<%@ page import="net.media.autotemplate.util.TemplateRenderingUtil" %>

<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
</head>
<body>
<%
    TemplateRenderingInboundParams templateRenderingParams = new TemplateRenderingInboundParams(request);
    String templateTag = Util.isStringSet(templateRenderingParams.getTemplate()) ? AutoTemplateDAL.getTemplateKey(templateRenderingParams.getTemplate()) : null;

    if (!Util.isStringSet(templateTag)) {
%>
<%="No Template Key Exists, something might be wrong."%>
<%
} else {
    String pfm_natasset = TemplateRenderingUtil.getNativeAsset(templateRenderingParams);
%>
<script id="mNCC" language="javascript">

    __bdata = "casc%3D2~pt%3D%3F~cttl%3D64addc68c6b608af~dl%3Den~rclcd%3D-1~ru_adg%3D603463~rcf_v2%3D2~dvc%3D2~ctr_mdl_id%3D2022022608~ctgid%3D1396935~max_brw%3Dsafari~cvr_scr%3D0.0012~tmt%3D%5B%5D~id%3D2073320529034_500083297_77249355622291~state%3DIL~sd_td%3D3714286488444342337522307077~bid_scr%3D-1.0~cvr_mdl_id%3Dlightgbm_2022022611~mm%3D1.0~ctr_uri%3D461175~vfci%3D2~bid_v%3D1~scr%3D1.1134~rclcd_v2%3D1~sclst_c%3D0.0~wxh%3D300x250~brw%3Dsafari~ctr_urc%3D327~vfcc%3D0~bid_mdl_id%3D1~adv%3D1702~aucsc%3D-1~en_1%3D0.0001~adgid%3D603503~cvr_v%3D119~m_ss%3D-1~vwb%3D0.88~m_os%3Dmac~bf%3D0.2~city%3Dlake%20forest~cmp%3D127520~adps%3D0~atg_bm%3D1.00~gmt%3D-600~qac%3D6~adid%3D555944~umm%3D1.5~rcf%3D1~lst_c%3D0.0~caid%3D598103~lang%3Den~itype%3DBRIGHTROLL~mptd%3D640~cc%3Dusa~st%3DMSNUSEN11~suptid%3DMSNUSEN11~crid%3D375117874~ctr_scr%3D0.0015~irs%3D0~ctr_uc%3D327.0758~mm_ptd%3D3496940661383432372~gv%3D386.35~cs%3Dybndspv2~ctr_v%3D6~csc%3D2~ctr_ui%3D461275.0~tgid%3D8b~bid%3D1.113385~ibc%3D1~dlog%3DdGlkPTgwODA2ODY4Mn5waWQ9OFBPOFg2UVAyQA%3D%3D";
    pfm_bdata = __bdata;
    pfm_stime = new Date().getTime();
    pfm_csip = "${csip}";
    pfm_bcpf = "${bcpf}";
    pfm_width = "<%=request.getParameter("width")%>";
    pfm_height = "<%=request.getParameter("height")%>";
    pfm_adt1 = "8CUSU8113";
    pfm_adt2 = "772493556";
    pfm_bdrId = "229";
    pfm_crid = "375117874";
    pfm_versionId = "3111299";
    pfm_auctionid = "${acid}";
    pfm_misc = {
        "kat": "{\"kasts\":\"tstype=-10408||gbid=-1\",\"katbid\":-21,\"katid\":808068682,\"kapc\":40,\"kals\":\"ttype=10202||pc=40||aghl=4||lmid=v601||fat=2||isagl=1\",\"kata\":\"aton\",\"kalog\":\"CI=2632||MPTD=640||SI=371||TPTD=13743903866884||SID=8||HID=1||UUID=2IakT2GsPlgOfNxEmT\"}"
    };
    pfm_requrl = "https://www.msn.com/en-us/";
    pfm_chnm = "${chnm}";
    pfm_pid = "8PO8X6QP2";
    pfm_ecrid = "1800080806868210300025000010300";
    pfm_tpid = "<%=templateTag%>";
    pfm_adomain = "fool.com";
    pfm_ctype = "1";
    pfm_natasset = '<%=pfm_natasset%>';
    pfm_app = 0;

</script>
<script src="https://c.pm-serv.co/npfm.js?cid=8CU734595"></script>
<%}%>
</body>
</html>
