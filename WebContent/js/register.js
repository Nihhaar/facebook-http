setupDate();

$("#register_btn").click(function(e){
	e.preventDefault();
	$.ajax({
		data: {
			fname: $("#firstname").val(),
			sname: $("#surname").val(),
			email: $("#email").val(),
			password: $("#password").val(),
			dd: $("#day").prev().text(),
			mm: $("#month").prev().text(),
			yyyy: $("#year").prev().text(),
			gender: $('input[name=gender]:checked', '.welcomeForm').val()
		},
		url: "/facebook-http/Register",
		method: 'POST',
		dataType: 'json',
		success: function(response){
			console.log(response);
			if(response.status){
				window.location.href = "http://localhost:8080/facebook-http/html/home.html";
			}
		},
		error: function(error){
			console.log(error);
		}
	});
});

function genOption(arg1, arg2, arg3){
    var btn = document.createElement("button");
    btn.classList.add("dropdown-item");
    btn.href = "";
    btn.textContent = arg1;
    if(arg2){
    	btn.counter = arg3;
    }
    return btn;
}

function setupDate(){
    $("#day").empty();
    $("#month").empty();
    $("#year").empty();

    /* Setup day */
    for(var i=1; i<32; i++)
      document.getElementById("day").appendChild(genOption(i));

    /* Setup month */
    $("#month").append($(genOption('Jan')));
    $("#month").append($(genOption('Feb')));
    $("#month").append($(genOption('Mar')));
    $("#month").append($(genOption('Apr')));
    $("#month").append($(genOption('May')));
    $("#month").append($(genOption('Jun')));
    $("#month").append($(genOption('Jul')));
    $("#month").append($(genOption('Aug')));
    $("#month").append($(genOption('Sep')));
    $("#month").append($(genOption('Oct')));
    $("#month").append($(genOption('Nov')));
    $("#month").append($(genOption('Dec')));

    /* Setup year */
    for(var i=1905; i<2018; i++)
      document.getElementById("year").appendChild(genOption(i));

    /* Register Clicks */
    $(".dropdown-menu button").click(function(e){
    	e.preventDefault();
        $(this).parent().prev().text($(this).text());
    });
}
