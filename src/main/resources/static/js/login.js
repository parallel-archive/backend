class Login {
    init() {
        document.addEventListener("open-login", e => {
            this.openLoginModal(e);
        })
        this.targetUrlInput = document.querySelector("input[name='targetUrl']");
    }

    openLoginModal(e) {
        e.preventDefault();
        this.loginModal = document.getElementById('login-modal');
        this.loginModal.querySelector("input[name='username']")?.setAttribute("required", "required");
        this.loginModal.querySelector("input[name='password']")?.setAttribute("required", "required");
        this.loginModal.classList.add('active');
        this.targetUrlInput.setAttribute("value", e.detail.targetUrl)
        document.getElementById("login-submit").focus();

        this.form = document.querySelector(".login-form");

        document.getElementById('login-close').addEventListener('click', e => {
            e.preventDefault();
            this.loginModal.querySelector("input[name='username']")?.removeAttribute("required");
            this.loginModal.querySelector("input[name='password']")?.removeAttribute("required");
            this.loginModal.classList.remove('active');
        });

        document.addEventListener("keyup", e => {
            if(e.key === "Enter") {
                this.form.submit();
            }
        });
    }
}

const lgn = new Login();
lgn.init();