function validateForm() {
    const file = document.getElementById("file").value;
    if (file === "") {
        alert("請上傳記帳Excel");
        return false;
    }
}