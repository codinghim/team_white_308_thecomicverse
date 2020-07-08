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


/*
jQuery(document).ready(function() {
  updateLoginUI();
});
*/

function viewSeriesSubmit(){
  document.getElementById("usernameInput1").value = window.sessionStorage.getItem("username");
  document.getElementById('submitViewSeries').submit();

}

$(document).ready(function () {
  document.getElementById("dummyUsernameForm6").value = window.sessionStorage.getItem("username");



  document.getElementById("BrowseBtn").addEventListener('click', function(event){

    if(document.getElementById("category1").checked) {
      document.getElementById('category1Hidden').disabled = true;
    }
    if(document.getElementById("category2").checked) {
      document.getElementById('category2Hidden').disabled = true;
    }
    if(document.getElementById("category3").checked) {
      document.getElementById('category3Hidden').disabled = true;
    }
    if(document.getElementById("category4").checked) {
      document.getElementById('category4Hidden').disabled = true;
    }
    if(document.getElementById("category5").checked) {
      document.getElementById('category5Hidden').disabled = true;
    }
    if(document.getElementById("category6").checked) {
      document.getElementById('category6Hidden').disabled = true;
    }

    document.getElementById("searchOptionhidden").value = document.getElementById("searchOption").value;
    document.getElementById("keywordhidden").value = document.getElementById("keywordinput").value;

/*
    alert("search opt for browse is" + document.getElementById("searchOptionhidden").value);
    alert("keyword for browse is" + document.getElementById("keywordhidden").value);
*/

    document.getElementById("updateBrowse").submit();

    });


});
