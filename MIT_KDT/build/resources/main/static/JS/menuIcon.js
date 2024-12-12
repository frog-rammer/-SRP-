function btnClick() {
    let contentA = document.getElementsByClassName("layout-sidebar");
    if(contentA.style.display === 'none') {
        contentA.style.display = 'block';
    }else {
        contentA.style.display = 'none';
    }
}
