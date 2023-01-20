class Publication {
    init() {
        this.errorSnackbar = document.querySelector(".error-snackbar");
        this.errorSnackbarContent = document.querySelector(".error-snackbar-content");
        this.errorSnackbarClose = document.getElementById("error-snackbar-close");
        document.querySelector("[data-modal='collections']")?.addEventListener("click", (e) => this.openCollectionsModal(e));
        document.querySelector("[data-modal='login']")?.addEventListener("click", e => {
            document.dispatchEvent(new CustomEvent("open-login", {
                bubbles: true,
                cancelable: true,
                detail: {targetUrl: window.location.pathname}
            }))
        });
        this.saveToCollectionButton = document.getElementById("save-to-collection-submit");
        this.cancelButton = document.getElementById("save-to-collection-cancel")

        this.cancelButton?.addEventListener('click', e => {
            e.preventDefault();
            this.collectionsModal.classList.remove("active");
        });

        this.saveToCollectionButton?.addEventListener('click', e => {
            e.preventDefault();
            e.stopPropagation();
            this.saveToCollection(e);
        })

        document?.addEventListener("keydown", e => {
            this.addOnEnter(e);
        });

        document.getElementById('add-collection-button')?.addEventListener('click', e => {
            e.preventDefault();
            addModal.classList.add('active');
            document.body.setAttribute("data-overlay-open", "data-overlay-open");
            addModal.querySelector('input').focus();
        });

        document.getElementById("create-collection-cancel")?.addEventListener("click", e => {
            e.preventDefault();
            this.collectionsModal.classList.remove("active");
            document.body.removeAttribute("data-overlay-open");
        });
    }

    openCollectionsModal(e) {
        e.preventDefault();
        this.collectionsModal = document.getElementById("rename-collection-modal");
        this.collectionsModal.classList.add("active");
        document.body.setAttribute("data-overlay-open", "data-overlay-open");
        this.saveToCollectionForm = document.getElementById("save-to-collection-form");
        this.collectionCheckboxes = this.collectionsModal.querySelectorAll('input')
        if(this.saveToCollectionForm) {
            this.saveToCollectionButton.setAttribute("disabled", "disabled");
            this.cancelButton?.focus();

            let urlArr = e.currentTarget.dataset.url.split("/");
            let hash = urlArr[urlArr.length - 1];
            this.saveToCollectionButton.dataset.hash = hash;

            this.collectionCheckboxes.forEach(input => {
                input.checked = false;
                input.addEventListener("change", e => {
                    this.saveToCollectionButton.dataset.collectionId = this.collectionsModal.querySelector("input:checked")?.dataset.id
                    if(this.collectionsModal.querySelector("input:checked") !== null) {
                        this.saveToCollectionButton.removeAttribute("disabled");
                    } else {
                        this.saveToCollectionButton.setAttribute("disabled", "disabled");
                    }

                    this.collectionCheckboxes.forEach(ip => {
                        if(ip.dataset.id !== e.target.dataset.id) {
                            ip.checked = false;
                        }
                    })
                })

            })

        } else {
            e.preventDefault();
            e.stopPropagation();
            const hash = window.location.pathname.split("/").pop();
            document.querySelector("#publication-hash").value = hash;
            const createCollectionForm = document.querySelector("#create-collection-form");
            createCollectionForm.addEventListener("submit", e => {
                createCollectionForm.submit();
            });
        }
    }

    saveToCollection(e) {
        if(e.currentTarget.dataset.collectionId) {
            fetch(`/api/collection/${e.currentTarget.dataset.collectionId}/publication/${e.currentTarget.dataset.hash}`, {method:'PUT', headers:{'Content-Type':'application/json'}}).then(d => {
                responseInterceptor(d);
                if (d.status === 200) {
                    this.openErrorSnackbar("Saved to collection.")
                    this.collectionsModal.classList.remove("active");
                } else if(d.status === 400) {
                    this.openErrorSnackbar("Publication is already in collection.");
                    this.collectionsModal.classList.remove("active");
                }else{
                    this.collectionsModal.classList.remove("active");
                    this.openErrorSnackbar("Something went wrong. Failed to save publication to collection.");
                }
            })
            .catch(e => {
                this.openErrorSnackbar(e?.message || "Something went wrong. Failed to save publication to collection.");
            });
        }
    }

    addOnEnter(e) {
        if(e.code === "Enter") {
            this.saveToCollectionButton.click();
            document?.removeEventListener("keydown", this.addOnEnter);
        }
    }

    openErrorSnackbar(message) {
        this.errorSnackbar.classList.add("active");
        this.timeout = window.setTimeout(() => {
            this.errorSnackbar.classList.remove("active");
        }, 3000);
        this.errorSnackbarContent.textContent = message;
        this.errorSnackbarClose.addEventListener("click", () => {
            this.errorSnackbar.classList.remove("active");
        })
    }
}

const pub = new Publication();
pub.init()
