function responseInterceptor (response) {
    if(response.status === 401){
        window.location.href = `/login?targetUrl=${window.location.pathname}${window.location.search}`;
    }
}

const pwd1 = document.querySelector("[pwd-1]");
const pwd2 = document.querySelector("[pwd-2]");

if(pwd1 && pwd2) {
    passwordMatchValidator();
}

function passwordMatchValidator() {
    const validationError = document.querySelector(".client-error");

    pwd1.addEventListener("input", () => {
        if(pwd1.value.length && pwd2.value.length && pwd1.value !== pwd2.value) {
            validationError.removeAttribute("hidden");
        } else {
            validationError.setAttribute("hidden", "hidden");

        }
    })

    pwd2.addEventListener("input", e => {
        if(pwd1.value.length && pwd2.value.length && pwd1.value !== pwd2.value) {
            validationError.removeAttribute("hidden");
        } else {
            validationError.setAttribute("hidden", "hidden");
        }
    })
}
