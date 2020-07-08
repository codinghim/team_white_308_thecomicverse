var currentUsername = null;
function loginSuccessful(newUsername) {
  currentUsername = newUsername;
  login(currentUsername);
}

function login(currentUsername) {
  window.sessionStorage.setItem("username", currentUsername);
}

function getUsername() {
  return window.sessionStorage.getItem("username");
}
