<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="net.media.autotemplate.dal.configs.GlobalConfig" %>
<%@ page import="net.media.autotemplate.util.Util" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Objects" %>
<%@ page import="net.media.autotemplate.bean.TemplateMetaInfo" %>
<!DOCTYPE html>
<html>
<head>
    <title>Bulk Preview</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css"
          integrity="sha384-WskhaSGFgHYWDcbwN70/dfYBj47jz9qbsMId/iRN3ewGhXQFZCSftd1LZCfmhktB" crossorigin="anonymous">
    <style>
        .form-control:disabled, .form-control[readonly] {
            background-color: #e9ecef;
            opacity: 0.4
        }

        fieldset {
            display: block;
            margin-inline-start: 2px;
            margin-inline-end: 2px;
            border: groove 2px ThreeDFace;
            padding-block-start: 0.35em;
            padding-inline-end: 0.75em;
            padding-block-end: 0.625em;
            padding-inline-start: 0.75em;
            min-inline-size: min-content;
            border-radius: 3px;
        }

        /* The snackbar - position it at the bottom and in the middle of the screen */
        #snackbar {
            visibility: hidden; /* Hidden by default. Visible on click */
            min-width: 250px; /* Set a default minimum width */
            margin-left: -125px; /* Divide value of min-width by 2 */
            background-color: #333; /* Black background color */
            color: #fff; /* White text color */
            text-align: center; /* Centered text */
            border-radius: 2px; /* Rounded borders */
            padding: 16px; /* Padding */
            position: fixed; /* Sit on top of the screen */
            z-index: 1; /* Add a z-index if needed */
            left: 50%; /* Center the snackbar */
            bottom: 30px; /* 30px from the bottom */
        }

        /* Show the snackbar when clicking on a button (class added with JavaScript) */
        #snackbar.show {
            visibility: visible; /* Show the snackbar */

            /* Add animation: Take 0.5 seconds to fade in and out the snackbar.
            However, delay the fade out process for 2.5 seconds */
            -webkit-animation: fadein 0.5s, fadeout 0.5s 2.5s;
            animation: fadein 0.5s, fadeout 0.5s 2.5s;
        }

        /* Animations to fade the snackbar in and out */
        @-webkit-keyframes fadein {
            from {
                bottom: 0;
                opacity: 0;
            }
            to {
                bottom: 30px;
                opacity: 1;
            }
        }

        @keyframes fadein {
            from {
                bottom: 0;
                opacity: 0;
            }
            to {
                bottom: 30px;
                opacity: 1;
            }
        }

        @-webkit-keyframes fadeout {
            from {
                bottom: 30px;
                opacity: 1;
            }
            to {
                bottom: 0;
                opacity: 0;
            }
        }

        @keyframes fadeout {
            from {
                bottom: 30px;
                opacity: 1;
            }
            to {
                bottom: 0;
                opacity: 0;
            }
        }
    </style>
</head>
<body class="bg-light" onload="setVisibilityOnLoad()">

<%
    List<String> sizes = GlobalConfig.getAllValidTemplateSizes();
    String inps = request.getParameter("frameworks");
    if (Objects.isNull(inps))
        inps = "";

    String responsiveWidthStr = request.getParameter("responsiveWidth");

    if (Objects.isNull(responsiveWidthStr))
        responsiveWidthStr = "";

    String selectedTtype = request.getParameter("ttype");
    if (Objects.isNull(selectedTtype))
        selectedTtype = "";

    String adgid = request.getParameter("adgid");
    if (Objects.isNull(adgid))
        adgid = "";

    String adid = request.getParameter("adid");
    if (Objects.isNull(adid))
        adid = "";

%>
<main role="main" class="container" style="max-width: 1700px">
    <div class="my-3 p-3 bg-white rounded box-shadow">
        <form>
            <div class="row">
                <div class="col-sm">
                    <div class="form-group">
                        <label for="exampleFormControlTextarea1">Enter Ids</label>
                        <textarea class="form-control" id="exampleFormControlTextarea1" rows="7"
                                  name="frameworks" value=""><%=inps%></textarea>
                    </div>

                </div>
                <div class="col-sm">
                    <div class="form-group">
                        <label for="exampleFormControlSelect1">Size</label>
                        <%
                            String selectedSize = request.getParameter("size");
                            if (Objects.isNull(selectedSize))
                                selectedSize = "";
                        %>
                        <select class="form-control" id="exampleFormControlSelect1" name="size"
                                value="<%=selectedSize%>">
                            <%
                                for (String s : sizes) {%>
                            <option <%=selectedSize.equals(s) ? "selected" : ""%> ><%=s%>
                            </option>
                            <% }
                                String selectedType = request.getParameter("type");
                                if (Objects.isNull(selectedType))
                                    selectedType = "";
                            %>
                        </select>
                        <label for="exampleFormControlSelect1">CreativeType</label>
                        <select class="form-control" id="exampleFormControlSelect2" name="type">
                            <option value="T" <%= "T".equals(selectedType) ? "selected" : "" %>>Template</option>
                            <option value="F" <%= "F".equals(selectedType) ? "selected" : "" %>>AT-Framework</option>
                        </select>
                        <label for="exampleFormControlSelect1">TemplateType</label>
                        <select class="form-control" id="exampleFormControlSelect3" name="ttype"
                                onchange="setAdInfoVisibility(this.value)">
                            <option value="keywords-only" <%= "keywords-only".equals(selectedTtype) ? "selected" : "" %> >
                                keywords-only
                            </option>
                            <option value="cm-native" <%= "cm-native".equals(selectedTtype) ? "selected" : "" %> >
                                cm-native
                            </option>
                        </select>

                    </div>
                </div>
                <div class="col-sm">
                    <form>
                        <fieldset>
                            <legend style="width: auto; font-size: 1em;"> <b>&nbsp;Native Ad Info&nbsp;</b> </legend>
                            <div class="form-group">
                                <label for="exampleFormControlTextarea1">Enter AdGroup Id</label>
                                <input id="adgid" class="form-control" type="text" name="adgid" value="<%=adgid%>"/>
                            </div>

                            <div class="form-group">
                                <label for="exampleFormControlTextarea1">Enter Ad Id</label>
                                <input id="adid" class="form-control" type="text" name="adid" value="<%=adid%>"/>
                            </div>
                        </fieldset>
                    </form>

                </div>
                <div class="col-sm">
                    <div class="form-group">
                        <label for="exampleFormControlTextarea1">Enter Responsive Width</label>
                        <input class="form-control" type="text" name="responsiveWidth" value="<%=responsiveWidthStr%>"/>
                    </div>
                    <div class="d-block text-left mt-3" style="padding-top: 1em">
                        <button type="submit" class="btn btn-primary" style="">Preview</button>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <%
        String frameworksStr = request.getParameter("frameworks");
        String size = request.getParameter("size");
        String type = request.getParameter("type");
        if (Util.isStringSet(frameworksStr) && Util.isStringSet(size) && Util.isStringSet(type)) {
            String[] strings = frameworksStr.split("\\s+");
            String width = size.split("x")[0];
            String height = size.split("x")[1];
            String responsiveWidth = request.getParameter("responsiveWidth");
            boolean show = true;
            if (!Util.isStringSet(responsiveWidth)) {
                responsiveWidth = width;
            }
            String root = "template";
            String query = type.equals("F") ? "fid" : "tid";
    %>
    <div class="my-3 p-3 bg-white rounded box-shadow">
        <div>
            <%if (show) {%>
            <div class="btn-group" role="group" aria-label="Basic example" style="float: right">
                <button type="button" class="btn btn-secondary" onclick="setWidth('<%=width%>')">Orignal</button>
                <button type="button" class="btn btn-secondary" onclick="setWidth('300px')">300</button>
                <button type="button" class="btn btn-secondary" onclick="setWidth('600px')">600</button>
                <button type="button" class="btn btn-secondary" onclick="setWidth('900px')">900</button>
            </div>
            <%}%>
            <h4 class="border-bottom border-gray pb-2 mb-0"><%= type.equals("T") ? "Templates" : "Frameworks" %>
                ( <%=size%>
                )
            </h4>
        </div>
        <% for (String framework : strings) {
            framework = framework.trim();
            TemplateMetaInfo templateMetaInfo = null;
            if(type.equals("T")){
                templateMetaInfo = Util.getTemplateMetaInfo(framework);
            }
        %>
        <div class="text-center" id="frame<%=framework%>"
             style="background-image: url('loader.gif');display: inline-block;    background-repeat: no-repeat, repeat; background-position: center; margin: 1em">

            <p>
            <p>
                <small>
                    <%= type.equals("T") ? templateMetaInfo.getTemplateName() : Util.getFrameworkName(framework) %>
                    <%= "[" + framework + "]" %>
                </small>
                <br>
                <small>
                    <%= type.equals("T") ? "[" + templateMetaInfo.getFrameworkId() + "]" : "" %>
                </small>
            </p>
            </p>

            <%if (selectedTtype.equals("keywords-only")) { %>
            <div style="height: <%=height%>px; width: <%=responsiveWidth%>px;">
                <iframe
                        class="frames"
                        src="<%=root%>.jsp?<%=query%>=<%=framework%>&width=<%=width%>&height=<%=height%>"
                        height="<%=height%>"
                        marginwidth="0" marginheight="0" padding="20px" scrolling="NO" width="<%=responsiveWidth%>"
                        frameBorder="0"
                        style="margin-left: 1em;margin-bottom: 1em;"
                        onload="disableLoader('frame<%=framework%>')"
                >
                </iframe>
            </div>
            <% } else { %>
            <div style="height: <%=height%>px; width: <%=responsiveWidth%>px;">
                <iframe
                        class="frames"
                        src="<%=root%>_test.jsp?tid=<%=framework%>&width=<%=width%>&height=<%=height%>&adgid=<%=adgid%>&adid=<%=adid%>"
                        height="<%=height%>"
                        marginwidth="0" marginheight="0" padding="20px" scrolling="NO" width="<%=responsiveWidth%>"
                        frameBorder="0"
                        style="margin-left: 1em;margin-bottom: 1em;"
                        onload="disableLoader('frame<%=framework%>')"
                >
                </iframe>
            </div>
            <% } %>

        </div>
        <%}%>
    </div>
    <%}%>
</main>
<div id="snackbar"></div>
<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
        integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
        crossorigin="anonymous"></script>
<script>

    function disableLoader(id) {
        console.log('clearimage' + id);
        $('#' + id).css('background-image', 'none');
    }

    function setWidth(width) {
        $(".frames").width(width);
    }

    function setAdInfoVisibility(ttype) {
        if (ttype === 'cm-native') {
            document.getElementById("adgid").disabled = false;
            document.getElementById("adid").disabled = false;
        } else {
            document.getElementById("adgid").disabled = true;
            document.getElementById("adid").disabled = true;
            document.getElementById("adgid").value = '';
            document.getElementById('adid').value = '';
        }

    }

    function setVisibilityOnLoad() {
        setAdInfoVisibility(document.getElementById('exampleFormControlSelect3').value);
    }

    function snackbar(text) {
        var x = document.getElementById("snackbar");
        x.className = "show";
        x.innerHTML = text;
        setTimeout(function () {
            x.className = x.className.replace("show", "");
        }, 3000);
    }
</script>
</body>
</html>
