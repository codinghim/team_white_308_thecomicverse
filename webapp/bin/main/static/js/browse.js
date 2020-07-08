function readURL(url) {
  console.log("Reading pre URL" + url);
  var request = new XMLHttpRequest();
  request.open("GET", url, true);
  request.responseType = "blob";
  request.onload = function() {
    var reader = new FileReader();
    reader.readAsDataURL(request.response);
    reader.onload = function(e) {
      $("#thumbnail").attr("src", e.target.result);
      console.log(e.target.result.length);
    };
  };
  request.send();
}

function viewSeriesSubmit(){
  document.getElementById("usernameInput1").value = window.sessionStorage.getItem("username");
  document.getElementById('submitViewSeries').submit()

}
/*
function readURL() {
    var file = document.getElementById("fileThumbnailInput").files[0];
    var reader = new FileReader();
    reader.onloadend = function () {
        console.log("RESULT", reader.result);
        document.getElementById("fileByteData").value = reader.result;
    };
    reader.readAsDataURL(file);
    console.log(reader.result);
}


*/
