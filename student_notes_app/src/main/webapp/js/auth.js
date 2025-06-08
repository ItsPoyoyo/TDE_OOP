// Authentication related JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const errorMessage = document.getElementById('error-message');

    // Login
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            if (!username || !password) {
                showError('Por favor, preencha todos os campos.');
                return;
            }

            const formData = new URLSearchParams();
            formData.append('username', username);
            formData.append('password', password);

            fetch('api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: formData,
                credentials: 'include' // ✅ Send cookies
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'success') {
                    window.location.href = 'dashboard.html';
                } else {
                    showError(data.message || 'Erro ao fazer login.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showError('Erro ao conectar com o servidor.');
            });
        });
    }

    // Register
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirm-password').value;

            if (!username || !email || !password || !confirmPassword) {
                showError('Por favor, preencha todos os campos.');
                return;
            }

            if (password !== confirmPassword) {
                showError('As senhas não coincidem.');
                return;
            }

            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                showError('Por favor, insira um email válido.');
                return;
            }

            const formData = new URLSearchParams();
            formData.append('username', username);
            formData.append('email', email);
            formData.append('password', password);

            fetch('api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: formData,
                credentials: 'include' // ✅ Send cookies
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'success') {
                    window.location.href = 'dashboard.html';
                } else {
                    showError(data.message || 'Erro ao registrar.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showError('Erro ao conectar com o servidor.');
            });
        });
    }

    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.style.display = 'block';
    }
});
