/**
 * Role-Based Login Handling Service
 * Handles admin and doctor authentication
 */

// Import required modules
import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

// Define API endpoints
const ADMIN_API = API_BASE_URL + '/admin/login';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

// Setup button event listeners when window loads
window.onload = function () {
    // Admin login button
    const adminBtn = document.getElementById('adminLogin');
    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }

    // Doctor login button
    const doctorBtn = document.getElementById('doctorLogin');
    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
        });
    }

    // Patient login button (if exists on index page)
    const patientLoginBtn = document.getElementById('patientLogin');
    if (patientLoginBtn) {
        patientLoginBtn.addEventListener('click', () => {
            openModal('patientLogin');
        });
    }

    // Patient signup button (if exists on index page)
    const patientSignupBtn = document.getElementById('patientSignup');
    if (patientSignupBtn) {
        patientSignupBtn.addEventListener('click', () => {
            openModal('patientSignup');
        });
    }
};

// Global admin login handler
window.adminLoginHandler = async function(event) {
    event.preventDefault();
    
    const username = document.getElementById('adminUsername').value;
    const password = document.getElementById('adminPassword').value;

    if (!username || !password) {
        alert('Please enter both username and password');
        return;
    }

    const admin = { username, password };

    try {
        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            
            // Store token and role
            localStorage.setItem('token', data.token);
            localStorage.setItem('userRole', 'admin');
            localStorage.setItem('adminId', data.adminId || '');
            
            // Call selectRole to handle role selection and redirection
            selectRole('admin');
            
            // Close modal
            closeModal();
            
            // Show success message
            showNotification('Admin login successful!', 'success');
            
        } else {
            const errorData = await response.json();
            alert(errorData.message || 'Invalid credentials!');
        }
    } catch (error) {
        console.error('Admin login error:', error);
        alert('Login failed. Please try again.');
    }
};

// Global doctor login handler
window.doctorLoginHandler = async function(event) {
    event.preventDefault();
    
    const email = document.getElementById('doctorEmail').value;
    const password = document.getElementById('doctorPassword').value;

    if (!email || !password) {
        alert('Please enter both email and password');
        return;
    }

    const doctor = { email, password };

    try {
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            
            // Store token and role
            localStorage.setItem('token', data.token);
            localStorage.setItem('userRole', 'doctor');
            localStorage.setItem('doctorId', data.doctorId || '');
            localStorage.setItem('doctorName', data.name || '');
            
            // Call selectRole to handle role selection and redirection
            selectRole('doctor');
            
            // Close modal
            closeModal();
            
            // Show success message
            showNotification('Doctor login successful!', 'success');
            
        } else {
            const errorData = await response.json();
            alert(errorData.message || 'Invalid credentials!');
        }
    } catch (error) {
        console.error('Doctor login error:', error);
        alert('Login failed. Please try again.');
    }
};

// Global patient login handler
window.patientLoginHandler = async function(event) {
    event.preventDefault();
    
    const email = document.getElementById('patientEmail').value;
    const password = document.getElementById('patientPassword').value;

    if (!email || !password) {
        alert('Please enter both email and password');
        return;
    }

    const patient = { email, password };

    try {
        // This endpoint will be defined in your backend
        const response = await fetch(API_BASE_URL + '/patient/login', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(patient)
        });

        if (response.ok) {
            const data = await response.json();
            
            // Store token and role
            localStorage.setItem('token', data.token);
            localStorage.setItem('userRole', 'loggedPatient');
            localStorage.setItem('patientId', data.patientId || '');
            localStorage.setItem('patientName', data.name || '');
            
            // Call selectRole to handle role selection and redirection
            selectRole('loggedPatient');
            
            // Close modal
            closeModal();
            
            // Show success message
            showNotification('Patient login successful!', 'success');
            
        } else {
            const errorData = await response.json();
            alert(errorData.message || 'Invalid credentials!');
        }
    } catch (error) {
        console.error('Patient login error:', error);
        alert('Login failed. Please try again.');
    }
};

// Global patient signup handler
window.patientSignupHandler = async function(event) {
    event.preventDefault();
    
    const name = document.getElementById('signupName').value;
    const email = document.getElementById('signupEmail').value;
    const phone = document.getElementById('signupPhone').value;
    const address = document.getElementById('signupAddress').value;
    const password = document.getElementById('signupPassword').value;

    if (!name || !email || !phone || !address || !password) {
        alert('Please fill in all fields');
        return;
    }

    const patient = { name, email, phone, address, password };

    try {
        // This endpoint will be defined in your backend
        const response = await fetch(API_BASE_URL + '/patient/signup', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(patient)
        });

        if (response.ok) {
            const data = await response.json();
            
            alert('Patient registration successful! Please login.');
            closeModal();
            
            // Switch to login modal
            openModal('patientLogin');
            
        } else {
            const errorData = await response.json();
            alert(errorData.message || 'Registration failed!');
        }
    } catch (error) {
        console.error('Patient signup error:', error);
        alert('Registration failed. Please try again.');
    }
};

// Helper function to show notifications
function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    // Add styles
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 5px;
        color: white;
        font-weight: bold;
        z-index: 10000;
        transition: all 0.3s ease;
        ${type === 'success' ? 'background: #4CAF50;' : ''}
        ${type === 'error' ? 'background: #f44336;' : ''}
        ${type === 'info' ? 'background: #2196F3;' : ''}
    `;
    
    document.body.appendChild(notification);
    
    // Remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Global function to close modal
window.closeModal = function() {
    const modal = document.getElementById('modal');
    if (modal) {
        modal.style.display = 'none';
    }
};

// Export for testing purposes
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { 
        adminLoginHandler: window.adminLoginHandler,
        doctorLoginHandler: window.doctorLoginHandler,
        patientLoginHandler: window.patientLoginHandler,
        patientSignupHandler: window.patientSignupHandler
    };
}