
    function refresh() {
        document.getElementById("codeImg").src ="/pc/getCode?time="+new Date().getTime();
     }

     function showMessage(message, type, time) {
         let str = ''
         switch (type) {
             case 'success':
                 str = '<div class="success-message" style="width: 300px;height: 40px;text-align: center;background-color:#daf5eb;;color: rgba(103,194,58,0.7);position: fixed;left: 50%;top: 5%;margin-left:-150px;margin-top:-20px;line-height: 40px;border-radius: 5px;z-index: 9999">\n' +
                     '    <span class="mes-text">' + message + '</span></div>'
                 break;
             case 'error':
                 str = '<div class="error-message" style="width: 300px;height: 40px;text-align: center;background-color: #f5f0e5;color: rgba(238,99,99,0.8);position: fixed;left: 50%;top: 10%;line-height: 40px;margin-left:-150px;margin-top:-20px;border-radius: 5px;;z-index: 9999">\n' +
                     '    <span class="mes-text">' + message + '</span></div>'
         }
         $('body').append(str);
         setTimeout(function () {
             $('.' + type + '-message').remove();
         }, time);
     }




     function login() {
        var userName = $("#userName").val();
        var password = $("#password").val();
        var code = $("#code").val();
        var timestamp = Number(new Date()).toString();
        if(password === "" || userName === "" || code === "") {
             showMessage('请将信息填写完整','error',3000);
        }else {
             $("#loginButton").empty();
        	 $("#loginButton").val("登录中...");
        	 $('#loginButton').attr("disabled", true);
        	 setTimeout(function() {
        	 $('#loginButton').removeAttr("disabled");
        	 $("#loginButton").empty();
        	 $("#loginButton").val('登 录');
        			},3000);

            var pad = CryptoJS.MD5(password).toString().toUpperCase();
            var sm3HashHex = SMUtils.SM3(pad);
            var key = sm3HashHex.substring(0,32);
            var iv = sm3HashHex.substring(sm3HashHex.length - 32,sm3HashHex.length);
            password = SMUtils.SM4EncryptCBC(timestamp,key,iv);
            $.ajax({
                url: "/pc/loginAction",
                type: "post",
                async: false,
                data: {"userName": userName, "password": password, "code": code, "timestamp": timestamp},
                datatype: 'json',
                success: function (data) {
                    if (data.code === 20000) {
                        window.location.href = "/pc/index";
                    } else {
                        refresh();
                        $("#password").val("");
                        $("#code").val("");
                        showMessage(data.message,'error',3000);
                        window.parent.refresh();
                    }
                }
            });
            return false ;
        }
    }

    $(function (){
        refresh();
    })