fetch('/api/myself', {
    method: "get",
    credentials: 'include'
}).then(function(resp) {
    if (resp.ok) {
        return resp.json();
    } else {
        throw new Error('user not signined.')
    }
}).then(function (myself) {
    new Vue({
        el: '#account-info',
        data: {
            myself: myself,
        },
    });

    let diaries = new Vue({
        el: '#diary-list',
        data: {
            diaries: [],
        },
        created: function () {
            this.getDiaries();
        },
        methods: {
            getDiaries: async function () {
                let diaries = await fetch('/api/diaries', {
                    credentials: 'include'
                }).then(function(resp) {
                    return resp.json()
                });
                this.diaries = diaries;
            }
        }
    });

    window.diaries = diaries;
}).catch(function (err) {
    console.log(err);
    window.location.href = '/Diary/public/signin/';
});
