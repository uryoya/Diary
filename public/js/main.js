const userDetail = document.querySelector('#user-detail');
const submitButton = document.querySelector('#signin');
function getUser() {
    fetch('/api/user', {
        credentials: 'include'
    })
    .then(function(resp) {
        return resp.json()
    })
    .then(function(user) {
        const u = document.createElement("div");
        u.innerHTML = user["name"];
        userDetail.appendChild(u);
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

submitButton.addEventListener("click", signin);

document.querySelector("#foo").addEventListener("click", function() {
    getUser();
    console.log("clicked!");
});

document.querySelector('#signout').addEventListener('click', signout);
