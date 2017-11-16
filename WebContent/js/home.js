document.getElementById('getfile').addEventListener('change', readURL, true);

/* uploading files by clicking anchor tag */
$(function(){
    $("#upload").on('click', function(e){
        e.preventDefault();
        $("#getfile:hidden").trigger('click');
    });
});

function readURL(){
   var imgHolder = document.getElementById('img-holder');
   var file = document.getElementById("getfile").files[0];
   var reader = new FileReader();

   if(file){
	 imgHolder.style.backgroundSize = 'initial';
     imgHolder.style.backgroundImage = "url('../gifs/loading.gif')";
     var bgImg = new Image();
     bgImg.onload = function(){
    	 setTimeout(function(){
    		 imgHolder.style.backgroundSize = 'cover';
        	 imgHolder.style.backgroundImage = 'url(' + bgImg.src + ')';
         }, 1000);
     };
     bgImg.src = window.URL.createObjectURL(file);
     
     var formData = new FormData();
     formData.append('picture', file);
     formData.append('text', $("#post-text").val());
     
	 $("#post_btn").click(function(e){
       e.preventDefault();
       $.ajax({
          url: '/facebook-http/CreatePost',
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
        	  /*if(!response.status){
        		  window.location.href = "http://localhost:8080/facebook-http/";
        	  }*/
          },
          error: function(error){
        	  console.log(error);
          }
      });
     });
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
