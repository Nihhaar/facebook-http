/* Global variables */
var username = "Nihhaar";
var selectname, selectlabel;
var maxposts = 10;
var count = 0;
var tweak = 0;

/* Check if this session is valid */
loginCheck();

/* JQuery - Infinite Scroll */
infiniteScroll();

/* Get followed Posts - First 10(default) posts */
getFollowedPosts();

/* Register the post button */
registerPostButton();

/* Register logout link */
logout();

document.getElementById('getfile1').addEventListener('change', readProfilePic, true);
document.getElementById('getfile2').addEventListener('change', readURL, true);

/*
 * JQuery - Infinite scroll for home page.
 */
function infiniteScroll(){
	$(window).scroll(function () {
		var theWindow = $(this);
		var theContainer = $('body');
		if ( theWindow.scrollTop() >= theContainer.height() - theWindow.height() - tweak ) {
			getFollowedPosts();
		}
	});
}

/*
 * Creates all the followed posts and places it in content div.
 */
function getFollowedPosts(){
	$.ajax({
		data: {
			limit: maxposts,
			offset: count
		},
		url: '/facebook-http/GetPosts',
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

/*
 * firstname - Name of the post creator
 * text - Content of the post
 * postid - Post ID of the post
 * comments - Json array of all the comments in format [{"commentor": "", "comment": ""},...]
 */
function createPost(firstname, text, postid, hasimage){
	var $div1 = $("<div>", {"class": "card fb-post-card"});
	var $div2 = $("<div>", {"class": "card-body"});
  var $h4 = $("<h4>", {"class": "card-title"});
	var $h6 = $("<h6>", {"class": "card-subtitle text-muted"});
  var $p = $("<p>", {"class": "card-text"});
	var $innerdiv1 = $("<div>");
	var $innerdiv2 = $("<div>", {"class": "hideComments hidden"});
	var $div3 = $("<div>", {"class": "comments hidden"});

  if(hasimage){
    var $divimg = $("<div>", {"class": "post-card-img"});
    $.ajax({
  		data: {
        postid: postid
  		},
  		url: '/facebook-http/GetPostImage',
  		type: 'POST',
  		dataType: 'json',
  		success: function(response){
        if(response.status){
          var imgsrc = window.URL.createObjectURL(base64ToBlob(response.data, 'image/jpeg'));
          $divimg.get(0).style.backgroundImage = 'url(' + imgsrc + ')';
          $p.append($divimg);
        }
  		}
  	});
  }

	/* Post footer banner */
	var $footer = $("<div>", {"class": "post-footer"});
	var $span1 = $("<span>", {"class": "post-footer-link"});
	var $span2 = $("<span>", {"class": "post-footer-link"});
	var $span3 = $("<span>", {"class": "post-footer-link"});

	var $a1 = $("<a>", {"class": "like", "href": "#"});
	var $a2 = $("<a>", {"class": "comment", "href": "#"});
	var $a3 = $("<a>", {"class": "share", "href": "#"});
	$a2.click(function(e){
		e.preventDefault();
		$(this).parent().parent().next().removeClass('hidden');
		/* Comments: Get the comments & loop through the comments json array */
		getComments(postid, $(this).parent().parent().next());
	});


	$a1.text("Like");
	$a2.text("Comment");
	$a3.text("Share");
	$span1.append($a1);
	$span2.append($a2);
	$span3.append($a3);
	$footer.append($span1);
	$footer.append($span2);
	$footer.append($span3);

  $h4.text(firstname);
  $h6.text("5hr ago");
  $p.text(text);
	$div3.append($innerdiv1);
	$div3.append($innerdiv2);
  $div2.append($h4);
  $div2.append($h6);
  $div2.append($p);
	$div2.append($footer);
	$div2.append($div3);
  $div1.append($div2);

	/* Button along with onclick action */

	/* Create final post */
	return $div1.get(0);
}

function getComments(postid, $div3){

	console.log($div3);
	$div3.removeClass('hidden');

	var $inpdiv = $("<div>");
	var $spanuser = $("<span>", {"class": "commentor"});
	$spanuser.text(username);
	var $inputComment = $("<input>", {"type": "text", "class": "addComment", "placeholder": "Write a comment..."});
	$inputComment.on("keypress", function(e){
		/* ENTER PRESSED*/
		console.log(e);
		if (e.keyCode == 13) {
			e.preventDefault();
			if(!($inputComment.val().trim() == "")){
				$.ajax({
					data: {
						parentcommentid: '-1',
						postid: postid,
						commentcontent: $inputComment.val()
					},
					url: '/facebook-http/AddComment',
					type: 'POST',
					success: function(data){
						console.log("Successfully posted comment!");
					}
				});
			}
		}
	});
	$inpdiv.append($spanuser);
	$inpdiv.append($inputComment);
	$div3.find('div').first().append($inpdiv);

	$.ajax({
		url: '/facebook-http/GetComments',
		type: 'POST',
		data: {
			postid: postid
		},
		dataType: 'json',
		success: function(response){
			console.log(response);
			if(response.status){
				var comments = response.data;
				for(var i=0; i<comments.length; i++){
					if(i<2){
						var $tmpdiv1 = $("<div>", {"class": "each-comment"});
						$tmpdiv1.append('<span class="commentor">' + comments[i].firstname + '</span><span class="commenttext">' + comments[i].text + '</span><br />');

						var $replyanchor = $('<a>', {"class": "reply", "href": "#"});
						$replyanchor.text("Reply");
						$replyanchor.click({commentid: comments[i].commentid}, getSubComments);

						var $viewreply = $('<a>', {"class": "view-reply", "href": "#"});
						if(comments[i].replies == 0){
							$viewreply.text('0 Replies');
						}
						else if(comments[i].replies == 1){
							$viewreply.text('1 Reply');
						}
						else{
							$viewreply.text(comments[i].replies + ' Replies');
						}
						$tmpdiv1.append($viewreply);

						var $replydiv = $('<div>', {"class": "reply-div hidden"});
						$tmpdiv1.append($replyanchor);
						$tmpdiv1.append($replydiv);
						$div3.find('div').first().append($tmpdiv1);
					}

					else if(i==2){
						var $tmpdiv1 = $("<div>", {"class": "each-comment"});
						$tmpdiv1.append('<span class="commentor">' + comments[i].firstname + '</span><span class="commenttext">' + comments[i].text + '</span><br />');

						var $replyanchor = $('<a>', {"class": "reply", "href": "#"});
						$replyanchor.text("Reply");
						$replyanchor.click({commentid: comments[i].commentid}, getSubComments);

						var $viewreply = $('<a>', {"class": "view-reply", "href": "#"});
						if(comments[i].replies == 0){
							$viewreply.text('0 Replies');
						}
						else if(comments[i].replies == 1){
							$viewreply.text('1 Reply');
						}
						else{
							$viewreply.text(comments[i].replies + ' Replies');
						}
						$tmpdiv1.append($viewreply);

						var $replydiv = $('<div>', {"class": "reply-div hidden"});
						$tmpdiv1.append($replyanchor);
						$tmpdiv1.append($replydiv);
						$div3.find('div').first().append($tmpdiv1);

						if(comments.length > 3){
							var $anchor = $("<a>", {"href": "#"});
							$anchor.text("More...");
							$anchor.click(function(e){
								e.preventDefault();
								$(this).parent().next().removeClass('hidden');
								$(this).parent().next().find('a').removeClass('hidden');
								$(this).addClass('hidden');
							});
							$div3.find('div').first().append($anchor);
						}
					}

					else{
						var $tmpdiv1 = $("<div>", {"class": "each-comment"});
						$tmpdiv1.append('<span class="commentor">' + comments[i].firstname + '</span><span class="commenttext">' + comments[i].text + '</span><br />');

						var $replyanchor = $('<a>', {"class": "reply", "href": "#"});
						$replyanchor.text("Reply");
						$replyanchor.click({commentid: comments[i].commentid}, getSubComments);
						var $viewreply = $('<a>', {"class": "view-reply", "href": "#"});

						if(comments[i].replies == 0){
							$viewreply.text('0 Replies');
						}
						else if(comments[i].replies == 1){
							$viewreply.text('1 Reply');
						}
						else{
							$viewreply.text(comments[i].replies + ' Replies');
						}
						$tmpdiv1.append($viewreply);

						var $replydiv = $('<div>', {"class": "reply-div hidden"});
						$tmpdiv1.append($replyanchor);
						$tmpdiv1.append($replydiv);
						$div3.find('div.hideComments').first().append($tmpdiv1);

						if(i==comments.length-1){
							var $anchor = $("<a>", {"href": "#"});
							$anchor.text("Less...");
							$anchor.click(function(e){
								e.preventDefault();
								$(this).parent().prev().removeClass('hidden');
								$(this).parent().prev().find('a').removeClass('hidden');
								$(this).addClass('hidden');
								$(this).parent().addClass('hidden');
							});
							$div3.find('div.hideComments').first().append($anchor);
						}
					}
				}
			}
		},
		error: function(error){
			console.log(error);
		}
	});
}

function getSubComments(event){
	event.preventDefault();
	var cmtid = event.data.commentid;
	var $div = $(this).next();
	console.log($div);
	console.log(cmtid);

	if($div.hasClass('hidden')){
		$div.removeClass('hidden');
		var $inpdiv = $("<div>");
		var $spanuser = $("<span>", {"class": "commentor"});
		$spanuser.text(username);
		var $inputComment = $("<input>", {"type": "text", "class": "addComment", "placeholder": "Write your reply..."});
		$inputComment.on("keypress", function(e){
			/* ENTER PRESSED*/
			console.log(e);
			if (e.keyCode == 13) {
				e.preventDefault();
				if(!($inputComment.val().trim() == "")){
					$.ajax({
						data: {
							parentcommentid: cmtid,
							commentcontent: $inputComment.val()
						},
						url: '/facebook-http/AddComment',
						type: 'POST',
						success: function(data){
							console.log("Successfully posted comment!");
						}
					});
				}
			}
		});
		$inpdiv.append($spanuser);
		$inpdiv.append($inputComment);
		$div.append($inpdiv);

		$.ajax({
			url: '/facebook-http/GetSubComments',
			type: 'POST',
			data: {
				commentid: cmtid
			},
			dataType: 'json',
			success: function(response){
				console.log(response);
				if(response.status){
					var subcomments = response.data;
					for(var i=0; i<subcomments.length; i++){
						var $tmpdiv1 = $("<div>");
						$tmpdiv1.append('<span class="commentor">' + subcomments[i].firstname + '</span><span class="commenttext">' + subcomments[i].text + '</span>');
						$div.append($tmpdiv1);
					}
				}
			},
			error: function(error){
				console.log(error);
			}
		});
	}
}

function logout(){
    $("#logout").off('click').on('click', function(e){
      e.preventDefault();
      $.ajax({
         url: '/facebook-http/CreatePostText',
         type: 'GET',
         success: function(response){
          console.log(response);
          window.location.href = "http://localhost:8080/facebook-http/html/login.html";
         },
         error: function(error){
          console.log(error);
         }
       });
    });
}

function registerPostButton(){
  $("#post_btn").off('click').on('click', function(e){
      e.preventDefault();
      $.ajax({
         url: '/facebook-http/CreatePostText',
         type: 'POST',
         data: {
           text: $("#post-text").val()
         },
         success: function(response){
          console.log(response);
         },
         error: function(error){
          console.log(error);
         }
       });
  });
}

function registerPostButtonForImage(formData){
  $("#post_btn").off('click').on('click', function(e){
      e.preventDefault();
      $.ajax({
         url: '/facebook-http/CreatePost',
         type: 'POST',

         // Form data
         data: formData,

         // Tell jQuery not to process data or worry about content-type
         // You *must* include these options!
         cache: false,
         contentType: false,
         processData: false,

         success: function(response){
          console.log(response);
         },
         error: function(error){
          console.log(error);
         }
     });
     $("#img-holder").remove();
     registerPostButton();
  });
}

/* uploading files by clicking anchor tag */
$(function(){
  $('.antiscroll-wrap').antiscroll();

  $("#profilepic").on('click', function(e){
      e.preventDefault();
      $("#getfile1:hidden").trigger('click');
  });

  $("#upload").on('click', function(e){
      e.preventDefault();
      $("#getfile2:hidden").trigger('click');
  });
});

function loginCheck(){
    $.ajax({
      url: '/facebook-http/LoginCheck',
      method: 'GET',
      success: function(response){
       console.log(response);
       if(!response.status){
         window.location.href = "http://localhost:8080/facebook-http/html/login.html";
       }
      },
      error: function(error){
       console.log(error);
        $("#profilepic").attr("src", src);
      }
   });
}

function readProfilePic(){
   var file = document.getElementById("getfile1").files[0];
   var reader = new FileReader();
   var src = $("#profilepic").attr("src");
   $("#profilepic").attr("src", '../gifs/loading.gif');

   if(file){
     new ImageCompressor(file, {
        quality: .6,
        success(result) {
          var formData = new FormData();
          formData.append('file', result, result.name);
          var bgImg = new Image();
          bgImg.onload = function(){
         	  setTimeout(function(){
               $.ajax({
                 url: '/facebook-http/ChangeProfilePic',
                 method: 'POST',
                 // Form data
                 data: formData,
                 // Tell jQuery not to process data or worry about content-type
                 // You *must* include these options!
                 cache: false,
                 contentType: false,
                 processData: false,
                 success: function(response){
               	  console.log(response);
               	  $("#profilepic").attr("src", bgImg.src);
                 },
                 error: function(error){
               	  console.log(error);
                   $("#profilepic").attr("src", src);
                 }
              });
            }, 1000);
          };
          bgImg.src = window.URL.createObjectURL(result);
        },
        error(e) {
          console.log(e.message);
        },
     });

   }
}

function readURL(){
   var $div = $("<div>", {id: "img-holder"});
   $div.insertAfter($("#post-text"));
   var imgHolder = document.getElementById('img-holder');
   var file = document.getElementById("getfile2").files[0];
   var reader = new FileReader();

   imgHolder.style.backgroundSize = 'initial';
   imgHolder.style.backgroundImage = "url('../gifs/loading.gif')";

   if(file){
     new ImageCompressor(file, {
        quality: .6,
        success(result) {
          var bgImg = new Image();
          bgImg.onload = function(){
       		   imgHolder.style.backgroundSize = 'cover';
           	 imgHolder.style.backgroundImage = 'url(' + bgImg.src + ')';
          };
          bgImg.src = window.URL.createObjectURL(result);

          var formData = new FormData();
          formData.append('picture', result, result.name);
          formData.append('text', $("#post-text").val());
          console.log(formData);
          registerPostButtonForImage(formData);
        },
        error(e) {
          console.log(e.message);
        },
     });
   }
   else{
     console.log("No file, wtf?");
   }
}

/*
 * function based on this answer from stackoverflow:
 * https://stackoverflow.com/questions/16245767/creating-a-blob-from-a-base64-string-in-javascript/16245768#16245768
 */
function base64ToBlob(base64, mime)
{
    mime = mime || '';
    var sliceSize = 1024;
    var byteChars = window.atob(base64);
    var byteArrays = [];

    for (var offset = 0, len = byteChars.length; offset < len; offset += sliceSize) {
        var slice = byteChars.slice(offset, offset + sliceSize);

        var byteNumbers = new Array(slice.length);
        for (var i = 0; i < slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
        }

        var byteArray = new Uint8Array(byteNumbers);

        byteArrays.push(byteArray);
    }

    return new Blob(byteArrays, {type: mime});
}
