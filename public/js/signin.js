new Vue({
    el: '#signin-form',
    data: {
        login: '',
        password: '',
    },
    methods: {
        signin: function() {
            const data = {
                login: this.login,
                password: this.password,
            };
            console.log(data);
            fetch('/api/signin', {
                method: 'post',
                credentials: 'include',
                body: JSON.stringify(data),
            }).then(function (resp) {
                if (resp.ok) window.location.href = '/Diary/public/';
                else if (resp.status === 400) throw Error('sign in failed.');
                else throw Error('network error.');
            }).catch(function (err) {
                console.log(err);
            });
        },
    },
});
