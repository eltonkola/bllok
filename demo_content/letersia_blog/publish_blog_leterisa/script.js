const themeSwitcher = document.getElementById('theme-switcher');
const body = document.body;

// Check for saved theme preference
const currentTheme = localStorage.getItem('theme');
if (currentTheme) {
    body.classList.add(currentTheme);
}

themeSwitcher.addEventListener('click', () => {
    body.classList.toggle('dark-theme');
    let theme = 'light-theme';
    if (body.classList.contains('dark-theme')) {
        theme = 'dark-theme';
    }
    localStorage.setItem('theme', theme);
});

// Category Toggle Functionality
document.addEventListener('DOMContentLoaded', function () {
    const categoryWidgets = document.querySelectorAll('.categories-widget');

    categoryWidgets.forEach(widget => {
        const toggles = widget.querySelectorAll('.category-toggle');
        const parentLinks = widget.querySelectorAll('li.has-children > a.category-parent-link');

        // Function to handle toggle
        function performToggle(listItem, toggleElement) {
            const subMenu = listItem.querySelector('ul');
            if (subMenu) {
                listItem.classList.toggle('expanded');
                if (listItem.classList.contains('expanded')) {
                    if(toggleElement) toggleElement.textContent = '[-]';
                    subMenu.style.display = 'block'; // Or use a class for this
                } else {
                    if(toggleElement) toggleElement.textContent = '[+]';
                    subMenu.style.display = 'none'; // Or use a class for this
                }
            }
        }

        toggles.forEach(toggle => {
            toggle.addEventListener('click', function (event) {
                event.preventDefault(); // Prevent default if toggle itself is a link
                const listItem = this.closest('li.has-children');
                if (listItem) {
                    performToggle(listItem, this);
                }
            });
        });

        // Optional: make parent link also toggle
        // parentLinks.forEach(link => {
        //     link.addEventListener('click', function(event) {
        //         event.preventDefault(); // Stop navigation
        //         const listItem = this.closest('li.has-children');
        //         const toggleSpan = listItem.querySelector('.category-toggle');
        //         if (listItem) {
        //             performToggle(listItem, toggleSpan);
        //         }
        //     });
        // });
    });
});
