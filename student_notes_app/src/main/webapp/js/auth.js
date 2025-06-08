// Authentication related JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Check which form is on the page
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const errorMessage = document.getElementById('error-message');
    
    // Handle login form submission
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
            // Validate input
            if (!username || !password) {
                showError('Por favor, preencha todos os campos.');
                return;
            }
            
            // Send login request
            const formData = new URLSearchParams();
            formData.append('username', username);
            formData.append('password', password);
            
            fetch('api/auth/login', {
                method: 'POST',
                body: formData,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'success') {
                    window.location.href = 'dashboard.html';
                } else {
                    showError(data.message || 'Erro ao fazer login. Verifique suas credenciais.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showError('Erro ao conectar com o servidor. Tente novamente mais tarde.');
            });
        });
    }
    
    // Handle register form submission
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirm-password').value;
            
            // Validate input
            if (!username || !email || !password || !confirmPassword) {
                showError('Por favor, preencha todos os campos.');
                return;
            }
            
            if (password !== confirmPassword) {
                showError('As senhas não coincidem.');
                return;
            }
            
            // Validate email format
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                showError('Por favor, insira um email válido.');
                return;
            }
            
            // Send register request
            const formData = new URLSearchParams();
            formData.append('username', username);
            formData.append('email', email);
            formData.append('password', password);
            
            fetch('api/auth/register', {
                method: 'POST',
                body: formData,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'success') {
                    window.location.href = 'dashboard.html';
                } else {
                    showError(data.message || 'Erro ao registrar. Tente novamente.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showError('Erro ao conectar com o servidor. Tente novamente mais tarde.');
            });
        });
    }
    
    // Helper function to show error messages
    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.style.display = 'block';
    }
});
