$(function() {
	init();
	$('#codeImg').click(function() {
		var token = ajax("login/getToken", "");
		var url = 'https://wmpass.baidu.com/wmpass/openservice/imgcaptcha?token='
				+ token + '&t=' + new Date() + '&color=3c78d8';
		$('#codeImg').attr('src', url);
	});

	$('#loginButton').click(function() {
				var param = {};
				param.username = $('#username').val();
				param.password = h($('#password').val());
				param.code = $('#code').val();
				ajax("login/bdlogin", param);
			});
})

function init() {
	var token = ajax("login/getToken", "");
	var url = 'https://wmpass.baidu.com/wmpass/openservice/imgcaptcha?token='
			+ token + '&t=' + new Date() + '&color=3c78d8';
	$('#codeImg').attr('src', url);
}
var a = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
h = function(r) {
	if (r) {
		r = c(r);
		var e = new RegExp("=", "g");
		return r = r.replace(e, ""), r = r.split("").reverse().join("")
	}
}

c = function(r) {
	var e, t, o, c, i, h;
	for (o = r.length, t = 0, e = ""; o > t;) {
		if (c = 255 & r.charCodeAt(t++), t == o) {
			e += a.charAt(c >> 2), e += a.charAt((3 & c) << 4), e += "==";
			break
		}
		if (i = r.charCodeAt(t++), t == o) {
			e += a.charAt(c >> 2), e += a.charAt((3 & c) << 4 | (240 & i) >> 4), e += a
					.charAt((15 & i) << 2), e += "=";
			break
		}
		h = r.charCodeAt(t++), e += a.charAt(c >> 2), e += a
				.charAt((3 & c) << 4 | (240 & i) >> 4), e += a
				.charAt((15 & i) << 2 | (192 & h) >> 6), e += a.charAt(63 & h)
	}
	return e
}

function ajax(url, param) {
	var data;
	$.ajax({
				type : "post",
				url : url,
				data : JSON.stringify(param),
				cache : false,
				async : false,
				dataType : "json",
				contentType : "application/json; charset=utf-8",
				error : function() {

				},
				success : function(response) {
					if (response.respCode == '0000') {
						data = response.token;
					}
				}
			});
	return data;
}