// Category dropdown box
$(".dropdown-menu a").click(function() {
  // Update dropdown UI
  $(this)
    .parents(".dropdown")
    .find(".btn")
    .html($(this).text());

  // Update dropdown value
  $(this)
    .parents(".dropdown")
    .find(".btn")
    .val($(this).data("value"));

  $("#categories").val($(this).data("value"));
});


function encodeImageFileAsURL() {
  var file = document.getElementById("fileThumbnailInput").files[0];
  var reader = new FileReader();
  reader.onloadend = function() {
    console.log("RESULT", reader.result);
    document.getElementById("fileByteData").value = reader.result;
    $("#preview").attr("src", reader.result);
  };
  reader.readAsDataURL(file);
  console.log(reader.result);
}

/*

function getUsername() {
  return window.localStorage.getItem("username");
}

function checkUsername1(){
  document.getElementById("temp1").innerHTML = getUsername()
}



$("#submitCreateSeries").submit(function (event) {

  var url = "/series/create_series";

  $.ajax({
    type: "POST",
    url: url,
    data: $("#submitCreateSeries").serialize(),
    success: function(data){
      alert(data);
    }
  });
  return false;
});
*/
