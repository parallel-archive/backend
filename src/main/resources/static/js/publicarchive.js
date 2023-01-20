class PublicArchive {
  init() {
    this.leftnav = document.querySelector(".leftnav");
    this.filterTitle = document.querySelector(".filter-title");
    this.filterTitleCheckbox = document.querySelector("#filter-title");
    this.form = document.querySelector("form");
    this.errorSnackbar = document.querySelector(".error-snackbar");
    this.errorSnackbarContent = document.querySelector(
      ".error-snackbar-content"
    );
    this.errorSnackbarClose = document.getElementById("error-snackbar-close");
    this.hiddenSort = document.querySelector(".options input[name='sort']");
    this.hiddenSortBy = document.querySelector(".options input[name='sortBy']");
    document.querySelector("#warning-close").addEventListener("click", (e) => {
      e.target.closest("#atc-warning").classList.remove("active");
      document.body.removeAttribute("data-overlay-open");
    });

    this.cardCheckboxes = Array.from(
      document.querySelectorAll("[data-save-to-collection]")
    );
    this.cardCheckboxes.forEach((cb) =>
      cb.addEventListener("change", (e) => this.handleSelection(e))
    );
    this.addToCollectionButtons = Array.from(
      document.querySelectorAll("[add-to-collection]")
    );

    this.collectionCheckboxes = document
      .getElementById("rename-collection-modal")
      .querySelectorAll("input");

    this.addToCollectionButtons.forEach((button) => {
      button.addEventListener("click", (e) => {
        e.preventDefault();
        if (e.target.getAttribute("data-modal") === "atc-warning") {
          document.querySelector("#atc-warning").classList.add("active");
        } else {
          let selectedPublication = document
            .querySelector("[data-save-to-collection]:checked")
            .getAttribute("id");
          e.target.setAttribute("data-url", selectedPublication);
          this.openCollectionsModal(e);
        }
      });
    });

    let searchParams = new URLSearchParams(window.location.search);
    this.hiddenSort.value = searchParams.get("sort");
    this.hiddenSortBy.value = searchParams.get("sortBy");

    const sortWidgets = Array.from(
      document.querySelectorAll("#publicarchive select")
    );
    sortWidgets.forEach((select) => {
      select?.addEventListener("change", (e) => {
        e.preventDefault();
        this.hiddenSort.value = e.target.value;
        this.hiddenSortBy.value = e.target
          .querySelector("option:checked")
          .getAttribute("sortby");
        this.submitButton.click();
      });
    });

    this.openAccordion();

    this.filterGroup = document.querySelector(".filtergroup");
    if (window.innerWidth <= 959) {
      this.filterTitleCheckbox.checked = false;
      this.filterTitle.addEventListener("click", this.toggleModal.bind(this));
    }

    const bookmarks = Array.from(document.querySelectorAll("[alt='bookmark']"));
    bookmarks.forEach((bm) =>
      bm.addEventListener("click", (e) => {
        if (e.target.dataset.modal === "login") {
          document.dispatchEvent(
            new CustomEvent("open-login", {
              bubbles: true,
              cancelable: true,
              detail: { targetUrl: "/" },
            })
          );
        } else if (e.target.dataset.modal === "collections") {
          this.openCollectionsModal(e);
        }
      })
    );

    const searchContainer = document.querySelector(".search-container");
    this.searchInput = document.querySelector("#search input");
    this.searchInput.addEventListener("focus", () => {
      searchContainer.setAttribute("active", "active");
    });
    this.searchInput.addEventListener("blur", () => {
      searchContainer.removeAttribute("active");
    });
    this.submitButton = document.querySelector("[apply-filters-button]");
    document?.addEventListener("keydown", (e) => {
      this.searchOnEnter(e);
    });

    document.addEventListener("click", (e) => {
      if (e.target.matches(".search-clear-button, .search-clear-button *")) {
        window.location.href = "/";
      } else if (e.target.matches("[search-submit]")) {
        document.querySelector("button[form='publicarchive']").click();
      }
    });
  }

  searchOnEnter(e) {
    if (e.code === "Enter" && e.target.matches("input#search-input")) {
      document.querySelector("button[form='publicarchive']").click();
      document
        .querySelector(".search-button")
        .setAttribute("disabled", "disabled");
      document?.removeEventListener("keydown", this.searchOnEnter);
    }
  }

  toggleModal() {
    if (!this.leftnav?.hasAttribute("modal-open")) {
      this.leftnav.setAttribute("modal-open", "modal-open");
      document.body.setAttribute("data-overlay-open", "data-overlay-open");
      const modalClose = document.querySelector(
        "[modal-open='modal-open'] .modal-header .close"
      );

      this.openAccordion();

      modalClose.addEventListener("click", (e) => {
        if (this.leftnav.hasAttribute("modal-open")) {
          this.leftnav.removeAttribute("modal-open");
          document.body.removeAttribute("data-overlay-open");
          this.filterTitleCheckbox.checked = false;
          this.filterTitle.addEventListener(
            "click",
            this.toggleModal.bind(this)
          );
        }
      });
    }
  }

  openAccordion() {
    Array.from(
      document.querySelectorAll(".accordion-option input:checked")
    ).forEach((i) => {
      let parentAccordion = i.closest(".accordion");
      let optionsController = parentAccordion.querySelector(
        ".accordion-controller"
      );
      if (optionsController && !optionsController.checked) {
        optionsController.checked = true;
      }
    });
  }

  handleSelection(e) {
    if (e.target.checked) {
      window.selectedPublication = document
        .querySelector("[data-save-to-collection]:checked")
        .getAttribute("id")
        .split("/")
        .pop();
      this.addToCollectionButtons.forEach((button) => {
        button.removeAttribute("disabled");
        this.cardCheckboxes.forEach((cb) => {
          if (cb !== e.target) {
            cb.checked = false;
          }
        });
      });
    }
    this.cardCheckboxes.every((cb) => cb.checked === false)
      ? this.addToCollectionButtons.forEach((button) => {
          button.setAttribute("disabled", "disabled");
          window.selectedPublication = null;
        })
      : "";
  }

  openCollectionsModal(e) {
    e.preventDefault();
    this.collectionsModal = document.getElementById("rename-collection-modal");
    this.collectionsModal.classList.add("active");
    document.body.setAttribute("data-overlay-open", "data-overlay-open");
    this.saveToCollectionForm = document.querySelector(
      ".save-to-collection-form"
    );
    if (this.saveToCollectionForm) {
      this.saveToCollectionButton = document.getElementById(
        "save-to-collection-submit"
      );
      this.saveToCollectionButton?.focus();

      let urlArr = e.currentTarget.dataset.url.split("/");
      let hash = urlArr[urlArr.length - 1];
      this.collectionsModal.querySelector(
        "#save-to-collection-submit"
      ).dataset.hash = hash;

      this.collectionsModal.querySelectorAll("input").forEach((input) => {
        input.checked = false;
        input.addEventListener("change", (e) => {
          this.collectionCheckboxes.forEach((cb) => {
            if (cb !== e.target) {
              cb.closest("li").classList.remove("selected");
              cb.checked = false;
            }
          });

          this.collectionsModal.querySelector(
            "#save-to-collection-submit"
          ).dataset.collectionId =
            this.collectionsModal.querySelector("input:checked")?.dataset.id;
          if (this.collectionsModal.querySelector("input:checked") !== null) {
            this.saveToCollectionButton.removeAttribute("disabled");
            e.target.parentNode.classList.add("selected");
          } else {
            this.saveToCollectionButton.setAttribute("disabled", "disabled");
            e.target.parentNode.classList.remove("selected");
          }
        });
      });
    }

    document
      .getElementById("save-to-collection-cancel")
      ?.addEventListener("click", (e) => {
        e.preventDefault();
        e.currentTarget.removeAttribute("data-url");
        this.collectionsModal.classList.remove("active");
        document.body.removeAttribute("data-overlay-open");
      });

    const href = window.location.href;
    this.collectionsModal
      .querySelector("#save-to-collection-submit")
      ?.addEventListener("click", (e) => {
        e.preventDefault();
        fetch(
          `/api/collection/${e.currentTarget.dataset.collectionId}/publication/${e.currentTarget.dataset.hash}`,
          { method: "PUT", headers: { "Content-Type": "application/json" } }
        )
          .then((d) => {
            responseInterceptor(d);
            if (d.status === 200) {
              this.openErrorSnackbar("Saved to collection.");
              this.collectionsModal.classList.remove("active");
              document.addEventListener("snackbar-close", () => {
                window.location.href = href;
              });
              window.location.href = href;
            } else if (d.status === 400) {
              this.openErrorSnackbar("Publication is already in collection.");
              document.addEventListener("snackbar-close", () => {
                window.location.href = href;
              });
            } else {
              this.collectionsModal.classList.remove("active");
              e.currentTarget.removeAttribute("data-url");
              this.openErrorSnackbar(
                "Something went wrong. Failed to save publication to collection."
              );
            }
          })
          .catch((e) => {
            this.openErrorSnackbar(
              e?.message ||
                "Something went wrong. Failed to save publication to collection."
            );
            e.currentTarget.removeAttribute("data-url");
          });
      });
    document?.addEventListener("keydown", (e) => {
      this.addOnEnter(e);
    });

    document.addEventListener("click", (e) => {
      if (e.target.matches("#create-collection-submit")) {
        e.preventDefault();
        e.stopPropagation();
        let createCollectionForm = e.target.closest("form");
        createCollectionForm.querySelector("#publication-hash").value =
          window.selectedPublication;
        createCollectionForm.submit();
      }
    });

    document
      .getElementById("add-collection-button")
      ?.addEventListener("click", (e) => {
        e.preventDefault();
        addModal.classList.add("active");
        document.body.setAttribute("data-overlay-open", "data-overlay-open");
        addModal.querySelector("input").focus();
      });

    document
      .getElementById("create-collection-cancel")
      ?.addEventListener("click", (e) => {
        e.preventDefault();
        this.collectionsModal.classList.remove("active");
        document.body.removeAttribute("data-overlay-open");
      });
  }

  addOnEnter(e) {
    if (e.code === "Enter") {
      document.querySelector("#create-collection-submit").click();
      document?.removeEventListener("keydown", this.addOnEnter);
    }
  }

  openErrorSnackbar(message) {
    this.errorSnackbar.classList.add("active");
    this.timeout = window.setTimeout(() => {
      this.errorSnackbar.classList.remove("active");
      document.dispatchEvent(new CustomEvent("snackbar-close"), {
        bubbles: true,
        cancelable: true,
        detail: {},
      });
    }, 3000);
    this.errorSnackbarContent.textContent = message;
    this.errorSnackbarClose.addEventListener("click", () => {
      this.errorSnackbar.classList.remove("active");
      document.dispatchEvent(new CustomEvent("snackbar-close"), {
        bubbles: true,
        cancelable: true,
        detail: {},
      });
    });
  }
}

const pa = new PublicArchive();
pa.init();
