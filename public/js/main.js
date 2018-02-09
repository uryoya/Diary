function getUser() {
    fetch('/api/user', {
        credentials: 'include'
    })
    .then(function(resp) {
        return resp.json()
    })
    .then(function(user) {
        console.log(user)
    });
}

function getAllUser() {
    fetch('/api/users', {
        credentials: 'include'
    })
    .then(function(resp) {
        return resp.json()
    })
    .then(function(users) {
        console.log(users);
    });
}

function signin() {
    const data = {
        "login": document.getElementById("form-login").value,
        "password": document.getElementById("form-password").value
    };
    console.log(data);
    fetch('/api/signin', {
        method: "POST",
        credentials: 'include',
        body: JSON.stringify(data)
    })
    .then(function(resp) {
        return resp.json();
    })
    .then(function(status) {
        if (status["message"] === "Success.") {
            console.log("login success.");
        } else {
            console.log("login failed.");
        }
    })
}

function signout() {
    fetch('/api/signout', {
        method: "POST",
        credentials: 'include'
    })
    .then(function(resp) {
        return resp.json();
    })
    .then(function(status) {
        console.log(status["message"]);
    });
}

document.querySelector('#signin').addEventListener("click", signin);
document.querySelector('#signout').addEventListener('click', signout);
document.querySelector("#foo").addEventListener("click", getUser);
document.querySelector('#users').addEventListener('click', getAllUser);
