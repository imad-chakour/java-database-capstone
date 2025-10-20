/**
 * Admin Dashboard - Managing Doctors
 */

import { openModal } from '../components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

// Event Binding
document.addEventListener('DOMContentLoaded', function() {
    // Add Doctor button
    document.getElementById('addDocBtn').addEventListener('click', () => {
        openModal('addDoctor');
    });

    // Load doctors on page load
    loadDoctorCards();

    // Search and filter event listeners
    document.getElementById('searchBar').addEventListener('input', filterDoctorsOnChange);
    document.getElementById('filterTime').addEventListener('change', filterDoctorsOnChange);
    document.getElementById('filterSpecialty').addEventListener('change', filterDoctorsOnChange);
});

/**
 * Load all doctor cards on page load
 */
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error('Error loading doctors:', error);
        showNotification('Failed to load doctors', 'error');
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
 * Add a new doctor (called from modal)
 */
window.adminAddDoctor = async function() {
    try {
        // Collect form data
        const name = document.getElementById('doctorName').value;
        const specialty = document.getElementById('specialization').value;
        const email = document.getElementById('doctorEmail').value;
        const password = document.getElementById('doctorPassword').value;
        const phone = document.getElementById('doctorPhone').value;

        // Get selected availability times
        const availabilityCheckboxes = document.querySelectorAll('input[name="availability"]:checked');
        const availableTimes = Array.from(availabilityCheckboxes).map(cb => cb.value);

        // Validation
        if (!name || !specialty || !email || !password || !phone) {
            alert('Please fill in all required fields');
            return;
        }

        if (availableTimes.length === 0) {
            alert('Please select at least one availability time slot');
            return;
        }

        // Get authentication token
        const token = localStorage.getItem('token');
        if (!token) {
            alert('Authentication required. Please log in again.');
            window.location.href = '/';
            return;
        }

        // Build doctor object
        const doctor = {
            name,
            specialty,
            email,
            password,
            phone,
            availableTimes
        };

        // Save doctor
        const result = await saveDoctor(doctor, token);

        if (result.success) {
            alert(result.message || 'Doctor added successfully!');
            closeModal();
            loadDoctorCards(); // Refresh the list
        } else {
            alert(result.message || 'Failed to add doctor');
        }

    } catch (error) {
        console.error('Error adding doctor:', error);
        alert('Failed to add doctor. Please try again.');
    }
}

/**
 * Show notification message
 */
function showNotification(message, type = 'info') {
    // Simple alert for now - can be enhanced with better UI
    alert(message);
}

// Global close modal function
window.closeModal = function() {
    const modal = document.getElementById('modal');
    if (modal) {
        modal.style.display = 'none';
    }
};