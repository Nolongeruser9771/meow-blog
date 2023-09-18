//Active menu

function activeMenu() {
    //truy cập vào tất cả <a>
    const links = document.querySelectorAll('#menu a');
    const currentPath = window.location.pathname;
    console.log(currentPath);

    //lấy href của link == đường dẫn? => add class "active"
    links.forEach(link => {
        console.log(link.getAttribute('href'));
        const path = link.getAttribute('href');
        if (currentPath===path) {
            //Thêm class active vào trong thẻ link
            link.querySelector('span').classList.add('active');
        }
    })
}

activeMenu();

//Toggle theme
const btnThemeToggle = document.getElementById('theme-toggle');
btnThemeToggle.addEventListener('click', () => {
    //Ghi nhớ lựa chọn
    if (document.body.classList.contains('dark')){
        localStorage.setItem('theme', 'light')
    } else {
        localStorage.setItem('theme', 'dark')
    }
    //toggle
    document.body.classList.toggle('dark');
})

//Active theme
function activeTheme(){
    //lấy giá trị đang lưu trong localStorage
    const themeValue = localStorage.getItem('theme');

    //kiểm tra xem có tồn tại giá trị theme trong localStorage ko
    if (themeValue) {
        if (themeValue === 'dark') {
            document.body.classList.add('dark')
        } else {
            document.body.classList.remove('dark')
        }
    } else {
        document.body.classList.remove('dark');
    }
}
activeTheme();