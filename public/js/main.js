function signin() {
    const form = new FormData(document.querySelector('#signin-form'));
    const data = {
        "login": form.get('login'),
        "password": form.get('password'),
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
    const form = new FormData(document.querySelector('#create-user-form'));
    const data = {
        "login": form.get('login'),
        "name": form.get('name'),
        "accessToken": form.get('access-token'),
        "password": form.get('password'),
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
    const form = new FormData(document.querySelector('#get-user-form'));
    const login_id = form.get('login-id');
    fetch(`/api/users/${login_id}`, {
        credentials: 'include'
    })
    .then(function(resp) {
        return resp.json()
    })
    .then(function(user) {
        console.log(user)
    });
}
function getAllUser() { fetch('/api/users', { credentials: 'include'
    })
    .then(function(resp) {
        return resp.json()
    })
    .then(function(users) {
        console.log(users);
    });
}

function updateUser() {
    const form = new FormData(document.getElementById('update-user-form'));
    const login_id = form.get('login-id');
    const data = {};
    if (form.get('name') !== '') data['name'] = form.get('name');
    if (form.get('access-token') !== '') data['access-token'] = form.get('access-token');
    if (form.get('password') !== '') data['password'] = form.get('password');
    console.log(data);
    fetch(`/api/users/${login_id}`, {
        credentials: 'include',
        method: 'put',
        body: JSON.stringify(data)
    })
    .then(function(resp) {
        return resp.json();
    })
    .then(function (user) {
       console.log(user);
    })
}

function updateUserAvatar() {
    const form = new FormData(document.getElementById('update-user-avatar-form'));
    const login_id = form.get('login-id');
    const image = form.get('image');
    fetch(`/api/users/${login_id}/avatar`, {
        credentials: 'include',
        method: 'put',
        body: image
    })
    .then(function(resp) {
        return resp.json();
    })
    .then(function (user) {
        console.log(user);
    })
}

document.querySelector('#signin-button').addEventListener('click', signin);
document.querySelector('#signout-button').addEventListener('click', signout);
document.querySelector('#create-user-button').addEventListener('click', createUser);
document.querySelector('#get-user-button').addEventListener('click', getUser);
document.querySelector('#get-users-button').addEventListener('click', getAllUser);
document.querySelector('#update-user-button').addEventListener('click', updateUser);
document.querySelector('#update-user-avatar-button').addEventListener('click', updateUserAvatar);
