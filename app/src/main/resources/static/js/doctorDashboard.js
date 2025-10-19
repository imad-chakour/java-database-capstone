/**
 * Doctor Dashboard – Managing Appointments
 */

import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// Global variables
const tableBody = document.getElementById('patientTableBody');
let selectedDate = new Date().toISOString().split('T')[0]; // Today's date
let token = localStorage.getItem('token');
let patientName = null;

// Initialize on DOM load
document.addEventListener('DOMContentLoaded', function() {
    // Set today's date in date picker
    document.getElementById('dateFilter').value = selectedDate;

    // Event listeners
    document.getElementById('todayAppointments').addEventListener('click', setTodayDate);
    document.getElementById('dateFilter').addEventListener('change', handleDateChange);
    document.getElementById('searchBar').addEventListener('input', handleSearch);

    // Load initial appointments
    loadAppointments();
});

/**
 * Set date to today and reload appointments
 */
function setTodayDate() {
    selectedDate = new Date().toISOString().split('T')[0];
    document.getElementById('dateFilter').value = selectedDate;
    loadAppointments();
}

/**
 * Handle date picker change
 */
function handleDateChange(event) {
    selectedDate = event.target.value;
    loadAppointments();
}

/**
 * Handle search input
 */
function handleSearch(event) {
    patientName = event.target.value.trim();
    if (patientName === '') {
        patientName = null;
    }
    loadAppointments();
}

/**
 * Load and display appointments
 */
async function loadAppointments() {
    try {
        // Clear existing content
        tableBody.innerHTML = '';

        // Get appointments
        const appointments = await getAllAppointments(selectedDate, patientName, token);

        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="no-appointments">
                        No appointments found for ${selectedDate === new Date().toISOString().split('T')[0] ? 'today' : 'selected date'}
                    </td>
                </tr>
            `;
            return;
        }

        // Create and append patient rows
        appointments.forEach(appointment => {
            const patientRow = createPatientRow(appointment);
            tableBody.appendChild(patientRow);
        });

    } catch (error) {
        console.error('Error loading appointments:', error);
        
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="error-message">
                    Failed to load appointments. Please try again.
                </td>
            </tr>
        `;
    }
}