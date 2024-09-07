(function($) {
    $.fn.cProcessFormUrl = {
		//requestURL: "http://localhost:819/feyi-cbn/engine/",
		//requestURL: document.location.origin + "/ECM6/",
		requestURL: document.location.href.replace("index.jsp", "").replace("main.html", ""),
    }

    var s = document.location.href.split("?");
    $.fn.cProcessFormUrl.requestURL = s[0].replace("index.jsp", "").replace("main.html", "");

    console.log( "ecmURL", $.fn.cProcessFormUrl.requestURL );
    console.log( "ecm", document.location );
}(jQuery));