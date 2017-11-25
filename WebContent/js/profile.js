var userid;

/* Get user id from URL */
getUserID();

/* Get username from userid */
getUserName();

/* JQuery - Infinite Scroll */
infiniteScroll();

/* Get followed Posts - First 10(default) posts */
getUserPosts();

function getUserID(){
	var vars = {};
  var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
      vars[key] = value;
  });
	userid = vars["id"];
	console.log(userid);
}

function getUserName(){
	$.ajax({
		data: {
			userid: userid
		},
		url: '/facebook-http/GetUserName',
		type: 'POST',
		dataType: 'json',
		success: function(response){
			console.log(response);
			$("#name_1").text(response.data);
		},
		error: function(error){
			console.log(error);
		}
	});
}

/*
 * JQuery - Infinite scroll for home page.
 */
function infiniteScroll(){
	$(window).scroll(function () {
		var theWindow = $(this);
		var theContainer = $('body');
		if ( theWindow.scrollTop() >= theContainer.height() - theWindow.height() - tweak ) {
			getUserPosts();
		}
	});
}

/*
 * Creates all the followed posts and places it in content div.
 */
function getUserPosts(){
	$.ajax({
		data: {
			limit: maxposts,
			offset: count,
			userid: userid
		},
		url: '/facebook-http/GetUserPosts',
		type: 'POST',
		dataType: 'json',
		success: function(response){
			for(var i=0; i<response.data.length; i++)
				$("#newsfeed").append(createPost(response.data[i].firstname, response.data[i].text, response.data[i].postid, response.data[i].hasimage));
			count += response.data.length;
			console.log("Followed Posts Incoming!");
		}
	});
}
