class Gdpr {
    init() {
        document.querySelector("[open-delete-modal]").addEventListener("click", this.openDeleteAccountModal);
        document.getElementById("delete-user-account").addEventListener("click", this.deleteUserAccount);

        document.getElementById("toggle-email-visibility").addEventListener("click", this.toggleUserEmailVisibility.bind(this));
        this.usernameFeedback = document.querySelector("[feedback-container]");

        this.timeout = null;
    }

    async toggleUserEmailVisibility(e) {
        e.preventDefault();
        document.getElementById("spinner").classList.add("loading");
        window.clearTimeout(this.timeout);
        const publicEmail = document.getElementById("publicEmail").checked;
        await fetch("/api/user/email/public", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({publicEmail})
            })
            .then(resp => resp.json())
            .then((response) => {
                responseInterceptor(response);
                if(response.resultCode === "SUCCESS") {
                    this.usernameFeedback.textContent = "We have set your username preferences."
                    this.usernameFeedback.classList.add("active");
                    this.timeout = window.setTimeout(() => {
                        this.usernameFeedback.classList.remove("active");
                        this.usernameFeedback.textContent = "";
                    }, 5000);

                }
            })
            .catch(() => {
                this.usernameFeedback.textContent = "We are sorry, we couldn't set your username preferences. Please try again later."
                this.usernameFeedback.classList.add("active");
                this.timeout = window.setTimeout(() => {
                    this.usernameFeedback.classList.remove("active");
                    this.usernameFeedback.textContent = "";
                }, 5000);
            })
            .finally(() => {
                document.getElementById("spinner").classList.remove("loading");
            });
    }
    
    openDeleteAccountModal(e) {
        e.preventDefault();
        this.deleteAccountModal = document.getElementById('delete-user-account-modal');
        this.deleteAccountModal.classList.add('active');
        
        document.getElementById('delete-user-account-cancel').addEventListener('click', e => {
            e.preventDefault();
            this.deleteAccountModal.classList.remove('active');
        });
    }

    async deleteUserAccount() {
        document.getElementById("spinner").classList.add("loading");
        await fetch("/api/user/delete", {
                method: "DELETE"
            })
            .then((response) => {
                responseInterceptor(response);
                window.location.href = "/login"; 
            })
            .catch(() => {
                this.deleteAccountModal.classList.remove('active');
                document.querySelector("[delete-error]").textContent = "We are sorry, we couldn't download your personal data. Please try again later."
            });
    }
}

const gdpr = new Gdpr();
gdpr.init();
