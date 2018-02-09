function signin() {
    const data = {
        "login": "uryoya",
        "password": "pass"
    };
    fetch('/api/signin', {
        method: 'post',
        credentials: 'include',
        body: JSON.stringify(data)
    })
        .then(function(resp) {
            if (resp.ok) {
                console.log("OK");
            } else {
                console.log("BUUUUUU");
                console.log(resp);
            }
        })
        .then(function(user) {
            console.log(user);
        });
}

function signout() {
    fetch('/api/signout', {
        method: "POST",
        credentials: 'include'
    })
    .then(function(resp) {
        console.log(resp.status);
    });
}

function createUser() {
    const data = {
        "login": document.getElementById('form-create-user-login').value,
        "name": document.getElementById('form-create-user-name').value,
        "accessToken": document.getElementById('form-create-user-atoken').value,
        "password": document.getElementById('form-create-user-password').value
    };
    fetch('/api/users', {
        method: "POST",
        credentials: 'include',
        body: JSON.stringify(data)
    })
    .then(function(resp) {
        console.log(resp);
        return resp.json();
    })
    .then(function(user) {
        console.log(user);
    });
}

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

document.querySelector('#signin').addEventListener("click", signin);
document.querySelector('#signout').addEventListener('click', signout);
document.querySelector('#create-user').addEventListener('click', createUser);
document.querySelector("#get-user").addEventListener("click", getUser);
document.querySelector('#get-users').addEventListener('click', getAllUser);
