$("#login_btn").click(function(e){
	e.preventDefault();
	$.ajax({
		data: {
			email: $("#inp_email").val(),
			password: $("#inp_pwd").val()
		},
		url: "/facebook-http/Login",
		method: 'POST',
		dataType: 'json',
		success: function(response){
			console.log(response);
			if(response.status){
				var id = response.data;
				window.location.href = "http://localhost:8080/facebook-http/html/home.html?id=" + id;
			}
		},
		error: function(error){
			console.log(error);
		}
	});
});
