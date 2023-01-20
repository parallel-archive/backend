class GlobalMenu {
    constructor() {
        this.userMenuOpen = false;
        this.drawerMenuOpen = false;
    }
    
    init() {
        this.globalMenu = document.querySelector("#globalmenu");
        this.backDrop = document.querySelector(".backdrop");
        if(this.drawerMenuOpen) {
            this.globalMenu.setAttribute("data-drawer-open", true);
        }

        Array.from(document.querySelectorAll("[globalmenu-login]")).forEach(el => {
            el.addEventListener("click", e => {
                e.preventDefault();
                if(this.drawerMenuOpen) {
                    this.closeDrawerMenu();
                }
                let targetUrl = e.target.getAttribute("target");
                document.dispatchEvent(new CustomEvent("open-login", {
                    bubbles: true,
                    cancelable: true,
                    detail: {targetUrl}
                }))
            })
        })
        this.setMenuItemToSelected();

        document.querySelector('[filtering-modal]')?.addEventListener('click', 
        e => document.querySelector('.action-buttons')?.classList.add('animated'))
    }

    setMenuItemToSelected() {
        let selectedMenu = document.querySelector(`[href^="/${window.location.pathname.split("/")[1]}"][menu-item]`);
        
        if (selectedMenu) {
            selectedMenu.setAttribute("data-selected", true);
        }
    }

    openDrawerMenu() {
        this.drawerMenuOpen = true;
        document.body?.setAttribute("data-overlay-open", "data-overlay-open");
        this.globalMenu.setAttribute("data-drawer-open", true);
    }

    closeDrawerMenu() {
        if (this.drawerMenuOpen) {
            this.drawerMenuOpen = false;
            document.body?.removeAttribute("data-overlay-open");
            this.globalMenu.setAttribute("data-drawer-open", false);
        }
    }
}

const gm = new GlobalMenu();
window.gm = gm;
gm.init();
