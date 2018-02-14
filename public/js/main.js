function prevUserInfo() {
    fetch(`/api/user}`, {
        credentials: 'include'
    })
        .then(function(resp) {
            return resp.json()
        })
        .then(function(user) {
            console.log(user)
        });
}

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
    const header = new Headers();
    header.append("Content-Type", image.type);
    fetch(`/api/users/${login_id}/avatar`, {
        credentials: 'include',
        method: 'put',
        body: image,
        headers: header
    })
    .then(function(resp) {
        return resp.json();
    })
    .then(function (user) {
        console.log(user);
    })
}

function deleteUser() {
    const form = new FormData(document.querySelector('#delete-user-form'));
    const login_id = form.get('login-id');
    fetch(`/api/users/${login_id}`, {
        credentials: 'include',
        method: 'delete'
    })
        .then(function(resp) {
            return resp.json()
        })
        .then(function(user) {
            console.log(user)
        });
}

function postDiary() {
    const form = new FormData(document.querySelector('#post-diary-form'));
    const data = {
        title: form.get('title'),
        body: form.get('body')
    };
    fetch('/api/diaries', {
        method: 'post',
        credentials: 'include',
        body: JSON.stringify(data)
    })
        .then(function(resp) {
            return resp.json();
        })
        .then(function(msg) {
            console.log(msg);
        });
}

function getAlldiary() {
    fetch('/api/diaries', {
        credentials: 'include'
    })
        .then(function(resp) {
            return resp.json()
        })
        .then(function(users) {
            console.log(users);
        });
}

document.querySelector('#signin-button').addEventListener('click', signin);
document.querySelector('#signout-button').addEventListener('click', signout);
document.querySelector('#create-user-button').addEventListener('click', createUser);
document.querySelector('#get-user-button').addEventListener('click', getUser);
document.querySelector('#get-users-button').addEventListener('click', getAllUser);
document.querySelector('#update-user-button').addEventListener('click', updateUser);
document.querySelector('#update-user-avatar-button').addEventListener('click', updateUserAvatar);
document.querySelector('#delete-user-button').addEventListener('click', deleteUser);
document.querySelector('#post-diary-button').addEventListener('click', postDiary);
document.querySelector('#get-diaries-button').addEventListener('click', getAlldiary);

