/**
 * Patient Dashboard – Viewing & Filtering Doctors
 */

import { createDoctorCard } from './components/doctorCard.js';
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { patientLogin, patientSignup } from './services/patientServices.js';

// Initialize on DOM load
document.addEventListener('DOMContentLoaded', function() {
    loadDoctorCards();

    // Modal triggers
    const signupBtn = document.getElementById('patientSignup');
    const loginBtn = document.getElementById('patientLogin');

    if (signupBtn) {
        signupBtn.addEventListener('click', () => openModal('patientSignup'));
    }

    if (loginBtn) {
        loginBtn.addEventListener('click', () => openModal('patientLogin'));
    }

    // Search and filter event listeners
    document.getElementById('searchBar').addEventListener('input', filterDoctorsOnChange);
    document.getElementById('filterTime').addEventListener('change', filterDoctorsOnChange);
    document.getElementById('filterSpecialty').addEventListener('change', filterDoctorsOnChange);
});

/**
 * Load all doctor cards
 */
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error('Error loading doctors:', error);
        alert('Failed to load doctors');
    }
}

/**
 * Handle search and filter changes
 */
function filterDoctorsOnChange() {
    const name = document.getElementById('searchBar').value;
    const time = document.getElementById('filterTime').value;
    const specialty = document.getElementById('filterSpecialty').value;

    filterDoctors(name, time, specialty).then(doctors => {
        const contentDiv = document.getElementById('content');
        contentDiv.innerHTML = doctors.length > 0 ? '' : '<p>No doctors found with the given filters.</p>';
        
        if (doctors.length > 0) {
            doctors.forEach(doctor => {
                const doctorCard = createDoctorCard(doctor);
                contentDiv.appendChild(doctorCard);
            });
        }
    }).catch(error => {
        console.error('Error filtering doctors:', error);
        alert('Failed to filter doctors');
    });
}

/**
 * Render doctor cards to the content area
 * @param {Array} doctors - Array of doctor objects
 */
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById('content');
    contentDiv.innerHTML = '';

    if (doctors.length === 0) {
        contentDiv.innerHTML = '<p>No doctors found.</p>';
        return;
    }

    doctors.forEach(doctor => {
        const doctorCard = createDoctorCard(doctor);
        contentDiv.appendChild(doctorCard);
    });
}

/**
 * Patient signup handler (called from modal)
 */
window.signupPatient = async function() {
    try {
        // Collect form data
        const name = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const phone = document.getElementById('phone').value;
        const address = document.getElementById('address').value;

        // Validation
        if (!name || !email || !password || !phone || !address) {
            alert('Please fill in all fields');
            return;
        }

        const patientData = {
            name,
            email,
            password,
            phone,
            address
        };

        const result = await patientSignup(patientData);

        if (result.success) {
            alert(result.message || 'Registration successful! Please login.');
            closeModal();
            // Reload the page
            window.location.reload();
        } else {
            alert(result.message || 'Registration failed!');
        }

    } catch (error) {
        console.error('Error during patient signup:', error);
        alert('Registration failed. Please try again.');
    }
}

/**
 * Patient login handler (called from modal)
 */
window.loginPatient = async function() {
    try {
        // Collect form data
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        // Validation
        if (!email || !password) {
            alert('Please enter both email and password');
            return;
        }

        const loginData = {
            email,
            password
        };

        const result = await patientLogin(loginData);

        if (result.success) {
            // Store JWT token in localStorage
            localStorage.setItem('token', result.token);
            localStorage.setItem('userRole', 'loggedPatient');
            
            alert('Login successful!');
            closeModal();
            
            // Redirect to logged patient dashboard
            window.location.href = 'loggedPatientDashboard.html';

        } else {
            alert(result.message || 'Login failed!');
        }

    } catch (error) {
        console.error('Error during patient login:', error);
        alert('Login failed. Please try again.');
    }
}

// Global close modal function
window.closeModal = function() {
    const modal = document.getElementById('modal');
    if (modal) {
        modal.style.display = 'none';
    }
};

// Global render content function
window.renderContent = function() {
    loadDoctorCards();
};