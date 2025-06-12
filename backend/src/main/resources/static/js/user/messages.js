// 메시지 표시 함수
function showMessage(message) {
    if (message) {
        setTimeout(function() {
            alert(message);
        }, 100);
    }
}

// 에러 메시지 표시 함수
function showError(error) {
    if (error) {
        const errorElement = document.getElementById('error-message');
        if (errorElement) {
            errorElement.textContent = error;
            errorElement.style.display = 'block';
        }
    }
}

// 폼 유효성 검사
function validateForm(form) {
    const password = form.querySelector('input[type="password"]');
    const confirmPassword = form.querySelector('input[name="confirmPassword"]');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
        showError('비밀번호가 일치하지 않습니다.');
        return false;
    }
    return true;
} 